package com.study.coupon.repository;

import com.study.coupon.domain.Coupon;
import com.study.coupon.domain.IssuedCoupon;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * [실습 5] 레포지토리 슬라이스 테스트 (관련 문서: 테스트코드 02.스프링테스트/03.레포지토리테스트.md)
 *
 *  - @DataJpaTest: JPA 관련 빈만 + 테스트마다 롤백 + 내장 DB(H2)로 교체
 *  - 테스트 대상은 "직접 작성한 쿼리"(findIssuable)와 "DB 제약"(유니크)
 *  - 저장 후 em.flush(); em.clear(); 를 하지 않으면 1차 캐시 때문에 실제 SQL·제약을 검증하지 못한다.
 */
@DataJpaTest
@DisplayName("쿠폰 레포지토리 (H2)")
class CouponRepositoryTest {

    static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 12, 12, 0);

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    IssuedCouponRepository issuedCouponRepository;

    @Autowired
    EntityManager em;

    /**
     * TODO 5-1. findIssuable: 만료 전 + 수량 남음 + 만료 임박 순
     *  - given: 아래 4개를 저장한 뒤 em.flush(); em.clear();
     *      A: 3일 뒤 만료, 수량 10              → 포함
     *      B: 1일 뒤 만료, 수량 10              → 포함 (A 보다 먼저)
     *      C: 이미 만료(NOW 정각에 만료)        → 제외  ← 경계값
     *      D: 1일 뒤 만료, 수량 1을 1개 발급함   → 제외 (소진)
     *  - when : couponRepository.findIssuable(NOW)
     *  - then : extracting(Coupon::getCode).containsExactly("B", "A")
     */
    @Test
    @DisplayName("만료되거나 소진된 쿠폰을 제외하고 만료 임박 순으로 조회한다")
    void findIssuable() {
        fail("TODO 5-1");
    }

    /**
     * TODO 5-2. 같은 회원이 같은 쿠폰을 두 번 받으면 DB 유니크 제약이 막는다
     *  - 첫 번째 IssuedCoupon 저장 + flush
     *  - 두 번째 저장은 assertThatThrownBy(() -> issuedCouponRepository.saveAndFlush(...))
     *      .isInstanceOf(DataIntegrityViolationException.class)
     *  - 포인트: flush 를 해야 INSERT 가 실제로 실행되어 제약 위반이 드러난다. (save 만 하면 통과해 버릴 수 있음)
     */
    @Test
    @DisplayName("같은 회원·같은 쿠폰 중복 발급은 유니크 제약 위반이다")
    void uniqueConstraint() {
        fail("TODO 5-2");
    }
}
