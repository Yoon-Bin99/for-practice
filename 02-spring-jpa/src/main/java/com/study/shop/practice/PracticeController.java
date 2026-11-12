package com.study.shop.practice;

import com.study.shop.order.service.AuditLog;
import com.study.shop.order.service.AuditLogRepository;
import com.study.shop.stock.StockConcurrencySimulator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** 실습 결과를 확인하기 위한 엔드포인트 모음. http/practice.http 에서 바로 호출할 수 있다. */
@RestController
@RequestMapping("/practice")
@RequiredArgsConstructor
public class PracticeController {

    private final StockConcurrencySimulator stockSimulator;
    private final NPlusOneInspector nPlusOneInspector;
    private final SelfInvocationService selfInvocationService;
    private final AuditLogRepository auditLogRepository;

    @PostMapping("/stock/{strategy}")
    public Map<String, Object> stock(@PathVariable String strategy,
                                     @RequestParam(defaultValue = "100") int requests,
                                     @RequestParam(defaultValue = "100") int stock) {
        return stockSimulator.run(strategy, requests, stock);
    }

    @GetMapping("/n-plus-one")
    public Map<String, Object> nPlusOne(@RequestParam(defaultValue = "lazy") String mode) {
        return nPlusOneInspector.inspect(mode);
    }

    @GetMapping("/self-invocation")
    public Map<String, String> selfInvocation() {
        return Map.of("result", selfInvocationService.outer());
    }

    @GetMapping("/audit-logs")
    public List<AuditLog> auditLogs() {
        return auditLogRepository.findAll();
    }
}
