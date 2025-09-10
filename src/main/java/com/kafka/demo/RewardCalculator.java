package com.kafka.demo;

import java.math.BigDecimal;

public class RewardCalculator  {
    // TODO: 실제 규칙(상품가격 * 9.8% 등)로 바꾸기
    public BigDecimal calculate(ShareCreatedEvent share) {
        // 예시: utmSource가 "direct"면 0원, 그 외는 980원 고정
        if ("direct".equalsIgnoreCase(share.utmSource())) return BigDecimal.ZERO;
        return BigDecimal.valueOf(980);
    }
}
