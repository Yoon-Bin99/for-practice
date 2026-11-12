package com.study.auth.config;

import com.study.auth.jwt.JwtAuthenticationFilter;
import com.study.auth.jwt.JwtTokenProvider;
import com.study.auth.jwt.TokenBlacklist;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * [실습 6] SecurityFilterChain 구성 (관련 문서: 인증·인가 02.스프링시큐리티구조/01.스프링시큐리티개요와필터체인.md, 03.인가아키텍처.md)
 */
@Configuration
@EnableMethodSecurity            // @PreAuthorize 사용 (TODO 8)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklist tokenBlacklist;
    private final SecurityErrorHandlers errorHandlers;

    /**
     * TODO 6-1. 필터 체인 설정
     *  1) JWT 를 Authorization 헤더로 보내는 Stateless API 이므로
     *       csrf 비활성화, formLogin·httpBasic 비활성화, 세션 정책 STATELESS
     *  2) URL 권한 (위에서부터 순서대로 매칭되므로 구체적인 규칙을 먼저)
     *       POST /api/auth/signup, /api/auth/login, /api/auth/refresh → permitAll
     *       /error                                                   → permitAll  (안 열면 500 에러가 401 로 가려진다!)
     *       /api/admin/**                                            → hasRole("ADMIN")
     *       그 외 모든 요청                                           → authenticated
     *  3) exceptionHandling: authenticationEntryPoint(errorHandlers.entryPoint()), accessDeniedHandler(...)
     *  4) addFilterBefore(new JwtAuthenticationFilter(tokenProvider, tokenBlacklist), UsernamePasswordAuthenticationFilter.class)
     *  - 포인트: 새 API 를 추가했을 때 "기본이 막힘(authenticated)"이 되도록 anyRequest() 는 마지막에 둔다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // TODO 6-1: 지금은 모두 허용
        return http.build();
    }

    /**
     * 비밀번호 인코더: DelegatingPasswordEncoder → 저장 값 앞에 {bcrypt} 같은 알고리즘 ID 가 붙는다.
     * 나중에 알고리즘을 바꿔도 기존 해시를 그대로 검증할 수 있다. (완성됨)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
