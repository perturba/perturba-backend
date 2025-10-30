package com.hyunwoosing.perturba.common.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@UtilityClass
public class TimeUtil {
    public static OffsetDateTime toKst(Instant i) {
        return (i == null) ? null : OffsetDateTime.ofInstant(i, ZoneId.of("Asia/Seoul"));
    }
}
