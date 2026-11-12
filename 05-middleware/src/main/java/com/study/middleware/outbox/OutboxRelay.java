package com.study.middleware.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * [실습 K2] Outbox 릴레이: 1초마다 PENDING 이벤트를 Kafka 로 발행
 *
 * 서버가 여러 대면 같은 이벤트를 동시에 읽을 수 있다 → ShedLock 이나 SELECT ... FOR UPDATE SKIP LOCKED 로 막는다 (06-batch 참고).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelay {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * TODO K2-1. PENDING 이벤트 발행
     *  1) outboxRepository.findTop100ByStatusOrderByIdAsc(PENDING)
     *  2) 하나씩 kafkaTemplate.send(topic, aggregateId, payload).get(3, TimeUnit.SECONDS)  ← 결과를 "기다려야" 성공을 확신할 수 있다
     *  3) 성공 → event.markSent(),  실패(예외) → event.markRetry() 후 break (뒤 이벤트를 먼저 보내면 순서가 뒤집힌다)
     *  - 변경 감지로 상태가 저장되도록 메서드에 @Transactional 이 붙어 있다.
     *  - 생각해 보기: 발행은 성공했는데 markSent 커밋 전에 서버가 죽으면? → 재시작 후 "다시" 발행된다 (중복)
     *    → 그래서 컨슈머가 멱등해야 한다 (TODO K3-1)
     */
    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void relay() {
        // TODO K2-1: 지금은 아무것도 발행하지 않는다 → outbox 에 PENDING 만 쌓인다
    }
}
