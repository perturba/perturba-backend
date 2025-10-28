package com.hyunwoosing.perturba.domain.job.entity;

import com.hyunwoosing.perturba.common.entity.BaseEntity;
import com.hyunwoosing.perturba.domain.guest.entity.GuestSession;
import com.hyunwoosing.perturba.domain.job.entity.enums.DistortionEval;
import com.hyunwoosing.perturba.domain.job.entity.enums.StrengthEval;
import com.hyunwoosing.perturba.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "job_feedbacks")
public class JobFeedback extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private TransformJob job;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "guest_id")
    private GuestSession guest;

    @Enumerated(EnumType.STRING)
    @Column(name = "strength_eval", nullable = false)
    private StrengthEval strengthEval;

    @Enumerated(EnumType.STRING)
    @Column(name = "distortion_eval", nullable = false)
    private DistortionEval distortionEval;


    //equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JobFeedback other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

