package com.study.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * [실습 B5] 일별 매출 집계 Job: Tasklet 하나로 "INSERT ... SELECT" (관련 문서: 미들웨어 05.배치와스케줄링/01.SpringBatch.md)
 *
 * 단순 집계는 Reader/Writer 로 데이터를 애플리케이션까지 끌어오지 않고 DB 에서 한 번에 처리하는 편이 빠르다.
 */
@Configuration
@RequiredArgsConstructor
public class DailySalesJobConfig {

    public static final String JOB_NAME = "dailySalesJob";

    private final JdbcTemplate jdbcTemplate;

    @Bean
    public Job dailySalesJob(JobRepository jobRepository, Step dailySalesStep, JobLoggingListener listener) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .listener(listener)
                .start(dailySalesStep)
                .build();
    }

    @Bean
    public Step dailySalesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("dailySalesStep", jobRepository)
                .tasklet(dailySalesTasklet(null), transactionManager)
                .build();
    }

    /**
     * TODO B5. 재실행해도 결과가 같은(멱등) 집계 Tasklet
     *  1) delete from daily_sales where sales_date = ?           ← 먼저 지운다
     *  2) insert into daily_sales(sales_date, total_amount, order_count)
     *       select ?, coalesce(sum(amount), 0), count(*) from sales_order
     *       where status = 'PAID' and ordered_at >= ? and ordered_at < ?     ← [당일 00:00, 다음날 00:00)
     *  3) contribution.incrementWriteCount(삽입 행 수);  return RepeatStatus.FINISHED;
     *  - 날짜는 Date.valueOf(targetDate), 시각은 Timestamp.valueOf(targetDate.atStartOfDay()) 로 바인딩
     *  - 포인트: 같은 날짜를 수동으로 다시 돌려야 할 때(데이터 보정) 중복 집계가 생기지 않아야 한다.
     *           "<= 23:59:59" 대신 "< 다음날 00:00" 을 쓰면 밀리초 경계 누락이 없다.
     */
    @Bean
    @StepScope
    public Tasklet dailySalesTasklet(@Value("#{jobParameters['targetDate']}") LocalDate targetDate) {
        return (contribution, chunkContext) -> {
            throw new UnsupportedOperationException("TODO B5 dailySalesTasklet");
        };
    }
}
