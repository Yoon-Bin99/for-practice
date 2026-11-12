package com.study.java.ex05_proxy;

import java.util.concurrent.atomic.AtomicLong;

/** 프록시가 감쌀 "진짜 객체(target)". 시간 측정 코드가 하나도 없다는 점이 포인트. */
public class SimpleOrderService implements OrderService {

    private final AtomicLong sequence = new AtomicLong();

    @Override
    public long placeOrder(long memberId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("주문 금액은 0보다 커야 합니다");
        }
        sleep(30);                                // DB 저장이라고 가정
        return sequence.incrementAndGet();
    }

    @Override
    public void placeBulk(long memberId, long... amounts) {
        for (long amount : amounts) {
            placeOrder(memberId, amount);         // this.placeOrder → 프록시를 거치지 않는다!
        }
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
