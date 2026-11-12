package com.study.shop.order.api;

import com.study.shop.order.domain.Order;
import com.study.shop.order.domain.OrderStatus;

import java.util.List;

/** 엔티티를 그대로 반환하지 않고 API 전용 DTO 로 변환한다 (지연 로딩·순환 참조·내부 필드 노출 방지). */
public record OrderResponse(Long orderId, String memberName, OrderStatus status, long totalPrice,
                            List<Line> lines) {

    public record Line(String productName, int quantity, long orderPrice) {
    }

    /** 트랜잭션 안에서 호출해야 한다 (member, orderItems, product 는 지연 로딩) */
    public static OrderResponse from(Order order) {
        List<Line> lines = order.getOrderItems().stream()
                .map(i -> new Line(i.getProduct().getName(), i.getQuantity(), i.getOrderPrice()))
                .toList();
        return new OrderResponse(order.getId(), order.getMember().getName(), order.getStatus(),
                order.getTotalPrice(), lines);
    }
}
