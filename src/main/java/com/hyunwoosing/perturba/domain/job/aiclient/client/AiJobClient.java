package com.hyunwoosing.perturba.domain.job.aiclient.client;

import com.hyunwoosing.perturba.common.storage.S3PresignService;
import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.job.aiclient.dto.AiAcceptedResponse;
import com.hyunwoosing.perturba.domain.job.aiclient.dto.AiFacePerturbRequest;
import com.hyunwoosing.perturba.domain.job.entity.TransformJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiJobClient {

    private final RestTemplate restTemplate;
    private final S3PresignService s3PresignService;

    @Value("${perturba.ai.base-url}")
    private String aiBaseUrl; // 예: https://ai.woojangpark.site

    public void sendFacePerturbJob(TransformJob job) {
        Asset input = job.getInputAsset();
        if (input == null) {
            log.warn("Job {} has no input asset, skip AI request", job.getId());
            return;
        }

        // presigned GET URL 발급
        String inputUrl = s3PresignService.presignGet(input.getObjectKey());

        String intensity = (job.getIntensity() != null)
                ? job.getIntensity().name()
                : "MEDIUM";

        AiFacePerturbRequest payload = new AiFacePerturbRequest(
                job.getId(),
                job.getPublicId(),
                inputUrl,
                intensity,   // "LOW" / "MEDIUM" / "HIGH"
                ""           // prompt는 일단 빈 문자열
        );

        String url = aiBaseUrl + "/v1/internal/ai/face-perturb";

        try {
            log.info("Calling AI server, jobId={}, url={}", job.getId(), url);
            restTemplate.postForEntity(url, payload, AiAcceptedResponse.class);
        } catch (Exception e) {
            log.error("Failed to call AI server for jobId={}", job.getId(), e);
        }
    }
}