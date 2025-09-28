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
    @Column(name = "image_type", nullable = false)
    private ImageType imageType;

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

}
