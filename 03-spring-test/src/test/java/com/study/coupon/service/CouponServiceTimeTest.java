package com.study.coupon.service;

import com.study.coupon.common.BusinessException;
import com.study.coupon.common.ErrorCode;
import com.study.coupon.domain.Coupon;
import com.study.coupon.repository.CouponRepository;
import com.study.coupon.repository.IssuedCouponRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * [실습 7] 시간 고정 통합 테스트 (관련 문서: 테스트코드 03.테스트설계/04.동시성과시간테스트.md)
 *
 * 서비스는 LocalDateTime.now(clock) 을 쓰므로, 테스트용 Clock 빈을 @Primary 로 등록하면 "지금"을 마음대로 정할 수 있다.
 * 이 테스트의 "지금" = 2026-11-12 12:00 (서울)
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("쿠폰 서비스 - 시간")
class CouponServiceTimeTest {

    static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 12, 12, 0);

    @TestConfiguration
    static class FixedClockConfig {
        /**
         * TODO 7-1. 고정된 Clock 빈
         *  - Clock.fixed(NOW.atZone(SEOUL).toInstant(), SEOUL)
         *  - @Primary: 운영용 ClockConfig 의 clock 빈보다 우선 주입되도록
         */
        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.system(SEOUL); // TODO 7-1: 지금은 실제 현재 시각
        }
    }

    @Autowired
    CouponService couponService;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    IssuedCouponRepository issuedCouponRepository;

    @AfterEach
    void cleanUp() {
        issuedCouponRepository.deleteAllInBatch();
        couponRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("만료 1분 전에는 발급된다")
    void beforeExpiration() {
        couponRepository.save(Coupon.create("LAST_MINUTE", 1000, 10, NOW.plusMinutes(1)));

        CouponService.IssueResult result = couponService.issue("LAST_MINUTE", 1L);

        assertThat(result.remainingQuantity()).isEqualTo(9);
    }

    /**
     * TODO 7-2. "지금"이 만료 시각과 같으면 COUPON_EXPIRED
     *  - 만료 시각 = NOW 인 쿠폰 저장 → issue → 예외 + errorCode 검증
     *  - 실제 시계를 쓰면 이 테스트는 실행할 때마다 결과가 달라진다 (Flaky). Clock 고정이 해결책.
     */
    @Test
    @DisplayName("만료 시각 정각에는 발급되지 않는다")
    void atExpiration() {
        fail("TODO 7-2");
    }
}
