package com.study.shop.order.repository;

import com.study.shop.order.domain.OrderStatus;

import java.time.LocalDate;

/** 모든 값이 선택(null 가능). 값이 있는 조건만 WHERE 에 들어가야 한다. */
public record OrderSearchCondition(OrderStatus status, String memberName, Long minPrice,
                                   LocalDate from, LocalDate to) {
}
