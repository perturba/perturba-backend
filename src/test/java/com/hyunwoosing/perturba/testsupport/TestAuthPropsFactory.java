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
        return new AuthProps(jwt, refresh);
    }
}