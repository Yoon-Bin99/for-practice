package com.study.java.ex01_value;

import com.study.java.common.Check;

import java.util.HashSet;
import java.util.Set;

public class Ex01Main {

    public static void main(String[] args) {
        Check.run("1-1 생성 검증", () -> {
            boolean thrown = false;
            try {
                new Money(-1);
            } catch (IllegalArgumentException e) {
                thrown = true;
            }
            Check.isTrue("음수 금액은 만들 수 없다", thrown);
        });

        Check.run("1-2, 1-3 연산은 새 객체를 반환", () -> {
            Money a = new Money(1000);
            Money b = a.plus(new Money(500));
            Check.equal("a 는 그대로", 1000L, a.amount());
            Check.equal("a + 500", 1500L, b.amount());
            Check.equal("1500 - 300", 1200L, b.minus(new Money(300)).amount());
            boolean thrown = false;
            try {
                a.minus(new Money(2000));
            } catch (IllegalArgumentException e) {
                thrown = true;
            }
            Check.isTrue("결과가 음수면 예외", thrown);
        });

        Check.run("1-4, 1-5 equals/hashCode", () -> {
            Check.isTrue("같은 금액이면 equals true", new Money(1000).equals(new Money(1000)));
            Set<Money> set = new HashSet<>();
            set.add(new Money(1000));
            set.add(new Money(1000));
            Check.equal("HashSet 에 같은 금액 2번 → 크기", 1, set.size());
        });
    }
}
