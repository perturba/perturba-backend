package com.hyunwoosing.perturba.domain.asset.entity;

import com.hyunwoosing.perturba.common.entity.BaseEntity;
import com.hyunwoosing.perturba.domain.asset.entity.enums.AssetKind;
import com.hyunwoosing.perturba.domain.job.entity.TransformJob;
import com.hyunwoosing.perturba.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name = "assets")
public class Asset extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private TransformJob job;

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false)
    private AssetKind kind;

    @Column(name = "object_key", length = 1024, nullable = false)
    private String objectKey;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "sha256_hex", length = 64, nullable = false)
    private String sha256Hex;

    @Column(name = "phash_hex", length = 16)
    private String phashHex;


    // equals, hashCode: id 기반
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset other = (Asset) o;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
