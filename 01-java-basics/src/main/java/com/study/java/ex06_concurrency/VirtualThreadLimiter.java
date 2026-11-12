package com.study.java.ex06_concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * [실습 6-D] 가상 스레드 + 동시성 제한 (관련 문서: JAVA·JVM 05.동시성/05.가상스레드.md)
 *
 * 가상 스레드는 수만 개를 만들어도 가볍지만, 그 뒤의 DB 커넥션·외부 API 는 여전히 한정 자원이다.
 * "스레드는 마음껏, 외부 자원 접근은 세마포어로 제한" 이 가상 스레드 시대의 기본 패턴이다.
 */
public class VirtualThreadLimiter {

    private final Semaphore permits;
    private final AtomicInteger current = new AtomicInteger();
    private final AtomicInteger maxObserved = new AtomicInteger();

    public VirtualThreadLimiter(int maxConcurrentCalls) {
        this.permits = new Semaphore(maxConcurrentCalls);
    }

    /**
     * TODO 6-6. 작업 taskCount 개를 가상 스레드로 실행하되, callDatabase() 는 동시에 최대 N개만 실행되게 하라.
     *  1) try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) { ... }
     *     → try 블록을 벗어날 때 모든 작업이 끝날 때까지 기다려 준다 (close() = 종료 대기)
     *  2) 각 작업: permits.acquire() → try { callDatabase(); } finally { permits.release(); }
     *  - 포인트: release 는 반드시 finally (예외가 나도 허가증을 돌려줘야 다른 작업이 진행된다)
     *  - acquire 의 InterruptedException 은 Thread.currentThread().interrupt() 로 처리
     */
    public void runAll(int taskCount) {
        throw new UnsupportedOperationException("TODO 6-6 runAll");
    }

    /** 100ms 걸리는 DB 호출이라고 가정 (완성됨) */
    void callDatabase() {
        int now = current.incrementAndGet();
        maxObserved.accumulateAndGet(now, Math::max);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            current.decrementAndGet();
        }
    }

    public int maxObservedConcurrency() {
        return maxObserved.get();
    }
}
