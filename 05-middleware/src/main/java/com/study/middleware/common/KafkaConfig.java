package com.study.middleware.common;

import com.study.middleware.coupon.FirstComeCouponService;
import com.study.middleware.order.OrderService;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * [실습 K4] 토픽 생성과 컨슈머 에러 처리 (관련 문서: 미들웨어 02.Kafka/02.SpringKafka.md)
 */
@Configuration
public class KafkaConfig {

    /** 파티션 3개. 같은 주문 ID(키)는 같은 파티션 → 순서 보장 */
    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(OrderService.TOPIC).partitions(3).replicas(1).build();
    }

    /** DLT 는 원래 토픽과 같은 파티션 번호로 보내므로 파티션 수를 맞춘다 */
    @Bean
    public NewTopic orderCreatedDlt() {
        return TopicBuilder.name(OrderService.TOPIC + ".DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic couponIssuedTopic() {
        return TopicBuilder.name(FirstComeCouponService.TOPIC).partitions(3).replicas(1).build();
    }

    /**
     * TODO K4-1. 실패한 메시지: 1초 간격 2번 재시도 → 그래도 실패하면 "{토픽}.DLT" 로 보내고 다음 메시지 진행
     *  - var recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
     *  - var handler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2));
     *  - handler.addNotRetryableExceptions(IllegalArgumentException.class);  ← 데이터가 잘못된 경우는 재시도해도 소용없다
     *  - Boot 가 CommonErrorHandler 빈을 리스너 컨테이너에 자동 적용한다.
     *  - 포인트: 에러 핸들러가 없으면 기본 동작(9번 재시도 후 로그만 남기고 건너뜀) → 실패 메시지가 "조용히" 사라진다.
     *  - 확인: POST /api/orders?memberId=1&amount=-100 → 로그 "☠️ DLT 도착"
     */
    @Bean
    public CommonErrorHandler kafkaErrorHandler(KafkaTemplate<String, String> kafkaTemplate) {
        return new DefaultErrorHandler(); // TODO K4-1: 기본 핸들러 (DLT 없음)
    }
}
