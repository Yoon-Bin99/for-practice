package com.study.coupon.common;

import com.study.coupon.domain.Coupon;
import com.study.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

/** 로컬 실행용 데이터. 테스트는 @ActiveProfiles("test") 로 실행해 이 데이터가 끼어들지 않게 한다. */
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CouponRepository couponRepository;
    private final Clock clock;

    @Override
    public void run(ApplicationArguments args) {
        LocalDateTime now = LocalDateTime.now(clock);
        couponRepository.save(Coupon.create("WELCOME", 3_000, 100, now.plusDays(30)));
        couponRepository.save(Coupon.create("FLASH", 5_000, 3, now.plusHours(1)));
        couponRepository.save(Coupon.create("EXPIRED", 1_000, 100, now.minusDays(1)));
    }
}
