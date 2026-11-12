package com.study.shop.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * [실습 2-3] 트랜잭션 전파 (관련 문서: Spring JPA 01.스프링핵심/06.트랜잭션.md)
 *
 * 주문 트랜잭션 안에서 호출되지만, 주문이 롤백돼도 감사 로그는 커밋되어야 한다.
 */
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * TODO 2-3. 전파 속성 선택
     *  - 기본값 REQUIRED 면 바깥(주문) 트랜잭션에 "참여"해서 함께 롤백된다.
     *  - REQUIRES_NEW 로 바꾸면 바깥 트랜잭션을 잠시 보류하고 "새 트랜잭션(새 커넥션)"으로 커밋된다.
     *  - 확인: 재고보다 많이 주문(실패)한 뒤 GET /practice/audit-logs 에 기록이 남는지 본다.
     *  - 이 메서드가 OrderService 와 "다른 빈"에 있는 이유도 생각해 보자. (같은 클래스면 자기 호출 → 프록시 미적용)
     */
    @Transactional // TODO 2-3: 지금은 REQUIRED → 주문과 함께 롤백된다
    public void record(String action) {
        auditLogRepository.save(new AuditLog(action));
    }
}
