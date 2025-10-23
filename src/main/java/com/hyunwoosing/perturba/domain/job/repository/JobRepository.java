package com.hyunwoosing.perturba.domain.job.repository;

import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.job.entity.TransformJob;
import com.hyunwoosing.perturba.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<TransformJob, Long> {
    Optional<TransformJob> findByPublicId(String publicId);
    Optional<TransformJob> findByUserAndInputAssetAndParamKey(User user, Asset input, String paramKey);
}
