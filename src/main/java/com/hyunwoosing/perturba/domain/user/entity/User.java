package com.hyunwoosing.perturba.domain.user.entity;

import com.hyunwoosing.perturba.common.entity.BaseEntity;
import com.hyunwoosing.perturba.domain.user.entity.enums.AuthProvider;
import com.hyunwoosing.perturba.domain.user.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "name", length = 300)
    private String name;

    @Column(name = "avatar_url", length = 300)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", length = 20, nullable = false)
    private AuthProvider authProvider;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false)
    private UserRole role;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;


    // business logic methods
    public void markLogin(Instant when) {
        this.lastLoginAt = when;
    }

    public void changeProfile(String name, String avatarUrl) {
        if (name != null) this.name = name;
        if (avatarUrl != null) this.avatarUrl = avatarUrl;
    }

    public void activate() { this.isActive = true;  }
    public void deactivate() { this.isActive = false; }


    //  equals, hashCode: id 기반
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User other = (User) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
