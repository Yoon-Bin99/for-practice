package com.study.coupon.repository;

import com.study.coupon.domain.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {

    boolean existsByCouponIdAndMemberId(Long couponId, Long memberId);

    long countByCouponId(Long couponId);
}
