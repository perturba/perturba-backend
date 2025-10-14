package com.hyunwoosing.perturba.domain.job.entity;

import com.hyunwoosing.perturba.common.entity.BaseEntity;
import com.hyunwoosing.perturba.domain.apikey.entity.ApiKey;
import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.guest.entity.GuestSession;
import com.hyunwoosing.perturba.domain.job.entity.enums.*;
import com.hyunwoosing.perturba.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "transform_jobs")
public class TransformJob extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long id;

    @Column(name = "public_id", length = 26, nullable = false, unique = true)
    private String publicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_channel", nullable = false)
    private ClientChannel clientChannel;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_mode", nullable = false)
    private RequestMode requestMode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id")
    private GuestSession guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_key_id")
    private ApiKey apiKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "input_asset_id", nullable = false)
    private Asset inputAsset;

    @Enumerated(EnumType.STRING)
    @Column(name = "intensity", nullable = false)
    private Intensity intensity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private JobStatus status = JobStatus.QUEUED;

    @Column(name = "fail_reason", length = 500)
    private String failReason;

    @Column(name = "responded_at", columnDefinition = "datetime(3)")
    private Instant respondedAt;

    @Column(name = "response_ms")
    private Integer responseMs;

    @Column(name = "idempotency_key", length = 64)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "notify_via", nullable = false)
    @Builder.Default
    private NotifyVia notifyVia = NotifyVia.NONE;

    @Column(name = "started_at", columnDefinition = "datetime(3)")
    private Instant startedAt;

    @Column(name = "completed_at", columnDefinition = "datetime(3)")
    private Instant completedAt;


    // business logic methods

    // 재대기 처리: 초기화 후 Queued 상태로
    public void resetToQueued() {
        this.status = JobStatus.QUEUED;
        this.failReason = null;
        this.startedAt = null;
        this.respondedAt = null;
        this.responseMs = null;
        this.completedAt = null;
    }

    // 처리 시작 기록
    public void startProcessing(Instant now) {
        this.status = JobStatus.PROCESSING;
        if (this.startedAt == null) this.startedAt = now;
    }

    // 응답 기록 (동기)
    public void recordResponse(Instant now, Integer elapsedMs) {
        this.respondedAt = now;
        this.responseMs = elapsedMs;
    }

    // 처리 완료 기록
    public void completeSuccessfully(Instant now) {
        if (this.startedAt == null) this.startedAt = now;
        this.status = JobStatus.DONE;
        this.completedAt = now;
        if (this.requestMode == RequestMode.SYNC && this.respondedAt == null) {
            this.respondedAt = now;
        }
    }
    public void completeWithFailure(String reason, Instant now) {
        this.status = JobStatus.FAILED;
        this.failReason = (reason == null || reason.isBlank()) ? "UNKNOWN" : reason;
        this.completedAt = now;
        if (this.requestMode == RequestMode.SYNC && this.respondedAt == null) {
            this.respondedAt = now;
        }
    }

    // 알림 방식 설정
    public void setNotify(NotifyVia via) {
        this.notifyVia = (via == null) ? NotifyVia.NONE : via;
    }

    //API Key 설정 (API 요청인 경우)
    public void useApiKey(ApiKey key) {
        this.apiKey = key;
        if (key != null) this.clientChannel = ClientChannel.API;
    }

    // 사용자 연동
    public void assignUser(User user) {
        this.user = user;
    }
    public void assignGuest(GuestSession guest) {
        this.guest = guest;
    }

    // 속성 변경
    public void updateIntensity(Intensity intensity) {
        this.intensity = intensity;
    }

    // 편의 함수
    public boolean isTerminal() {
        return this.status == JobStatus.DONE || this.status == JobStatus.FAILED;
    }
    public boolean isSSE() {
        return this.notifyVia == NotifyVia.SSE;
    }


    // equals, hashCode: id 기반
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransformJob other = (TransformJob) o;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
