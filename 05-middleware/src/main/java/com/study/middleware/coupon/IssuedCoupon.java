package com.study.middleware.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Redis 가 앞단에서 걸러 주지만, DB 유니크 제약이 최종 방어선 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"couponId", "memberId"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssuedCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long couponId;

    private Long memberId;

    public IssuedCoupon(Long couponId, Long memberId) {
        this.couponId = couponId;
        this.memberId = memberId;
    }
}
