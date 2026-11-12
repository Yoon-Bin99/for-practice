package com.study.java.ex06_concurrency;

import com.study.java.common.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Ex06Main {

    public static void main(String[] args) throws Exception {
        Check.run("6-1, 6-2 카운터 경쟁 조건", () -> {
            int threads = 8, perThread = 100_000;
            long unsafe = runCounter(new Counters.UnsafeCounter(), threads, perThread);
            System.out.println("    UnsafeCounter 결과: " + unsafe + " (기대 " + threads * perThread + " → 대부분 더 작게 나옴)");
            Check.equal("SynchronizedCounter", (long) threads * perThread,
                    runCounter(new Counters.SynchronizedCounter(), threads, perThread));
            Check.equal("AtomicCounter", (long) threads * perThread,
                    runCounter(new Counters.AtomicCounter(), threads, perThread));
        });

        Check.run("6-3, 6-4 생산자-소비자", () -> {
            BoundedBuffer<Integer> buffer = new BoundedBuffer<>(5);
            List<Integer> consumed = java.util.Collections.synchronizedList(new ArrayList<>());
            Thread producer = Thread.ofPlatform().start(() -> {
                try {
                    for (int i = 1; i <= 50; i++) buffer.put(i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (UnsupportedOperationException e) {
                    System.out.println("⏳ " + e.getMessage());
                }
            });
            Thread consumer = Thread.ofPlatform().start(() -> {
                try {
                    for (int i = 1; i <= 50; i++) consumed.add(buffer.take());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (UnsupportedOperationException e) {
                    System.out.println("⏳ " + e.getMessage());
                }
            });
            join(producer);
            join(consumer);
            Check.equal("50개를 순서대로 소비", 50, consumed.size());
            Check.isTrue("순서 유지", consumed.isEmpty() || consumed.get(49) == 50);
        });

        Check.run("6-5 CompletableFuture 병렬 호출", () -> {
            ExecutorService pool = Executors.newFixedThreadPool(8);
            try {
                ProductInfoAggregator aggregator = new ProductInfoAggregator(new SlowApi(), pool);
                long start = System.currentTimeMillis();
                ProductInfoAggregator.ProductInfo info = aggregator.load(7L);
                long elapsed = System.currentTimeMillis() - start;
                Check.equal("결과", new ProductInfoAggregator.ProductInfo(7L, 15_000L, 3, 0.0), info);
                Check.isTrue("병렬 + 타임아웃 → 약 500ms 안팎 (실제 " + elapsed + "ms, 순차면 2300ms 이상)", elapsed < 1200);
            } finally {
                pool.shutdownNow();
            }
        });

        Check.run("6-6 가상 스레드 + 세마포어", () -> {
            VirtualThreadLimiter limiter = new VirtualThreadLimiter(50);
            long start = System.currentTimeMillis();
            limiter.runAll(1_000);
            long elapsed = System.currentTimeMillis() - start;
            Check.isTrue("동시 실행 수가 50 이하 (관찰값 " + limiter.maxObservedConcurrency() + ")",
                    limiter.maxObservedConcurrency() > 0 && limiter.maxObservedConcurrency() <= 50);
            Check.isTrue("1000개 × 100ms 를 50개씩 → 약 2초 (실제 " + elapsed + "ms)", elapsed >= 1900);
        });
    }

    private static long runCounter(Counters.Counter counter, int threads, int perThread) {
        CountDownLatch done = new CountDownLatch(threads);
        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int t = 0; t < threads; t++) {
                pool.submit(() -> {
                    try {
                        for (int i = 0; i < perThread; i++) counter.increment();
                    } finally {
                        done.countDown();
                    }
                });
            }
            await(done);
        }
        return counter.get();
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void join(Thread t) {
        try {
            t.join(5_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** 가격·재고 300ms, 평점 2000ms (타임아웃 대상) */
    static class SlowApi implements ProductInfoAggregator.ExternalApi {
        @Override
        public long fetchPrice(long id) {
            sleep(300);
            return 15_000;
        }

        @Override
        public int fetchStock(long id) {
            sleep(300);
            return 3;
        }

        @Override
        public double fetchRating(long id) {
            sleep(2_000);
            return 4.5;
        }

        private static void sleep(long ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
