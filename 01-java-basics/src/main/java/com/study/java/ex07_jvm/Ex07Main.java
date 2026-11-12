package com.study.java.ex07_jvm;

import com.study.java.common.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 실행 팁: IntelliJ 실행 설정의 VM options 에 -Xmx256m -Xlog:gc 를 넣으면 GC 로그도 볼 수 있다.
 */
public class Ex07Main {

    public static void main(String[] args) throws Exception {
        Check.run("7-1 ThreadLocal 정리", () -> {
            RequestHandler handler = new RequestHandler();
            ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();   // 톰캣 스레드 재사용 흉내
            try {
                Future<String> first = singleThreadPool.submit(() -> handler.handle("kim", RequestContext::currentUser));
                Future<String> second = singleThreadPool.submit(() -> handler.handle(null, RequestContext::currentUser));
                Check.equal("첫 요청의 사용자", "kim", first.get());
                Check.equal("로그인하지 않은 두 번째 요청 (같은 스레드)", null, second.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                singleThreadPool.shutdown();
            }
        });

        Check.run("7-2 힙 사용량 관찰", () -> {
            System.gc();
            long before = HeapObserver.usedHeapMb();
            List<byte[]> holder = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                holder.add(new byte[1024 * 1024]);     // 1MB × 50 = Heap 에 객체 할당, holder 가 참조 중
            }
            long allocated = HeapObserver.usedHeapMb();
            holder = null;                              // 참조 끊기 → GC 대상 (도달 불가능)
            System.gc();                                // GC 요청 (실행 보장은 아님)
            long after = HeapObserver.usedHeapMb();
            System.out.printf("    전 %dMB → 할당 후 %dMB → 참조 해제·GC 후 %dMB (최대 %dMB)%n",
                    before, allocated, after, HeapObserver.maxHeapMb());
            Check.isTrue("할당하면 사용량이 늘어난다", allocated - before >= 40);
            Check.isTrue("참조를 끊고 GC 하면 줄어든다", after < allocated);
            System.out.println("    MXBean: " + HeapObserver.heapSummary());
        });
    }
}
