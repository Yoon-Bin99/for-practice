package com.study.shop.order.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * [실습 7-3] 요청 DTO 검증 (관련 문서: Spring JPA 01.스프링핵심/05.예외처리와검증.md)
 *
 * TODO 7-3. 아래 규칙에 맞게 Bean Validation 애노테이션을 붙이세요.
 *  - memberId: 필수                                   → @NotNull
 *  - zipcode : 필수(공백 X), 최대 10자                 → @NotBlank @Size(max = 10)
 *  - street  : 필수(공백 X)                            → @NotBlank
 *  - lines   : 1개 이상, 각 항목도 검증                → @NotEmpty List<@Valid OrderLine>
 *  - OrderLine.productId 필수, quantity 1~100          → @NotNull / @Min(1) @Max(100)
 *  - 포인트: 형식·범위처럼 "요청만 보고 알 수 있는 것"은 여기서, 재고 부족 같은 규칙은 도메인에서 검사한다.
 */
public record OrderCreateRequest(
        Long memberId, String zipcode, String street, String detail, List<OrderLine> lines
) {

    public record OrderLine(
            Long productId, int quantity
    ) {
    }
}
