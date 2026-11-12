package com.study.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Job 실행 도우미 (완성됨)
 *
 * 같은 Job + 같은 식별 파라미터 = 같은 JobInstance
 *  - 이미 COMPLETED 면 JobInstanceAlreadyCompleteException → 같은 날짜 정산을 실수로 두 번 돌리지 않게 막아 준다
 *  - FAILED 면 다시 실행할 때 실패한 지점부터 재시작
 */
@Component
@RequiredArgsConstructor
public class JobRunner {

    private final JobLauncher jobLauncher;
    private final Job dormantMemberJob;     // Job 빈이 2개 → 파라미터 이름(=빈 이름)으로 구분해 주입
    private final Job dailySalesJob;

    public Map<String, Object> runDormant(LocalDate baseDate) {
        JobParameters params = new JobParametersBuilder()
                .addLocalDate("baseDate", baseDate)
                .toJobParameters();
        return run(dormantMemberJob, params);
    }

    public Map<String, Object> runDailySales(LocalDate targetDate) {
        JobParameters params = new JobParametersBuilder()
                .addLocalDate("targetDate", targetDate)
                .toJobParameters();
        return run(dailySalesJob, params);
    }

    private Map<String, Object> run(Job job, JobParameters params) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("job", job.getName());
        result.put("params", params.toString());
        try {
            JobExecution execution = jobLauncher.run(job, params);
            result.put("status", execution.getStatus().name());
            execution.getStepExecutions().forEach(step -> result.put(step.getStepName(), Map.of(
                    "read", step.getReadCount(), "filter", step.getFilterCount(),
                    "write", step.getWriteCount(), "skip", step.getSkipCount(), "commit", step.getCommitCount())));
            if (execution.getStatus() == BatchStatus.FAILED) {
                result.put("errors", execution.getAllFailureExceptions().stream().map(Throwable::getMessage).toList());
            }
        } catch (JobInstanceAlreadyCompleteException e) {
            result.put("status", "ALREADY_COMPLETED");
            result.put("message", "같은 파라미터로 이미 성공한 Job 입니다. 날짜를 바꾸거나 일부러 재실행하려면 파라미터를 추가하세요.");
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", e.getMessage());
        }
        return result;
    }
}
