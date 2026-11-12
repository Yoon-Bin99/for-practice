package com.study.batch.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.time.Duration;

/** Job 시작·종료 로그 (실무에서는 실패 시 Slack 알림 등) */
@Slf4j
@Component
public class JobLoggingListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("▶ {} 시작 params={}", jobExecution.getJobInstance().getJobName(), jobExecution.getJobParameters());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long ms = Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime()).toMillis();
        log.info("■ {} 종료 status={} {}ms", jobExecution.getJobInstance().getJobName(), jobExecution.getStatus(), ms);
        for (StepExecution step : jobExecution.getStepExecutions()) {
            log.info("   step={} read={} filter={} write={} skip={} commit={}", step.getStepName(), step.getReadCount(),
                    step.getFilterCount(), step.getWriteCount(), step.getSkipCount(), step.getCommitCount());
        }
    }
}
