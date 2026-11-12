package com.study.auth.order;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * [실습 8] 메서드 보안: "내 주문만 볼 수 있다" (관련 문서: 인증·인가 03.스프링시큐리티실전/06.권한설계와메서드보안.md)
 *
 * URL 규칙(/api/members/**)만으로는 "로그인한 사람" 까지만 알 수 있고, "본인인가" 는 알 수 없다.
 * GET /api/members/2/orders 를 회원 1 이 호출하면? → 다른 사람 주문이 보이는 IDOR 취약점
 */
@Service
public class OrderQueryService {

    public record OrderView(Long orderId, Long memberId, String productName, long amount) { }

    /**
     * TODO 8-1. @PreAuthorize 로 소유자 확인
     *  - 본인이거나 관리자면 허용:
     *      "#memberId == authentication.principal.memberId() or hasRole('ADMIN')"
     *  - #memberId: 메서드 파라미터 (컴파일 옵션 -parameters 덕분에 이름으로 참조 가능)
     *  - authentication.principal: 필터에서 넣은 LoginMember (record 라서 memberId() 메서드 호출 형태로 쓴다)
     *  - 실패하면 AuthorizationDeniedException → GlobalExceptionHandler 가 403 으로 변환
     */
    // TODO 8-1: @PreAuthorize 를 붙이세요 (지금은 누구나 남의 주문을 볼 수 있다)
    public List<OrderView> findOrders(Long memberId) {
        return List.of(
                new OrderView(memberId * 100 + 1, memberId, "키보드", 89_000),
                new OrderView(memberId * 100 + 2, memberId, "마우스", 39_000));
    }
}
