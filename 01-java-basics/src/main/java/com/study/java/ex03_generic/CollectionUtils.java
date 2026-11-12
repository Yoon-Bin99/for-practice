package com.study.java.ex03_generic;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * [실습 3] 제네릭과 와일드카드 (관련 문서: JAVA·JVM 01.자바기초심화/04.제네릭.md)
 *
 * PECS: Producer-Extends, Consumer-Super
 *  - 값을 "꺼내 쓰기만" 하는 쪽(생산자)  → ? extends T
 *  - 값을 "넣기만" 하는 쪽(소비자)       → ? super T
 * 이 규칙을 지키면 List<Integer> 를 List<Number> 에 복사하는 것처럼 유연한 API 가 된다.
 *
 * TODO 의 핵심은 "구현"보다 "시그니처"다. 메서드 선언부의 와일드카드를 직접 정해 보자.
 */
public final class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * TODO 3-1. src 의 모든 원소를 dst 에 추가하는 copy 메서드를 완성하라.
     *  - 현재 시그니처 copy(List<T> dst, List<T> src) 로는 Ex03Main 의 copy(numbers, integers) 가 컴파일되지 않는다.
     *  - dst 는 넣기만 하므로 ? super T, src 는 꺼내기만 하므로 ? extends T 로 바꾼 뒤 본문을 구현한다.
     *  - 시그니처를 바꾸면 Ex03Main 의 주석 처리된 줄을 풀어 컴파일되는지 확인하자.
     */
    public static <T> void copy(List<T> dst, List<T> src) { throw new UnsupportedOperationException("TODO 3-1 copy"); }

    /**
     * TODO 3-2. 비교 기준(comparator)으로 가장 큰 원소를 반환하라. 비어 있으면 NoSuchElementException.
     *  - Comparator<? super T> 인 이유: Comparator<Object> 나 Comparator<Number> 로도 Integer 를 비교할 수 있어야 한다.
     */
    public static <T> T max(Collection<? extends T> items, Comparator<? super T> comparator) {
        throw new UnsupportedOperationException("TODO 3-2 max");
    }

    /**
     * 조건을 만족하는 원소 수 (완성됨: Predicate 에도 ? super T 가 쓰이는 것을 확인)
     */
    public static <T> long count(Collection<? extends T> items, Predicate<? super T> condition) {
        return items.stream().filter(condition).count();
    }
}
