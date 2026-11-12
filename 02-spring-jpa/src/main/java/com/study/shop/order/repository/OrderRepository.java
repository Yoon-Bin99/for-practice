package com.study.shop.order.repository;

import com.study.shop.order.domain.Order;
import com.study.shop.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * [실습 3] N+1 문제 해결 (관련 문서: Spring JPA 03.JPA실전/01.N+1문제.md)
 *
 * /practice/n-plus-one?mode=... 를 호출하면 각 방식으로 주문 30건 + 회원 이름을 조회하고 실행된 SQL 수를 알려 준다.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /** mode=lazy: 주문만 조회 → 회원 이름에 접근할 때마다 SELECT (N+1 관찰용, 완성됨) */
    @Query("select o from Order o where o.status = :status")
    List<Order> findAllByStatus(@Param("status") OrderStatus status);

    /**
     * TODO 3-1. mode=fetch-join: 주문과 회원을 한 번의 SQL 로 조회
     *  - JPQL: select o from Order o join fetch o.member where o.status = :status
     *  - 일반 join 과 차이: join fetch 는 회원 엔티티까지 SELECT 해서 영속성 컨텍스트에 올린다.
     */
    @Query("select o from Order o where o.status = :status") // TODO 3-1: fetch join 으로 바꾸세요
    List<Order> findAllWithMember(@Param("status") OrderStatus status);

    /**
     * TODO 3-2. mode=entity-graph: JPQL 대신 @EntityGraph 로 같은 효과
     *  - @EntityGraph(attributePaths = "member")
     */
    // TODO 3-2: @EntityGraph 를 붙이세요
    List<Order> findByStatus(OrderStatus status);

    /**
     * mode=paging: 컬렉션(orderItems)이 필요한 페이징
     *  - ToOne(member) 은 fetch join, 컬렉션은 지연 로딩 + default_batch_fetch_size 로 해결한다. (application.yml 참고)
     *  - fetch join 이 있으면 count 쿼리는 따로 지정해 불필요한 조인을 없앤다.
     */
    @Query(value = "select o from Order o join fetch o.member",
           countQuery = "select count(o) from Order o")
    Page<Order> findPageWithMember(Pageable pageable);
}
