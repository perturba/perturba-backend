package com.hyunwoosing.perturba.domain.apikey.repository;

import com.hyunwoosing.perturba.domain.apikey.entity.ApiKey;
import com.hyunwoosing.perturba.domain.apikey.entity.enums.ApiKeyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByKeyHashHexAndStatus(String keyHashHex, ApiKeyStatus status);
    List<ApiKey> findByOwner_IdAndStatus(Long ownerId, ApiKeyStatus status);

    default void revokeAllByOwnerId(Long ownerId) {
        List<ApiKey> keys = findByOwner_IdAndStatus(ownerId, ApiKeyStatus.ACTIVE);
        keys.forEach(ApiKey::revoke);
        saveAll(keys);
    }
}
