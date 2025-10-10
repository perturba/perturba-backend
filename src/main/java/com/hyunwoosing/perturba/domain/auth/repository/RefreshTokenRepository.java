package com.hyunwoosing.perturba.domain.auth.repository;

import com.hyunwoosing.perturba.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHashHexAndRevokedAtIsNull(String tokenHashHex); // revokedAt이 Null인 것 중 tokenHashHex가 일치하는 것 (만료 안된 것)
}