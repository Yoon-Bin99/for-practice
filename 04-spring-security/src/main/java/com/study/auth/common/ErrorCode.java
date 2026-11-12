package com.study.auth.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A002", "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "유효하지 않은 토큰입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A004", "접근 권한이 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A005", "이메일 또는 비밀번호가 올바르지 않습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "A006", "리프레시 토큰이 유효하지 않습니다. 다시 로그인해 주세요."),
    REFRESH_TOKEN_REUSED(HttpStatus.UNAUTHORIZED, "A007", "토큰 재사용이 감지되어 모든 세션을 종료했습니다. 다시 로그인해 주세요."),

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "M001", "이미 가입된 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M002", "회원을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
