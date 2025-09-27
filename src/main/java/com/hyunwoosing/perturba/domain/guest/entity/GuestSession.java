package com.hyunwoosing.perturba.domain.guest.entity;

import com.hyunwoosing.perturba.common.entity.BaseEntity;
import com.hyunwoosing.perturba.common.util.BytesToHexConverter;
import com.hyunwoosing.perturba.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class GuestSession extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_token", length = 36, nullable = false, unique = true)
    private String publicToken;

    @Convert(converter = BytesToHexConverter.class)
    @Column(name = "token_hash", columnDefinition = "binary(32)", nullable = false)
    private String tokenHashHex;

    @Column(name = "expires_at", columnDefinition = "timestamp", nullable = false)
    private Instant expiresAt;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="linked_user_id")
    private User linkedUser;


    // business logic methods
    public boolean isExpired(Instant now) { return now != null && now.isAfter(expiresAt); }


    //equals, hashCode: id 기반
    @Override public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass()!=o.getClass()) return false;
        GuestSession other = (GuestSession) o;
        return id != null && id.equals(other.id);
    }
    @Override public int hashCode(){ return getClass().hashCode(); }
}
