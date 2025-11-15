package com.hyunwoosing.perturba.domain.external.service;

import com.hyunwoosing.perturba.common.storage.S3PresignService;
import com.hyunwoosing.perturba.common.util.ParamKeyUtil;
import com.hyunwoosing.perturba.domain.apikey.entity.ApiKey;
import com.hyunwoosing.perturba.domain.apikey.repository.ApiKeyRepository;
import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.exception.AssetErrorCode;
import com.hyunwoosing.perturba.domain.asset.exception.AssetException;
import com.hyunwoosing.perturba.domain.asset.repository.AssetRepository;
import com.hyunwoosing.perturba.domain.external.mapper.ExternalJobMapper;
import com.hyunwoosing.perturba.domain.external.web.dto.request.ExternalTransformRequest;
import com.hyunwoosing.perturba.domain.external.web.dto.response.ExternalJobResultResponse;
import com.hyunwoosing.perturba.domain.external.web.dto.response.ExternalTransformResponse;
import com.hyunwoosing.perturba.domain.job.entity.TransformJob;
import com.hyunwoosing.perturba.domain.job.entity.enums.ClientChannel;
import com.hyunwoosing.perturba.domain.job.entity.enums.JobStatus;
import com.hyunwoosing.perturba.domain.job.entity.enums.NotifyVia;
import com.hyunwoosing.perturba.domain.job.entity.enums.RequestMode;
import com.hyunwoosing.perturba.domain.job.error.JobErrorCode;
import com.hyunwoosing.perturba.domain.job.error.JobException;
import com.hyunwoosing.perturba.domain.job.repository.JobRepository;
import com.hyunwoosing.perturba.domain.user.entity.User;
import com.hyunwoosing.perturba.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ExternalTransformService {
    private final AssetRepository assetRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final S3PresignService s3PresignService;


    @Transactional
    public ExternalTransformResponse createTransform(ExternalTransformRequest req, Long ownerUserId, Long apiKeyId) {
        if (ownerUserId == null || apiKeyId == null) {
            throw new JobException(JobErrorCode.UNAUTHORIZED, "API Key 인증이 필요합니다.");
        }
        User owner = userRepository.findById(ownerUserId).orElseThrow(() -> new JobException(JobErrorCode.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
        ApiKey apiKey = apiKeyRepository.findById(apiKeyId).orElseThrow(() -> new JobException(JobErrorCode.UNAUTHORIZED, "API Key를 찾을 수 없습니다."));

        Asset input = assetRepository.findByPublicIdAndOwner_Id(req.inputAssetPublicId(), ownerUserId)
                .orElseThrow(() -> new AssetException(AssetErrorCode.NOT_YOUR_ASSET, "입력 이미지가 존재하지 않거나 이 API Key의 소유자가 아닙니다."));

        // TransformJob 생성
        String paramKey = ParamKeyUtil.of(req.intensity());

        TransformJob job = TransformJob.builder()
                .clientChannel(ClientChannel.API)
                .requestMode(RequestMode.ASYNC)
                .user(owner)
                .apiKey(apiKey)
                .inputAsset(input)
                .intensity(req.intensity())
                .status(JobStatus.PROGRESS)
                .notifyVia(NotifyVia.NONE)
                .paramKey(paramKey)
                .build();

        job.useApiKey(apiKey);
        job.markProgress(Instant.now());

        //TODO: AI 서버 호출
        // input.getObjectKey(), job.getId(), job.getPublicId() 넘겨줌
        // AI 서버는 작업 끝나면 /v1/internal/jobs/... API 호출해서 완료/실패 상태 업데이트

        TransformJob saved = jobRepository.save(job);
        return ExternalJobMapper.toTransformResponse(saved);
    }


    @Transactional
    public ExternalJobResultResponse getResult(String jobPublicId, Long apiKeyId) {
        if (apiKeyId == null) {
            throw new JobException(JobErrorCode.UNAUTHORIZED, "API Key 인증이 필요합니다.");
        }
        TransformJob job = jobRepository.findByPublicId(jobPublicId).orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND, "해당 작업을 찾을 수 없습니다."));

        if (job.getApiKey() == null || !Objects.equals(job.getApiKey().getId(), apiKeyId)) {
            throw new JobException(JobErrorCode.UNAUTHORIZED, "해당 작업에 접근할 수 없습니다.");
        }

        //상태가 PROGRESS여도 그대로 status=PROGRESS로 리턴
        //COMPLETED인 경우 presign URL 포함
        return ExternalJobMapper.toResultResponse(job, s3PresignService);
    }
}
