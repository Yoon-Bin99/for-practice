package com.study.auth.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 64) String password,
        @NotBlank @Size(max = 30) String name,
        @Pattern(regexp = "^01[016-9]-?\\d{3,4}-?\\d{4}$", message = "휴대폰 번호 형식이 아닙니다.") String phone
) {
}
