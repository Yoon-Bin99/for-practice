package com.study.coupon.service;

import com.study.coupon.common.BusinessException;
import com.study.coupon.common.ErrorCode;
import com.study.coupon.domain.DiscountPolicy;
import com.study.coupon.domain.Grade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 결제: 할인 계산 → PG 승인 → (승인 시에만) 알림
 * 협력 객체가 3개 → Mockito 로 외부(PaymentClient, NotificationSender)만 가짜로 바꿔 테스트한다.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final DiscountPolicy discountPolicy;
    private final PaymentClient paymentClient;
    private final NotificationSender notificationSender;

    public record PayCommand(String orderNo, Long memberId, long price, Grade grade, long couponAmount) { }

    public record PaymentReceipt(String orderNo, long paidAmount, String transactionId) { }

    public PaymentReceipt pay(PayCommand cmd) {
        long discount = discountPolicy.discount(cmd.price(), cmd.grade(), cmd.couponAmount());
        long amount = cmd.price() - discount;

        PaymentClient.PaymentResult result =
                paymentClient.approve(new PaymentClient.PaymentRequest(cmd.orderNo(), amount));
        if (!result.approved()) {
            throw new BusinessException(ErrorCode.PAYMENT_FAILED);
        }

        notificationSender.send(cmd.memberId(), "결제 완료: " + amount + "원");
        return new PaymentReceipt(cmd.orderNo(), amount, result.transactionId());
    }
}
