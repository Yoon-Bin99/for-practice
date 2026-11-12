package com.study.middleware.coupon;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {

    long countByCouponId(Long couponId);
}
