package com.kafka.demo.core;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.kafka.demo.dto.ApiDtos.RewardEvent;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class SseHub {
    private final ConcurrentHashMap<String, CopyOnWriteArraySet<SseEmitter>> subscribers = new ConcurrentHashMap<>();
    private static final long TIMEOUT_MS = Duration.ofMinutes(30).toMillis();

    public SseEmitter subscribe(String userId) {
        var emitter = new SseEmitter(TIMEOUT_MS);
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onCompletion(() -> remove(userId, emitter));
        subscribers.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(emitter);
        // first comment/heartbeat
        try { emitter.send(SseEmitter.event().comment("connected")); } catch (IOException ignored) {}
        return emitter;
    }

    public void publish(RewardEvent event) {
        var set = subscribers.get(event.userId());
        if (set == null) return;
        for (var emitter : set) {
            try { emitter.send(SseEmitter.event().name("reward").data(event)); }
            catch (IOException e) { emitter.complete(); }
        }
    }

    private void remove(String userId, SseEmitter e) {
        var set = subscribers.get(userId);
        if (set != null) { set.remove(e); }
    }
}
