package com.kafka.demo;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ShareCreatedConsumer {
    private final RewardCalculator calculator = new RewardCalculator(); // 데모용
    private final InMemoryProcessedStore store;
    private final RewardEventProducer rewardProducer;

    public ShareCreatedConsumer(InMemoryProcessedStore store, RewardEventProducer rewardProducer) {
        this.store = store;
        this.rewardProducer = rewardProducer;
    }

    @KafkaListener(topics = "${app.topics.share-created}", groupId = "reward-service")
    public void onMessage(ShareCreatedEvent share) {
        System.out.println("[CONSUMER] received shareId=" + share.shareId() + ", utmSource=" + share.utmSource());

        // 이벤트 ID 확보: body 우선, 없으면 shareId+timestamp로 대체
        String eid = share.eventId() != null ? share.eventId()
                : (share.shareId() + ":" + share.timestamp());

        // 중복 처리 차단
        if (store.seen(eid)) {
            System.out.println("[CONSUMER] duplicate skip eid=" + eid);
            return;
        }

        // 보상 계산
        BigDecimal amount = calculator.calculate(share);
        if (amount.signum() <= 0) return; // 0원이면 스킵

        // 발행
        var reward = RewardGrantedEvent.now(share.shareId(), share.userId(), amount, "KRW");
        System.out.println("[CONSUMER] sending reward amount=" + amount);
        rewardProducer.send(reward);
    }
}
