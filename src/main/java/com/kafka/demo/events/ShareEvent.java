package com.kafka.demo.events;

import java.util.Objects;
import java.util.UUID;

public record ShareEvent(
        String eventId,   // 이벤트 식별자 (UUID)
        String shareId,   // 공유 식별자 (파티션 키로 사용)
        String userId,    // 공유한 사용자
        String linkId,    // 링크 식별자
        String url,       // 실제 공유 URL
        String utmSource, // utmSource (kakao 등)
        long timestamp    // epoch millis
) {
    public ShareEvent {
        Objects.requireNonNull(shareId, "shareId");
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(linkId, "linkId");
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(utmSource, "utmSource");
    }

    // 편의 팩토리: 서버에서 생성 시 사용
    public static ShareEvent now(String shareId, String userId, String linkId, String url, String utmSource) {
        return new ShareEvent(
                UUID.randomUUID().toString(),
                shareId,
                userId,
                linkId,
                url,
                utmSource,
                System.currentTimeMillis()
        );
    }

    // Kafka 키로 쓸 값 (정렬/순서 보장을 위해 shareId 고정 권장)
    public String key() { return shareId; }

    // 이벤트 타입 버저닝 (헤더로 넣어도 되고 바디에 둬도 됨)
    public String type() { return "share.created.v1"; }
}
