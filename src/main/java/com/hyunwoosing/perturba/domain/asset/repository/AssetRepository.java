package com.hyunwoosing.perturba.domain.asset.repository;

import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {
}
