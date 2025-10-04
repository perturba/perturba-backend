package com.hyunwoosing.perturba.common.util;

import jakarta.servlet.http.HttpServletResponse;

public final class CookieUtil {
    //쿠기 추가, 만료 편하게 하기 위한 util 클래스 ..

    private CookieUtil() {}

    public static void add(HttpServletResponse res,
                           String name,
                           String value,
                           int maxAgeSeconds,
                           String path,
                           String domain,
                           boolean secure,
                           boolean httpOnly,
                           String sameSite) {
        StringBuilder sb = new StringBuilder();

        sb.append(name).append("=").append(value)
                .append("; Max-Age=").append(maxAgeSeconds)
                .append("; Path=").append(path != null ? path : "/");

        if (domain != null && !domain.isBlank()) {
            sb.append("; Domain=").append(domain);
        }

        if (secure) {
            sb.append("; Secure");
        }

        if (httpOnly) {
            sb.append("; HttpOnly");
        }

        if (sameSite != null && !sameSite.isBlank()) {
            sb.append("; SameSite=").append(sameSite);
        }

        res.addHeader("Set-Cookie", sb.toString());
    }


    public static void expire(HttpServletResponse res,
                              String name,
                              String path,
                              String domain) {
        add(res, name, "", 0, path, domain, true, true, "Lax");
    }
}
