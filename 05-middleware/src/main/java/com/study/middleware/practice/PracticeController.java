package com.study.middleware.practice;

import com.study.middleware.coupon.FirstComeCouponService;
import com.study.middleware.coupon.IssuedCouponRepository;
import com.study.middleware.lock.LockedStockService;
import com.study.middleware.outbox.OutboxEvent;
import com.study.middleware.outbox.OutboxRepository;
import com.study.middleware.outbox.OutboxStatus;
import com.study.middleware.point.PointHistoryRepository;
import com.study.middleware.product.Product;
import com.study.middleware.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

/** 동시성·중복 상황을 재현하는 실습용 엔드포인트 (완성됨) */
@RestController
@RequestMapping("/practice")
@RequiredArgsConstructor
public class PracticeController {

    private final LockedStockService lockedStockService;
    private final ProductRepository productRepository;
    private final FirstComeCouponService couponService;
    private final IssuedCouponRepository issuedCouponRepository;
    private final OutboxRepository outboxRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /** [R3] 재고 100 상품에 requests 명이 동시에 1개씩 주문 */
    @PostMapping("/lock-stock")
    public Map<String, Object> lockStock(@RequestParam(defaultValue = "100") int requests) {
        Product product = productRepository.save(new Product("락 실습 상품", 1000, 100));
        Map<String, Object> result = runConcurrently(requests, i -> lockedStockService.decrease(product.getId(), 1));
        result.put("finalStock", productRepository.findById(product.getId()).map(Product::getStock).orElse(-1));
        result.put("expected", "finalStock = 100 - success");
        return result;
    }

    /** [R4] 수량 100장 쿠폰에 서로 다른 회원 requests 명 + 같은 회원 중복 요청 섞어서 동시에 */
    @PostMapping("/coupon-race")
    public Map<String, Object> couponRace(@RequestParam(defaultValue = "300") int requests) throws InterruptedException {
        long couponId = System.currentTimeMillis();       // 실행할 때마다 새 쿠폰
        couponService.reset(couponId);
        Map<String, Object> result = runConcurrently(requests,
                i -> couponService.issue(couponId, (long) (i % 250), 100));   // 회원 0~249, 일부는 중복 요청
        Thread.sleep(2000);                                 // Kafka 컨슈머가 DB 에 저장할 시간
        result.put("redisIssued", couponService.issuedCountInRedis(couponId));
        result.put("dbIssued", issuedCouponRepository.countByCouponId(couponId));
        result.put("expected", "success = redisIssued = dbIssued = 100");
        return result;
    }

    /** [K3] 이미 발행된 주문 이벤트를 한 번 더 발행 → 포인트가 중복 적립되지 않아야 한다 */
    @PostMapping("/duplicate-event/{orderId}")
    public Map<String, Object> duplicateEvent(@PathVariable Long orderId) throws InterruptedException {
        OutboxEvent event = outboxRepository.findAll().stream()
                .filter(e -> e.getAggregateId().equals(String.valueOf(orderId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 주문의 outbox 이벤트가 없습니다: " + orderId));
        kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload());
        Thread.sleep(1500);
        return Map.of("orderId", orderId,
                "pointHistoryCount", pointHistoryRepository.countByOrderId(orderId),
                "expected", "pointHistoryCount = 1");
    }

    @GetMapping("/outbox")
    public Map<String, Long> outbox() {
        Map<String, Long> body = new LinkedHashMap<>();
        for (OutboxStatus s : OutboxStatus.values()) {
            body.put(s.name(), outboxRepository.countByStatus(s));
        }
        return body;
    }

    @GetMapping("/points/{memberId}")
    public Map<String, Long> points(@PathVariable Long memberId) {
        return Map.of("memberId", memberId, "point", pointHistoryRepository.sumByMemberId(memberId));
    }

    private Map<String, Object> runConcurrently(int requests, IntConsumer task) {
        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(requests);
        long begin = System.currentTimeMillis();
        try (ExecutorService pool = Executors.newFixedThreadPool(32)) {
            for (int i = 0; i < requests; i++) {
                int n = i;
                pool.submit(() -> {
                    try {
                        start.await();
                        task.accept(n);
                        success.incrementAndGet();
                    } catch (Exception e) {
                        fail.incrementAndGet();
                    } finally {
                        done.countDown();
                    }
                });
            }
            start.countDown();
            done.await(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("requests", requests);
        result.put("success", success.get());
        result.put("fail", fail.get());
        result.put("elapsedMs", System.currentTimeMillis() - begin);
        return result;
    }
}
