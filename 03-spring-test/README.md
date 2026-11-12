# 03-spring-test · 테스트 코드 실습

쿠폰 발급·결제 도메인은 **완성되어 있고**, `src/test` 의 테스트 코드가 빈칸입니다. 테스트를 직접 작성하는 것이 이 프로젝트의 실습입니다.

- Spring Boot 3.5 / JUnit 5 / AssertJ / Mockito / MockMvc·MockMvcTester / Testcontainers(MySQL) / H2
- 빈칸 테스트에는 `fail("TODO ...")` 가 들어 있어 **처음에는 빨간불**입니다. 직접 작성해서 초록불로 바꾸세요.
- 각 클래스에 "예시: 완성된 테스트"가 하나씩 있으니 먼저 읽고 같은 형식으로 작성하면 됩니다.

## 실행

- 전체: IntelliJ에서 `src/test/java` 우클릭 → Run 'Tests in ...', 또는 `./gradlew test`
- 하나만: 테스트 메서드 왼쪽 ▶ 버튼
- `CouponRepositoryMySqlTest` 는 **Docker 실행 중**일 때만 동작합니다. Docker가 없으면 자동으로 건너뜁니다(skipped).

## 실습 목록 (권장 순서)

| 번호 | 테스트 클래스 | 종류 | 핵심 포인트 | 관련 문서 |
|---|---|---|---|---|
| 1-1, 1-2 | `domain/DiscountPolicyTest` | 순수 단위 | **경계값 고르기**, `@ParameterizedTest` + `@CsvSource`, 예외 검증 | 01.테스트기초/02·03 |
| 2-1, 2-2 | `domain/CouponTest` | 도메인 단위 | 시간 경계(정각 만료), 실패한 연산은 상태를 바꾸지 않음, `@Nested` | 01.테스트기초/01·02 |
| 3-1 ~ 3-3 | `service/PaymentServiceTest` | Mockito | 외부만 Mock, `never()`, **ArgumentCaptor**, `willThrow` | 01.테스트기초/04 |
| 4-1 ~ 4-3 | `api/CouponControllerTest` | `@WebMvcTest` | 400 검증 실패 응답, 예외 → 409, `@MockitoBean`, `MockMvcTester` | 02.스프링테스트/02 |
| 5-1, 5-2 | `repository/CouponRepositoryTest` | `@DataJpaTest` | 직접 작성한 쿼리, **flush/clear**, 유니크 제약 | 02.스프링테스트/03 |
| 5-3 | `repository/CouponRepositoryMySqlTest` | Testcontainers | `@Container` + `@ServiceConnection`, H2와 실제 DB 차이 | 02.스프링테스트/04 |
| 6-1, 6-2 | `concurrency/CouponIssueConcurrencyTest` | 동시성 | ExecutorService + CountDownLatch, **`@Transactional` 금지**, 직접 정리 | 03.테스트설계/04 |
| 7-1, 7-2 | `service/CouponServiceTimeTest` | 시간 고정 | `Clock.fixed` + `@Primary` 테스트 빈 | 03.테스트설계/04 |

(관련 문서는 모두 「테스트코드 스터디 자료」 기준)

## 더 해 보기

1. **문제를 일부러 만들어 테스트가 잡는지 확인**
   - `CouponService.issue` 의 `findByCodeForUpdate` 를 `findByCode` 로 바꾸고 6-1을 실행 → 30장보다 많이 발급되는지
   - `DiscountPolicy` 의 `price >= COUPON_MIN_ORDER_AMOUNT` 를 `>` 로 바꾸고 1-1을 실행 → 어떤 행이 실패하는지
2. **컨텍스트 캐시 관찰**: 전체 테스트를 실행하고 로그에서 스프링 배너(`Spring Boot ::`)가 몇 번 찍히는지 세어 보세요. `CouponIssueConcurrencyTest`와 `CouponServiceTimeTest`는 설정(`@TestConfiguration`)이 달라 컨텍스트를 따로 만듭니다.
3. 3-x 테스트를 `@SpringBootTest` + `@MockitoBean` 으로 바꿔 실행 시간을 비교해 보세요. 단위 테스트가 얼마나 빠른지 체감할 수 있습니다.

## 참고: Spring Boot 4 / Testcontainers 2 로 올릴 때

| 항목 | 이 프로젝트 (Boot 3.5) | Boot 4 |
|---|---|---|
| `@WebMvcTest` 패키지 | `org.springframework.boot.test.autoconfigure.web.servlet` | `org.springframework.boot.webmvc.test.autoconfigure` + `spring-boot-starter-webmvc-test` |
| `@DataJpaTest` 패키지 | `org.springframework.boot.test.autoconfigure.orm.jpa` | `org.springframework.boot.data.jpa.test.autoconfigure` + `spring-boot-starter-data-jpa-test` |
| Testcontainers 모듈 | `org.testcontainers:mysql` | `org.testcontainers:testcontainers-mysql`, `org.testcontainers.mysql.MySQLContainer` |
