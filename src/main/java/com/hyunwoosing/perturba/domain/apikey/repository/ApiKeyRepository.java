package com.hyunwoosing.perturba.domain.apikey.repository;

import com.hyunwoosing.perturba.domain.apikey.entity.ApiKey;
import com.hyunwoosing.perturba.domain.apikey.entity.enums.ApiKeyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByKeyHashHexAndStatus(String keyHashHex, ApiKeyStatus status);
    Optional<ApiKey> findFirstByOwner_IdAndStatus(Long ownerId, ApiKeyStatus status);
    List<ApiKey> findByOwner_Id(Long ownerId);
    void deleteByOwner_Id(Long ownerId);
}
