package com.hyunwoosing.perturba.domain.apikey.service;

public interface ApiUsageService { //추후 Redis등 외부 저장소 연동 가능성때문에 인터페이스로 분리
    //분당 LateLimit 검사
    boolean tryConsumePerMinute(Long apiKeyId, int limitPerMin);

    //일일 사용량 검사
    boolean tryConsumeDaily(Long apiKeyId, int dailyQuota);
}