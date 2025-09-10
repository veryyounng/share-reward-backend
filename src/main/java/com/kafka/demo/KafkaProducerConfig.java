package com.kafka.demo;

import com.kafka.demo.RewardGrantedEvent;
import com.kafka.demo.ShareCreatedEvent;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {
    @Bean
    public ProducerFactory<String, ShareCreatedEvent> shareProducerFactory(KafkaProperties props) {
        return new DefaultKafkaProducerFactory<>(props.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, ShareCreatedEvent> shareKafkaTemplate(
            ProducerFactory<String, ShareCreatedEvent> pf) {
        return new KafkaTemplate<>(pf);
    }

    // RewardGrantedEvent용
    @Bean
    public ProducerFactory<String, RewardGrantedEvent> rewardProducerFactory(KafkaProperties props) {
        return new DefaultKafkaProducerFactory<>(props.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, RewardGrantedEvent> rewardKafkaTemplate(
            ProducerFactory<String, RewardGrantedEvent> pf) {
        return new KafkaTemplate<>(pf);
    }

}
