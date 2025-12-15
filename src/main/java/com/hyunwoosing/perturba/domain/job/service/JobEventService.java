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
    private static final long KEEPALIVE_MS = Duration.ofSeconds(15).toMillis();

    private final ObjectMapper om;

    private final ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> registry = new ConcurrentHashMap<>();
    private final ScheduledExecutorService keepAlivePool = Executors.newSingleThreadScheduledExecutor();

    public SseEmitter subscribe(String jobPublicId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        registry.computeIfAbsent(jobPublicId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(jobPublicId, emitter));
        emitter.onTimeout(() -> remove(jobPublicId, emitter));
        emitter.onError(e -> remove(jobPublicId, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("init")
                    .id(UUID.randomUUID().toString())
                    .reconnectTime(3000L)
                    .data(om.writeValueAsString(Map.of("status", "CONNECTED")))
            );
        } catch (IOException e) {
            remove(jobPublicId, emitter);
            return emitter;
        }

        keepAlivePool.scheduleAtFixedRate(() -> {
            if (!isRegistered(jobPublicId, emitter)) return;
            try {
                emitter.send(SseEmitter.event().comment("keepalive"));
            } catch (IOException ex) {
                remove(jobPublicId, emitter);
            }
        }, KEEPALIVE_MS, KEEPALIVE_MS, TimeUnit.MILLISECONDS);

        return emitter;
    }

    public void emitProgress(String jobPublicId) {
        broadcast(jobPublicId, "progress", Map.of("status", "PROGRESS"), false);
    }

    public void emitCompleted(String jobPublicId, Map<String, Object> resultPayload) {
        var payload = new ConcurrentHashMap<>(resultPayload);
        payload.put("status", "COMPLETED");
        broadcast(jobPublicId, "completed", payload, true);
    }

    public void emitFailed(String jobPublicId, String reason) {
        broadcast(jobPublicId, "failed", Map.of("status", "FAILED", "reason", reason), true);
    }

    private void broadcast(String publicId, String event, Object data, boolean terminal) {
        List<SseEmitter> emitters = registry.get(publicId);
        if (emitters == null || emitters.isEmpty()) return;

        for (SseEmitter em : emitters) {
            try {
                em.send(SseEmitter.event()
                        .id(UUID.randomUUID().toString())
                        .name(event)
                        .data(om.writeValueAsString(data))
                );
                if (terminal) em.complete();
            } catch (IOException e) {
                remove(publicId, em);
            }
        }
        if (terminal) registry.remove(publicId);
    }

    private boolean isRegistered(String publicId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> list = registry.get(publicId);
        return list != null && list.contains(emitter);
    }

    private void remove(String publicId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> list = registry.get(publicId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) registry.remove(publicId);
        }
    }
}
