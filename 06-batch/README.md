# 06-batch · Spring Batch + 스케줄링 실습

**청크 처리(Reader → Processor → Writer), JobParameters와 중복 실행 방지, 멱등한 집계, 다중 서버 스케줄링(ShedLock)**을 실습합니다.

- Spring Boot 3.5 (Spring Batch 5.2) / ShedLock 5 / H2
- 시작 시 회원 20,000명, 주문 30,000건이 자동 생성됩니다. 별도 인프라는 필요 없습니다.
- 확인은 `http/practice.http`에서 합니다. 날짜 파라미터는 **오늘·어제 날짜로 바꿔서** 실행하세요.

## 실습 목록 (권장 순서)

| 번호 | 파일 | 주제 | 핵심 포인트 |
|---|---|---|---|
| B1 | `job/DormantMemberJobConfig` | `JdbcPagingItemReader` | 정렬 키 기반(키셋) 페이징, `@StepScope` + jobParameters |
| B2 | 〃 | Processor | `null` 반환 = 필터링 (filterCount) |
| B3 | 〃 | `JdbcBatchItemWriter` | 1,000건을 JDBC 배치 UPDATE로 |
| B4 | 〃 | 청크 Step | `chunk(1000, tx)`, `faultTolerant().skip().skipLimit()` |
| B5 | `job/DailySalesJobConfig` | Tasklet 집계 | **삭제 후 삽입(멱등)**, `[당일 00:00, 다음날 00:00)` 경계 |
| B6-1 | `schedule/ShedLockConfig` | ShedLock | JDBC LockProvider, `usingDbTime()` |
| B6-2 | `schedule/DailySalesScheduler` | 다중 서버 스케줄 | `@SchedulerLock`, `lockAtMostFor` / `lockAtLeastFor` |

## 확인 포인트

| 실습 | 기대 결과 |
|---|---|
| `POST /batch/dormant` | `dormantMemberStep`의 read ≈ 1년 이상 미접속 수, filter = 그중 VIP 수, write = 나머지 |
| `GET /batch/stats` | DORMANT 수 = `expectedDormant`, `vipDormant` = 0 |
| 같은 `baseDate`로 다시 실행 | `ALREADY_COMPLETED` |
| `POST /batch/daily-sales` 같은 날짜 두 번 | 두 번째는 `ALREADY_COMPLETED`, 다른 날짜는 새로 집계 |
| 스케줄러 로그 (30초마다) | **한 주기에 서버 A, B 중 한쪽만** "실행" 로그가 남음 (B6 전에는 둘 다 실행되거나 둘 다 안 됨) |

## 생각해 볼 것

- **왜 OFFSET 페이징이면 누락이 생길까?** 1페이지(1~1000번째) 회원을 DORMANT로 바꾸면 조건(`status = 'ACTIVE'`)에서 빠지므로, 2페이지(`OFFSET 1000`)는 원래 2001번째부터 읽게 됩니다. `JdbcPagingItemReader`는 `WHERE id > 마지막ID`로 읽어서 이 문제가 없습니다. (`JpaPagingItemReader`는 OFFSET 방식이라 주의)
- **청크 크기를 10, 1000, 5000으로 바꿔 보세요.** 응답의 `commit` 수와 소요 시간(콘솔 로그)이 어떻게 변하나요?
- **실패 후 재시작**: `dormantProcessor`에서 id가 15000이면 `IllegalStateException`을 던지게 바꾸고 실행 → FAILED. 원래대로 돌린 뒤 같은 파라미터로 다시 실행하면 실패한 청크부터 이어서 처리됩니다.

## 관련 문서

미들웨어 스터디 자료 `05.배치와스케줄링/01.SpringBatch.md`, `02.스케줄링과분산환경.md`

## 참고: Spring Batch 6 (Boot 4)

`StepBuilder.chunk(size, tx)` 대신 `ChunkOrientedStepBuilder`, 기본 JobRepository가 메모리(Resourceless)로 바뀌고 `@EnableJdbcJobRepository`로 DB 메타데이터를 켭니다. 재시도는 Spring Framework 7의 `RetryPolicy`, skip은 `SkipPolicy` 기반입니다.
