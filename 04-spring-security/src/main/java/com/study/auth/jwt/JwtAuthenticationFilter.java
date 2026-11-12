package com.study.auth.jwt;

import com.study.auth.common.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * [실습 4] JWT 인증 필터 (관련 문서: 인증·인가 02.스프링시큐리티구조/01·02, 03.스프링시큐리티실전/02.JWT인증구현.md)
 *
 *   요청 ─▶ [JwtAuthenticationFilter] ─▶ ... ─▶ [AuthorizationFilter: URL 권한 검사] ─▶ 컨트롤러
 *             토큰이 유효하면 SecurityContext 에 인증 정보 저장
 *
 * 핵심 포인트
 *  - 필터는 "인증 정보를 채우기만" 하고, 막는 것은 뒤의 AuthorizationFilter 가 한다.
 *    → 토큰이 없거나 틀려도 여기서 응답을 쓰지 않고 다음 필터로 넘긴다. (공개 API 는 그대로 통과해야 하니까)
 *  - 실패 이유(만료/위조)는 request attribute 에 남겨 EntryPoint 가 응답 코드를 고르게 한다.
 *  - 이 필터는 @Component 로 등록하지 않는다. (등록하면 서블릿 필터로도 한 번 더 등록된다) → SecurityConfig 에서 new
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String ERROR_ATTRIBUTE = "auth.error";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklist tokenBlacklist;

    /**
     * TODO 4-1. 토큰 꺼내기 → 검증 → SecurityContext 저장
     *  1) Authorization 헤더가 "Bearer " 로 시작하면 뒤의 토큰 문자열을 꺼낸다
     *  2) tokenProvider.parseAccessToken(token)
     *  3) tokenBlacklist.isRevoked(jti) 면 → request.setAttribute(ERROR_ATTRIBUTE, ErrorCode.INVALID_TOKEN), 인증하지 않음
     *  4) 정상이면
     *       var authentication = UsernamePasswordAuthenticationToken.authenticated(
     *               loginMember, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
     *       SecurityContext context = SecurityContextHolder.createEmptyContext();
     *       context.setAuthentication(authentication);
     *       SecurityContextHolder.setContext(context);
     *  5) ExpiredJwtException → ERROR_ATTRIBUTE = TOKEN_EXPIRED,  JwtException | IllegalArgumentException → INVALID_TOKEN
     *  6) 마지막에 항상 filterChain.doFilter(request, response)
     *  - "ROLE_" 접두사: hasRole("ADMIN") 은 내부적으로 "ROLE_ADMIN" 권한을 찾는다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // TODO 4-1: 지금은 토큰을 전혀 확인하지 않는다 → 인증이 필요한 API 는 모두 401
        filterChain.doFilter(request, response);
    }
}
