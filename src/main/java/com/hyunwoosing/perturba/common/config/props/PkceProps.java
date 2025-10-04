package com.hyunwoosing.perturba.common.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "perturba.pkce")
public record PkceProps(String cookieName, String signSecret) {

}