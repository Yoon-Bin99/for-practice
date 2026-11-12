package com.study.batch.schedule;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

/**
 * [실습 B6-1] ShedLock 설정 (관련 문서: 미들웨어 05.배치와스케줄링/02.스케줄링과분산환경.md)
 *
 * 서버가 3대면 @Scheduled 도 3번 실행된다. 공유 DB 의 shedlock 테이블에 락을 걸어 한 서버만 실행하게 한다.
 */
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class ShedLockConfig {

    /**
     * TODO B6-1. JDBC 기반 LockProvider
     *  - new JdbcTemplateLockProvider(
     *        JdbcTemplateLockProvider.Configuration.builder()
     *            .withJdbcTemplate(new JdbcTemplate(dataSource))
     *            .usingDbTime()          ← 서버마다 시계가 조금씩 달라도 DB 시간 기준으로 판단
     *            .build())
     */
    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return lockConfiguration -> java.util.Optional.empty(); // TODO B6-1: 지금은 "항상 락 획득 실패" 하는 가짜 → 스케줄 작업이 실행되지 않는다
    }
}
