package com.study.java.ex07_jvm;

/**
 * [실습 7-A] ThreadLocal 정리 누락 (관련 문서: JAVA·JVM 05.동시성/04.동시성유틸과ThreadLocal.md)
 *
 * 스프링의 SecurityContextHolder, 로그의 MDC, 트랜잭션 동기화가 모두 ThreadLocal 을 쓴다.
 * 톰캣처럼 "스레드를 재사용하는 풀"에서 값을 지우지 않으면
 *  - 다음 요청이 이전 사용자의 값을 보게 되는 보안 사고
 *  - 큰 객체가 스레드에 계속 붙어 있는 메모리 누수
 * 가 생긴다.
 */
public final class RequestContext {

    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    private RequestContext() {
    }

    public static void setUser(String userId) {
        CURRENT_USER.set(userId);
    }

    public static String currentUser() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
