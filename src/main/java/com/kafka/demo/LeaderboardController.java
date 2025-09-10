package com.kafka.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kafka.demo.core.InMemoryStore;

@RestController
@RequestMapping("/api")
public class LeaderboardController {
    public record LeaderboardRow(String userId, long totalAmount, int count) {}

    private final InMemoryStore store;

    public LeaderboardController(InMemoryStore store) {
        this.store = store;
    }

    // 프론트가 호출하는 형식: GET /api/leaderboard?period=7d
    @GetMapping("/leaderboard")
    public List<LeaderboardRow> leaderboard(@RequestParam(defaultValue = "7d") String period) {
        List<Map.Entry<String, Long>> tops = store.topByAmount(10);
        var out = new ArrayList<LeaderboardRow>();
        for (var e : tops) {
            String userId = e.getKey();
            long total = e.getValue();
            int count = store.userCount(userId);
            out.add(new LeaderboardRow(userId, total, count));
        }
        return out;
    }
}
