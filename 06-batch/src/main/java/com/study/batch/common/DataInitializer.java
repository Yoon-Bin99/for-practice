package com.study.batch.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 실습 데이터: 회원 20,000명(약 절반은 1년 넘게 미접속, 그중 일부 VIP), 최근 3일 주문 30,000건.
 * JdbcTemplate.batchUpdate 로 넣는다 (대량 INSERT 는 JPA 보다 JDBC 배치가 훨씬 빠르다).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        Random random = new Random(42);
        LocalDateTime now = LocalDateTime.now();

        List<Object[]> members = new ArrayList<>();
        for (long id = 1; id <= 20_000; id++) {
            boolean longInactive = random.nextBoolean();
            LocalDateTime lastLogin = longInactive ? now.minusDays(400 + random.nextInt(300)) : now.minusDays(random.nextInt(300));
            String email = (id % 50 == 0 ? "member" + id + "@vip.com" : "member" + id + "@shop.com");
            members.add(new Object[]{id, "회원" + id, email, "ACTIVE", Timestamp.valueOf(lastLogin)});
        }
        jdbcTemplate.batchUpdate("insert into member(id, name, email, status, last_login_at) values (?, ?, ?, ?, ?)",
                members);

        List<Object[]> orders = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (long id = 1; id <= 30_000; id++) {
            LocalDateTime orderedAt = today.minusDays(random.nextInt(3)).atStartOfDay().plusMinutes(random.nextInt(24 * 60));
            String status = random.nextInt(10) == 0 ? "CANCELED" : "PAID";
            orders.add(new Object[]{id, 1 + random.nextInt(20_000), 1_000L * (1 + random.nextInt(100)), status,
                    Timestamp.valueOf(orderedAt)});
        }
        jdbcTemplate.batchUpdate("insert into sales_order(id, member_id, amount, status, ordered_at) values (?, ?, ?, ?, ?)",
                orders);
        log.info("✅ 실습 데이터: 회원 20,000 / 주문 30,000 (최근 3일)");
    }
}
