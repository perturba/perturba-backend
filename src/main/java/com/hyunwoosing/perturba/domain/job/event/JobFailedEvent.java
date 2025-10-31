package com.hyunwoosing.perturba.domain.job.event;

public record JobFailedEvent(String publicId, String reason) {}
