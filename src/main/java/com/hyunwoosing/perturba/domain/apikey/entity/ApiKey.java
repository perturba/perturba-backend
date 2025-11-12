package com.hyunwoosing.perturba.domain.apikey.entity;

import com.hyunwoosing.perturba.common.entity.BaseEntity;
import com.hyunwoosing.perturba.domain.apikey.entity.enums.ApiKeyStatus;
import com.hyunwoosing.perturba.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "keyHashHex")
@Table(name = "api_keys",
        indexes = {
                @Index(name = "idx_api_keys_keyhash", columnList = "key_hash"),
                @Index(name = "idx_api_keys_owner", columnList = "user_id"),
                @Index(name = "idx_api_keys_keyhash_status", columnList = "key_hash,status")
        })
@Entity
public class ApiKey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "api_key_id")
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @Column(name = "label", length = 100)
    private String label;

    //HEX 문자열(64)로 저장
    @Column(name = "key_hash", length = 64, nullable = false, unique = true)
    private String keyHashHex;

    @Column(name = "scopes", columnDefinition = "jsonb") //PG
    private String scopesJson;

    @Column(name = "rate_per_min")
    private Integer ratePerMin;

    @Column(name = "daily_quota")
    private Integer dailyQuota;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    @Builder.Default
    private ApiKeyStatus status = ApiKeyStatus.ACTIVE;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    //Business methods
    public void markUsed(Instant when) { this.lastUsedAt = when; }
    public void revoke() { this.status = ApiKeyStatus.REVOKED; }
    public boolean isExpired(Instant now) { return expiresAt != null && now != null && now.isAfter(expiresAt); }

    //equals/hashCode
    @Override
    public boolean equals(Object o) { if (this == o) return true; if (!(o instanceof ApiKey other)) return false; return id != null && id.equals(other.id); }
    @Override
    public int hashCode() { return getClass().hashCode(); }
}
