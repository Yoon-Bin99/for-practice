package com.study.shop.product;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * [실습 5-A] 동시성 제어용 쿼리 (관련 문서: Spring JPA 03.JPA실전/05.동시성과락.md)
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * TODO 5-1. 비관적 락으로 조회 (SELECT ... FOR UPDATE)
     *  - @Lock(LockModeType.PESSIMISTIC_WRITE) 를 붙인다.
     *  - 이 행을 다른 트랜잭션이 FOR UPDATE / UPDATE 하려면 내 트랜잭션이 끝날 때까지 기다린다.
     *  - 락 대기 시간 힌트: @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
     */
    // TODO 5-1: 여기에 @Lock 과 락 타임아웃 힌트를 붙이세요
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    /**
     * TODO 5-2. 원자적 UPDATE 로 재고 차감 (조회 없이 DB 가 계산)
     *  - JPQL: update Product p set p.stock = p.stock - :qty where p.id = :id and p.stock >= :qty
     *  - 반환값 = 수정된 행 수 (0 이면 재고 부족)
     *  - @Modifying(clearAutomatically = true): 벌크 연산 후 영속성 컨텍스트를 비워 옛 값을 보지 않게
     *  - 포인트: "읽고 → 계산하고 → 쓰기"를 DB 한 문장으로 처리하므로 갱신 분실이 생기지 않는다.
     */
    @Modifying @Query("update Product p set p.stock = p.stock where p.id = :id and p.stock >= :qty") // TODO 5-2: 재고가 줄지 않는 쿼리 → 완성하세요
    int decreaseStockAtomically(@Param("id") Long id, @Param("qty") int qty);
}
