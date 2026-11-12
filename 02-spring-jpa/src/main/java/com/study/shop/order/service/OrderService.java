package com.study.shop.order.service;

import com.study.shop.common.aop.LogExecutionTime;
import com.study.shop.common.exception.BusinessException;
import com.study.shop.common.exception.ErrorCode;
import com.study.shop.member.Member;
import com.study.shop.member.MemberRepository;
import com.study.shop.order.api.OrderCreateRequest;
import com.study.shop.order.api.OrderResponse;
import com.study.shop.order.domain.Address;
import com.study.shop.order.domain.Order;
import com.study.shop.order.domain.OrderItem;
import com.study.shop.order.repository.OrderRepository;
import com.study.shop.product.Product;
import com.study.shop.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * [실습 2] 서비스와 트랜잭션 (관련 문서: Spring JPA 01.스프링핵심/06.트랜잭션.md, 02.JPA기초/02.영속성컨텍스트.md)
 *
 *  - 클래스에 readOnly = true, 쓰기 메서드에만 @Transactional
 *  - 서비스는 "흐름"만 조율하고 규칙은 도메인(Order, Product)이 가진다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final AuditLogService auditLogService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * TODO 2-1. 주문 생성
     *  1) auditLogService.record("ORDER_TRY member=" + memberId) — 실패해도 남아야 하는 기록 (TODO 2-3 과 연결)
     *  2) 회원 조회 (없으면 BusinessException(MEMBER_NOT_FOUND))
     *  3) 각 주문 줄마다 상품 조회 (없으면 PRODUCT_NOT_FOUND) → OrderItem.create(product, quantity) (여기서 재고 차감)
     *  4) Order.create(...) → orderRepository.save(order)  (OrderItem 은 cascade 로 함께 저장)
     *  5) eventPublisher.publishEvent(new OrderPlacedEvent(...))
     *  6) order.getId() 반환
     *  - 포인트: 재고 차감에 productRepository.save() 가 없다. 트랜잭션이 끝날 때 변경 감지로 UPDATE 된다.
     *  - 중간에 예외(재고 부족)가 나면 이미 차감한 다른 상품 재고까지 전부 롤백된다.
     */
    @Transactional
    @LogExecutionTime
    public Long placeOrder(OrderCreateRequest request) {
        throw new UnsupportedOperationException("TODO 2-1 OrderService.placeOrder");
    }

    /**
     * TODO 2-2. 주문 취소
     *  - 주문 조회(없으면 ORDER_NOT_FOUND) → order.cancel()
     *  - 포인트: 끝. save 도 update 도 호출하지 않는다. (영속 상태 엔티티의 변경 감지)
     */
    @Transactional
    public void cancel(Long orderId) {
        throw new UnsupportedOperationException("TODO 2-2 OrderService.cancel");
    }

    /** 조회는 readOnly 트랜잭션 안에서 DTO 로 변환해 반환 (OSIV 가 꺼져 있어도 안전, 완성됨) */
    public OrderResponse find(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        return OrderResponse.from(order);
    }

    /** OSIV 실습용: 엔티티를 그대로 반환 (컨트롤러에서 지연 로딩하면 예외가 나는 것을 관찰) */
    public Order findEntity(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }
}
