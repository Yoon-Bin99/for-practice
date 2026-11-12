package com.study.coupon.domain;

import com.study.coupon.common.BusinessException;
import com.study.coupon.common.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * [실습 2] 도메인 객체 테스트 + 시간 경계값 (관련 문서: 테스트코드 03.테스트설계/04.동시성과시간테스트.md)
 *
 * Coupon.issue(now) 는 현재 시간을 "인자"로 받는다 → LocalDateTime.now() 에 의존하지 않아 언제 실행해도 같은 결과.
 */
@DisplayName("쿠폰 발급")
class CouponTest {

    private static final LocalDateTime EXPIRES_AT = LocalDateTime.of(2026, 12, 31, 23, 59, 59);

    @Nested
    @DisplayName("만료 시각 기준")
    class Expiration {

        @Test
        @DisplayName("만료 1초 전에는 발급된다 (예시: 완성된 테스트)")
        void beforeExpiration() {
            Coupon coupon = Coupon.create("TEST", 1000, 10, EXPIRES_AT);

            coupon.issue(EXPIRES_AT.minusSeconds(1));

            assertThat(coupon.getIssuedQuantity()).isEqualTo(1);
        }

        /**
         * TODO 2-1. 만료 시각 "정각"에는 발급되지 않는다
         *  - coupon.issue(EXPIRES_AT) → BusinessException, errorCode = COUPON_EXPIRED
         *  - AssertJ: .isInstanceOf(BusinessException.class).extracting("errorCode").isEqualTo(ErrorCode.COUPON_EXPIRED)
         *  - 그리고 발급 수량은 그대로 0 인지도 확인 (실패한 연산이 상태를 바꾸면 안 된다)
         */
        @Test
        @DisplayName("만료 시각 정각에는 발급되지 않는다")
        void atExpiration() {
            fail("TODO 2-1");
        }
    }

    @Nested
    @DisplayName("수량 기준")
    class Quantity {

        /**
         * TODO 2-2. 수량을 모두 소진하면 더 이상 발급되지 않는다
         *  - totalQuantity = 2 인 쿠폰을 2번 발급 → 3번째에 COUPON_SOLD_OUT
         *  - remainingQuantity() 가 0 인지도 확인
         */
        @Test
        @DisplayName("수량을 모두 소진하면 발급되지 않는다")
        void soldOut() {
            fail("TODO 2-2");
        }
    }
}
