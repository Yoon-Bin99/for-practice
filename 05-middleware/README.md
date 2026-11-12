# 05-middleware · Redis + Kafka 실습

Redis로 **캐시, 분산 락, 선착순 쿠폰, 랭킹, 레이트 리밋**을, Kafka로 **Transactional Outbox, 멱등 컨슈머, DLT**를 실습합니다.

- Spring Boot 3.5 / Spring Data Redis / Spring Kafka / H2
- Docker로 Redis 7.4와 Kafka 4.0(KRaft)을 띄워서 사용합니다.

## 실행

```bash
docker compose up -d          # redis, kafka, kafka-ui 실행
# MiddlewareApplication 실행
docker compose down           # 실습 후 종료
```

- 확인용 요청은 `http/practice.http`에 있습니다.
- **Kafka UI** http://localhost:8989 에서 토픽, 메시지, 컨슈머 그룹, 랙(lag)을 볼 수 있습니다.
- **Redis CLI**: `docker exec -it study-redis redis-cli` 실행 후 `KEYS *` 대신 `SCAN 0`, `TTL product:1`, `ZREVRANGE ranking:weekly 0 -1 WITHSCORES`

## 실습 목록

### Redis (관련 문서: 미들웨어 `01.Redis/*`)

| 번호 | 파일 | 주제 | 핵심 포인트 |
|---|---|---|---|
| R1-1, R1-2 | `product/ProductCacheService` | Cache-Aside 직접 구현 | TTL + 지터, 수정 시 커밋 **후 삭제** |
| R2-1, R2-2 | `product/ProductAnnotationCacheService` | `@Cacheable` / `@CacheEvict` | 프록시 기반, DTO 캐시 |
| R3-1 ~ R3-3 | `lock/RedisLock`, `lock/LockedStockService` | 분산 락 | `SET NX PX`, 소유자 확인 해제(Lua), **락 → 트랜잭션 → 해제 순서** |
| R4-1 | `resources/scripts/issue_coupon.lua` | 선착순 쿠폰 | Lua로 "확인 후 추가"를 원자적으로, 성공 건만 Kafka → DB |
| R5-1 ~ R5-3 | `ranking/RankingService` | Sorted Set 랭킹 | `incrementScore`, `reverseRangeWithScores`, 순위는 0부터 |
| R6-1 | `ratelimit/RateLimiter` | 레이트 리밋 | INCR + EXPIRE를 Lua로 묶는 이유 |

### Kafka (관련 문서: 미들웨어 `02.Kafka/*`)

| 번호 | 파일 | 주제 | 핵심 포인트 |
|---|---|---|---|
| K1-1 | `order/OrderService` | Transactional Outbox | 주문과 이벤트를 **같은 트랜잭션**에 저장, Kafka 직접 호출 X |
| K2-1 | `outbox/OutboxRelay` | Outbox 릴레이 | 발행 결과 대기, 실패 시 중단(순서), 재발행 가능성 |
| K3-1 | `point/PointService` | 멱등 컨슈머 | 처리 기록 테이블, 같은 트랜잭션 |
| K4-1 | `common/KafkaConfig` | 에러 처리 | 재시도 + DLT, 재시도 불필요한 예외 구분 |

권장 순서: `R1 → R2 → R3 → R4 → R5 → R6 → K1 → K2 → K3 → K4`

## 확인 포인트

| 실습 | TODO 전 | TODO 후 |
|---|---|---|
| R1 | 매번 `source: DB`, 약 300ms | 두 번째부터 `CACHE`, 수 ms |
| R3 `/practice/lock-stock` | `finalStock`이 `100 - success`보다 큼(갱신 분실) | 정확히 일치 |
| R4 `/practice/coupon-race` | success 300, DB 250 (초과 발급) | success = redis = DB = 100 |
| R6 | 무제한 | 10초에 6번째부터 429 |
| K1~K2 | `/practice/outbox` 에 PENDING만 쌓임 | SENT로 바뀜, 포인트 적립 |
| K3 `/practice/duplicate-event/{id}` | `pointHistoryCount` 2 | 1 |
| K4 음수 금액 주문 | 실패 로그 후 메시지가 조용히 사라짐 | 콘솔에 `☠️ DLT 도착` |

## 실험해 보기

- **Kafka를 끈 채로 주문**: `docker stop study-kafka` → 주문 API는 성공하고 outbox에 PENDING이 쌓입니다. `docker start study-kafka` 후 자동으로 발행되는지 확인하세요. (Outbox가 유실을 막는 장면)
- **Redis를 끈 채로 조회**: `docker stop study-redis` → 캐시 조회가 1초 타임아웃 후 실패합니다. `CacheErrorHandler`로 DB 조회로 우회하도록 바꿔 보세요. (미들웨어 `01.Redis/05.SpringDataRedis.md`)
- `RedisLock`의 `leaseTime`을 100ms로 줄이고 `LockedStockService` 트랜잭션 안에 `Thread.sleep(300)`을 넣어, 락 만료 후 무슨 일이 생기는지 관찰해 보세요.

## 참고

- Boot 4에서는 `spring-kafka` 대신 `spring-boot-starter-kafka`, JSON 직렬화기는 `JacksonJsonSerializer`(Jackson 3)를 씁니다. 이 실습은 문자열로 보내고 직접 변환하므로 영향이 적습니다.
- 실무의 분산 락은 Redisson `RLock`(대기, 자동 연장, 재진입)을 사용합니다.
