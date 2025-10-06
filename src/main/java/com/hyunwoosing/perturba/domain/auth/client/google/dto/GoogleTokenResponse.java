package com.hyunwoosing.perturba.domain.auth.client.google.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleTokenResponse {
    private String access_token;
    private String id_token;
    private String refresh_token;
    private String token_type;
    private Integer expires_in;
    private String scope;
}
