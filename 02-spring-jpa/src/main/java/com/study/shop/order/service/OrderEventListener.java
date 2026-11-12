package com.study.shop.order.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * [실습 2-4] 커밋 후에만 실행해야 하는 작업 (메일, 알림, 메시지 발행)
 */
@Slf4j
@Component
public class OrderEventListener {

    /**
     * TODO 2-4. 커밋 성공 후에만 알림 보내기
     *  - @EventListener 는 publishEvent 를 호출한 "즉시" 실행된다 → 주문이 롤백돼도 알림이 나간다.
     *  - @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) 으로 바꾸면 커밋된 경우에만 실행된다.
     *  - 확인: 정상 주문 → 로그 "주문 완료 알림" 출력 / 재고 부족 주문 → 출력되지 않아야 함
     */
    @EventListener // TODO 2-4
    public void sendNotification(OrderPlacedEvent event) {
        log.info("📨 주문 완료 알림 발송 orderId={} memberId={} amount={}",
                event.orderId(), event.memberId(), event.totalPrice());
    }
}
