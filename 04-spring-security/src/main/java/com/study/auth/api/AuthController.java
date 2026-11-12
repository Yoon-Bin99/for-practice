package com.study.auth.api;

import com.study.auth.jwt.LoginMember;
import com.study.auth.member.AuthService;
import com.study.auth.member.MemberService;
import com.study.auth.member.SignupRequest;
import com.study.auth.token.TokenPair;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final AuthService authService;

    public record LoginRequest(@NotBlank String email, @NotBlank String password) { }

    public record RefreshRequest(@NotBlank String refreshToken) { }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> signup(@RequestBody @Valid SignupRequest request) {
        return Map.of("memberId", memberService.signup(request));
    }

    @PostMapping("/login")
    public TokenPair login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request.email(), request.password());
    }

    @PostMapping("/refresh")
    public TokenPair refresh(@RequestBody @Valid RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    /** 인증 필요: Access Token 을 헤더에, Refresh Token 을 본문에 */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal LoginMember loginMember,
                                       @RequestBody(required = false) RefreshRequest request) {
        authService.logout(loginMember, request == null ? null : request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
