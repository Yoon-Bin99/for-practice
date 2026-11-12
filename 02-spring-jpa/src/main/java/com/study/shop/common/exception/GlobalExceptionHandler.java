package com.study.shop.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;

/**
 * [실습 7] 전역 예외 처리 (관련 문서: Spring JPA 01.스프링핵심/05.예외처리와검증.md)
 *
 * 모든 에러 응답을 RFC 9457 ProblemDetail 형식 + 우리 서비스의 에러 코드(code)로 통일한다.
 * {
 *   "type": "about:blank", "title": "Conflict", "status": 409,
 *   "detail": "재고가 부족합니다.", "instance": "/api/orders", "code": "P002"
 * }
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * TODO 7-1. 비즈니스 예외 → ProblemDetail
     *  - ErrorCode 의 status, message 로 ProblemDetail.forStatusAndDetail(...) 생성
     *  - problem.setProperty("code", ...) 로 에러 코드 추가
     *  - 4xx 는 클라이언트 실수이므로 log.warn (error 로 남기면 알림이 시끄러워진다)
     */
    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusiness(BusinessException e) {
        throw e; // TODO 7-1: 지금은 처리하지 않고 다시 던져서 500 이 된다
    }

    /**
     * TODO 7-2. @Valid 검증 실패(400) → 어떤 필드가 왜 틀렸는지 응답
     *  - e.getBindingResult().getFieldErrors() 를 [{field, message}, ...] 형태로 변환해 "errors" 속성에 추가
     *  - code 는 ErrorCode.INVALID_INPUT
     *  - 이 메서드는 부모(ResponseEntityExceptionHandler)의 메서드를 재정의한 것
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        return super.handleMethodArgumentNotValid(e, headers, status, request); // TODO 7-2
    }

    /** 예상하지 못한 예외: 스택 트레이스는 로그에만, 클라이언트에는 내부 정보를 숨긴다 (완성됨) */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception e) {
        log.error("예상하지 못한 예외", e);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_ERROR.getMessage());
        problem.setProperty("code", ErrorCode.INTERNAL_ERROR.getCode());
        return problem;
    }
}
