package com.study.shop.order.repository;

import com.study.shop.order.domain.OrderStatus;

/** 목록 화면 전용 DTO (필요한 컬럼만 조회) */
public record OrderSummary(Long orderId, String memberName, OrderStatus status, long totalPrice) {
}
