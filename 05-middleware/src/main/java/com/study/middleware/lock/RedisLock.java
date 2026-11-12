package com.study.middleware.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * [실습 R3] Redis 분산 락 직접 구현 (관련 문서: 미들웨어 01.Redis/03.Redis활용패턴.md)
 *
 *  획득: SET lock:{key} {내 토큰} NX PX {만료ms}   → 키가 없을 때만 성공
 *  해제: "값이 내 토큰일 때만" DEL                 → Lua 로 원자적으로
 *
 * 실무에서는 Redisson(RLock: 대기·자동 연장·Pub/Sub 기반 대기)을 쓰지만, 원리를 알기 위해 직접 만든다.
 */
@Component
@RequiredArgsConstructor
public class RedisLock {

    /**
     * TODO R3-2. 소유자 확인 후 삭제하는 Lua 스크립트
     *  if redis.call('GET', KEYS[1]) == ARGV[1] then return redis.call('DEL', KEYS[1]) else return 0 end
     *  - 왜 그냥 DEL 하면 안 될까? 내 작업이 길어져 락이 만료되고 다른 서버가 락을 잡았는데,
     *    내가 뒤늦게 DEL 하면 "남의 락"을 지워 버린다. GET 과 DEL 사이에 끼어들지 못하게 Lua 로 묶는다.
     */
    private static final RedisScript<Long> UNLOCK_SCRIPT = RedisScript.of(
            "return redis.call('DEL', KEYS[1])", // TODO R3-2: 소유자 확인 없이 삭제 → 남의 락을 지울 수 있다
            Long.class);

    private final StringRedisTemplate redis;

    /**
     * TODO R3-1. 락 획득 시도 (최대 waitTime 동안 재시도)
     *  - 토큰 = UUID.randomUUID().toString()  (나만 아는 값 → 해제할 때 소유자 확인용)
     *  - Boolean ok = redis.opsForValue().setIfAbsent("lock:" + key, token, leaseTime);   ← SET NX PX
     *  - 성공하면 Optional.of(token), 실패하면 50ms 쉬고 재시도, waitTime 이 지나면 Optional.empty()
     *  - 포인트: leaseTime(자동 만료)이 없으면, 락을 잡은 서버가 죽었을 때 영원히 아무도 못 들어간다.
     */
    public Optional<String> tryLock(String key, Duration waitTime, Duration leaseTime) {
        return Optional.of("no-lock"); // TODO R3-1: 지금은 락 없이 항상 성공
    }

    public boolean unlock(String key, String token) {
        Long deleted = redis.execute(UNLOCK_SCRIPT, List.of("lock:" + key), token);
        return deleted != null && deleted > 0;
    }
}
