package com.kafka.demo.dto;

import java.time.Instant;
import java.util.UUID;

public class ApiDtos {
    public record CreateShareReq(String userId, String itemUrl, String utmSource) {}
    public record SimulateClickReq(String shareId, String userAgent) {}

    // Responses
    public record CreateShareRes(String shareId, String shortUrl, String createdAt) {}
    public record RewardsSummary(String userId, long totalAmount, int count, RewardEvent[] recent) {}
    public record LeaderboardRow(String userId, long totalAmount, int count) {}

    // Stream event
    public record RewardEvent(
            String eventId,
            String shareId,
            String userId,
            String rewardId,
            long amount,
            String currency,
            long timestamp
    ) {
        public static RewardEvent demo(String shareId, String userId) {
            long amount = 500 + (long)(Math.random() * 1500);
            return new RewardEvent(
                    UUID.randomUUID().toString(),
                    shareId,
                    userId,
                    UUID.randomUUID().toString(),
                    amount,
                    "KRW",
                    Instant.now().toEpochMilli()
            );
        }
    }
}
