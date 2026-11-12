package com.study.java.ex05_proxy;

import com.study.java.common.Check;

public class Ex05Main {

    public static void main(String[] args) {
        Check.run("5-1, 5-2 동적 프록시", () -> {
            TimingInvocationHandler handler = new TimingInvocationHandler(new SimpleOrderService());
            OrderService proxy = TimingInvocationHandler.createProxy(OrderService.class, handler);

            System.out.println("프록시 클래스 이름: " + proxy.getClass().getName());   // jdk.proxy1.$Proxy0 같은 이름
            long id = proxy.placeOrder(1L, 10_000);
            Check.equal("원본 결과가 그대로 반환", 1L, id);

            boolean sameException = false;
            try {
                proxy.placeOrder(1L, -1);
            } catch (IllegalArgumentException e) {
                sameException = true;    // InvocationTargetException 이 아니라 원본 예외여야 한다
            }
            Check.isTrue("원본 예외가 그대로 전달된다", sameException);
            Check.equal("측정 로그 수 (성공 1 + 실패 1)", 2, handler.logs().size());
            System.out.println("    로그: " + handler.logs());

            // 자기 호출 관찰: placeBulk 안에서 placeOrder 를 3번 호출하지만…
            handler.logs().clear();
            proxy.placeBulk(1L, 1000, 2000, 3000);
            Check.equal("자기 호출은 프록시를 거치지 않아 placeBulk 1건만 기록", 1, handler.logs().size());
            System.out.println("    → 스프링에서 같은 클래스 안의 @Transactional 메서드 호출이 무시되는 이유가 바로 이것");
        });
    }
}
