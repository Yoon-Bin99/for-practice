package com.study.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * [실습 B1~B4] 휴면 회원 전환 Job (관련 문서: 미들웨어 05.배치와스케줄링/01.SpringBatch.md)
 *
 *  대상: status = ACTIVE 이고 마지막 로그인이 기준일(baseDate)로부터 1년 이전, VIP 는 제외
 *
 *  dormantMemberJob
 *   └ dormantMemberStep (chunk = 1000)
 *        Reader    : 대상 회원을 1000명씩 읽음          (TODO B1)
 *        Processor : VIP 걸러내기, 회원 ID 로 변환       (TODO B2)
 *        Writer    : 1000건을 JDBC 배치 UPDATE 한 번에  (TODO B3)
 *        → 1000건마다 커밋. 중간에 실패해도 이미 커밋된 청크는 남고, 재실행 시 실패한 청크부터.
 */
@Configuration
@RequiredArgsConstructor
public class DormantMemberJobConfig {

    public static final String JOB_NAME = "dormantMemberJob";
    private static final int CHUNK_SIZE = 1000;

    private final DataSource dataSource;

    @Bean
    public Job dormantMemberJob(JobRepository jobRepository, Step dormantMemberStep, JobLoggingListener listener) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .listener(listener)
                .start(dormantMemberStep)
                .build();
    }

    /**
     * TODO B4. 청크 지향 Step 구성
     *  - new StepBuilder("dormantMemberStep", jobRepository)
     *        .<MemberRow, Long>chunk(CHUNK_SIZE, transactionManager)
     *        .reader(dormantCandidateReader(null)).processor(dormantProcessor()).writer(dormantWriter())
     *        .faultTolerant().skip(IllegalArgumentException.class).skipLimit(10)
     *        .build();
     *  - dormantCandidateReader(null): @StepScope 프록시라 실제 값은 실행 시점에 jobParameters 에서 주입된다.
     *  - skip: 잘못된 데이터 몇 건 때문에 2만 건 전체가 실패하지 않도록 (최대 10건까지 건너뜀)
     */
    @Bean
    public Step dormantMemberStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("dormantMemberStep", jobRepository).tasklet((contribution, ctx) -> { throw new UnsupportedOperationException("TODO B4 dormantMemberStep"); }, transactionManager).build();
    }

    /**
     * TODO B1. 대상 회원 Reader (JdbcPagingItemReader)
     *  - selectClause("select id, name, email, last_login_at"), fromClause("from member")
     *  - whereClause("where status = 'ACTIVE' and last_login_at < :threshold")
     *  - sortKeys(Map.of("id", Order.ASCENDING))   ← 정렬 키 필수
     *  - parameterValues(Map.of("threshold", baseDate.minusYears(1).atStartOfDay()))
     *  - pageSize(CHUNK_SIZE), rowMapper((rs, i) -> new MemberRow(...)), name("dormantCandidateReader")
     *  - 포인트: JdbcPagingItemReader 는 두 번째 페이지부터 "WHERE id > 마지막ID" 로 읽는다(키셋 페이징).
     *           OFFSET 방식이면 앞 페이지가 DORMANT 로 바뀌면서 조건에서 빠져 → 다음 페이지가 밀려 "절반이 누락"된다.
     */
    @Bean
    @StepScope
    public JdbcPagingItemReader<MemberRow> dormantCandidateReader(
            @Value("#{jobParameters['baseDate']}") LocalDate baseDate) {
        throw new UnsupportedOperationException("TODO B1 dormantCandidateReader");
    }

    /**
     * TODO B2. Processor
     *  - VIP(member.isVip()) 면 null 반환 → "필터링"되어 Writer 로 가지 않는다 (filterCount 로 집계)
     *  - 아니면 member.id() 반환
     */
    @Bean
    public ItemProcessor<MemberRow, Long> dormantProcessor() {
        return member -> { throw new UnsupportedOperationException("TODO B2 dormantProcessor"); };
    }

    /**
     * TODO B3. Writer: JDBC 배치 UPDATE
     *  - new JdbcBatchItemWriterBuilder<Long>()
     *        .dataSource(dataSource)
     *        .sql("update member set status = 'DORMANT', dormant_at = :now where id = :id")
     *        .itemSqlParameterSourceProvider(id -> new MapSqlParameterSource("id", id).addValue("now", LocalDateTime.now()))
     *        .build();
     *  - 포인트: 1000건을 한 건씩 UPDATE 하지 않고 JDBC 배치로 묶어 한 번에 보낸다 (JPA 변경 감지보다 훨씬 빠름)
     */
    @Bean
    @StepScope
    public JdbcBatchItemWriter<Long> dormantWriter() {
        throw new UnsupportedOperationException("TODO B3 dormantWriter");
    }
}
