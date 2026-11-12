package com.study.auth.config;

import com.study.auth.crypto.AesGcmCipher;
import com.study.auth.crypto.BlindIndex;
import com.study.auth.member.Member;
import com.study.auth.member.MemberRepository;
import com.study.auth.member.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** 실습 계정: admin@study.com / admin1234!  ·  user@study.com / user1234! */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AesGcmCipher aesGcmCipher;
    private final BlindIndex blindIndex;

    @Override
    public void run(ApplicationArguments args) {
        memberRepository.save(Member.create("admin@study.com", passwordEncoder.encode("admin1234!"), "관리자",
                Role.ADMIN, null, null));
        try {
            String phone = "010-1234-5678";
            memberRepository.save(Member.create("user@study.com", passwordEncoder.encode("user1234!"), "일반회원",
                    Role.USER, aesGcmCipher.encrypt(phone), blindIndex.of(phone)));
            log.info("✅ 실습 계정 생성: admin@study.com / admin1234!, user@study.com / user1234!");
        } catch (UnsupportedOperationException e) {
            memberRepository.save(Member.create("user@study.com", passwordEncoder.encode("user1234!"), "일반회원",
                    Role.USER, null, null));
            log.warn("⏳ 암호화 TODO(9, 10)를 구현하기 전이라 전화번호 없이 계정을 만들었습니다: {}", e.getMessage());
        }
    }
}
