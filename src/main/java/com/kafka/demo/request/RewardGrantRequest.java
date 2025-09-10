package com.kafka.demo.request;

import java.math.BigDecimal;

public record RewardGrantRequest(
        String shareId,
        String userId,
        BigDecimal amount,
        String currency
) {}
