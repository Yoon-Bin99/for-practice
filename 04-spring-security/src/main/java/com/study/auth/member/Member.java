package com.study.auth.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(indexes = @Index(name = "idx_member_phone_bidx", columnList = "phoneBlindIndex"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** 평문 X, 해시 O ("{bcrypt}$2a$10$...") */
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    /** AES-GCM 암호문 (Base64). 복호화할 수 있어야 하는 개인정보 → 양방향 암호화 */
    @Column(length = 200)
    private String phoneEncrypted;

    /** 암호문은 매번 달라 검색이 안 되므로, 검색용 HMAC 값을 따로 저장 (블라인드 인덱스) */
    @Column(length = 64)
    private String phoneBlindIndex;

    private Member(String email, String password, String name, Role role, String phoneEncrypted, String phoneBlindIndex) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.phoneEncrypted = phoneEncrypted;
        this.phoneBlindIndex = phoneBlindIndex;
    }

    public static Member create(String email, String encodedPassword, String name, Role role,
                                String phoneEncrypted, String phoneBlindIndex) {
        return new Member(email, encodedPassword, name, role, phoneEncrypted, phoneBlindIndex);
    }
}
