package com.study.auth.jwt;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 로그아웃한 Access Token 의 jti 를 "남은 만료 시간 동안만" 보관한다.
 * 실습에서는 메모리(ConcurrentHashMap)를 쓰지만, 서버가 여러 대라면 Redis 에 SET jti "1" EX 남은초 로 저장한다.
 */
@Component
public class TokenBlacklist {

    private final Map<String, Instant> revoked = new ConcurrentHashMap<>();

    public void revoke(String tokenId, Instant expiresAt) {
        revoked.put(tokenId, expiresAt);
    }

    public boolean isRevoked(String tokenId) {
        Instant expiresAt = revoked.get(tokenId);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt.isBefore(Instant.now())) {
            revoked.remove(tokenId);      // 이미 만료된 토큰은 블랙리스트에 둘 필요가 없다
            return false;
        }
        return true;
    }
}
