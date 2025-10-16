package com.hyunwoosing.perturba.common.storage;


import com.hyunwoosing.perturba.common.config.props.S3Props;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URI;
import java.time.Duration;

@RequiredArgsConstructor
@Service
public class S3PresignService {
    private final S3Props s3;

    public PresignedPutObjectRequest presignPut(String objectKey, String mimeType){
        try(S3Presigner presigner = S3Presigner.builder().credentialsProvider(DefaultCredentialsProvider.create()).region(Region.of(s3.region())).build()) {
            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(s3.bucket())
                    .key(objectKey)
                    .contentType(mimeType)
                    .build();

            return presigner.presignPutObject(b -> b
                    .putObjectRequest(putReq)
                    .signatureDuration(Duration.ofSeconds(s3.presignExpireSec())));
        }
    }

    public String publicUrl(String objectKey) {
        if (s3.publicBaseUrl() != null && !s3.publicBaseUrl().isBlank()) {
            return (s3.publicBaseUrl().endsWith("/") ? s3.publicBaseUrl() : s3.publicBaseUrl() + "/") + objectKey;
        }
        return URI.create("https://" + s3.bucket() + ".s3." + s3.region() + ".amazonaws.com/" + objectKey).toString();
    }
}
