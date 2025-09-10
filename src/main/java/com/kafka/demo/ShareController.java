package com.kafka.demo;

import com.kafka.demo.core.InMemoryStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/shares")
public class ShareController {

    private final ShareEventProducer producer;
    private final InMemoryStore store;

    public ShareController(ShareEventProducer producer, InMemoryStore store) {
        this.producer = producer;
        this.store = store;
    }

    // 프론트에서 보내는 요청 형태와 1:1 매칭
    public record CreateShareReq(
            @NotBlank String userId,
            @NotBlank String itemUrl,
            String utmSource
    ) {}

    // 프론트가 기대하는 응답
    public record CreateShareRes(
            String shareId,
            String shortUrl,
            String createdAt
    ) {}

    @PostMapping
    public ResponseEntity<CreateShareRes> create(@RequestBody CreateShareReq req) {
        // shareId, linkId 서버에서 생성
        String shareId = "s-" + UUID.randomUUID().toString().substring(0, 8);
        String linkId  = "l-" + UUID.randomUUID().toString().substring(0, 6);

        // 기존 이벤트 타입을 사용한다고 가정 (필드명 예시는 맞춰주세요)
        ShareCreatedEvent event = ShareCreatedEvent.now(
                shareId,               // ✔ 서버 생성
                req.userId(),
                linkId,                // ✔ 서버 생성
                req.itemUrl(),         // ✔ 프론트 itemUrl -> 이벤트 url
                req.utmSource()
        );

        // 실제 카프카 발행
        producer.send(event);

        store.saveShare(shareId, req.userId(), req.itemUrl(), req.utmSource());
        // (단축 URL 서비스가 아직 없으니 null/임시 값)
        CreateShareRes res = new CreateShareRes(
                shareId,
                null,
                Instant.now().toString()
        );
        return ResponseEntity.ok(res);
    }

    @GetMapping("/ping")
    public String ping() {
        return "ok";
    }
}
