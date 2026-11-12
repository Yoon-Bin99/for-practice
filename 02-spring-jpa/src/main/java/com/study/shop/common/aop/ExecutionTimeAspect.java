package com.study.shop.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * [실습 6] @Aspect 로 실행 시간 측정 (관련 문서: Spring JPA 01.스프링핵심/03.AOP와프록시.md)
 *
 * 01-java-basics 의 동적 프록시 실습을 스프링이 대신 해 주는 버전이다.
 * Pointcut "@annotation(...)" → @LogExecutionTime 이 붙은 메서드만 프록시가 가로챈다.
 */
@Slf4j
@Aspect
@Component
public class ExecutionTimeAspect {

    /**
     * TODO 6-1. @Around 어드바이스 구현
     *  1) System.nanoTime() 으로 시작 시간
     *  2) joinPoint.proceed() 로 원본 메서드 실행 → 결과 반환 (proceed 를 빼먹으면 원본이 실행되지 않는다!)
     *  3) finally 에서 log.info("{} {}ms", joinPoint.getSignature().toShortString(), 걸린ms)
     */
    @Around("@annotation(com.study.shop.common.aop.LogExecutionTime)")
    public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed(); // TODO 6-1: 지금은 시간 측정 없이 원본만 실행
    }
}
