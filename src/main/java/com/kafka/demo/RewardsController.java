package com.kafka.demo;

import com.kafka.demo.core.InMemoryStore;
import com.kafka.demo.dto.ApiDtos;
import com.kafka.demo.request.RewardGrantRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kafka.demo.dto.ApiDtos.RewardEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class RewardsController {
    private final InMemoryStore store;

    public RewardsController(InMemoryStore store) {
        this.store = store;
    }

    // 프론트에서 쓰는 바디: { "shareId": "...", "userAgent": "..." }
    public record SimulateClickReq(String shareId, String userAgent) {}

    public record RewardsSummary(String userId, long totalAmount, int count, List<RewardEvent> recent) {}

    /**
     * 데모용 클릭 시뮬레이션: shareId → userId 찾고, 보상 1건 생성/적재
     * 프론트는 202만 받으면 OK
     */
    @PostMapping("/simulate-click")
    public ResponseEntity<Void> simulateClick(@RequestBody SimulateClickReq req) {
        if (req == null || req.shareId() == null) return ResponseEntity.badRequest().build();

        var userIdOpt = store.findUserIdByShare(req.shareId());
        if (userIdOpt.isEmpty()) return ResponseEntity.accepted().build(); // 알 수 없는 shareId는 무시

        var userId = userIdOpt.get();

        // 랜덤 금액의 데모 이벤트 (ApiDtos.RewardEvent 사용)
        long amount = 500 + (long)(Math.random() * 1500);
        var evt = new RewardEvent(
                UUID.randomUUID().toString(),
                req.shareId(),
                userId,
                UUID.randomUUID().toString(),
                amount,
                "KRW",
                Instant.now().toEpochMilli()
        );
        store.pushReward(evt);

        return ResponseEntity.accepted().build();
    }

    /**
     * 실제 금액/통화로 보상을 적립하고 싶을 때 사용하는 수동 API
     * Body: RewardGrantRequest(shareId, userId, amount(BigDecimal), currency)
     */
    @PostMapping("/rewards/grant")
    public ResponseEntity<RewardEvent> grant(@RequestBody RewardGrantRequest req) {
        // userId 우선순위: 요청 바디의 userId > shareId로 역추적
        String userId = req.userId();
        if (userId == null || userId.isBlank()) {
            userId = store.findUserIdByShare(req.shareId()).orElse(null);
        }
        if (userId == null) return ResponseEntity.badRequest().build();

        var evt = new RewardEvent(
                UUID.randomUUID().toString(),
                req.shareId(),
                userId,
                UUID.randomUUID().toString(),
                req.amount().longValue(),   // ApiDtos.RewardEvent.amount는 long
                req.currency() == null ? "KRW" : req.currency(),
                Instant.now().toEpochMilli()
        );
        store.pushReward(evt);
        return ResponseEntity.ok(evt);
    }

    /**
     * 요약 조회: 누적 금액/건수/최근 5건
     */
    @GetMapping("/rewards/summary")
    public RewardsSummary summary(@RequestParam String userId) {
        var recent = store.recent(userId, 5);
        return new RewardsSummary(userId, store.totalAmount(userId), store.count(userId), recent);
    }
}
