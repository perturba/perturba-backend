package com.hyunwoosing.perturba.domain.job.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobEventService {

    private static final long SSE_TIMEOUT_MS = Duration.ofMinutes(30).toMillis();
    private final ObjectMapper om = new ObjectMapper();
    private final ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> registry = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String jobPublicId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        registry.computeIfAbsent(jobPublicId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(jobPublicId, emitter));
        emitter.onTimeout(() -> remove(jobPublicId, emitter));
        emitter.onError(e -> remove(jobPublicId, emitter));

        try {
            emitter.send(SseEmitter.event().comment("connected"));
            emitter.send("retry: 3000\n\n");
        } catch (IOException ignored) {}

        return emitter;
    }

    public void emitProgress(String jobPublicId) {
        Map<String, Object> payload = Map.of("status", "PROGRESS");
        broadcast(jobPublicId, "progress", payload, false);
    }

    public void emitCompleted(String jobPublicId, Map<String, Object> resultPayload) {
        var payload = new ConcurrentHashMap<>(resultPayload);
        payload.put("status", "COMPLETED");
        broadcast(jobPublicId, "completed", payload, true);
    }

    public void emitFailed(String jobPublicId, String reason) {
        Map<String, Object> payload = Map.of("status", "FAILED", "reason", reason);
        broadcast(jobPublicId, "failed", payload, true);
    }



    //private
    private void broadcast(String publicId, String event, Object data, boolean terminal) {
        List<SseEmitter> emitters = registry.get(publicId);
        if (emitters == null || emitters.isEmpty())
            return;

        for (SseEmitter em : emitters) {
            try {
                em.send(SseEmitter.event()
                        .id(UUID.randomUUID().toString())
                        .name(event)
                        .data(om.writeValueAsString(data)));
                if (terminal) em.complete();
            } catch (IOException e) {
                remove(publicId, em);
            }
        }
        if (terminal) registry.remove(publicId);
    }

    private void remove(String publicId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> list = registry.get(publicId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) registry.remove(publicId);
        }
    }
}
