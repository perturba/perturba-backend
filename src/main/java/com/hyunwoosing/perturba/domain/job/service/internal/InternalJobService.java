package com.hyunwoosing.perturba.domain.job.service.internal;

import com.hyunwoosing.perturba.common.storage.S3PresignService;
import com.hyunwoosing.perturba.common.util.S3ObjectKeyUtil;
import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.entity.enums.AssetKind;
import com.hyunwoosing.perturba.domain.asset.entity.enums.AssetStatus;
import com.hyunwoosing.perturba.domain.asset.repository.AssetRepository;
import com.hyunwoosing.perturba.domain.job.entity.TransformJob;
import com.hyunwoosing.perturba.domain.job.error.JobErrorCode;
import com.hyunwoosing.perturba.domain.job.error.JobException;
import com.hyunwoosing.perturba.domain.job.repository.JobRepository;
import com.hyunwoosing.perturba.domain.job.web.dto.response.internal.ResultUploadUrlResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@RequiredArgsConstructor
public class InternalJobService {
    private final JobRepository jobRepository;
    private final AssetRepository assetRepository;
    private final S3PresignService s3PresignService;

    @Transactional
    public ResultUploadUrlResponse issueResultUploadUrls(Long jobId){
        TransformJob job = jobRepository.findById(jobId).orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND, "해당 작업을 찾을 수 없습니다."));
        String publicId = job.getPublicId();
        User owner = job.getUser();
        String ownerSlug = (owner == null) ? "guest" : owner.getId().toString();

        //ObjectKey 3종
        String perturbedObjectKey = S3ObjectKeyUtil.jobResultJpegKey(ownerSlug, publicId, "perturbed");
        String deepfakeObjectKey = S3ObjectKeyUtil.jobResultJpegKey(ownerSlug, publicId, "deepfake");
        String visObjectKey = S3ObjectKeyUtil.jobResultJpegKey(ownerSlug, publicId, "vis");

        //Asset 세개 생성
        createIfAbsent(job, owner, AssetKind.PERTURBED, perturbedObjectKey);
        createIfAbsent(job, owner, AssetKind.DEEPFAKE_OUTPUT, deepfakeObjectKey);
        createIfAbsent(job, owner, AssetKind.PERTURBATION_VIS, visObjectKey);

        //Presigned URL 3종 발급
        ResultUploadUrlResponse.Item perturbedPresignUrl = presignPut(perturbedObjectKey);
        ResultUploadUrlResponse.Item deepfakePresignUrl = presignPut(deepfakeObjectKey);
        ResultUploadUrlResponse.Item visPresignUrl = presignPut(visObjectKey);

        return ResultUploadUrlResponse.builder()
                .perturbed(perturbedPresignUrl)
                .deepfake(deepfakePresignUrl)
                .perturbationVis(visPresignUrl)
                .build();
    }







    //private
    private void createIfAbsent(TransformJob job, User owner, AssetKind kind, String key) {
        assetRepository.findByObjectKey(key).orElseGet(() -> {
            String ownerPart = (owner == null) ? "guest" : String.valueOf(owner.getId());
            String source = ownerPart + "|" + job.getId() + "|" + kind.name() + "|" + key;

            // 실제 이미지 sha는 아니지만, owner+job+kind+key 기준으로 유일한 64자리 hex 생성
            String fakeSha256 = DigestUtils.sha256Hex(source);

            return assetRepository.save(Asset.builder()
                        .job(job)
                        .owner(owner)              // null이면 guest 경로
                        .kind(kind)
                        .objectKey(key)
                        .mimeType("image/jpeg")
                        .status(AssetStatus.UPLOADING)
                        .sha256Hex(fakeSha256)
                        .build()
            );
        });

    }

    private ResultUploadUrlResponse.Item presignPut(String key) {
        PresignedPutObjectRequest signed = s3PresignService.presignPut(key, "image/jpeg");
        return ResultUploadUrlResponse.Item.builder()
                .method("PUT")
                .uploadUrl(signed.url().toString())
                .headers(signed.signedHeaders())
                .objectKey(key)
                .build();
    }

}
