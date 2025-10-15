package com.hyunwoosing.perturba.common.config.props;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.s3")
public record S3Props(
        String bucket,
        String region,
        Integer presignExpireSec,
        String publicBaseUrl
) {}