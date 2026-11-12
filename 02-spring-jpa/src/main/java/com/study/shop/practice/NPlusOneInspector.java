package com.study.shop.practice;

import com.study.shop.order.domain.Order;
import com.study.shop.order.domain.OrderItem;
import com.study.shop.order.domain.OrderStatus;
import com.study.shop.order.repository.OrderRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/** 조회 방식별 실행 SQL 수를 센다 (완성됨). TODO 3-1, 3-2 와 batch size 설정을 바꿔 가며 비교하세요. */
@Component
@RequiredArgsConstructor
public class NPlusOneInspector {

    private final OrderRepository orderRepository;
    private final EntityManagerFactory emf;

    @Transactional(readOnly = true)
    public Map<String, Object> inspect(String mode) {
        Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        int touched;
        switch (mode) {
            case "lazy" -> touched = touchMembers(orderRepository.findAllByStatus(OrderStatus.ORDERED));
            case "fetch-join" -> touched = touchMembers(orderRepository.findAllWithMember(OrderStatus.ORDERED));
            case "entity-graph" -> touched = touchMembers(orderRepository.findByStatus(OrderStatus.ORDERED));
            case "paging" -> {
                Page<Order> page = orderRepository.findPageWithMember(PageRequest.of(0, 10));
                touched = touchMembers(page.getContent());
                page.getContent().forEach(o -> o.getOrderItems().forEach(OrderItem::getQuantity));   // 컬렉션 지연 로딩
            }
            default -> throw new IllegalArgumentException("mode: lazy | fetch-join | entity-graph | paging");
        }

        return Map.of(
                "mode", mode,
                "orders", touched,
                "sqlCount", stats.getPrepareStatementCount(),
                "hint", "lazy 는 1 + (회원 수), fetch-join·entity-graph 는 1, paging 은 batch size 설정 전/후를 비교");
    }

    private int touchMembers(List<Order> orders) {
        orders.forEach(o -> o.getMember().getName());      // 회원 이름 접근 → 프록시 초기화
        return orders.size();
    }
}
