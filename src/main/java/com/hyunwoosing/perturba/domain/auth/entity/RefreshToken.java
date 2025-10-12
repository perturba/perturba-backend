package com.hyunwoosing.perturba.domain.auth.entity;

import com.hyunwoosing.perturba.common.entity.BaseEntity;
import com.hyunwoosing.perturba.common.util.BytesToHexConverter;
import com.hyunwoosing.perturba.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(exclude = "tokenHashHex")
@Table(name = "refresh_tokens")
@Entity
public class RefreshToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Convert(converter = BytesToHexConverter.class)
    @Column(name = "token_hash", columnDefinition = "binary(32)", nullable = false, unique = true)
    private String tokenHashHex;

    @Column(name = "expires_at", columnDefinition = "timestamp")
    private Instant expiresAt;

    @Column(name = "rotated_from", length = 16)
    private String rotatedFrom;

    @Column(name = "revoked_at", columnDefinition = "timestamp", nullable = true)
    private Instant revokedAt;

    @Column(name = "client_ip", length = 45)
    private String clientIp;


    // business logic methods
    public void revoke(Instant when){ this.revokedAt = when; }
    public boolean isRevoked(){ return revokedAt != null; }
    public boolean isExpired(Instant now){ return expiresAt != null && now.isAfter(expiresAt); }

    //equals, hashCode: id 기반
    @Override public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass()!=o.getClass()) return false;
        RefreshToken other = (RefreshToken) o;
        return id != null && id.equals(other.id);
    }
    @Override public int hashCode(){ return getClass().hashCode(); }
}
