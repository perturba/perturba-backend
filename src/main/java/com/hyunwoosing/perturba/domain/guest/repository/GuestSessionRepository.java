package com.hyunwoosing.perturba.domain.guest.repository;

import com.hyunwoosing.perturba.domain.guest.entity.GuestSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestSessionRepository extends JpaRepository<GuestSession, Long> {
    Optional<GuestSession> findByPublicToken(String publicToken);
}
