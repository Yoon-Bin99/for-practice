package com.study.auth.token;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 리프레시 토큰 저장소 (실습용 메모리 구현). 운영에서는 Redis(TTL) 또는 DB 테이블에 저장한다.
 *
 *  - 토큰 원문이 아니라 SHA-256 해시를 키로 저장 → 저장소가 유출돼도 토큰을 바로 쓸 수 없다.
 *  - familyId: 한 번의 로그인에서 회전(rotation)으로 이어지는 토큰들의 묶음
 */
@Component
public class RefreshTokenStore {

    public record Entry(Long memberId, String familyId, Instant expiresAt, boolean used) {

        public Entry markUsed() {
            return new Entry(memberId, familyId, expiresAt, true);
        }
    }

    private final Map<String, Entry> byHash = new ConcurrentHashMap<>();

    public void save(String tokenHash, Entry entry) {
        byHash.put(tokenHash, entry);
    }

    public Optional<Entry> find(String tokenHash) {
        return Optional.ofNullable(byHash.get(tokenHash));
    }

    /** 같은 family 의 토큰을 모두 삭제 (재사용 감지·로그아웃 시) */
    public void revokeFamily(String familyId) {
        byHash.values().removeIf(e -> e.familyId().equals(familyId));
    }

    public int size() {
        return byHash.size();
    }
}
