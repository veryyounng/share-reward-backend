package com.kafka.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShareEventProducer {
    @Value("${app.topics.share-created}")
    private String topic;
    private final KafkaTemplate<String, ShareCreatedEvent> kafkaTemplate;

    public ShareEventProducer(KafkaTemplate<String, ShareCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(ShareCreatedEvent event) {
        kafkaTemplate.send(topic, event.shareId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("[Kafka] send FAIL: " + ex.getMessage());
                        return;
                    }
                    var md = result.getRecordMetadata();
                    System.out.printf("[Kafka] sent topic=%s partition=%d offset=%d key=%s%n",
                            md.topic(), md.partition(), md.offset(), event.shareId());
                });
    }
}
