package com.study.middleware.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.middleware.common.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * [실습 R1] Cache-Aside 패턴 직접 구현 (관련 문서: 미들웨어 01.Redis/02.캐시전략.md)
 *
 *   조회: 캐시 확인 → (없으면) DB 조회 → 캐시에 저장(TTL) → 반환
 *   수정: DB 수정 → 커밋 후 캐시 "삭제" (갱신 X)
 *
 * GET /api/products/{id}/cache-aside 를 두 번 호출해 응답 시간(source, elapsedMs)을 비교해 보자.
 * DB 조회는 일부러 300ms 걸리게 해 두었다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private static final Duration BASE_TTL = Duration.ofMinutes(10);

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;
    private final TransactionTemplate transactionTemplate;

    public record CacheResult(ProductView product, String source) { }

    static String key(Long productId) {
        return "product:" + productId;
    }

    /**
     * TODO R1-1. Cache-Aside 조회
     *  1) String cached = redis.opsForValue().get(key(id)); 있으면 JSON → ProductView 로 변환해 new CacheResult(view, "CACHE")
     *  2) 없으면 loadFromDb(id) 로 조회
     *  3) redis.opsForValue().set(key, JSON, ttlWithJitter())  ← TTL 필수!
     *  4) new CacheResult(view, "DB")
     *  - 포인트: TTL 에 랜덤 지터를 더하는 이유 → 많은 키가 "동시에" 만료되어 DB 로 몰리는 캐시 눈사태(Avalanche) 방지
     */
    public CacheResult get(Long id) {
        return new CacheResult(loadFromDb(id), "DB"); // TODO R1-1: 지금은 매번 DB 조회
    }

    /**
     * TODO R1-2. 가격 변경 + 캐시 무효화
     *  - transactionTemplate.executeWithoutResult(...) 안에서 상품 조회 후 changePrice
     *  - 트랜잭션이 "끝난 뒤" redis.delete(key(id))
     *  - 포인트: 캐시를 새 값으로 "갱신"하지 않고 "삭제"한다. 동시에 수정이 일어날 때 늦게 도착한 옛 값이 캐시를 덮어쓰는 문제를 피한다.
     *           커밋 전에 지우면, 그 사이 다른 요청이 옛 DB 값을 다시 캐시에 채울 수 있다.
     */
    public void changePrice(Long id, long price) {
        transactionTemplate.executeWithoutResult(status -> productRepository.findById(id).orElseThrow(() -> notFound(id)).changePrice(price)); // TODO R1-2: 캐시 무효화가 없다 → 10분 동안 옛 가격
    }

    private Duration ttlWithJitter() {
        return BASE_TTL.plusSeconds(ThreadLocalRandom.current().nextLong(0, 60));
    }

    /** 느린 DB 조회 흉내 (300ms) */
    private ProductView loadFromDb(Long id) {
        sleep(300);
        return transactionTemplate.execute(status ->
                ProductView.from(productRepository.findById(id).orElseThrow(() -> notFound(id))));
    }

    private ProductView fromJson(String json) {
        try {
            return objectMapper.readValue(json, ProductView.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private String toJson(ProductView view) {
        try {
            return objectMapper.writeValueAsString(view);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    static BusinessException notFound(Long id) {
        return new BusinessException(HttpStatus.NOT_FOUND, "P001", "상품을 찾을 수 없습니다: " + id);
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
