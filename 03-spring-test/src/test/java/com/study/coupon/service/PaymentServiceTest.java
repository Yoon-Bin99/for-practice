package com.study.coupon.service;

import com.study.coupon.common.BusinessException;
import com.study.coupon.common.ErrorCode;
import com.study.coupon.domain.DiscountPolicy;
import com.study.coupon.domain.Grade;
import com.study.coupon.service.PaymentClient.PaymentRequest;
import com.study.coupon.service.PaymentClient.PaymentResult;
import com.study.coupon.service.PaymentService.PayCommand;
import com.study.coupon.service.PaymentService.PaymentReceipt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

/**
 * [실습 3] Mockito 로 협력 객체 대체 (관련 문서: 테스트코드 01.테스트기초/04.테스트더블과Mockito.md)
 *
 *  - 외부 시스템(PaymentClient, NotificationSender) 만 Mock
 *  - 할인 정책(DiscountPolicy)은 진짜 객체 사용 → "도메인은 실제 객체, 외부만 가짜"
 *  - 결과 값은 상태 검증(assertThat), 알림처럼 반환값 없는 부수 효과는 행위 검증(then(...).should())
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("결제 서비스")
class PaymentServiceTest {

    @Mock
    PaymentClient paymentClient;

    @Mock
    NotificationSender notificationSender;

    @Captor
    ArgumentCaptor<PaymentRequest> requestCaptor;

    PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(new DiscountPolicy(), paymentClient, notificationSender);
    }

    @Test
    @DisplayName("승인되면 영수증을 반환하고 알림을 보낸다 (예시: 완성된 테스트)")
    void approved() {
        // given
        given(paymentClient.approve(any(PaymentRequest.class))).willReturn(new PaymentResult(true, "TX-1"));
        PayCommand cmd = new PayCommand("ORD-1", 7L, 20_000, Grade.BRONZE, 0);

        // when
        PaymentReceipt receipt = paymentService.pay(cmd);

        // then
        assertThat(receipt.paidAmount()).isEqualTo(20_000);
        assertThat(receipt.transactionId()).isEqualTo("TX-1");
        then(notificationSender).should().send(7L, "결제 완료: 20000원");
    }

    /**
     * TODO 3-1. 승인 거절이면 PAYMENT_FAILED 예외 + 알림을 보내지 않는다
     *  - given: approve 가 new PaymentResult(false, null) 반환
     *  - then : 예외 검증 + then(notificationSender).should(never()).send(anyLong(), anyString())
     *  - 포인트: "일어나지 않아야 하는 일"을 검증하는 것도 행위 검증의 중요한 용도다.
     */
    @Test
    @DisplayName("승인 거절이면 예외가 나고 알림을 보내지 않는다")
    void rejected() {
        fail("TODO 3-1");
    }

    /**
     * TODO 3-2. PG 에 "할인된 금액"이 전달되는지 ArgumentCaptor 로 확인
     *  - GOLD 등급, 가격 30,000원, 쿠폰 2,000원 → 할인 5,000원 → PG 요청 금액 25,000원
     *  - then(paymentClient).should().approve(requestCaptor.capture());
     *    requestCaptor.getValue().amount() == 25_000, orderNo 도 확인
     *  - 포인트: 반환값만으로는 알 수 없는 "외부로 보낸 값"을 검증할 때 Captor 를 쓴다.
     */
    @Test
    @DisplayName("PG 에는 할인이 적용된 금액으로 승인을 요청한다")
    void sendsDiscountedAmount() {
        fail("TODO 3-2");
    }

    /**
     * TODO 3-3. PG 가 예외(타임아웃 등)를 던지면 그대로 전파되고 알림은 보내지 않는다
     *  - given(paymentClient.approve(any())).willThrow(new IllegalStateException("PG timeout"))
     */
    @Test
    @DisplayName("PG 호출이 실패하면 예외가 전파되고 알림을 보내지 않는다")
    void pgFailure() {
        fail("TODO 3-3");
    }
}
