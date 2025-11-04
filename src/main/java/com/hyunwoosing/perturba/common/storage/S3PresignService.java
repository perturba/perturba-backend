package com.hyunwoosing.perturba.common.storage;


import com.hyunwoosing.perturba.common.config.props.S3Props;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URI;
import java.time.Duration;

@RequiredArgsConstructor
@Service
public class S3PresignService {
    private final S3Props s3;
    private final S3Presigner presigner;

    //Put Presign
    public PresignedPutObjectRequest presignPut(String objectKey, String mimeType){
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(s3.bucket())
                .key(objectKey)
                .contentType(mimeType)
                .build();

        return presigner.presignPutObject(b -> b
                .putObjectRequest(putReq)
                .signatureDuration(Duration.ofSeconds(s3.presignExpireSec()))
        );
    }

    public String presignGet(String objectKey) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(s3.bucket())
                .key(objectKey)
                .build();

        PresignedGetObjectRequest presigned = presigner.presignGetObject(b -> b
                .getObjectRequest(getReq)
                .signatureDuration(Duration.ofSeconds(s3.presignExpireSec()))
        );

        return presigned.url().toString();
    }
}
