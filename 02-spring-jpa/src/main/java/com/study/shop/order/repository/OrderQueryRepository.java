package com.study.shop.order.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.shop.order.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

import static com.study.shop.member.QMember.member;
import static com.study.shop.order.domain.QOrder.order;

/**
 * [실습 4] QueryDSL 동적 검색 (관련 문서: Spring JPA 03.JPA실전/03.QueryDSL과동적쿼리.md)
 *
 * Q클래스(QOrder, QMember)는 빌드할 때 애노테이션 프로세서가 만든다.
 * IntelliJ 에서 빨간 줄이 보이면 Gradle → Tasks → build → classes 를 한 번 실행하세요.
 *
 * 핵심 포인트: 조건 메서드가 "값이 없으면 null" 을 반환하면 where() 가 그 조건을 무시한다.
 */
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<OrderSummary> search(OrderSearchCondition cond, Pageable pageable) {
        List<OrderSummary> content = queryFactory
                .select(Projections.constructor(OrderSummary.class,
                        order.id, member.name, order.status, order.totalPrice))
                .from(order)
                .join(order.member, member)
                .where(
                        statusEq(cond.status()),
                        memberNameContains(cond.memberName()),
                        totalPriceGoe(cond.minPrice()),
                        createdBetween(cond.from(), cond.to()))
                .orderBy(order.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // TODO 4-5. 전체 개수 쿼리 + PageableExecutionUtils
        //  - 같은 where 조건으로 order.count() 를 조회하는 countQuery 를 만든다. (fetch() 하지 않고 JPAQuery 로 둔다)
        //  - PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne)
        //  - 포인트: 마지막 페이지이거나 첫 페이지에 데이터가 size 보다 적으면 count 쿼리를 생략해 준다.
        return PageableExecutionUtils.getPage(content, pageable, () -> 0L); // TODO 4-5: 전체 개수가 항상 0
    }

    /**
     * TODO 4-1. 주문 상태 조건 (status 가 null 이면 null 반환)
     */
    private BooleanExpression statusEq(OrderStatus status) {
        return null; // TODO 4-1
    }

    /**
     * TODO 4-2. 회원 이름 부분 일치 (StringUtils.hasText 로 빈 문자열도 걸러내기)
     */
    private BooleanExpression memberNameContains(String name) {
        return null; // TODO 4-2
    }

    /**
     * TODO 4-3. 최소 금액 이상
     */
    private BooleanExpression totalPriceGoe(Long minPrice) {
        return null; // TODO 4-3
    }

    /**
     * TODO 4-4. 주문일 기간 조건 (from, to 각각 없을 수 있음)
     *  - 둘 다 null → null
     *  - from 만 → createdAt >= from 00:00
     *  - to 만   → createdAt <  (to + 1일) 00:00   ← "to 당일 23:59:59 까지" 포함하는 경계값 처리가 포인트
     *  - 둘 다   → 두 조건을 and 로
     */
    private BooleanExpression createdBetween(LocalDate from, LocalDate to) {
        return null; // TODO 4-4
    }
}
