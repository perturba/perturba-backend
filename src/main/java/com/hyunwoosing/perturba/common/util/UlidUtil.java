package com.hyunwoosing.perturba.common.util;

import com.github.f4b6a3.ulid.UlidCreator;

public final class UlidUtil {
    private UlidUtil() {}
    public static String newUlid() {
        return UlidCreator.getUlid().toString();
    }
}