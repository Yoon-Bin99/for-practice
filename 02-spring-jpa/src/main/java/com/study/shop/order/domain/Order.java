package com.study.shop.order.domain;

import com.study.shop.common.BaseTimeEntity;
import com.study.shop.common.exception.BusinessException;
import com.study.shop.common.exception.ErrorCode;
import com.study.shop.member.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * [실습 1-B] 연관관계와 애그리거트 (관련 문서: Spring JPA 02.JPA기초/04.연관관계매핑.md)
 *
 *   Member 1 ── N Order 1 ── N OrderItem N ── 1 Product
 *
 *  - 외래 키(member_id, order_id)가 있는 쪽이 연관관계의 주인 (@ManyToOne)
 *  - Order 가 OrderItem 의 생명주기를 소유 → cascade = ALL, orphanRemoval = true
 *  - OrderItem 은 Order 를 통해서만 추가·삭제 (애그리거트 루트)
 */
@Entity
@Table(name = "orders")       // order 는 SQL 예약어
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)        // ★ 기본값 EAGER 이므로 LAZY 를 꼭 명시
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)              // ★ ORDINAL 금지
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false)
    private long totalPrice;

    /**
     * TODO 1-2. 주문 생성 (정적 팩토리)
     *  1) new Order() 후 member, address, status = ORDERED 설정
     *  2) items 를 하나씩 addItem(...) 으로 추가 (직접 orderItems.add 하지 말 것)
     *  3) totalPrice = 각 항목 totalPrice() 의 합
     *  - 포인트: 주문 항목은 cascade 로 함께 저장되므로 서비스에서 OrderItem 을 따로 save 하지 않는다.
     */
    public static Order create(Member member, Address address, List<OrderItem> items) {
        throw new UnsupportedOperationException("TODO 1-2 Order.create");
    }

    /**
     * TODO 1-3. 연관관계 편의 메서드
     *  - 객체 쪽(orderItems 리스트)과 주인 쪽(item.assignOrder(this)) 양쪽에 값을 넣는다.
     *  - 포인트: 주인(OrderItem.order)에 값을 넣지 않으면 DB 의 order_id 가 NULL 로 저장된다.
     */
    public void addItem(OrderItem item) {
        orderItems.add(item); // TODO 1-3: 주인 쪽 값 설정이 빠져 있다
    }

    /**
     * TODO 1-4. 주문 취소 (도메인 규칙)
     *  - 이미 SHIPPED 면 BusinessException(ALREADY_SHIPPED), 이미 CANCELED 면 BusinessException(ALREADY_CANCELED)
     *  - 상태를 CANCELED 로 바꾸고, 모든 주문 항목의 cancel() 을 호출해 재고를 되돌린다.
     */
    public void cancel() {
        throw new UnsupportedOperationException("TODO 1-4 Order.cancel");
    }

    public void ship() {
        this.status = OrderStatus.SHIPPED;
    }
}
