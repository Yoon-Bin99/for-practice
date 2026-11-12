package com.study.coupon.concurrency;

import com.study.coupon.domain.Coupon;
import com.study.coupon.repository.CouponRepository;
import com.study.coupon.repository.IssuedCouponRepository;
import com.study.coupon.service.CouponService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * [실습 6] 동시성 테스트 (관련 문서: 테스트코드 03.테스트설계/04.동시성과시간테스트.md)
 *
 * 주의: 이 클래스에는 @Transactional 을 붙이지 않는다.
 *  - 각 스레드는 별도 트랜잭션으로 실행되어야 하고, 테스트 트랜잭션 안에 저장한 쿠폰은 다른 스레드에서 보이지 않는다.
 *  - 대신 @AfterEach 에서 데이터를 직접 지운다.
 *
 * 실험: CouponService.issue 의 findByCodeForUpdate 를 findByCode 로 바꾸고 다시 실행해 보자 → 초과 발급 재현
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("쿠폰 동시 발급")
class CouponIssueConcurrencyTest {

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

    /**
     * TODO 6-1. 수량 30장 쿠폰에 서로 다른 회원 100명이 동시에 요청 → 정확히 30장만 발급
     *  1) given: Coupon.create("RACE", 1000, 30, 내일) 저장
     *  2) ExecutorService(32 스레드) + CountDownLatch start(1), done(100) + AtomicInteger success/fail
     *  3) 각 작업: start.await() → couponService.issue("RACE", 회원ID) → success++ / 예외면 fail++ → finally done.countDown()
     *  4) 모든 작업 제출 후 start.countDown() 으로 "동시에 출발", done.await(10, SECONDS)
     *  5) then: success 30, fail 70, issuedCouponRepository.count() 30, 쿠폰 remainingQuantity 0
     *  - 포인트: 출발 신호(start)로 요청을 최대한 겹치게 해야 경쟁 조건이 드러난다.
     */
    @Test
    @DisplayName("100명이 동시에 요청해도 30장만 발급된다")
    void onlyTotalQuantityIssued() throws InterruptedException {
        fail("TODO 6-1");
    }

    /**
     * TODO 6-2. 같은 회원이 동시에 10번 요청(따닥) → 1장만 발급
     *  - 6-1 과 같은 구조, 회원 ID 만 모두 같게
     *  - then: success 1, issuedCouponRepository.count() 1
     */
    @Test
    @DisplayName("같은 회원이 동시에 여러 번 요청해도 1장만 발급된다")
    void onePerMember() throws InterruptedException {
        fail("TODO 6-2");
    }
}
