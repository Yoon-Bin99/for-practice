package com.study.batch.schedule;

import com.study.batch.job.JobRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * [실습 B6-2] 서버 두 대를 흉내: 같은 스케줄이 "서버 A", "서버 B" 에서 동시에 돈다.
 *
 * 실습을 위해 운영 주기(매일 새벽) 대신 30초마다 실행한다. 로그에서 매 주기 한쪽만 실행되는지 확인하자.
 * 같은 날짜로 두 번째부터는 ALREADY_COMPLETED 가 나오는데, 이것도 Spring Batch 의 중복 실행 방지 기능이다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailySalesScheduler {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final JobRunner jobRunner;

    /**
     * TODO B6-2. 두 메서드에 "같은 이름"의 @SchedulerLock 을 붙이세요.
     *  - @SchedulerLock(name = "dailySalesJob", lockAtMostFor = "5m", lockAtLeastFor = "20s")
     *  - lockAtMostFor : 실행 서버가 죽어도 이 시간이 지나면 락이 풀린다 (작업 최대 시간보다 길게)
     *  - lockAtLeastFor: 작업이 0.1초 만에 끝나도 20초는 락을 유지 → 서버 간 시계 차이로 다른 서버가 곧바로 또 실행하는 것을 방지
     *  - 운영 cron 예: @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")  (스프링 cron 은 초부터 6자리)
     */
    @Scheduled(fixedRate = 30_000, initialDelay = 10_000)
    // TODO B6-2
    public void serverA() {
        run("서버 A");
    }

    @Scheduled(fixedRate = 30_000, initialDelay = 10_000)
    // TODO B6-2
    public void serverB() {
        run("서버 B");
    }

    private void run(String server) {
        LocalDate yesterday = LocalDate.now(SEOUL).minusDays(1);
        log.info("⏰ [{}] 일별 매출 집계 실행 date={}", server, yesterday);
        log.info("⏰ [{}] 결과 {}", server, jobRunner.runDailySales(yesterday));
    }
}
