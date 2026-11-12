package com.study.auth.crypto;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * [실습 9] AES-256-GCM 양방향 암호화 (관련 문서: 인증·인가 01.인증인가기초/06.암호화기초.md)
 *
 * 저장 형식: Base64( IV(12바이트) + 암호문 + 인증태그(16바이트) )
 *
 * 핵심 포인트
 *  - IV(nonce)는 암호화할 때마다 "새로 랜덤" → 같은 평문도 매번 다른 암호문 (패턴 노출 방지)
 *  - IV 는 비밀이 아니므로 암호문 앞에 붙여 저장하고, 복호화할 때 앞 12바이트를 떼어 쓴다
 *  - GCM 은 인증 태그로 "변조 여부"까지 검사 → 한 비트라도 바뀌면 AEADBadTagException
 *  - 같은 키로 같은 IV 를 재사용하면 GCM 의 안전성이 무너진다 → SecureRandom 필수
 */
public class AesGcmCipher {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BIT = 128;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SecretKey key;

    public AesGcmCipher(byte[] keyBytes) {
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("AES-256 키는 32바이트여야 합니다. 현재: " + keyBytes.length);
        }
        this.key = new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * TODO 9-1. 암호화
     *  1) byte[] iv = new byte[IV_LENGTH]; RANDOM.nextBytes(iv);
     *  2) Cipher cipher = Cipher.getInstance(TRANSFORMATION);
     *     cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
     *  3) byte[] cipherText = cipher.doFinal(평문 UTF-8 바이트);   ← 태그가 뒤에 자동으로 붙는다
     *  4) ByteBuffer 로 iv + cipherText 를 이어 붙여 Base64 인코딩
     *  - GeneralSecurityException 은 IllegalStateException 으로 감싸 던진다
     */
    public String encrypt(String plainText) {
        throw new UnsupportedOperationException("TODO 9-1 AesGcmCipher.encrypt");
    }

    /**
     * TODO 9-2. 복호화 (encrypt 의 역순)
     *  1) Base64 디코딩 → ByteBuffer.wrap
     *  2) 앞 12바이트를 iv 로 읽고, 나머지를 cipherText 로
     *  3) DECRYPT_MODE 로 init 후 doFinal → UTF-8 문자열
     *  4) AEADBadTagException 이면 IllegalStateException("변조되었거나 키가 다릅니다.") — 구분해서 처리하는 것이 포인트
     */
    public String decrypt(String encoded) {
        throw new UnsupportedOperationException("TODO 9-2 AesGcmCipher.decrypt");
    }
}
