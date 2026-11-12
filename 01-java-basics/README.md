# 01-java-basics · 자바 기초 실습

순수 자바(21)만 사용합니다. 외부 라이브러리가 없으니 IntelliJ에서 바로 실행됩니다.

## 진행 방법

1. 각 패키지의 클래스에서 `TODO` 주석을 읽고 비어 있는 부분을 구현합니다. 빈칸에는 컴파일이 되도록 `throw new UnsupportedOperationException("TODO ...")`가 들어 있으니 지우고 작성하세요.
2. 같은 패키지의 `ExNNMain`을 실행합니다.
3. 콘솔에 `✅`가 나오면 성공, `❌`는 결과가 다름, `⏳`는 아직 구현하지 않은 TODO입니다.

## 실습 목록

| 패키지 | 주제 | TODO | 핵심 포인트 | 관련 문서 |
|---|---|---|---|---|
| `ex01_value` | 불변 값 객체 `Money` | 1-1 ~ 1-5 | 생성 시점 검증, 연산은 새 객체 반환, equals·hashCode 함께 재정의 | JAVA·JVM `01.자바기초심화/02.Object와불변객체.md` |
| `ex02_collection` | `MyHashMap` 직접 구현, `LruCache` | 2-1 ~ 2-7 | 해시 섞기, `(n-1) & hash`, 체이닝, resize(0.75), LinkedHashMap 접근 순서 | JAVA·JVM `02.컬렉션/02.Map과Set내부구조.md` |
| `ex03_generic` | PECS 와일드카드 | 3-1, 3-2 | `? extends`(꺼내기) / `? super`(넣기), 시그니처 설계 | JAVA·JVM `01.자바기초심화/04.제네릭.md` |
| `ex04_stream` | 주문 통계 | 4-1 ~ 4-4 | `groupingBy`, `partitioningBy`, 정렬·limit, Optional 반환 | JAVA·JVM `03.함수형과모던자바/02.스트림API.md` |
| `ex05_proxy` | JDK 동적 프록시로 시간 측정 AOP | 5-1, 5-2 | `InvocationHandler`, 리플렉션 호출, 예외 포장 해제, **자기 호출 관찰** | Spring JPA `01.스프링핵심/03.AOP와프록시.md` |
| `ex06_concurrency` | 경쟁 조건, 생산자-소비자, CompletableFuture, 가상 스레드 | 6-1 ~ 6-6 | synchronized vs CAS, Condition + while, 병렬 호출 + 타임아웃, 세마포어로 자원 보호 | JAVA·JVM `05.동시성/*` |
| `ex07_jvm` | ThreadLocal 누수, 힙 관찰 | 7-1, 7-2 | 스레드 풀에서 ThreadLocal 정리, 참조 해제와 GC | JAVA·JVM `04.JVM/01.JVM구조와메모리.md` |

## 권장 순서

`ex01 → ex02 → ex03 → ex04 → ex05 → ex06 → ex07`

`ex05_proxy`를 끝내면 `02-spring-jpa`의 AOP·트랜잭션 실습이 왜 그렇게 동작하는지 바로 이해됩니다.

## 더 해 보기

- `MyHashMap`에 `remove(key)`를 추가해 보세요. (연결 리스트에서 노드 제거)
- `ex07_jvm/Ex07Main`을 VM 옵션 `-Xmx64m -Xlog:gc`로 실행하고, 할당 크기를 늘려 `OutOfMemoryError`가 나는 지점을 관찰해 보세요.
