package com.study.middleware.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * [실습 R6] 고정 윈도우 레이트 리밋: "회원당 10초에 5번"
 *
 *  키: rate:{회원}:{현재시각/10초}  → INCR, 처음 만들어졌으면 EXPIRE 10
 */
@Component
@RequiredArgsConstructor
public class RateLimiter {

    /**
     * TODO R6-1. INCR 과 EXPIRE 를 하나의 Lua 로 묶기
     *  local current = redis.call('INCR', KEYS[1])
     *  if current == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end
     *  return current
     *  - 포인트: Java 에서 increment() 후 expire() 를 따로 호출하면, 그 사이 서버가 죽었을 때 만료 없는 키가 영원히 남아
     *           그 회원은 다시는 요청하지 못한다.
     */
    private static final RedisScript<Long> INCR_WITH_TTL = RedisScript.of(
            "return 0", // TODO R6-1: 지금은 항상 0 → 제한 없음
            Long.class);

    private final StringRedisTemplate redis;

    /** @return 허용이면 true */
    public boolean tryAcquire(String subject, int limit, int windowSeconds) {
        long window = System.currentTimeMillis() / 1000 / windowSeconds;
        String key = "rate:" + subject + ":" + window;
        Long count = redis.execute(INCR_WITH_TTL, List.of(key), String.valueOf(windowSeconds));
        return count != null && count <= limit;
    }
}
