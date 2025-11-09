package com.hyunwoosing.perturba.domain.apikey.repository;

import com.hyunwoosing.perturba.domain.apikey.entity.ApiKey;
import com.hyunwoosing.perturba.domain.apikey.entity.enums.ApiKeyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    List<ApiKey> findByOwner_Id(Long ownerId);
    List<ApiKey> findByOwner_IdAndStatus(Long ownerId, ApiKeyStatus status);

    default List<ApiKey> findActiveByOwnerId(Long ownerId) {
        return findByOwner_IdAndStatus(ownerId, ApiKeyStatus.ACTIVE);
    }

    void deleteByOwner_Id(Long ownerId);
    Optional<ApiKey> findFirstByOwner_IdAndStatus(Long ownerId, ApiKeyStatus status);
    Optional<ApiKey> findByKeyHashHexAndStatus(String keyHashHex, ApiKeyStatus status);

}
