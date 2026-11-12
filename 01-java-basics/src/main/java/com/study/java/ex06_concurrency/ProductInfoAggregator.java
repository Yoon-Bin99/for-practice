package com.study.java.ex06_concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * [실습 6-C] CompletableFuture 로 여러 외부 호출을 병렬로 (관련 문서: JAVA·JVM 05.동시성/03.Executor와CompletableFuture.md)
 *
 * 상품 상세 화면 = 가격 API(300ms) + 재고 API(300ms) + 리뷰 API(느림)
 *  - 순서대로 부르면 합계 시간, 병렬로 부르면 가장 느린 것의 시간
 *  - 리뷰처럼 "없어도 화면은 보여 줄 수 있는" 정보는 타임아웃 + 기본값(fallback)
 */
public class ProductInfoAggregator {

    public record ProductInfo(long productId, long price, int stock, double rating) { }

    public interface ExternalApi {
        long fetchPrice(long productId);

        int fetchStock(long productId);

        double fetchRating(long productId);      // 가끔 매우 느림
    }

    private final ExternalApi api;
    private final ExecutorService executor;     // 외부 호출 전용 스레드 풀 (공용 ForkJoinPool 사용 X)

    public ProductInfoAggregator(ExternalApi api, ExecutorService executor) {
        this.api = api;
        this.executor = executor;
    }

    /**
     * TODO 6-5. 세 API 를 병렬로 호출해 하나로 합치기
     *  1) CompletableFuture.supplyAsync(() -> api.fetchPrice(id), executor) 형태로 세 개를 동시에 시작
     *  2) 평점은 .completeOnTimeout(0.0, 500, TimeUnit.MILLISECONDS) 로 500ms 안에 안 오면 0.0
     *  3) .exceptionally(e -> 0.0) 으로 평점 API 가 실패해도 화면은 보여 주기
     *  4) thenCombine 으로 합치거나, allOf(...) 후 join() 으로 결과를 모아 ProductInfo 생성
     *  - 포인트: supplyAsync 에 executor 를 "꼭" 넘긴다. 생략하면 공용 풀을 써서 다른 작업까지 막힐 수 있다.
     */
    public ProductInfo load(long productId) {
        throw new UnsupportedOperationException("TODO 6-5 load");
    }
}
