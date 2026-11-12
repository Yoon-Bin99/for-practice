package com.study.middleware.coupon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/** 발급 이벤트 → DB 저장 (완성됨). 중복 메시지는 유니크 제약 위반으로 걸러지고 무시된다. */
@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssuedConsumer {

    private final IssuedCouponRepository issuedCouponRepository;

    @KafkaListener(topics = FirstComeCouponService.TOPIC, groupId = "coupon-writer")
    public void onIssued(String message) {
        String[] parts = message.split(":");
        try {
            issuedCouponRepository.save(new IssuedCoupon(Long.valueOf(parts[0]), Long.valueOf(parts[1])));
        } catch (DataIntegrityViolationException e) {
            log.info("이미 저장된 발급 이벤트 무시: {}", message);
        }
    }
}
