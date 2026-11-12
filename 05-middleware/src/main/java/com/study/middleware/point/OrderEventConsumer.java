package com.study.middleware.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.middleware.order.OrderCreatedEvent;
import com.study.middleware.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/** order-created 소비 (완성됨). 문자열 JSON 을 직접 변환해 Jackson 버전·타입 헤더 문제를 피한다. */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;
    private final PointService pointService;

    @KafkaListener(topics = OrderService.TOPIC, groupId = "point-service")
    public void onOrderCreated(ConsumerRecord<String, String> record) throws Exception {
        log.debug("수신 partition={} offset={} key={}", record.partition(), record.offset(), record.key());
        OrderCreatedEvent event = objectMapper.readValue(record.value(), OrderCreatedEvent.class);
        pointService.earn(event);
    }

    /** DLT 로 간 메시지 확인용 (실무에서는 알림 + 원인 해결 후 재처리) */
    @KafkaListener(topics = OrderService.TOPIC + ".DLT", groupId = "point-service-dlt")
    public void onDeadLetter(ConsumerRecord<String, String> record) {
        log.error("☠️ DLT 도착 key={} value={}", record.key(), record.value());
    }
}
