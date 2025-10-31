package com.hyunwoosing.perturba.domain.asset.repository;

import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findBySha256HexAndOwner(String sha256Hex, User owner);

    Optional<Asset> findByS3Url(String s3Url);
}
