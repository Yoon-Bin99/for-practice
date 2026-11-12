package com.study.auth.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

/**
 * 컨트롤러·서비스에서 발생한 예외 처리.
 * 필터(JwtAuthenticationFilter)에서 생긴 인증 실패는 여기까지 오지 않는다 → security 패키지의 EntryPoint 가 처리
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusiness(BusinessException e) {
        return toProblem(e.getErrorCode());
    }

    /** @PreAuthorize 실패 (메서드 보안) → 403 */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ProblemDetail handleDenied(AuthorizationDeniedException e) {
        return toProblem(ErrorCode.FORBIDDEN);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ProblemDetail problem = toProblem(ErrorCode.INVALID_INPUT);
        problem.setProperty("errors", e.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", String.valueOf(fe.getDefaultMessage())))
                .toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    public static ProblemDetail toProblem(ErrorCode code) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(code.getStatus(), code.getMessage());
        problem.setProperty("code", code.getCode());
        return problem;
    }
}
