package com.hyunwoosing.perturba.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Perturba API",
                version = "v1",
                description = "Perturba 이미지 변환 서비스 REST API"
        )
)
//Access JWT
@SecurityScheme(
        name = "access-jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
//외부 API Key 헤더
@SecurityScheme(
        name = "external-api-key",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-Perturba-External-API-Key"
)
//게스트 쿠키
@SecurityScheme(
        name = "guest-cookie",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = "perturba_guest"
)
//리프레시 쿠키
@SecurityScheme(
        name = "refresh-cookie",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = "perturba_refresh"
)
public class OpenApiConfig {
}
