package com.study.coupon.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "CP001", "쿠폰을 찾을 수 없습니다."),
    COUPON_EXPIRED(HttpStatus.CONFLICT, "CP002", "만료된 쿠폰입니다."),
    COUPON_SOLD_OUT(HttpStatus.CONFLICT, "CP003", "쿠폰이 모두 소진되었습니다."),
    ALREADY_ISSUED(HttpStatus.CONFLICT, "CP004", "이미 발급받은 쿠폰입니다."),
    PAYMENT_FAILED(HttpStatus.BAD_GATEWAY, "PY001", "결제에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
