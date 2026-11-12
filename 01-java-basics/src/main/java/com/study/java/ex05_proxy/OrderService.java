package com.study.java.ex05_proxy;

public interface OrderService {

    long placeOrder(long memberId, long amount);

    /** 내부에서 placeOrder 를 여러 번 호출 → 자기 호출(self-invocation) 관찰용 */
    void placeBulk(long memberId, long... amounts);
}
