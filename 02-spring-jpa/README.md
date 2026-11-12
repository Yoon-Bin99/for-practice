# 02-spring-jpa · 스프링 + JPA 실습

회원·상품·주문 도메인으로 **엔티티 설계, 트랜잭션, N+1, QueryDSL, 재고 동시성, 예외 처리, AOP**를 실습합니다.

- Spring Boot 3.5 / Java 21 / H2 인메모리 DB(MySQL 모드) / QueryDSL 5.1 (jakarta)
- 별도 DB 설치 없이 `ShopApplication`을 실행하면 됩니다. 시작할 때 회원 10명, 상품 10개, 주문 30건이 자동으로 생성됩니다.
- H2 콘솔: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:shop`, 사용자 `sa`)

## 진행 방법

1. `TODO` 주석을 읽고 빈칸을 구현합니다. 빈칸 상태에서도 애플리케이션은 실행되며, 해당 기능을 호출하면 `UnsupportedOperationException`이 나거나 잘못된 결과가 나옵니다.
2. `http/practice.http`를 IntelliJ에서 열고 요청 왼쪽의 ▶ 버튼으로 결과를 확인합니다.
3. 콘솔의 SQL 로그(`org.hibernate.SQL`)를 꼭 같이 보세요. JPA 실습의 절반은 **어떤 SQL이 몇 번 나가는지** 확인하는 것입니다.

> QueryDSL의 `QOrder`, `QMember` 클래스는 빌드할 때 생성됩니다. IntelliJ에서 빨간 줄이 보이면 Gradle 탭에서 `build > classes`를 한 번 실행하세요.

## 실습 목록 (권장 순서)

| 번호 | 파일 | 주제 | 핵심 포인트 | 확인 방법 |
|---|---|---|---|---|
| 1-1 | `product/Product` | 재고 차감 규칙 | setter 대신 도메인 메서드, save 없이 변경 감지 | 앱 시작 로그 "초기 데이터 생성" |
| 1-2 ~ 1-4 | `order/domain/Order` | 주문 생성·연관관계 편의 메서드·취소 | 주인 쪽(`OrderItem.order`) 값 설정, cascade, 상태 규칙 | 앱 시작 로그, 주문 취소 요청 |
| 2-1, 2-2 | `order/service/OrderService` | 주문 생성·취소 | 트랜잭션 범위, 변경 감지, 예외 시 전체 롤백 | `POST /api/orders` |
| 2-3 | `order/service/AuditLogService` | 전파 속성 | REQUIRED vs **REQUIRES_NEW** | 재고 부족 주문 후 `/practice/audit-logs` |
| 2-4 | `order/service/OrderEventListener` | 커밋 후 실행 | `@EventListener` vs **`@TransactionalEventListener(AFTER_COMMIT)`** | 실패한 주문에서 알림 로그가 안 나와야 함 |
| 2-5 | `practice/SelfInvocationService` | 자기 호출 | 같은 클래스 내부 호출은 프록시를 거치지 않음 | `/practice/self-invocation` |
| 3-1, 3-2 | `order/repository/OrderRepository` | N+1 해결 | fetch join, `@EntityGraph`, batch size | `/practice/n-plus-one?mode=...`의 `sqlCount` |
| 4-1 ~ 4-5 | `order/repository/OrderQueryRepository` | QueryDSL 동적 검색 | null 이면 조건 무시, 날짜 경계값, count 쿼리 분리 | `GET /api/orders?...` |
| 5-0 ~ 5-5 | `product/*`, `stock/*` | 재고 동시성 | 갱신 분실 관찰 → `@Version`, 비관적 락, 원자적 UPDATE, 낙관적 락 재시도 | `/practice/stock/{strategy}`의 `finalStock`, `elapsedMs` |
| 6-1 | `common/aop/ExecutionTimeAspect` | AOP | `@Around` + `proceed()` | 주문 생성 시 `[실행 시간]` 로그 |
| 7-1 ~ 7-3 | `common/exception/*`, `order/api/OrderCreateRequest` | 예외·검증 | ProblemDetail + 에러 코드, Bean Validation | 잘못된 요청 → 400/409 응답 본문 |

## 실습별 관찰 포인트

### N+1 (3-1, 3-2)
| mode | 기대 `sqlCount` | 설명 |
|---|---|---|
| `lazy` | 11 | 주문 1번 + 회원 10명 각각 |
| `fetch-join` | 1 | TODO 3-1 완료 후 |
| `entity-graph` | 1 | TODO 3-2 완료 후 |
| `paging` | batch size 설정 전 12 안팎 → 설정 후 2~3 | 컬렉션(`orderItems`)은 fetch join 대신 `default_batch_fetch_size` |

### 재고 동시성 (5-0 ~ 5-5): 100명이 동시에 1개씩, 재고 100
| strategy | 기대 결과 | 생각해 볼 것 |
|---|---|---|
| `unsafe` (5-0 전) | 성공 100, **finalStock > 0** | 갱신 분실 |
| `unsafe` (5-0 후) | 일부 실패, `correct=true` | `@Version`이 충돌을 감지해 실패로 바꿈 |
| `pessimistic` | 성공 100, finalStock 0 | 순서대로 대기, 락 범위 = 트랜잭션 범위 |
| `atomic` | 성공 100, finalStock 0 | 가장 빠름, 대신 규칙이 SQL로 이동 |
| `optimistic` | 성공 100, finalStock 0, **가장 느림** | 충돌이 잦을 때는 재시도 비용이 큼 |

### OSIV
`open-in-view: false`이므로 `/api/orders/{id}/osiv-test`는 500이 나는 것이 정상입니다. 로그의 `LazyInitializationException`을 확인하고, 서비스에서 DTO로 변환하는 `find()`와 비교해 보세요.

## 관련 문서

- Spring JPA 스터디 자료: `01.스프링핵심/03·05·06`, `02.JPA기초/02·03·04·05`, `03.JPA실전/01·03·05`
- JAVA·JVM 스터디 자료: `01.자바기초심화/05` (프록시 원리)

## 더 해 보기

- `application.yml`의 H2 URL을 MySQL로 바꾸고(Docker `mysql:8.4`) 같은 동시성 실습을 실행해 결과를 비교해 보세요.
- 주문 생성에 쿠폰 할인 규칙을 추가하고, 할인 계산 로직을 엔티티·값 객체에 넣어 보세요.
