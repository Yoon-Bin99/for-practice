package com.study.coupon.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * [실습 1] 순수 단위 테스트 + 파라미터화 테스트 (관련 문서: 테스트코드 01.테스트기초/02.JUnit5.md, 03.AssertJ.md)
 *
 * 스프링 없이 new 로 만들어 테스트한다 → 수 ms 안에 끝난다.
 * 핵심은 "어떤 입력값을 고르느냐"다. DiscountPolicy 의 규칙을 읽고 경계값을 찾아보자.
 */
@DisplayName("할인 정책")
class DiscountPolicyTest {

    private final DiscountPolicy policy = new DiscountPolicy();

    @Test
    @DisplayName("브론즈 등급, 쿠폰 없음 → 할인 0원 (예시: 완성된 테스트)")
    void bronzeWithoutCoupon() {
        // given
        long price = 20_000;

        // when
        long discount = policy.discount(price, Grade.BRONZE, 0);

        // then
        assertThat(discount).isZero();
    }

    /**
     * TODO 1-1. 경계값 테스트 케이스 작성
     *  - "가격, 등급, 쿠폰금액, 기대할인" 형식으로 행을 채운다. 최소 아래 경우는 포함하자.
     *    · 실버 5% 기본 계산               예) 10000, SILVER, 0, 500
     *    · 쿠폰 적용 최소 금액 "정확히" 10,000원 (적용 O)
     *    · 쿠폰 적용 최소 금액보다 1원 적은 9,999원 (적용 X)
     *    · 총 할인 상한 10,000원에 걸리는 경우
     *    · 쿠폰 금액 자체가 상한보다 큰 경우
     *    · 가격 0원
     *  - 본문: assertThat(policy.discount(price, grade, coupon)).isEqualTo(expected)
     *  - 포인트: 정상 케이스 1개보다 "경계 바로 앞/뒤" 케이스가 버그를 잡는다.
     */
    @ParameterizedTest(name = "가격 {0}, {1}, 쿠폰 {2} → 할인 {3}")
    @CsvSource({
            "0, BRONZE, 0, 0"   // TODO 1-1: 경계값 행들을 추가하세요
    })
    void discountCases(long price, Grade grade, long coupon, long expected) {
        fail("TODO 1-1: 검증 코드를 작성하세요");
    }

    /**
     * TODO 1-2. 예외 테스트
     *  - 가격이 음수면 IllegalArgumentException, 메시지에 "0 이상" 포함
     *  - assertThatThrownBy(() -> ...).isInstanceOf(...).hasMessageContaining(...)
     */
    @Test
    @DisplayName("가격이 음수면 예외가 발생한다")
    void negativePrice() {
        fail("TODO 1-2");
    }
}
