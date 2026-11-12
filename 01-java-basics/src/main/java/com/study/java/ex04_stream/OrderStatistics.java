package com.study.java.ex04_stream;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * [실습 4] 스트림으로 통계 만들기 (관련 문서: JAVA·JVM 03.함수형과모던자바/02.스트림API.md, 03.Optional.md)
 *
 * 실무에서 "DB 에서 가져온 목록을 화면용으로 가공"하는 코드가 대부분 이런 모양이다.
 * 핵심 포인트
 *  - filter → map → collect 흐름
 *  - Collectors.groupingBy(분류 기준, 하위 수집기) 로 SQL 의 GROUP BY 를 표현
 *  - 결과가 없을 수 있으면 Optional 로 반환 (null 반환 X)
 *
 * 모든 통계는 "결제 완료(PAID)" 주문만 대상으로 한다.
 */
public class OrderStatistics {

    private final List<Order> orders;

    public OrderStatistics(List<Order> orders) {
        this.orders = List.copyOf(orders);
    }

    /**
     * TODO 4-1. 카테고리별 결제 금액 합계 (카테고리 이름순 정렬된 Map)
     *  - groupingBy(Order::category, TreeMap::new, summingLong(Order::amount))
     */
    public Map<String, Long> totalAmountByCategory() {
        throw new UnsupportedOperationException("TODO 4-1 totalAmountByCategory");
    }

    /**
     * TODO 4-2. 결제 금액 합계 상위 N명의 회원 ID (금액 내림차순)
     *  1) 회원별 합계 Map 을 만들고  2) entrySet 을 값 기준 내림차순 정렬  3) limit(n)  4) 키만 추출
     */
    public List<Long> topSpenders(int n) {
        throw new UnsupportedOperationException("TODO 4-2 topSpenders");
    }

    /**
     * TODO 4-3. 특정 회원의 가장 최근 결제 주문
     *  - 없을 수 있으므로 Optional<Order> 반환 → max(Comparator.comparing(Order::orderedAt))
     */
    public Optional<Order> latestOrderOf(long memberId) {
        throw new UnsupportedOperationException("TODO 4-3 latestOrderOf");
    }

    /**
     * TODO 4-4. 고액 주문(기준 이상) / 일반 주문으로 나누어 각 개수 세기
     *  - partitioningBy(조건, counting()) → Map<Boolean, Long>
     */
    public Map<Boolean, Long> countByHighValue(long threshold) {
        throw new UnsupportedOperationException("TODO 4-4 countByHighValue");
    }

    /**
     * 날짜별 주문 수 (완성됨: 참고용)
     */
    public Map<LocalDate, Long> countByDate() {
        return orders.stream()
                .filter(Order::isPaid)
                .collect(Collectors.groupingBy(Order::orderedAt, TreeMap::new, Collectors.counting()));
    }
}
