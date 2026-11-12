package com.study.shop.order.domain;

import com.study.shop.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    /** 주문 당시 가격 (상품 가격이 나중에 바뀌어도 주문 금액은 그대로) */
    private long orderPrice;

    private OrderItem(Product product, int quantity, long orderPrice) {
        this.product = product;
        this.quantity = quantity;
        this.orderPrice = orderPrice;
    }

    /** 주문 항목 생성 + 재고 차감 (재고가 부족하면 여기서 예외) */
    public static OrderItem create(Product product, int quantity) {
        product.decreaseStock(quantity);
        return new OrderItem(product, quantity, product.getPrice());
    }

    /** Order.addItem 에서만 호출 (연관관계의 주인 쪽 값 설정) */
    void assignOrder(Order order) {
        this.order = order;
    }

    void cancel() {
        product.increaseStock(quantity);
    }

    public long totalPrice() {
        return orderPrice * quantity;
    }
}
