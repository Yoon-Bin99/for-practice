package com.study.java.ex06_concurrency;

import java.util.concurrent.atomic.AtomicLong;

/**
 * [실습 6-A] 경쟁 조건(Race Condition)과 해결 (관련 문서: JAVA·JVM 05.동시성/02.동기화와메모리모델.md)
 *
 * count++ 는 한 줄이지만 실제로는 "읽기 → 1 더하기 → 쓰기" 세 단계라
 * 두 스레드가 동시에 읽으면 한쪽 결과가 사라진다(갱신 분실).
 */
public final class Counters {

    private Counters() {
    }

    public interface Counter {
        void increment();

        long get();
    }

    /** 문제 있는 카운터 (완성됨: 결과가 기대보다 작게 나오는 것을 관찰) */
    public static class UnsafeCounter implements Counter {
        private long count;

        @Override
        public void increment() {
            count++;
        }

        @Override
        public long get() {
            return count;
        }
    }

    /**
     * TODO 6-1. synchronized 로 해결
     *  - increment 와 get 모두 같은 락(this)을 잡아야 한다.
     *  - get 에도 필요한 이유: 락은 상호 배제뿐 아니라 "가시성(다른 스레드가 쓴 최신 값이 보임)"도 보장한다.
     */
    public static class SynchronizedCounter implements Counter {
        private long count;

        @Override
        public void increment() { count++; } // TODO 6-1: 지금은 UnsafeCounter 와 같다

        @Override
        public long get() { return count; } // TODO 6-1
    }

    /**
     * TODO 6-2. AtomicLong 으로 해결 (락 없이 CAS 연산)
     *  - incrementAndGet() 은 CPU 의 Compare-And-Swap 명령으로 원자적으로 증가시킨다.
     *  - 단일 변수 증감은 synchronized 보다 가볍다.
     */
    public static class AtomicCounter implements Counter {
        private final AtomicLong count = new AtomicLong();

        @Override
        public void increment() {
            throw new UnsupportedOperationException("TODO 6-2 AtomicCounter.increment");
        }

        @Override
        public long get() {
            throw new UnsupportedOperationException("TODO 6-2 AtomicCounter.get");
        }
    }
}
