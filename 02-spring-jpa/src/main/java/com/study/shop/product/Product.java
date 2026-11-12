package com.study.shop.product;

import com.study.shop.common.BaseTimeEntity;
import com.study.shop.common.exception.BusinessException;
import com.study.shop.common.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [실습 1-A] 엔티티 안에 비즈니스 규칙 두기 (관련 문서: Spring JPA 02.JPA기초/03.엔티티매핑.md)
 *
 * setStock(...) 같은 setter 대신 "의도가 드러나는 메서드"(decreaseStock)에 규칙을 넣는다.
 * 그러면 서비스가 아무리 많아져도 "재고는 음수가 될 수 없다"는 규칙이 한곳에서 지켜진다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(nullable = false)
    private long price;

    @Column(nullable = false)
    private int stock;

    /**
     * TODO 5-0. 낙관적 락 켜기
     *  - @Version 을 붙이면 UPDATE 가 "... WHERE id = ? AND version = ?" 로 바뀌고, 0건이면 예외가 난다.
     *  - 붙이기 전/후에 POST /practice/stock/unsafe 결과를 비교해 보자.
     *    전: 성공 100인데 재고가 남음(갱신 분실)  /  후: 충돌한 요청이 실패로 바뀜(갱신 분실은 사라짐)
     */
    // TODO 5-0: @Version
    private Long version;

    private Product(String name, String category, long price, int stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public static Product create(String name, String category, long price, int stock) {
        return new Product(name, category, price, stock);
    }

    /**
     * TODO 1-1. 재고 차감
     *  - quantity 가 0 이하면 IllegalArgumentException
     *  - 재고보다 많이 주문하면 BusinessException(ErrorCode.OUT_OF_STOCK)
     *  - 통과하면 stock 을 줄인다.
     *  - 포인트: 값을 바꾸기만 하면 된다. save() 를 부르지 않아도 트랜잭션 커밋 시 "변경 감지"로 UPDATE 가 나간다.
     */
    public void decreaseStock(int quantity) {
        throw new UnsupportedOperationException("TODO 1-1 Product.decreaseStock");
    }

    public void increaseStock(int quantity) {
        this.stock += quantity;
    }

    /** 동시성 실습에서 재고를 초기화할 때만 사용 */
    public void resetStock(int stock) {
        this.stock = stock;
    }
}
