package com.study.coupon.service;

/** 외부 결제 대행사(PG) API. 테스트에서는 Mock 으로 대체한다. */
public interface PaymentClient {

    record PaymentRequest(String orderNo, long amount) { }

    record PaymentResult(boolean approved, String transactionId) { }

    PaymentResult approve(PaymentRequest request);
}
