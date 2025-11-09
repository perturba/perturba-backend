package com.hyunwoosing.perturba.domain.apikey.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ApiUsageServiceImpl implements ApiUsageService {

    private final Map<String, AtomicInteger> perMinute = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> perDay = new ConcurrentHashMap<>();
    private static final DateTimeFormatter MIN_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);

    //인메모리 API 사용량 카운팅
    @Override
    public boolean tryConsumePerMinute(Long apiKeyId, int limitPerMin) {
        if (limitPerMin == 0)
            return true;
        String key = apiKeyId + ":" + MIN_FMT.format(Instant.now());
        AtomicInteger c = perMinute.computeIfAbsent(key, k -> new AtomicInteger(0));
        int v = c.incrementAndGet();

        return v <= limitPerMin;
    }

    @Override
    public boolean tryConsumeDaily(Long apiKeyId, int dailyQuota) {
        if (dailyQuota == 0)
            return true;
        String key = apiKeyId + ":" + DAY_FMT.format(Instant.now());
        AtomicInteger c = perDay.computeIfAbsent(key, k -> new AtomicInteger(0));
        int v = c.incrementAndGet();

        return v <= dailyQuota;
    }
}