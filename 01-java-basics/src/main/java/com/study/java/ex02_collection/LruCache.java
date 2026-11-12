package com.study.java.ex02_collection;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * [실습 2-B] LRU 캐시 (Least Recently Used: 가장 오래 안 쓴 것부터 버린다)
 *
 * LinkedHashMap 은 "순서를 기억하는 HashMap" 이다.
 *  - accessOrder = false (기본): 넣은 순서
 *  - accessOrder = true        : get/put 으로 "접근한 순서" → 가장 오래 안 쓴 항목이 맨 앞
 * 그리고 put 직후 removeEldestEntry(맨 앞 항목) 를 호출해 true 면 그 항목을 지운다.
 *
 * 핵심 포인트: 직접 연결 리스트를 짜지 않아도 두 가지 설정만으로 LRU 가 완성된다.
 * (로컬 캐시, Caffeine 같은 라이브러리의 기본 아이디어 / 미들웨어 01.Redis/02.캐시전략.md)
 */
public class LruCache<K, V> extends LinkedHashMap<K, V> {

    private final int maxSize;

    public LruCache(int maxSize) {
        // TODO 2-6. 접근 순서 모드로 부모 생성자 호출
        //  - LinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder)
        //  - 세 번째 인자가 핵심
        super(); // TODO 2-6: 지금은 "넣은 순서" 모드
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        // TODO 2-7. 최대 크기를 넘으면 가장 오래된 항목 제거
        //  - true 를 반환하면 LinkedHashMap 이 eldest 를 지워 준다.
        return false; // TODO 2-7
    }
}
