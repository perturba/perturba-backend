package com.hyunwoosing.perturba.common.config.props;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "perturba.auth")
public record AuthProps(Google google, Jwt jwt, Refresh refresh) {

    public record Google(String clientId,
                         String clientSecret,
                         String redirectUri,
                         String authBaseUri,
                         String tokenUri  ) {

    }

    public record Jwt(String issuer, String hmacSecret, long accessTtlSec) {

    }

    public record Refresh(String cookieDomain, String cookiePath, int ttlDays) {

    }
}