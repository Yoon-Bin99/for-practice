package com.study.middleware.point;

import com.study.middleware.order.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [실습 K3] 멱등 컨슈머 (관련 문서: 미들웨어 02.Kafka/03.전달보장과순서.md)
 *
 * Kafka 는 At-least-once → 같은 메시지가 두 번 올 수 있다 (재시도, 리밸런싱, Outbox 재발행).
 * 같은 주문에 포인트를 두 번 주면 안 된다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final ProcessedEventRepository processedEventRepository;
    private final PointHistoryRepository pointHistoryRepository;

    /**
     * TODO K3-1. 멱등하게 포인트 적립 (주문 금액의 1%)
     *  1) processedEventRepository.existsById(event.eventId()) 면 log 남기고 return (이미 처리)
     *  2) amount 가 음수면 IllegalArgumentException → 재시도 후 DLT 로 간다 (TODO K4-1)
     *  3) PointHistory 저장
     *  4) processedEventRepository.save(new ProcessedEvent(eventId))
     *  - 포인트: 3) 과 4) 가 "같은 트랜잭션" → 함께 커밋되거나 함께 롤백된다.
     *           동시에 같은 이벤트 두 개가 처리되면 ProcessedEvent PK 중복으로 한쪽이 롤백된다.
     */
    @Transactional
    public void earn(OrderCreatedEvent event) {
        pointHistoryRepository.save(new PointHistory(event.memberId(), event.orderId(), event.amount() / 100)); // TODO K3-1: 중복 처리됨
    }
}
