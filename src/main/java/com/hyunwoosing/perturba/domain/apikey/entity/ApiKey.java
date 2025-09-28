package com.hyunwoosing.perturba.domain.apikey.entity;

import com.hyunwoosing.perturba.common.entity.BaseEntity;
import com.hyunwoosing.perturba.common.util.BytesToHexConverter;
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
@Table(name = "api_keys")
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

    //key_hash binary(32)
    @Convert(converter = BytesToHexConverter.class)
    @Column(name = "key_hash", columnDefinition = "binary(32)", nullable = false)
    private String keyHashHex;

    @Column(name = "scopes", columnDefinition = "json")
    private String scopesJson;

    @Column(name = "rate_per_min")
    private Integer ratePerMin;

    @Column(name = "daily_quota")
    private Integer dailyQuota;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    @Builder.Default
    private ApiKeyStatus status = ApiKeyStatus.ACTIVE;

    @Column(name = "last_used_at", columnDefinition = "timestamp")
    private Instant lastUsedAt;

    @Column(name = "expires_at", columnDefinition = "timestamp")
    private Instant expiresAt;


    // business logic methods
    public void markUsed(Instant when) {
        this.lastUsedAt = when;
    }
    public void revoke() {
        this.status = ApiKeyStatus.REVOKED;
    }
    public boolean isExpired(Instant now) {
        return expiresAt != null && now != null && now.isAfter(expiresAt);
    }

    // equals, hashCode: id 기반
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiKey other = (ApiKey) o;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
