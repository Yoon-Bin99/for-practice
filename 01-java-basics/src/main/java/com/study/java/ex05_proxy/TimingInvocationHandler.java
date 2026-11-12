package com.study.java.ex05_proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * [실습 5] JDK 동적 프록시로 "실행 시간 측정 AOP" 만들기
 * (관련 문서: JAVA·JVM 01.자바기초심화/05.enum과애노테이션과리플렉션.md, Spring JPA 01.스프링핵심/03.AOP와프록시.md)
 *
 * 스프링의 @Transactional, @Cacheable 이 정확히 이 구조다.
 *
 *   호출자 ──▶ [프록시] ──invoke()──▶ ① 부가 기능(시작 시간) ② method.invoke(target) ③ 부가 기능(종료 로그)
 *
 * 핵심 포인트
 *  - 프록시는 인터페이스를 구현한 "가짜 객체"이고, 모든 호출이 invoke() 로 모인다.
 *  - 원본 메서드는 리플렉션 method.invoke(target, args) 로 호출한다.
 *  - 원본이 던진 예외는 InvocationTargetException 으로 "포장"되어 오므로 꺼내서 다시 던져야 한다.
 */
public class TimingInvocationHandler implements InvocationHandler {

    private final Object target;
    private final List<String> logs = new ArrayList<>();

    public TimingInvocationHandler(Object target) {
        this.target = target;
    }

    /**
     * TODO 5-1. 프록시의 모든 호출이 들어오는 곳
     *  1) 시작 시각 기록 (System.nanoTime())
     *  2) method.invoke(target, args) 로 원본 호출, 결과 반환
     *  3) InvocationTargetException 이면 e.getCause() 를 다시 던진다 (원본 예외 그대로 전달)
     *  4) finally 에서 logs.add(method.getName() + " " + 걸린ms + "ms") 기록 → 성공·실패 모두 측정
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        throw new UnsupportedOperationException("TODO 5-1 invoke");
    }

    public List<String> logs() {
        return logs;
    }

    /**
     * TODO 5-2. 프록시 객체 생성
     *  - Proxy.newProxyInstance(클래스 로더, 구현할 인터페이스 배열, InvocationHandler)
     *  - 클래스 로더: type.getClassLoader(), 인터페이스: new Class<?>[]{type}
     *  - 반환 타입이 Object 이므로 type.cast(...) 로 캐스팅
     */
    public static <T> T createProxy(Class<T> type, InvocationHandler handler) {
        throw new UnsupportedOperationException("TODO 5-2 createProxy");
    }
}
