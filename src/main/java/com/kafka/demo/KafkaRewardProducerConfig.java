package com.kafka.demo;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

public class KafkaRewardProducerConfig {
    @Bean
    public ProducerFactory<String, RewardGrantedEvent> rewardProducerFactory() {
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        ));
    }
    @Bean
    public KafkaTemplate<String, RewardGrantedEvent> rewardKafkaTemplate() {
        return new KafkaTemplate<>(rewardProducerFactory());
    }
}
