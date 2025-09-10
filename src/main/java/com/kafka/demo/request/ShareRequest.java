package com.kafka.demo.request;

public record ShareRequest(
        String shareId,
        String userId,
        String linkId,
        String url,
        String utmSource
) {}
