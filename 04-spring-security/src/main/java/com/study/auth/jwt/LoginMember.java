package com.study.auth.jwt;

import com.study.auth.member.Role;

import java.time.Instant;

/**
 * SecurityContext 에 넣을 인증 주체(principal).
 * 컨트롤러에서 @AuthenticationPrincipal LoginMember 로 꺼내 쓰고, @PreAuthorize 에서 authentication.principal.memberId 로 참조한다.
 */
public record LoginMember(Long memberId, String email, Role role, String tokenId, Instant expiresAt) {
}
