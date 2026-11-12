package com.study.coupon.service;

import com.study.coupon.common.BusinessException;
import com.study.coupon.common.ErrorCode;
import com.study.coupon.domain.Coupon;
import com.study.coupon.domain.IssuedCoupon;
import com.study.coupon.repository.CouponRepository;
import com.study.coupon.repository.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final Clock clock;

    public record IssueResult(String code, Long memberId, int remainingQuantity) { }

    public record CouponView(String code, long discountAmount, int remainingQuantity, LocalDateTime expiresAt) { }

    /** 쿠폰 행을 비관적 락으로 잠가 수량 확인·중복 확인·발급을 한 줄로 세운다. */
    @Transactional
    public IssueResult issue(String code, Long memberId) {
        Coupon coupon = couponRepository.findByCodeForUpdate(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
        if (issuedCouponRepository.existsByCouponIdAndMemberId(coupon.getId(), memberId)) {
            throw new BusinessException(ErrorCode.ALREADY_ISSUED);
        }
        LocalDateTime now = LocalDateTime.now(clock);
        coupon.issue(now);
        issuedCouponRepository.save(new IssuedCoupon(coupon.getId(), memberId, now));
        return new IssueResult(coupon.getCode(), memberId, coupon.remainingQuantity());
    }

    public List<CouponView> findIssuable() {
        return couponRepository.findIssuable(LocalDateTime.now(clock)).stream()
                .map(c -> new CouponView(c.getCode(), c.getDiscountAmount(), c.remainingQuantity(), c.getExpiresAt()))
                .toList();
    }
}
