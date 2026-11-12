package com.study.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.auth.common.ErrorCode;
import com.study.auth.common.GlobalExceptionHandler;
import com.study.auth.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

/**
 * [실습 7] 필터 단계의 401 / 403 응답 (관련 문서: 인증·인가 02.스프링시큐리티구조/04.예외처리.md)
 *
 * 필터에서 생긴 인증·인가 실패는 DispatcherServlet 앞이라 @RestControllerAdvice 가 잡지 못한다.
 * ExceptionTranslationFilter 가 아래 두 핸들러를 호출한다.
 *  - AuthenticationEntryPoint: 인증 안 됨(로그인 필요)  → 401
 *  - AccessDeniedHandler    : 인증은 됐지만 권한 없음  → 403
 */
@Component
@RequiredArgsConstructor
public class SecurityErrorHandlers {

    private final ObjectMapper objectMapper;

    /**
     * TODO 7-1. 401 응답
     *  - JwtAuthenticationFilter 가 남긴 request.getAttribute(ERROR_ATTRIBUTE) 가 ErrorCode 면 그것을, 없으면 UNAUTHORIZED
     *  - write(response, code, request.getRequestURI()) 호출
     *  - 포인트: "토큰 만료(A002)"와 "토큰 없음/위조"를 구분해 줘야 프론트가 재발급 요청을 할지 로그인 화면으로 보낼지 결정한다.
     */
    public AuthenticationEntryPoint entryPoint() {
        return (request, response, authException) -> {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // TODO 7-1: 본문 없는 401
        };
    }

    /** 403 응답 (완성됨) */
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                write(response, ErrorCode.FORBIDDEN, request.getRequestURI());
    }

    private void write(HttpServletResponse response, ErrorCode code, String path) throws IOException {
        ProblemDetail problem = GlobalExceptionHandler.toProblem(code);
        problem.setInstance(URI.create(path));
        response.setStatus(code.getStatus().value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), problem);
    }
}
