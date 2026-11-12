package com.study.middleware.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.middleware.outbox.OutboxEvent;
import com.study.middleware.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * [실습 K1] Transactional Outbox (관련 문서: 미들웨어 02.Kafka/03.전달보장과순서.md)
 *
 * ❌ 주문 저장 + kafkaTemplate.send() 를 같이 하면
 *    - 발행 성공 → DB 롤백: 존재하지 않는 주문의 이벤트가 나간다
 *    - DB 커밋 → 발행 실패(서버 다운): 이벤트가 영영 사라진다
 * ✅ 이벤트를 "같은 DB 트랜잭션"으로 outbox 테이블에 저장하고, 발행은 OutboxRelay 가 따로 한다.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    public static final String TOPIC = "order-created";

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    /**
     * TODO K1-1. 주문 저장 + Outbox 저장을 하나의 트랜잭션으로
     *  1) Order 저장
     *  2) new OrderCreatedEvent(UUID 문자열, orderId, memberId, amount) 를 JSON 으로 변환
     *  3) outboxRepository.save(new OutboxEvent(eventId, TOPIC, 주문ID 문자열(파티션 키), json))
     *  4) 주문 ID 반환
     *  - 여기서는 Kafka 를 "전혀" 호출하지 않는다는 점이 핵심. Kafka 가 꺼져 있어도 주문은 성공한다.
     *  - amount 가 음수면 컨슈머에서 처리 실패 → DLT 실습에 사용 (검증하지 않고 그대로 저장)
     */
    @Transactional
    public Long placeOrder(Long memberId, long amount) {
        throw new UnsupportedOperationException("TODO K1-1 OrderService.placeOrder");
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
