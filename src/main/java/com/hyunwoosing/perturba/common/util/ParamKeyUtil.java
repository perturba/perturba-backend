package com.hyunwoosing.perturba.common.util;

import com.hyunwoosing.perturba.domain.job.entity.enums.Intensity;
import org.apache.commons.codec.digest.DigestUtils;

public final class ParamKeyUtil {
    private ParamKeyUtil() {}
    public static String of(Intensity intensity) {
        String s = "intensity=" + intensity.name();
        return DigestUtils.sha256Hex(s);
    }
}