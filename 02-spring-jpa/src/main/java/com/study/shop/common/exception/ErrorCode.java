package com.study.shop.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C999", "서버 오류가 발생했습니다."),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "회원을 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "상품을 찾을 수 없습니다."),
    OUT_OF_STOCK(HttpStatus.CONFLICT, "P002", "재고가 부족합니다."),
    STOCK_CONFLICT(HttpStatus.CONFLICT, "P003", "동시에 많은 주문이 몰렸습니다. 잠시 후 다시 시도해 주세요."),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),
    ALREADY_SHIPPED(HttpStatus.CONFLICT, "O002", "이미 배송된 주문은 취소할 수 없습니다."),
    ALREADY_CANCELED(HttpStatus.CONFLICT, "O003", "이미 취소된 주문입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
