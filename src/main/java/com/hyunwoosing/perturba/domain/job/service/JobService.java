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
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public TransformJob create(CreateJobRequest req, User user, Long guestId) {
        Asset input = assetRepo.findById(req.inputAssetId()).orElseThrow(() ->
                new JobException(JobErrorCode.INPUT_ASSET_NOT_FOUND, "입력된 Asset 을 찾을 수 없습니다."));

        if (user != null && input.getOwner() != null && !Objects.equals(input.getOwner().getId(), user.getId())) {
            throw new JobException(JobErrorCode.ASSET_NOT_OWNED_BY_USER, "현재 유저의 소유가 아닌 Asset 입니다.");
        }

        //중복 확인
        String paramKey = ParamKeyUtil.of(req.intensity());
        if (user != null) {
            Optional<TransformJob> existing = jobRepo.findByUserAndInputAssetAndParamKey(user, input, paramKey);
            if (existing.isPresent()){
                return existing.get();
            }
        }

        TransformJob job = TransformJob.builder()
                .clientChannel(req.clientChannel())
                .requestMode(req.requestMode())
                .user(user)
                .guest(resolveGuest(guestId))
                .inputAsset(input)
                .intensity(req.intensity())
                .notifyVia(req.notifyVia())
                .status(JobStatus.QUEUED)
                .build();

        //TODO: 외부 AI 서버 호출(Flask/Django)

        return jobRepo.save(job);
    }

    private GuestSession resolveGuest(Long guestId) {
        if (guestId == null) return null;
        return guestRepo.findById(guestId).orElse(null);
    }

    @Transactional(readOnly = true)
    public TransformJob getByPublicId(String publicId) {
        return jobRepo.findByPublicId(publicId).orElseThrow(() ->
                new JobException(JobErrorCode.JOB_NOT_FOUND, "해당 작업을 찾을 수 없습니다."));
    }


    @Transactional
    public void saveFeedback(String publicId, FeedbackRequest req, User user, Long guestId) {
        getByPublicId(publicId);
        //TODO: Feedback 엔티티 설계 후 저장

    }




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
}
