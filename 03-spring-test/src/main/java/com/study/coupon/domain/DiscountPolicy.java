package com.study.coupon.domain;

import org.springframework.stereotype.Component;

/**
 * 할인 정책 (순수 자바 로직 → 스프링 없이 단위 테스트하기 좋은 대상)
 *
 * 규칙
 *  1. 등급 할인: 가격 × 등급 할인율(BRONZE 0%, SILVER 5%, GOLD 10%), 소수점 버림
 *  2. 쿠폰 할인: 주문 금액이 10,000원 "이상"일 때만 적용
 *  3. 총 할인은 10,000원을 넘을 수 없고, 가격보다 클 수 없다
 *  4. 가격이 음수면 IllegalArgumentException
 */
@Component
public class DiscountPolicy {

    public static final long MAX_DISCOUNT = 10_000;
    public static final long COUPON_MIN_ORDER_AMOUNT = 10_000;

    public long discount(long price, Grade grade, long couponAmount) {
        if (price < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다: " + price);
        }
        long gradeDiscount = price * grade.discountRate() / 100;
        long couponDiscount = price >= COUPON_MIN_ORDER_AMOUNT ? couponAmount : 0;
        return Math.min(gradeDiscount + couponDiscount, Math.min(MAX_DISCOUNT, price));
    }
}
