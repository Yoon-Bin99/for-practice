package com.study.auth.jwt;

import com.study.auth.member.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * [실습 3] JWT 발급·검증 (관련 문서: 인증·인가 01.인증인가기초/04.토큰과JWT.md, 03.스프링시큐리티실전/02.JWT인증구현.md)
 *
 * JWT = Base64Url(Header).Base64Url(Payload).Signature
 *  - Payload 는 "암호화가 아니라 인코딩"이다 → 누구나 읽을 수 있으니 비밀번호·개인정보를 넣지 말 것
 *  - Signature 는 서버의 비밀 키로 만든 HMAC → 내용을 바꾸면 검증에 실패한다 (변조 방지)
 *
 * 라이브러리: jjwt 0.12.x
 */
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final JwtProperties properties;
    private final Clock clock;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));   // 256비트 이상 → HS256
        this.clock = Clock.systemUTC();
    }

    /**
     * TODO 3-1. Access Token 발급
     *  - Jwts.builder()
     *      .subject(회원 ID 문자열)          ← "누구의 토큰인가" (sub)
     *      .claim("email", email).claim("role", role.name())
     *      .id(UUID.randomUUID().toString()) ← jti: 로그아웃 시 블랙리스트에 올릴 토큰 식별자
     *      .issuedAt(Date.from(now)).expiration(Date.from(now + accessTokenTtl))
     *      .signWith(key)
     *      .compact()
     *  - 포인트: 만료 시간을 짧게(15분). 탈취되어도 피해 기간이 짧다. 대신 리프레시 토큰으로 재발급한다.
     */
    public String createAccessToken(Long memberId, String email, Role role) {
        throw new UnsupportedOperationException("TODO 3-1 createAccessToken");
    }

    /**
     * TODO 3-2. Access Token 검증 + LoginMember 로 변환
     *  - Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
     *    → 서명이 틀리면 SignatureException(JwtException), 만료면 ExpiredJwtException 이 "던져진다"
     *  - 여기서는 예외를 잡지 않는다. 어떤 이유로 실패했는지는 필터가 판단한다. (TODO 4-1)
     *  - claims.getSubject(), claims.get("email", String.class), Role.valueOf(claims.get("role", String.class)),
     *    claims.getId(), claims.getExpiration().toInstant() 로 LoginMember 생성
     */
    public LoginMember parseAccessToken(String token) {
        throw new UnsupportedOperationException("TODO 3-2 parseAccessToken");
    }

    public long accessTokenTtlSeconds() {
        return properties.accessTokenTtl().toSeconds();
    }
}
