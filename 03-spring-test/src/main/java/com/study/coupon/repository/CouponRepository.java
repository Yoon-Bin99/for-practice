package com.study.coupon.repository;

import com.study.coupon.domain.Coupon;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.code = :code")
    Optional<Coupon> findByCodeForUpdate(@Param("code") String code);

    /** 지금 발급 가능한 쿠폰: 만료 전 + 수량 남음, 만료 임박 순 (직접 작성한 쿼리 → 테스트 대상) */
    @Query("""
           select c from Coupon c
           where c.expiresAt > :now and c.issuedQuantity < c.totalQuantity
           order by c.expiresAt asc
           """)
    List<Coupon> findIssuable(@Param("now") LocalDateTime now);
}
