package com.hyunwoosing.perturba.domain.guest.entity;

import com.hyunwoosing.perturba.common.entity.BaseEntity;
import com.hyunwoosing.perturba.common.util.BytesToHexConverter;
import com.hyunwoosing.perturba.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "guest_sessions")
@Entity
@Check(constraints = "octet_length(token_hash) = 32")
public class GuestSession extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private Long id;

    @Column(name = "public_token", length = 36, nullable = false, unique = true)
    private String publicToken;

    @Convert(converter = BytesToHexConverter.class)
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHashHex;

    @Column(name = "expires_at", nullable = false)
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
