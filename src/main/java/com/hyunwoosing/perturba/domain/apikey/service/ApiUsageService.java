package com.hyunwoosing.perturba.domain.apikey.service;

public interface ApiUsageService {
    //분당 LateLimit 검사
    boolean tryConsumePerMinute(Long apiKeyId, int limitPerMin);

    //일일 사용량 검사
    boolean tryConsumeDaily(Long apiKeyId, int dailyQuota);
}