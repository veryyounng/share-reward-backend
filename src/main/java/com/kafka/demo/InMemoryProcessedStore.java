package com.kafka.demo;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryProcessedStore {
    private final Set<String> done = ConcurrentHashMap.newKeySet();
    public boolean seen(String eventId) { return !done.add(eventId); }
}
