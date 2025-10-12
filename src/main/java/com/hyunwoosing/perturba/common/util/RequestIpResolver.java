package com.hyunwoosing.perturba.common.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RequestIpResolver {
    public String resolve(HttpServletRequest req){
        //IP 추적
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "CF-Connecting-IP"};

        for(String header : headers){
            String v = req.getHeader(header);

            if (v != null && !v.isBlank()) {
                int comma = v.indexOf(',');
                return comma > 0 ? v.substring(0, comma).trim() : v.trim();
            }
        }

        return req.getRemoteAddr();
    }
}
