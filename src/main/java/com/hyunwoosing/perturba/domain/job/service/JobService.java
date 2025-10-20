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
import com.hyunwoosing.perturba.domain.job.web.dto.response.JobResultResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
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
        } else if (guestId != null) {
            Optional<TransformJob> existing = jobRepo.findByGuest_IdAndInputAssetAndParamKey(guestId, input, paramKey);
            if (existing.isPresent()){
                return existing.get();
            }
        }

        TransformJob job = TransformJob.builder() //Todo: Mapper 생성 및 변경
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
        return jobRepo.findByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException("job not found"));
    }

    @Transactional(readOnly = true)
    public JobResultResponse buildResult(String publicId) {
        TransformJob j = getByPublicId(publicId);
        if (j.getStatus() != JobStatus.COMPLETED) {
            // 미완료면 404 등으로 매핑
            throw new EntityNotFoundException("result not ready");
        }
        return new JobResultResponse(
                sec(j.getInputAsset()),
                sec(j.getPerturbedAsset()),
                sec(j.getDeepfakeOutputAsset()),
                sec(j.getPerturbationVisAsset())
        );
    }

    private JobResultResponse.Section sec(Asset asset) {
        if (asset == null) return null;
        return new JobResultResponse.Section(
                asset.getId(),
                asset.getS3Url(),
                asset.getMimeType(),
                asset.getWidth(),
                asset.getHeight()
        );
    }

    @Transactional
    public void saveFeedback(String publicId, FeedbackRequest req, User user, Long guestId) {
        //TODO: Feedback 엔티티 설계 후 저장
        getByPublicId(publicId);
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
