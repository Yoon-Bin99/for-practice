package com.study.auth.token;

import com.study.auth.common.BusinessException;
import com.study.auth.common.ErrorCode;
import com.study.auth.jwt.JwtProperties;
import com.study.auth.jwt.JwtTokenProvider;
import com.study.auth.member.Member;
import com.study.auth.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

/**
 * [실습 5] 리프레시 토큰 회전(Rotation)과 재사용 감지 (관련 문서: 인증·인가 03.스프링시큐리티실전/03.리프레시토큰과로그아웃.md)
 *
 *  로그인 ─▶ RT1 발급 (family F)
 *  재발급 ─▶ RT1 사용 처리, RT2 발급 (family F)
 *  재발급 ─▶ RT2 사용 처리, RT3 발급 ...
 *  만약 이미 사용한 RT1 이 다시 들어오면? → 누군가 훔친 토큰을 쓰고 있다 → family F 전체 폐기, 재로그인 요구
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final RefreshTokenStore store;
    private final JwtTokenProvider tokenProvider;
    private final JwtProperties properties;
    private final MemberRepository memberRepository;

    /** 로그인 성공 시: 새 family 로 토큰 쌍 발급 (완성됨) */
    public TokenPair issueNewFamily(Member member) {
        return issue(member, UUID.randomUUID().toString());
    }

    /**
     * TODO 5-1. 리프레시 토큰으로 재발급 (회전 + 재사용 감지)
     *  1) Entry entry = store.find(hash(refreshToken)) — 없으면 REFRESH_TOKEN_INVALID
     *  2) entry.used() 가 true 면 → 재사용 감지!
     *       log.warn(...) 후 store.revokeFamily(entry.familyId()), REFRESH_TOKEN_REUSED 예외
     *  3) 만료(entry.expiresAt() 이 지금보다 이전)면 REFRESH_TOKEN_INVALID
     *  4) store.save(같은 hash, entry.markUsed())  ← 삭제가 아니라 "사용됨" 표시를 남겨야 재사용을 감지할 수 있다
     *  5) 회원 조회 후 issue(member, entry.familyId()) 로 같은 family 의 새 토큰 쌍 반환
     */
    public TokenPair rotate(String refreshToken) {
        throw new UnsupportedOperationException("TODO 5-1 RefreshTokenService.rotate");
    }

    /** 로그아웃: 이 리프레시 토큰의 family 전체 폐기 (완성됨) */
    public void revoke(String refreshToken) {
        store.find(hash(refreshToken)).ifPresent(e -> store.revokeFamily(e.familyId()));
    }

    private TokenPair issue(Member member, String familyId) {
        String accessToken = tokenProvider.createAccessToken(member.getId(), member.getEmail(), member.getRole());
        String refreshToken = randomToken();
        store.save(hash(refreshToken), new RefreshTokenStore.Entry(member.getId(), familyId,
                Instant.now().plus(properties.refreshTokenTtl()), false));
        return new TokenPair(accessToken, refreshToken, tokenProvider.accessTokenTtlSeconds());
    }

    /** 리프레시 토큰은 JWT 일 필요가 없다: 추측 불가능한 랜덤 값이면 충분 (서버에서 상태로 관리하므로) */
    private static String randomToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
