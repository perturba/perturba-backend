package com.hyunwoosing.perturba.domain.job.repository;

import com.hyunwoosing.perturba.domain.job.entity.JobFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobFeedbackRepository extends JpaRepository<JobFeedback, Long> {
}
