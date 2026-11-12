package com.study.coupon.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/** 애플리케이션 실행용 가짜 구현 (실제 PG·문자 연동 대신 로그만 남김) */
@Slf4j
@Configuration
public class LoggingClients {

    @Bean
    public PaymentClient paymentClient() {
        return request -> {
            log.info("[PG] 결제 승인 요청 orderNo={} amount={}", request.orderNo(), request.amount());
            return new PaymentClient.PaymentResult(true, "TX-" + UUID.randomUUID());
        };
    }

    @Bean
    public NotificationSender notificationSender() {
        return (memberId, message) -> log.info("[알림] memberId={} {}", memberId, message);
    }
}
