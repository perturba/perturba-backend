package com.hyunwoosing.perturba.common.config;

import com.hyunwoosing.perturba.common.config.props.S3Props;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@EnableConfigurationProperties(S3Props.class)
public class S3Config {

    @Bean
    public S3Presigner s3Presigner(S3Props s3Props) {
        return S3Presigner.builder()
                .region(Region.of(s3Props.region()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}