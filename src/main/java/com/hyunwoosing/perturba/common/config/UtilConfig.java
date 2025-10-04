package com.hyunwoosing.perturba.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunwoosing.perturba.common.config.props.PkceProps;
import com.hyunwoosing.perturba.common.security.SignedPayload;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilConfig {

    @Bean
    public SignedPayload signedPayload(PkceProps pkceProps, ObjectMapper objectMapper) {
        return new SignedPayload(pkceProps, objectMapper);
    }
}