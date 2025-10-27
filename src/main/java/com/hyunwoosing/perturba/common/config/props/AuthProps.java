package com.hyunwoosing.perturba.common.config.props;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "perturba.auth")
public record AuthProps(
        Jwt jwt,
        Refresh refresh,
        Guest guest
) {
    public record Jwt(String issuer, String hmacSecret, long accessTtlSec) {
    }

    public record Refresh(String cookieName, String cookieDomain, String cookiePath, int ttlDays) {
    }

    public record Guest(String cookieName, String cookieDomain, String cookiePath, int ttlDays) {
    }
}