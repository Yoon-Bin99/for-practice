package com.study.java.ex01_value;

/**
 * [실습 1] 불변 값 객체 (관련 문서: JAVA·JVM 01.자바기초심화/02.Object와불변객체.md)
 *
 * 금액을 long 으로 그대로 들고 다니면 "음수 금액", "단위 혼동" 같은 실수를 막을 수 없다.
 * 값 객체(Value Object)는 규칙(0 이상)을 생성 시점에 강제하고, 한 번 만들면 바뀌지 않는다(불변).
 *
 * 핵심 포인트
 *  - 불변: 필드 final, setter 없음, 연산은 "새 객체"를 반환
 *  - 동등성: 같은 금액이면 같은 객체로 취급 → equals 와 hashCode 를 "함께" 재정의
 */
public final class Money implements Comparable<Money> {

    public static final Money ZERO = new Money(0);

    private final long amount;

    public Money(long amount) {
        // TODO 1-1. 생성 시점 검증
        //  - 금액이 음수면 IllegalArgumentException("금액은 0 이상이어야 합니다: " + amount) 을 던진다.
        //  - 왜? 값 객체는 "잘못된 상태로는 아예 만들어질 수 없게" 하는 것이 핵심이다.

        this.amount = amount;
    }

    public long amount() {
        return amount;
    }

    public Money plus(Money other) {
        // TODO 1-2. 더하기
        //  - this 를 바꾸지 말고 "새 Money" 를 반환한다. (불변 객체의 연산 규칙)
        throw new UnsupportedOperationException("TODO 1-2 Money.plus");
    }

    public Money minus(Money other) {
        // TODO 1-3. 빼기
        //  - 결과가 음수가 되면 생성자 검증에 걸려 예외가 나야 한다. (규칙을 한곳에서만 검사)
        throw new UnsupportedOperationException("TODO 1-3 Money.minus");
    }

    public Money times(int quantity) {
        return new Money(Math.multiplyExact(amount, quantity));
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return this.amount >= other.amount;
    }

    @Override
    public boolean equals(Object o) {
        // TODO 1-4. equals 재정의
        //  - 같은 참조면 true, Money 가 아니면 false, 금액이 같으면 true
        //  - 패턴 매칭 instanceof (o instanceof Money other) 를 써 보자.
        return super.equals(o); // TODO 1-4: 지금은 동일성(==) 비교라 HashSet 중복 제거가 안 된다
    }

    @Override
    public int hashCode() {
        // TODO 1-5. hashCode 재정의
        //  - equals 가 같으면 hashCode 도 반드시 같아야 한다. (HashMap/HashSet 이 버킷을 찾는 기준)
        //  - Long.hashCode(amount) 를 활용
        return super.hashCode(); // TODO 1-5
    }

    @Override
    public int compareTo(Money o) {
        return Long.compare(amount, o.amount);
    }

    @Override
    public String toString() {
        return String.format("%,d원", amount);
    }
}
