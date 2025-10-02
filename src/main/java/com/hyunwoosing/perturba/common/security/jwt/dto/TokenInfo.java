package com.hyunwoosing.perturba.common.security.jwt.dto;

public record TokenInfo(String accessToken, String refreshToken) {

    public static TokenInfo of(String accessToken, String refreshToken) {
        return new TokenInfo(accessToken, refreshToken);
    }

}