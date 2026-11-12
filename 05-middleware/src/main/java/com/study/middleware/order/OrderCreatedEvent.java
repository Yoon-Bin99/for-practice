package com.study.middleware.order;

/** Kafka 로 보낼 이벤트. eventId 는 컨슈머의 멱등 처리 키 */
public record OrderCreatedEvent(String eventId, Long orderId, Long memberId, long amount) {
}
