package com.hyunwoosing.perturba.domain.job.service;

import com.hyunwoosing.perturba.common.util.ParamKeyUtil;
import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.repository.AssetRepository;
import com.hyunwoosing.perturba.domain.guest.entity.GuestSession;
import com.hyunwoosing.perturba.domain.guest.repository.GuestSessionRepository;
import com.hyunwoosing.perturba.domain.job.entity.TransformJob;
import com.hyunwoosing.perturba.domain.job.entity.enums.JobStatus;
import com.hyunwoosing.perturba.domain.job.error.JobErrorCode;
import com.hyunwoosing.perturba.domain.job.error.JobException;
import com.hyunwoosing.perturba.domain.job.repository.JobRepository;
import com.hyunwoosing.perturba.domain.job.web.dto.request.CreateJobRequest;
import com.hyunwoosing.perturba.domain.job.web.dto.request.FeedbackRequest;
import com.hyunwoosing.perturba.domain.user.entity.User;
import org.springframework.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepo;
    private final AssetRepository assetRepo;
    private final GuestSessionRepository guestRepo;

    //todo: 멱등키는 같은데 파라미터가 다른경우 에러처리 필요할듯.. 어디에서 에러를 터트릴지는 고민

    @Transactional
    public TransformJob create(CreateJobRequest req, User user, Long guestId, @Nullable String idemKey) {
        Asset input = assetRepo.findById(req.inputAssetId()).orElseThrow(() ->
                new JobException(JobErrorCode.INPUT_ASSET_NOT_FOUND, "입력된 Asset 을 찾을 수 없습니다."));

        if (user != null && input.getOwner() != null && !Objects.equals(input.getOwner().getId(), user.getId())) {
            throw new JobException(JobErrorCode.ASSET_NOT_OWNED_BY_USER, "현재 유저의 소유가 아닌 Asset 입니다.");
        }

        //멱등키 검증
        if (idemKey != null && !idemKey.isBlank()) {
            Optional<TransformJob> existingByIdempotency = findByIdempotency(user, guestId, idemKey);
            if (existingByIdempotency.isPresent())
                return existingByIdempotency.get();
        }

        //user, input, paramKey 가 동일한 job이 존재할 경우 기존의 응답을 재사용, 게스트는 정책상 적용 안할듯..
        String paramKey = ParamKeyUtil.of(req.intensity());
        Optional<TransformJob> existing = findExistingJob(user, input, paramKey);
        if (existing.isPresent())
            return existing.get();


        TransformJob job = TransformJob.builder()
                .clientChannel(req.clientChannel())
                .requestMode(req.requestMode())
                .user(user)
                .guest(resolveGuest(guestId))
                .inputAsset(input)
                .intensity(req.intensity())
                .notifyVia(req.notifyVia())
                .status(JobStatus.QUEUED)
                .paramKey(paramKey)
                .idempotencyKey((idemKey != null && !idemKey.isBlank()) ? idemKey : null)
                .build();

        //TODO: 외부 AI 서버 호출(Flask/Django)

        try {
            job = jobRepo.save(job);
        } catch (DataIntegrityViolationException e) {
            //동시성처리, 같은 멱등키로 동시에 들어온 경우 유니크 충돌 발생, 다시 조회후 반환
            if (idemKey != null && !idemKey.isBlank()) {
                if (user != null) {
                    return jobRepo.findByUserAndIdempotencyKey(user, idemKey).orElseThrow(() -> e);
                } else if (guestId != null) {
                    return jobRepo.findByGuest_IdAndIdempotencyKey(guestId, idemKey).orElseThrow(() -> e);
                }
            }
            throw e;
        }

        return job;
    }

    @Transactional(readOnly = true)
    public TransformJob getByPublicId(String publicId) {
        return jobRepo.findByPublicId(publicId).orElseThrow(() ->
                new JobException(JobErrorCode.JOB_NOT_FOUND, "해당 작업을 찾을 수 없습니다."));
    }

    @Transactional
    public void saveFeedback(String publicId, FeedbackRequest req, User user, Long guestId) {
        getByPublicId(publicId);
        //todo: feedback 저장
    }

    //mark.. 사용안할수도?
    @Transactional
    public void markStarted(String publicId) {
        TransformJob j = getByPublicId(publicId);
        j.markStarted(Instant.now());
        jobRepo.save(j);
    }
    @Transactional
    public void markProgress(String publicId) {
        TransformJob j = getByPublicId(publicId);
        j.markProgress();
        jobRepo.save(j);
    }
    @Transactional
    public void markCompleted(String publicId, Asset perturbed, Asset df, Asset vis) {
        TransformJob j = getByPublicId(publicId);
        j.markCompleted(Instant.now(), perturbed, df, vis);
        jobRepo.save(j);
    }
    @Transactional

    public void markFailed(String publicId, String reason) {
        TransformJob j = getByPublicId(publicId);
        j.markFailed(reason, Instant.now());
        jobRepo.save(j);
    }




    //private
    private GuestSession resolveGuest(Long guestId) {
        if (guestId == null) return null;
        return guestRepo.findById(guestId).orElse(null);
    }

    private Optional<TransformJob> findExistingJob(User user, Asset input, String paramKey) {
        if (user == null){
            return Optional.empty();
        }
        return jobRepo.findByUserAndInputAssetAndParamKey(user, input, paramKey);
    }

    private Optional<TransformJob> findByIdempotency(User user, Long guestId, String idemKey) {
        if ((idemKey == null || idemKey.isBlank())){
            return Optional.empty();
        }
        if (user != null) {
            return jobRepo.findByUserAndIdempotencyKey(user, idemKey);
        }
        if (guestId != null) {
            return jobRepo.findByGuest_IdAndIdempotencyKey(guestId, idemKey);
        }
        return Optional.empty();
    }
}
