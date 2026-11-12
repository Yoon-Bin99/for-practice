package com.study.batch.api;

import com.study.batch.job.JobRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class BatchController {

    private final JobRunner jobRunner;
    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/dormant")
    public Map<String, Object> dormant(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baseDate) {
        return jobRunner.runDormant(baseDate);
    }

    @PostMapping("/daily-sales")
    public Map<String, Object> dailySales(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return jobRunner.runDailySales(date);
    }

    /** 결과 확인용 */
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return Map.of(
                "members", jdbcTemplate.queryForList("select status, count(*) as cnt from member group by status"),
                "vipDormant", jdbcTemplate.queryForObject(
                        "select count(*) from member where status = 'DORMANT' and email like '%@vip.com'", Long.class),
                "expectedDormant", jdbcTemplate.queryForObject(
                        "select count(*) from member where last_login_at < dateadd('YEAR', -1, current_date) and email not like '%@vip.com'",
                        Long.class),
                "dailySales", jdbcTemplate.queryForList("select * from daily_sales order by sales_date"));
    }

    @GetMapping("/executions")
    public List<Map<String, Object>> executions() {
        return jdbcTemplate.queryForList("""
                select i.job_name, e.job_execution_id, e.status, e.start_time, e.end_time
                from batch_job_execution e join batch_job_instance i on e.job_instance_id = i.job_instance_id
                order by e.job_execution_id desc
                """);
    }
}
