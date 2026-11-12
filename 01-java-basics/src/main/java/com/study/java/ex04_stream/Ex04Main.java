package com.study.java.ex04_stream;

import com.study.java.common.Check;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.study.java.ex04_stream.Order.Status.CANCELED;
import static com.study.java.ex04_stream.Order.Status.PAID;

public class Ex04Main {

    public static void main(String[] args) {
        LocalDate d1 = LocalDate.of(2026, 11, 1);
        LocalDate d2 = LocalDate.of(2026, 11, 2);
        List<Order> orders = List.of(
                new Order(1, 10, "book", 15_000, PAID, d1),
                new Order(2, 10, "food", 30_000, PAID, d2),
                new Order(3, 20, "book", 50_000, PAID, d1),
                new Order(4, 20, "food", 99_000, CANCELED, d2),   // 취소 → 통계 제외
                new Order(5, 30, "digital", 120_000, PAID, d2),
                new Order(6, 30, "book", 8_000, PAID, d1));
        OrderStatistics stats = new OrderStatistics(orders);

        Check.run("4-1 카테고리별 합계", () ->
                Check.equal("합계", Map.of("book", 73_000L, "digital", 120_000L, "food", 30_000L),
                        stats.totalAmountByCategory()));
        Check.run("4-2 상위 2명", () ->
                Check.equal("회원 ID", List.of(30L, 20L), stats.topSpenders(2)));
        Check.run("4-3 최근 주문", () -> {
            Check.equal("회원 10 의 최근 주문 ID", 2L, stats.latestOrderOf(10).map(Order::id).orElse(-1L));
            Check.isTrue("없는 회원은 Optional.empty", stats.latestOrderOf(999).isEmpty());
        });
        Check.run("4-4 고액 주문 분할", () ->
                Check.equal("5만원 이상 / 미만", Map.of(true, 2L, false, 3L), stats.countByHighValue(50_000)));
    }
}
