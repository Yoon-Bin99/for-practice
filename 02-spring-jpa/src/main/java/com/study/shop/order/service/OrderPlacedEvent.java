package com.study.shop.order.service;

public record OrderPlacedEvent(Long orderId, Long memberId, long totalPrice) {
}
