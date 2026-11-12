package com.study.shop.stock;

import com.study.shop.product.Product;
import com.study.shop.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/** 동시 주문 시뮬레이터 (완성됨). 결과를 보며 TODO 5-x 를 채워 나가세요. */
@Component
@RequiredArgsConstructor
public class StockConcurrencySimulator {

    private final ProductRepository productRepository;
    private final StockService stockService;
    private final OptimisticStockFacade optimisticStockFacade;
    private final TransactionTemplate transactionTemplate;

    public Map<String, Object> run(String strategy, int requests, int initialStock) {
        Long productId = prepareProduct(initialStock);
        BiConsumer<Long, Integer> action = switch (strategy) {
            case "unsafe" -> stockService::decreaseUnsafe;
            case "pessimistic" -> stockService::decreaseWithPessimisticLock;
            case "optimistic" -> optimisticStockFacade::decrease;
            case "atomic" -> stockService::decreaseAtomically;
            default -> throw new IllegalArgumentException("strategy: unsafe | pessimistic | optimistic | atomic");
        };

        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(requests);
        long begin = System.currentTimeMillis();

        try (ExecutorService pool = Executors.newFixedThreadPool(16)) {
            for (int i = 0; i < requests; i++) {
                pool.submit(() -> {
                    try {
                        start.await();
                        action.accept(productId, 1);
                        success.incrementAndGet();
                    } catch (Exception e) {
                        fail.incrementAndGet();
                    } finally {
                        done.countDown();
                    }
                });
            }
            start.countDown();
            done.await(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int finalStock = productRepository.findById(productId).map(Product::getStock).orElse(-1);
        return Map.of(
                "strategy", strategy,
                "requests", requests,
                "success", success.get(),
                "fail", fail.get(),
                "expectedStock", Math.max(0, initialStock - success.get()),
                "finalStock", finalStock,
                "correct", finalStock == initialStock - success.get(),
                "elapsedMs", System.currentTimeMillis() - begin);
    }

    private Long prepareProduct(int initialStock) {
        return transactionTemplate.execute(status ->
                productRepository.save(Product.create("동시성 실습 상품", "practice", 1000, initialStock)).getId());
    }
}
