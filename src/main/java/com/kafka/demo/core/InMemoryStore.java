package com.kafka.demo.core;

import com.kafka.demo.dto.ApiDtos;
import com.kafka.demo.dto.ApiDtos.RewardEvent;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryStore {
    @Getter
    private final Map<String, Map<String,String>> shares = new ConcurrentHashMap<>();

    // userId -> reward events (latest first, max 200)
    private final Map<String, Deque<ApiDtos.RewardEvent>> rewards = new ConcurrentHashMap<>();

    public void saveShare(String shareId, String userId, String itemUrl, String utm) {
        var m = new HashMap<String,String>();
        m.put("userId", userId);
        m.put("itemUrl", itemUrl);
        m.put("utm", utm == null ? "" : utm);
        shares.put(shareId, m);
    }

    public Optional<String> findUserIdByShare(String shareId) {
        var m = shares.get(shareId);
        return Optional.ofNullable(m == null ? null : m.get("userId"));
    }

    public void pushReward(RewardEvent evt) {
        rewards.computeIfAbsent(evt.userId(), k -> new ArrayDeque<>());
        var dq = rewards.get(evt.userId());
        dq.addFirst(evt);
        while (dq.size() > 200) dq.removeLast();
    }

    public List<RewardEvent> recent(String userId, int n) {
        var dq = rewards.getOrDefault(userId, new ArrayDeque<>());
        var it = dq.iterator();
        var out = new ArrayList<RewardEvent>(n);
        int i = 0;
        while (it.hasNext() && i < n) { out.add(it.next()); i++; }
        return out;
    }

    public long totalAmount(String userId) {
        return rewards.getOrDefault(userId, new ArrayDeque<>())
                .stream().mapToLong(RewardEvent::amount).sum();
    }

    public int count(String userId) {
        return rewards.getOrDefault(userId, new ArrayDeque<>()).size();
    }

    public List<Map.Entry<String, Long>> topByAmount(int topN) {
        var sums = new HashMap<String, Long>();
        for (var e : rewards.entrySet()) {
            long sum = e.getValue().stream().mapToLong(RewardEvent::amount).sum();
            sums.put(e.getKey(), sum);
        }
        return sums.entrySet().stream()
                .sorted((a,b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(topN).toList();
    }

    public int userCount(String userId) {
        return rewards.getOrDefault(userId, new ArrayDeque<>()).size();
    }
}
