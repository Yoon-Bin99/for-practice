package com.study.shop.practice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * [실습 2-5] 자기 호출(self-invocation) 문제 (관련 문서: Spring JPA 01.스프링핵심/03.AOP와프록시.md)
 *
 * GET /practice/self-invocation 결과에서 "트랜잭션 활성화=false" 가 나오면 프록시를 거치지 않은 것이다.
 */
@Service
@RequiredArgsConstructor
public class SelfInvocationService {

    private final InnerTxService innerTxService;

    /** 트랜잭션 없이 시작하는 바깥 메서드 */
    public String outer() {
        // TODO 2-5. 같은 클래스의 inner() 대신 "다른 빈"(innerTxService)의 inner() 를 호출하도록 바꾸세요.
        //  - this.inner() 는 프록시가 아닌 원본 객체의 메서드를 직접 호출 → @Transactional 이 무시된다.
        //  - 01-java-basics 의 ex05_proxy 에서 관찰한 것과 같은 원리
        return inner(); // TODO 2-5: 자기 호출
    }

    @Transactional
    public String inner() {
        return "트랜잭션 활성화=" + TransactionSynchronizationManager.isActualTransactionActive();
    }
}
