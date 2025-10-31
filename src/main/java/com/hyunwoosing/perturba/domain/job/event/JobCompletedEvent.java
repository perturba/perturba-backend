package com.hyunwoosing.perturba.domain.job.event;

import java.util.Map;

public record JobCompletedEvent(String publicId, Map<String, Object> payload) {}

