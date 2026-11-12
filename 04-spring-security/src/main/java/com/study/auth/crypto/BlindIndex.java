package com.study.auth.crypto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;

/**
 * [실습 10] 블라인드 인덱스 (암호화한 값으로 검색하기)
 *
 * AES-GCM 암호문은 매번 달라서 WHERE phone_encrypted = ? 검색이 불가능하다.
 * → 같은 입력이면 항상 같은 값이 나오는 HMAC-SHA256 을 따로 저장해 검색에 쓴다.
 *
 * 왜 그냥 SHA-256 이 아니라 HMAC 인가?
 *  - 전화번호는 경우의 수가 적어서(010-XXXX-XXXX) 키 없는 해시는 전부 계산해 보면 원래 번호를 알아낼 수 있다.
 *  - HMAC 은 비밀 키가 있어야 계산 가능 → DB 만 유출돼서는 역추적 불가
 */
public class BlindIndex {

    private final SecretKeySpec key;

    public BlindIndex(byte[] keyBytes) {
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    /**
     * TODO 10-1. 정규화 후 HMAC-SHA256 → 16진수 문자열
     *  1) 정규화: 하이픈·공백 제거 (010-1234-5678 과 01012345678 이 같은 값이 되도록)  ← 포인트
     *  2) Mac mac = Mac.getInstance("HmacSHA256"); mac.init(key);
     *  3) HexFormat.of().formatHex(mac.doFinal(정규화값 UTF-8 바이트))
     */
    public String of(String value) {
        throw new UnsupportedOperationException("TODO 10-1 BlindIndex.of");
    }
}
