package com.study.coupon.domain;

import com.study.coupon.common.BusinessException;
import com.study.coupon.common.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 선착순 쿠폰. 시간은 파라미터로 받는다(now) → 도메인 테스트에서 Clock 없이도 시간을 마음대로 정할 수 있다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    private long discountAmount;

    private int totalQuantity;

    private int issuedQuantity;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private Coupon(String code, long discountAmount, int totalQuantity, LocalDateTime expiresAt) {
        this.code = code;
        this.discountAmount = discountAmount;
        this.totalQuantity = totalQuantity;
        this.expiresAt = expiresAt;
    }

    public static Coupon create(String code, long discountAmount, int totalQuantity, LocalDateTime expiresAt) {
        return new Coupon(code, discountAmount, totalQuantity, expiresAt);
    }

    /** 만료 시각 "정각"부터는 발급 불가 (now >= expiresAt → 만료) */
    public void issue(LocalDateTime now) {
        if (!now.isBefore(expiresAt)) {
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }
        if (issuedQuantity >= totalQuantity) {
            throw new BusinessException(ErrorCode.COUPON_SOLD_OUT);
        }
        issuedQuantity++;
    }

    public int remainingQuantity() {
        return totalQuantity - issuedQuantity;
    }
}
