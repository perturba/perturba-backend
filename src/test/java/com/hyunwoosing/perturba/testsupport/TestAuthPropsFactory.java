package com.hyunwoosing.perturba.testsupport;

import com.hyunwoosing.perturba.common.config.props.AuthProps;

public final class TestAuthPropsFactory {

    private TestAuthPropsFactory(){}

    public static AuthProps defaultProps() {
        AuthProps.Jwt jwt = new AuthProps.Jwt(
                "perturba-test-issuer",
                "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
                900
        );
        AuthProps.Refresh refresh = new AuthProps.Refresh(
                "perturba_refresh",
                "localhost",
                "/",
                30
        );
        AuthProps.Guest guest = new AuthProps.Guest(
                "perturba_guest",
                "localhost",
                "/",
                7
        );
        AuthProps.Idempotency idempotency = new AuthProps.Idempotency(
                "Idempotency-Key",
                "perturba_idempotency"
        );
        return new AuthProps(jwt, refresh, guest, idempotency);
    }
}

/*
*     public record Jwt(String issuer, String hmacSecret, long accessTtlSec) {
    }

    public record Refresh(String cookieName, String cookieDomain, String cookiePath, int ttlDays) {
    }

    public record Guest(String cookieName, String cookieDomain, String cookiePath, int ttlDays) {
    }

    public record Idempotency(String headerName, String cookieName) {
    }
* */