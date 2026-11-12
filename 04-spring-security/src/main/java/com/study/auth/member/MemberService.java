package com.study.auth.member;

import com.study.auth.common.BusinessException;
import com.study.auth.common.ErrorCode;
import com.study.auth.crypto.AesGcmCipher;
import com.study.auth.crypto.BlindIndex;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * [실습 1] 회원 가입: 비밀번호 해시 + 개인정보 암호화 (관련 문서: 인증·인가 01.인증인가기초/02.비밀번호저장.md, 06.암호화기초.md)
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AesGcmCipher aesGcmCipher;
    private final BlindIndex blindIndex;

    public record MemberView(Long id, String email, String name, Role role, String phone) { }

    /**
     * TODO 1-1. 회원 가입
     *  1) 이메일 중복이면 BusinessException(DUPLICATE_EMAIL)  (동시 가입은 DB unique 제약이 최종 방어)
     *  2) 비밀번호는 passwordEncoder.encode(...)  → "{bcrypt}$2a$10$..." 형태로 저장 (단방향 해시 + 솔트)
     *  3) 전화번호가 있으면: phoneEncrypted = aesGcmCipher.encrypt(phone), phoneBlindIndex = blindIndex.of(phone)
     *  4) Member.create(..., Role.USER, ...) 저장 후 id 반환
     *  - 포인트: 비밀번호는 "되돌릴 필요가 없으니" 해시, 전화번호는 "다시 보여 줘야 하니" 암호화. 목적에 따라 방식이 다르다.
     */
    @Transactional
    public Long signup(SignupRequest request) {
        throw new UnsupportedOperationException("TODO 1-1 MemberService.signup");
    }

    /** 내 정보: 전화번호를 복호화해서 보여 준다 */
    public MemberView me(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return toView(member);
    }

    /** 관리자 검색: 전화번호 → 블라인드 인덱스 → WHERE phone_blind_index = ? */
    public List<MemberView> searchByPhone(String phone) {
        return memberRepository.findByPhoneBlindIndex(blindIndex.of(phone)).stream()
                .map(this::toView)
                .toList();
    }

    public List<MemberView> findAll() {
        return memberRepository.findAll().stream().map(this::toView).toList();
    }

    private MemberView toView(Member m) {
        String phone = m.getPhoneEncrypted() == null ? null : aesGcmCipher.decrypt(m.getPhoneEncrypted());
        return new MemberView(m.getId(), m.getEmail(), m.getName(), m.getRole(), phone);
    }
}
