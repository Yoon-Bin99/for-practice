package com.study.auth.member;

import com.study.auth.common.BusinessException;
import com.study.auth.common.ErrorCode;
import com.study.auth.jwt.LoginMember;
import com.study.auth.jwt.TokenBlacklist;
import com.study.auth.token.RefreshTokenService;
import com.study.auth.token.TokenPair;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [실습 2] 로그인·로그아웃 (관련 문서: 인증·인가 03.스프링시큐리티실전/02·03)
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklist tokenBlacklist;

    /**
     * TODO 2-1. 로그인
     *  1) 이메일로 회원 조회
     *  2) passwordEncoder.matches(입력 비밀번호, 저장된 해시) 로 비교   ← equals 로 비교하면 안 된다 (해시는 매번 다른 솔트)
     *  3) 회원이 없거나 비밀번호가 틀리면 "같은" 에러 INVALID_CREDENTIALS
     *     → "없는 이메일입니다" 처럼 구분해 알려 주면 가입된 이메일을 알아내는 데 악용된다 (계정 열거 공격)
     *  4) refreshTokenService.issueNewFamily(member) 로 토큰 쌍 발급
     */
    public TokenPair login(String email, String rawPassword) {
        throw new UnsupportedOperationException("TODO 2-1 AuthService.login");
    }

    public TokenPair refresh(String refreshToken) {
        return refreshTokenService.rotate(refreshToken);
    }

    /**
     * TODO 2-2. 로그아웃
     *  - JWT 는 서버가 "회수"할 수 없다 → 남은 만료 시간 동안 jti 를 블랙리스트에 올린다
     *      tokenBlacklist.revoke(loginMember.tokenId(), loginMember.expiresAt())
     *  - 리프레시 토큰도 폐기: refreshTokenService.revoke(refreshToken)  (null 이면 건너뜀)
     */
    public void logout(LoginMember loginMember, String refreshToken) {
        throw new UnsupportedOperationException("TODO 2-2 AuthService.logout");
    }
}
