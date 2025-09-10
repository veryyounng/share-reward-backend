package com.kafka.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RewardEventProducer {
    @Value("${app.topics.reward-granted}")
    private String topic;
    private final KafkaTemplate<String, RewardGrantedEvent> kafkaTemplate;

    public RewardEventProducer(KafkaTemplate<String, RewardGrantedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(RewardGrantedEvent event) {
        kafkaTemplate.send(topic, event.key(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("[Kafka] send FAIL: " + ex.getMessage());
                        return;
                    }
                    var md = result.getRecordMetadata();
                    System.out.printf("[REWARD] sent topic=%s key=%s amount=%s%n", topic, event.key(), event.amount());

                });
    }
}
