package com.study.coupon.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 1인 1매: (coupon_id, member_id) 유니크 제약이 최종 방어선 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_issued_coupon", columnNames = {"coupon_id", "member_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssuedCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    private LocalDateTime issuedAt;

    public IssuedCoupon(Long couponId, Long memberId, LocalDateTime issuedAt) {
        this.couponId = couponId;
        this.memberId = memberId;
        this.issuedAt = issuedAt;
    }
}
