package com.hyunwoosing.perturba.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "datetime(3)")
    protected Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "datetime(3)")
    protected Instant updatedAt;
}
