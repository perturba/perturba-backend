package com.hyunwoosing.perturba.domain.job.repository;

import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.job.entity.TransformJob;
import com.hyunwoosing.perturba.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<TransformJob, Long> {
    Optional<TransformJob> findByPublicId(String publicId);
    Optional<TransformJob> findByUserAndInputAssetAndParamKey(User user, Asset input, String paramKey);

    Optional<TransformJob> findByUserAndIdempotencyKey(User user, String idempotencyKey);
    Optional<TransformJob> findByGuest_IdAndIdempotencyKey(Long guestId, String idempotencyKey);

    Page<TransformJob> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<TransformJob> findByGuest_IdOrderByCreatedAtDesc(Long guestId, Pageable pageable);


    Optional<TransformJob> findByPublicIdAndApiKey_Id(String publicId, Long apiKeyId); //외부 API 호출용
}
