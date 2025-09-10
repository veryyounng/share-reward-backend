package com.kafka.demo;

import java.util.UUID;

public record ShareCreatedEvent(
        String eventId,
        String shareId,
        String userId,
        String linkId,
        String url,
        String utmSource,
        long   timestamp
) {
    public static ShareCreatedEvent now(
            String shareId,
            String userId,
            String linkId,
            String url,
            String utmSource
    ) {
        return new ShareCreatedEvent(
                UUID.randomUUID().toString(),
                shareId,
                userId,
                linkId,
                url,
                utmSource,
                System.currentTimeMillis()
        );
    }

    // ✅ 프로듀서에서 사용할 키/타입
    public String key() { return shareId; }
    public String type() { return "share.created.v1"; }
}
