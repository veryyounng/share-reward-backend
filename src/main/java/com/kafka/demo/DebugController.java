package com.kafka.demo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/debug")
    public class DebugController {
        private final RewardEventProducer rewardProducer;
        public DebugController(RewardEventProducer rewardProducer) { this.rewardProducer = rewardProducer; }

        @PostMapping("/reward")
        public String sendReward(@RequestParam String shareId, @RequestParam String userId) {
            var reward = RewardGrantedEvent.now(shareId, userId, new java.math.BigDecimal("980"), "KRW");
            rewardProducer.send(reward);
            return "ok";
        }
    }
