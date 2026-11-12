# 스터디 실습자료

「스터디 자료모음」의 개념을 **직접 코드로 구현해 보는** 실습 프로젝트 모음입니다.
핵심 로직만 비워 두었고, 각 빈칸 위의 `TODO` 주석에 **무엇을 구현하는지**와 **왜 중요한지(포인트)**를 적어 두었습니다.

## 프로젝트 구성

| 프로젝트 | 주제 | 필요한 것 | TODO | 관련 학습 자료 |
|---|---|---|---|---|
| [01-java-basics](./01-java-basics) | 값 객체, HashMap 구현, 제네릭, 스트림, 동적 프록시, 동시성, JVM | JDK 21 | 28 | JAVA·JVM |
| [02-spring-jpa](./02-spring-jpa) | 엔티티·연관관계, 트랜잭션·전파, N+1, QueryDSL, 재고 동시성, 예외, AOP | JDK 21 | 27 | Spring JPA |
| [03-spring-test](./03-spring-test) | 단위·Mockito·WebMvcTest·DataJpaTest·Testcontainers·동시성·시간 테스트 | JDK 21 (+ Docker 선택) | 17 | 테스트코드 |
| [04-spring-security](./04-spring-security) | BCrypt, JWT, 인증 필터, 필터 체인, 리프레시 토큰 회전, 메서드 보안, AES-GCM | JDK 21 | 14 | 인증·인가 |
| [05-middleware](./05-middleware) | Redis 캐시·분산 락·선착순·랭킹·레이트 리밋, Kafka Outbox·멱등 컨슈머·DLT | JDK 21 + **Docker** | 16 | 미들웨어 |
| [06-batch](./06-batch) | Spring Batch 청크 처리, 멱등 집계, ShedLock 다중 서버 스케줄링 | JDK 21 | 7 | 미들웨어 |

- 공통: Java 21, Gradle 8.14 (Wrapper 포함), Spring Boot 3.5
- **JDK 21은 따로 설치하지 않아도 됩니다.** 각 프로젝트가 Gradle Toolchain + Foojay 플러그인을 사용해서, 처음 빌드할 때 JDK 21(Temurin)을 자동으로 내려받아 `~/.gradle/jdks`에 둡니다.
- DB는 모두 H2 인메모리라 설치가 필요 없습니다. `05-middleware`만 `docker compose up -d`로 Redis·Kafka를 띄웁니다.

## 여는 방법 (IntelliJ)

- **전체**: `스터디 실습자료` 폴더를 열면 루트의 `settings.gradle`(Composite Build)이 6개 프로젝트를 모두 Gradle 프로젝트로 인식합니다.
- **하나만**: 원하는 프로젝트 폴더(예: `02-spring-jpa`)만 열어도 됩니다. 각 프로젝트가 독립된 Gradle 프로젝트입니다.
- **Gradle을 실행하는 JDK(Gradle JVM)는 17~24 중 하나**여야 합니다. Gradle 8.14는 Java 25 이상에서 실행되지 않습니다. `Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JVM`에서 **17**을 고르세요. 코드 컴파일·실행은 위에서 자동으로 받은 JDK 21이 담당합니다.
- 처음 열면 의존성을 내려받느라 몇 분 걸릴 수 있습니다. Lombok을 쓰는 프로젝트는 `Settings > Build > Compiler > Annotation Processors > Enable annotation processing`을 켜 주세요.

## 실습 방식

```java
public Money plus(Money other) {
    // TODO 1-2. 더하기
    //  - this 를 바꾸지 말고 "새 Money" 를 반환한다. (불변 객체의 연산 규칙)
    throw new UnsupportedOperationException("TODO 1-2 Money.plus");   // ← 지우고 직접 구현
}
```

1. 각 프로젝트의 `README.md`에서 실습 순서와 확인 방법을 봅니다.
2. `TODO` 주석을 읽고 빈칸을 채웁니다. 빈칸 상태에서도 **컴파일·실행은 되도록** 임시 코드가 들어 있습니다.
3. 결과를 확인합니다.
   - `01`: 각 패키지의 `ExNNMain` 실행 → ✅ / ❌ / ⏳ 출력
   - `02`, `04`, `05`, `06`: 애플리케이션 실행 후 `http/practice.http`의 요청 실행
   - `03`: 테스트 실행 (처음엔 `fail("TODO")`로 빨간불 → 직접 작성해서 초록불)
4. IntelliJ 하단 **TODO 탭**(`View > Tool Windows > TODO`)에서 남은 빈칸을 한눈에 볼 수 있습니다.

> 빈칸은 "그 메서드에서 가장 중요한 로직"에만 뚫었습니다. 나머지 코드(설정, 확인용 컨트롤러, 초기 데이터)는 완성되어 있으니, 빈칸 주변 코드를 읽으면서 **왜 이 부분이 핵심인지** 생각해 보세요.

## 권장 순서

```
01-java-basics ─▶ 02-spring-jpa ─▶ 03-spring-test ─▶ 04-spring-security ─▶ 05-middleware ─▶ 06-batch
 (프록시·동시성)     (트랜잭션·N+1·락)    (테스트로 검증)       (JWT 인증)            (Redis·Kafka)       (배치)
```

학습 자료와 함께 보면 좋은 조합:

| 학습 자료 단계 (스터디 자료모음 README) | 실습 |
|---|---|
| 1단계 언어·CS 기초 | 01 |
| 2단계 스프링·DB | 02 (1~4번) |
| 3단계 웹·보안 | 04 |
| 4단계 실전 백엔드 | 02 (5번 동시성), 03 |
| 5단계 미들웨어·배포 | 05 |
| 6단계 심화·운영 | 06, 01 (ex07 JVM) |

## 참고

- 정답 코드는 따로 넣지 않았습니다. 막히면 TODO 주석의 힌트와 관련 학습 문서를 먼저 보세요.
- Spring Boot 4(Spring 7)로 올릴 때 달라지는 점은 각 프로젝트 README 하단에 정리해 두었습니다.
