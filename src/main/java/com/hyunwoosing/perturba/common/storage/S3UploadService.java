package com.hyunwoosing.perturba.common.storage;

import com.hyunwoosing.perturba.common.config.props.S3Props;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Props s3Props;
    private final S3Client s3Client;

    public void upload(String objectKey, MultipartFile file) {
        try {
            Map<String, String> metadata = new HashMap<>();
            if (file.getOriginalFilename() != null) {
                metadata.put("original-filename", file.getOriginalFilename());
            }

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(s3Props.bucket())
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .metadata(metadata)
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            log.error("Failed to read multipart file for S3 upload, key={}", objectKey, e);
            throw new RuntimeException("Failed to read file for S3 upload", e);
        } catch (S3Exception e) {
            log.error("Failed to upload to S3, key={}", objectKey, e);
            throw new RuntimeException("Failed to upload to S3", e);
        }
    }


    public void upload(String objectKey, byte[] bytes, String contentType) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(s3Props.bucket())
                    .key(objectKey)
                    .contentType(contentType)
                    .contentLength((long) bytes.length)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(bytes));
        } catch (S3Exception e) {
            log.error("Failed to upload to S3, key={}", objectKey, e);
            throw new RuntimeException("Failed to upload to S3", e);
        }
    }
}
