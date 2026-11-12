package com.study.java.ex06_concurrency;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * [실습 6-B] 생산자-소비자: 크기 제한이 있는 버퍼 (관련 문서: CS 03.운영체제/04.동기화.md)
 *
 * java.util.concurrent.ArrayBlockingQueue 가 내부적으로 하는 일을 직접 만들어 본다.
 *  - 가득 차면 put 하는 스레드는 "자리가 날 때까지" 대기
 *  - 비어 있으면 take 하는 스레드는 "데이터가 올 때까지" 대기
 *
 * 핵심 포인트
 *  - Condition 두 개(notFull, notEmpty)로 "무엇을 기다리는지" 구분
 *  - 대기 조건은 반드시 while 로 검사 (깨어났는데 다른 스레드가 먼저 가져갔을 수 있음, spurious wakeup)
 *  - lock() 후 반드시 finally 에서 unlock()
 */
public class BoundedBuffer<T> {

    private final Deque<T> items = new ArrayDeque<>();
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }

    /**
     * TODO 6-3. 넣기
     *  1) lock.lock()
     *  2) while (가득 참) notFull.await();
     *  3) items.addLast(item)
     *  4) notEmpty.signal()  → 기다리던 소비자 하나를 깨움
     *  5) finally 에서 unlock
     */
    public void put(T item) throws InterruptedException {
        throw new UnsupportedOperationException("TODO 6-3 put");
    }

    /**
     * TODO 6-4. 꺼내기 (put 의 반대)
     *  - while (비어 있음) notEmpty.await();  → removeFirst()  → notFull.signal()
     */
    public T take() throws InterruptedException {
        throw new UnsupportedOperationException("TODO 6-4 take");
    }

    public int size() {
        lock.lock();
        try {
            return items.size();
        } finally {
            lock.unlock();
        }
    }
}
