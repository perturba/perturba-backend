package com.hyunwoosing.perturba.domain.job.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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
    private static final long KEEPALIVE_MS   = Duration.ofSeconds(15).toMillis();

    private final ObjectMapper om;

    private final ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> registry = new ConcurrentHashMap<>();

    private final ScheduledExecutorService keepAlivePool =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "sse-keepalive");
                t.setDaemon(true);
                return t;
            });

    @PostConstruct
    void startKeepAliveSweep() {
        keepAlivePool.scheduleAtFixedRate(this::sweepKeepAlive, KEEPALIVE_MS, KEEPALIVE_MS, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    void shutdown() {
        keepAlivePool.shutdownNow();
        registry.clear();
    }

    public SseEmitter subscribe(String jobPublicId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        registry.computeIfAbsent(jobPublicId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(jobPublicId, emitter));
        emitter.onTimeout(() -> remove(jobPublicId, emitter));
        emitter.onError(e -> remove(jobPublicId, emitter));

        // 초기 이벤트
        try {
            sendEvent(emitter, "init", Map.of("status", "CONNECTED"), 3000L);
        } catch (IOException e) {
            remove(jobPublicId, emitter);
        }

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
                sendEvent(em, event, data, null);
                if (terminal) em.complete();
            } catch (IOException e) {
                remove(publicId, em);
            } catch (Exception e) {
                // send 중 예기치 못한 런타임 예외도 정리
                remove(publicId, em);
            }
        }

        if (terminal) {
            registry.remove(publicId);
        }
    }

    private void sweepKeepAlive() {
        if (registry.isEmpty()) return;

        for (Map.Entry<String, CopyOnWriteArrayList<SseEmitter>> entry : registry.entrySet()) {
            String publicId = entry.getKey();
            CopyOnWriteArrayList<SseEmitter> list = entry.getValue();
            if (list == null || list.isEmpty()) {
                registry.remove(publicId);
                continue;
            }

            for (SseEmitter emitter : list) {
                try {
                    // comment 대신 ping 이벤트로 보내는 게 프록시/브라우저에서 더 안정적인 경우가 많음
                    sendEvent(emitter, "ping", Map.of("ts", System.currentTimeMillis()), null);
                } catch (IOException e) {
                    remove(publicId, emitter);
                } catch (Exception e) {
                    remove(publicId, emitter);
                }
            }
        }
    }

    private void sendEvent(SseEmitter emitter, String name, Object payload, Long reconnectTimeMs) throws IOException {
        String json = toJson(payload);

        SseEmitter.SseEventBuilder builder = SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(name)
                .data(json);

        if (reconnectTimeMs != null) {
            builder.reconnectTime(reconnectTimeMs);
        }

        emitter.send(builder);
    }

    private String toJson(Object payload) throws JsonProcessingException {
        // SSE는 문자열 payload가 제일 안전하게 동작하는 편이라 통일
        return om.writeValueAsString(payload);
    }

    private void remove(String publicId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> list = registry.get(publicId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) registry.remove(publicId);
        }
        try {
            emitter.complete();
        } catch (Exception ignored) {
        }
    }
}
