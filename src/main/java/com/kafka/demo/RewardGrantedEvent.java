package com.kafka.demo;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record RewardGrantedEvent(
        String eventId,    // 이벤트 식별자 (UUID)
        String shareId,    // 어떤 공유에 대한 리워드인지 (순서 맞추려고 key 동일 권장)
        String userId,     // 리워드 받는 사용자
        String rewardId,   // 리워드 식별자
        BigDecimal amount, // 금액 (BigDecimal 권장)
        String currency,   // "KRW" 등
        long timestamp     // epoch millis
) {
    public RewardGrantedEvent {
        Objects.requireNonNull(shareId, "shareId");
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(rewardId, "rewardId");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
    }

    public static RewardGrantedEvent now(String shareId, String userId, BigDecimal amount, String currency) {
        return new RewardGrantedEvent(
                UUID.randomUUID().toString(),
                shareId,
                userId,
                UUID.randomUUID().toString(),
                amount,
                currency,
                System.currentTimeMillis()
        );
    }

    public String key() { return shareId; }
    public String type() { return "reward.granted.v1"; }
}
