package com.study.middleware.coupon;

import com.study.middleware.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 선착순 쿠폰: Redis Lua 로 앞단에서 걸러내고(빠름), 성공한 요청만 Kafka 로 넘겨 DB 에 비동기 저장한다.
 *
 *  [사용자] → [API] → Redis Lua (중복·수량 검사, 수 ms) ─성공─▶ Kafka "coupon-issued" ─▶ [컨슈머] → DB
 */
@Service
@RequiredArgsConstructor
public class FirstComeCouponService {

    public static final String TOPIC = "coupon-issued";
    private static final RedisScript<Long> ISSUE_SCRIPT =
            RedisScript.of(new ClassPathResource("scripts/issue_coupon.lua"), Long.class);

    private final StringRedisTemplate redis;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void issue(Long couponId, Long memberId, int maxQuantity) {
        Long result = redis.execute(ISSUE_SCRIPT, List.of("coupon:" + couponId + ":issued"),
                String.valueOf(memberId), String.valueOf(maxQuantity));
        if (result == null || result == -2) {
            throw new BusinessException(HttpStatus.CONFLICT, "CP001", "쿠폰이 모두 소진되었습니다.");
        }
        if (result == -1) {
            throw new BusinessException(HttpStatus.CONFLICT, "CP002", "이미 발급받은 쿠폰입니다.");
        }
        kafkaTemplate.send(TOPIC, String.valueOf(couponId), couponId + ":" + memberId);
    }

    public long issuedCountInRedis(Long couponId) {
        Long size = redis.opsForSet().size("coupon:" + couponId + ":issued");
        return size == null ? 0 : size;
    }

    public void reset(Long couponId) {
        redis.delete("coupon:" + couponId + ":issued");
    }
}
