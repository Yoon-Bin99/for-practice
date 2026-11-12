package com.study.coupon.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/** 현재 시간을 직접 쓰지 않고 Clock 빈을 주입받는다 → 테스트에서 시간을 고정할 수 있다. */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Asia/Seoul"));
    }
}
