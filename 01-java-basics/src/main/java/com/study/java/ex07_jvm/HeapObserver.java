package com.study.java.ex07_jvm;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

/**
 * [실습 7-B] 힙 사용량 관찰 (관련 문서: JAVA·JVM 04.JVM/01.JVM구조와메모리.md, 02.가비지컬렉션.md)
 *
 *  - Runtime: totalMemory(현재 JVM 이 OS 에서 확보한 힙), freeMemory(그중 비어 있는 양), maxMemory(-Xmx)
 *  - 사용 중인 힙 = totalMemory - freeMemory
 */
public final class HeapObserver {

    private HeapObserver() {
    }

    /**
     * TODO 7-2. 현재 사용 중인 힙을 MB 단위로 반환
     *  - Runtime rt = Runtime.getRuntime();  (total - free) / 1024 / 1024
     */
    public static long usedHeapMb() {
        throw new UnsupportedOperationException("TODO 7-2 usedHeapMb");
    }

    public static long maxHeapMb() {
        return Runtime.getRuntime().maxMemory() / 1024 / 1024;
    }

    /** MXBean 으로 보는 방법 (완성됨: 모니터링 도구가 쓰는 API) */
    public static String heapSummary() {
        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        return String.format("used=%dMB committed=%dMB max=%dMB",
                heap.getUsed() >> 20, heap.getCommitted() >> 20, heap.getMax() >> 20);
    }
}
