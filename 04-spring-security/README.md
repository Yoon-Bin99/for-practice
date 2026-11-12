# 04-spring-security · 인증·인가 실습

JWT 기반 로그인 API를 직접 완성합니다. **비밀번호 해시, 토큰 발급·검증, 인증 필터, 필터 체인, 리프레시 토큰 회전, 로그아웃, 메서드 보안, 개인정보 암호화**까지 다룹니다.

- Spring Boot 3.5 (Spring Security 6.5) / jjwt 0.12 / H2
- 실습 계정: `admin@study.com / admin1234!` (ADMIN, id=1), `user@study.com / user1234!` (USER, id=2)
- 확인은 `http/practice.http`에서 위에서부터 순서대로 실행합니다. 로그인 응답의 토큰이 자동으로 변수에 저장됩니다.

## 요청이 처리되는 흐름

```
요청 ─▶ JwtAuthenticationFilter (TODO 4) ── 토큰 검증, SecurityContext 저장
     ─▶ AuthorizationFilter (TODO 6) ────── URL 규칙 검사 ─ 실패 ─▶ EntryPoint 401 / AccessDeniedHandler 403 (TODO 7)
     ─▶ Controller ─▶ @PreAuthorize (TODO 8) ─ 실패 ─▶ GlobalExceptionHandler 403
```

## 실습 목록 (권장 순서)

| 번호 | 파일 | 주제 | 핵심 포인트 |
|---|---|---|---|
| 9-1, 9-2 | `crypto/AesGcmCipher` | AES-256-GCM | IV는 매번 랜덤, IV + 암호문 저장, 변조 감지 |
| 10-1 | `crypto/BlindIndex` | 블라인드 인덱스 | 정규화 후 HMAC, 키 없는 해시가 위험한 이유 |
| 1-1 | `member/MemberService` | 회원 가입 | 비밀번호는 **해시**, 전화번호는 **암호화** (목적이 다름) |
| 3-1, 3-2 | `jwt/JwtTokenProvider` | JWT 발급·검증 | sub·jti·exp, 서명 키, 예외는 호출한 쪽이 판단 |
| 6-1 | `config/SecurityConfig` | 필터 체인 | STATELESS, URL 규칙 순서, `/error` 허용, 필터 위치 |
| 4-1 | `jwt/JwtAuthenticationFilter` | 인증 필터 | 막지 말고 **채우기만**, `ROLE_` 접두사, 실패 이유 전달 |
| 7-1 | `config/SecurityErrorHandlers` | 401 응답 | 만료(A002)와 위조·없음 구분 |
| 2-1 | `member/AuthService` | 로그인 | `matches()`, **계정 열거 방지**(같은 에러) |
| 5-1 | `token/RefreshTokenService` | 리프레시 토큰 | 회전, "사용됨" 표시, **재사용 감지 → family 폐기** |
| 2-2 | `member/AuthService` | 로그아웃 | JWT는 회수 불가 → jti 블랙리스트(남은 만료 시간만큼) |
| 8-1 | `order/OrderQueryService` | 메서드 보안 | IDOR 방지, `#파라미터`와 `authentication.principal` |

암호화(9, 10)를 먼저 하는 이유: 앱 시작 시 실습 계정의 전화번호를 암호화해서 저장하기 때문입니다. 구현 전이면 전화번호 없이 계정이 만들어집니다.

## 확인 포인트

| 요청 | 기대 결과 |
|---|---|
| 토큰 없이 `/api/me` | 401 `A001` |
| 위조 토큰 | 401 `A003` |
| `access-token-ttl`을 `10s`로 바꾸고 10초 후 요청 | 401 `A002` |
| USER가 `/api/admin/**` | 403 `A004` |
| USER가 `/api/members/1/orders` | 403 `A004` |
| 같은 refresh 토큰 두 번 사용 | 두 번째 401 `A007`, 이후 최신 토큰도 401 `A006` |
| 로그아웃 후 같은 access 토큰 | 401 `A003` |

## 관련 문서 (「인증·인가 스터디 자료」)

`01.인증인가기초/02·04·06`, `02.스프링시큐리티구조/01~04`, `03.스프링시큐리티실전/02·03·06`

## 더 해 보기

- 리프레시 토큰을 `HttpOnly; Secure; SameSite=Strict` 쿠키로 내려주도록 바꿔 보세요. (XSS로부터 토큰 보호)
- `RefreshTokenStore`, `TokenBlacklist`를 Redis로 바꿔 보세요. (서버가 여러 대일 때 필요, `05-middleware` 참고)
- 로그인 실패 5회 시 10분 잠금을 추가해 보세요. (무차별 대입 방지)
