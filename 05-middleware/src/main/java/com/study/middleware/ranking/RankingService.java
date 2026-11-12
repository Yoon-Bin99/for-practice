package com.study.middleware.ranking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * [실습 R5] Sorted Set 실시간 랭킹 (관련 문서: 미들웨어 01.Redis/01.Redis개요와자료구조.md)
 *
 * DB 로 "ORDER BY score DESC" 를 매번 하면 데이터가 많을수록 느려진다.
 * Sorted Set 은 점수 순으로 정렬된 상태를 유지 → 점수 갱신·순위 조회 모두 O(log N).
 */
@Service
@RequiredArgsConstructor
public class RankingService {

    private static final String KEY = "ranking:weekly";

    private final StringRedisTemplate redis;

    public record Rank(long rank, String member, double score) { }

    /**
     * TODO R5-1. 점수 더하기
     *  - redis.opsForZSet().incrementScore(KEY, member, score)
     *  - 키에 만료 시간 설정: redis.expire(KEY, Duration.ofDays(7)) (기간별 랭킹은 TTL 로 자동 정리)
     */
    public void addScore(String member, double score) {
        throw new UnsupportedOperationException("TODO R5-1 addScore");
    }

    /**
     * TODO R5-2. 상위 N명
     *  - Set<ZSetOperations.TypedTuple<String>> tuples = redis.opsForZSet().reverseRangeWithScores(KEY, 0, n - 1);
     *    (reverse = 점수 높은 순, 인덱스는 0부터 & 끝 포함)
     *  - 순서대로 new Rank(1부터 순위, tuple.getValue(), tuple.getScore()) 리스트로 변환
     */
    public List<Rank> top(int n) {
        throw new UnsupportedOperationException("TODO R5-2 top");
    }

    /**
     * TODO R5-3. 내 순위 (1부터, 없으면 -1)
     *  - Long r = redis.opsForZSet().reverseRank(KEY, member);  ← 0부터 시작하므로 +1
     */
    public long myRank(String member) {
        throw new UnsupportedOperationException("TODO R5-3 myRank");
    }
}
