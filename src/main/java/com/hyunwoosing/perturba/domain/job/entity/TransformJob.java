package com.hyunwoosing.perturba.domain.job.entity;

import com.hyunwoosing.perturba.common.entity.BaseEntity;
import com.hyunwoosing.perturba.common.util.UlidUtil;
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
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_jobs_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", foreignKey = @ForeignKey(name = "fk_jobs_guest"))
    private GuestSession guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_key_id", foreignKey = @ForeignKey(name = "fk_jobs_apikey"))
    private ApiKey apiKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "input_asset_id", nullable = false, foreignKey = @ForeignKey(name = "fk_jobs_input"))
    private Asset inputAsset;

    @Enumerated(EnumType.STRING)
    @Column(name = "intensity", nullable = false)
    private Intensity intensity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private JobStatus status = JobStatus.PROGRESS;

    @Column(name = "fail_reason", length = 500)
    private String failReason;

    // 동기 응답 기록
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perturbed_asset_id", foreignKey = @ForeignKey(name = "fk_jobs_perturbed_asset"))
    private Asset perturbedAsset;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "df_output_asset_id", foreignKey = @ForeignKey(name = "fk_jobs_df_output_asset"))
    private Asset deepfakeOutputAsset;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perturbation_vis_asset_id", foreignKey = @ForeignKey(name = "fk_jobs_perturb_vis_asset"))
    private Asset perturbationVisAsset;

    //parameter set hash -> 중복방지용
    @Column(name = "param_key", nullable = false, length = 64)
    private String paramKey;

    //Business Logic Methods

    //작업 재시작
    public void resetToQueued() {
        this.status = JobStatus.PROGRESS;
        this.failReason = null;
        this.startedAt = null;
        this.respondedAt = null;
        this.responseMs = null;
        this.completedAt = null;
        this.perturbedAsset = null;
        this.deepfakeOutputAsset = null;
        this.perturbationVisAsset = null;
    }

    public void markProgress(Instant now) {
        this.status = JobStatus.PROGRESS;
        if (this.startedAt == null) this.startedAt = now;
    }

    //작업 완료
    public void markCompleted(Instant now,
                              Asset perturbed,
                              Asset deepfakeOutput,
                              Asset visiblePerturbation) {
        if (this.startedAt == null) this.startedAt = now;   // 안전장치
        this.status = JobStatus.COMPLETED;
        this.completedAt = now;
        this.perturbedAsset = perturbed;
        this.deepfakeOutputAsset = deepfakeOutput;
        this.perturbationVisAsset = visiblePerturbation;
        if (this.requestMode == RequestMode.SYNC && this.respondedAt == null) {
            this.respondedAt = now;
        }
    }

    //작업 실패
    public void markFailed(String reason, Instant now) {
        this.status = JobStatus.FAILED;
        this.failReason = (reason == null || reason.isBlank()) ? "UNKNOWN" : reason;
        this.completedAt = now;
        if (this.requestMode == RequestMode.SYNC && this.respondedAt == null) {
            this.respondedAt = now;
        }
    }

    //알림 방식 (SSE, NONE) 설정
    public void setNotify(NotifyVia via) {
        this.notifyVia = (via == null) ? NotifyVia.NONE : via;
    }

    //API key 설정 (API 사용시)
    public void useApiKey(ApiKey key) {
        this.apiKey = key;
        if (key != null) this.clientChannel = ClientChannel.API;
    }

    //사용자, 게스트 연동 (혹시 몰라서 넣음)
    public void assignUser(User user) { this.user = user; }
    public void assignGuest(GuestSession guest) { this.guest = guest; }

    //강도변경
    public void updateIntensity(Intensity intensity) { this.intensity = intensity; }

    //편의메서드
    public boolean isTerminal() { return this.status == JobStatus.COMPLETED || this.status == JobStatus.FAILED; }
    public boolean isSSE() { return this.notifyVia == NotifyVia.SSE; }



    //equals, hashCode: id 기반
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransformJob other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() { return getClass().hashCode(); }

    @PrePersist
    void prePersist() {
        if (this.publicId == null || this.publicId.isBlank()) {
            this.publicId = UlidUtil.newUlid();
        }
    }
}
