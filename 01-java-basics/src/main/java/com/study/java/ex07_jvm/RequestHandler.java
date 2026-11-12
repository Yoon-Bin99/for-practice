package com.study.java.ex07_jvm;

import java.util.function.Supplier;

/** 필터(또는 인터셉터)처럼 요청마다 사용자 정보를 ThreadLocal 에 넣고 비즈니스 로직을 실행한다. */
public class RequestHandler {

    /**
     * TODO 7-1. 요청 처리
     *  - userId 가 null 이 아니면 RequestContext.setUser(userId)
     *  - logic.get() 결과를 반환
     *  - 포인트: 예외가 나도 반드시 정리되도록 finally 에서 RequestContext.clear()
     *    (지금 코드는 clear 가 없어서, 로그인하지 않은 요청이 이전 요청의 사용자로 처리된다)
     */
    public <T> T handle(String userId, Supplier<T> logic) {
        if (userId != null) RequestContext.setUser(userId); return logic.get(); // TODO 7-1: 정리 누락
    }
}
