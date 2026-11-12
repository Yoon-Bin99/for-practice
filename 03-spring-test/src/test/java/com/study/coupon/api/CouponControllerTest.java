package com.study.coupon.api;

import com.study.coupon.common.BusinessException;
import com.study.coupon.common.ErrorCode;
import com.study.coupon.service.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * [실습 4] 컨트롤러 슬라이스 테스트 (관련 문서: 테스트코드 02.스프링테스트/02.컨트롤러테스트.md)
 *
 *  - @WebMvcTest: 컨트롤러·ControllerAdvice·검증 등 "웹 계층"만 띄운다 (서비스·DB 없음)
 *  - 서비스는 @MockitoBean 으로 교체 (Boot 3.4+, 예전 @MockBean 은 deprecated)
 *  - 검증 대상: URL 매핑, 요청 검증(400), 응답 JSON, 예외 → 에러 응답 변환
 */
@WebMvcTest(CouponController.class)
@DisplayName("쿠폰 API")
class CouponControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MockMvcTester mvc;          // Spring 6.2+ AssertJ 스타일

    @MockitoBean
    CouponService couponService;

    @Test
    @DisplayName("발급에 성공하면 201 과 결과를 반환한다 (예시: 완성된 테스트)")
    void issueSuccess() throws Exception {
        given(couponService.issue("WELCOME", 1L)).willReturn(new CouponService.IssueResult("WELCOME", 1L, 99));

        mockMvc.perform(post("/api/coupons/{code}/issue", "WELCOME")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"memberId": 1}
                                """))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("WELCOME"))
                .andExpect(jsonPath("$.remainingQuantity").value(99));
    }

    /**
     * TODO 4-1. 요청 검증 실패 → 400 + 에러 코드 C001 + 어떤 필드가 틀렸는지
     *  - 본문 {} (memberId 없음) 으로 요청
     *  - status().isBadRequest(), jsonPath("$.code").value("C001"), jsonPath("$.errors[0].field").value("memberId")
     *  - then(couponService).shouldHaveNoInteractions() → 검증에 실패하면 서비스가 호출되면 안 된다
     */
    @Test
    @DisplayName("memberId 가 없으면 400 을 반환하고 서비스를 호출하지 않는다")
    void invalidRequest() throws Exception {
        fail("TODO 4-1");
    }

    /**
     * TODO 4-2. 서비스가 비즈니스 예외를 던지면 → 409 + 에러 코드
     *  - given(couponService.issue(anyString(), anyLong())).willThrow(new BusinessException(ErrorCode.COUPON_SOLD_OUT))
     *  - status().isConflict(), jsonPath("$.code").value("CP003")
     *  - 포인트: 예외 → HTTP 응답 변환은 컨트롤러 계층의 책임이므로 여기서 검증한다.
     */
    @Test
    @DisplayName("쿠폰이 소진되면 409 와 에러 코드를 반환한다")
    void soldOut() throws Exception {
        fail("TODO 4-2");
    }

    /**
     * TODO 4-3. MockMvcTester(AssertJ 스타일)로 목록 조회 검증
     *  - assertThat(mvc.get().uri("/api/coupons/issuable")).hasStatusOk()
     *        .bodyJson().extractingPath("$[0].code").isEqualTo("WELCOME");
     *  - throws Exception 이 필요 없고, 실패 메시지가 더 친절하다.
     */
    @Test
    @DisplayName("발급 가능한 쿠폰 목록을 반환한다")
    void issuable() {
        given(couponService.findIssuable()).willReturn(List.of(
                new CouponService.CouponView("WELCOME", 3_000, 100, LocalDateTime.of(2026, 12, 31, 0, 0))));
        fail("TODO 4-3");
    }
}
