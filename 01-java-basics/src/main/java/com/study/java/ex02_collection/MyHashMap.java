package com.study.java.ex02_collection;

import java.util.Objects;

/**
 * [실습 2-A] HashMap 직접 구현 (관련 문서: JAVA·JVM 02.컬렉션/02.Map과Set내부구조.md, CS 01.자료구조/04.해시테이블.md)
 *
 * 구조: 배열(버킷) + 각 칸은 연결 리스트(체이닝)
 *
 *   table[0] → null
 *   table[1] → Node(k1) → Node(k9)     ← 해시 충돌: 같은 칸에 연결
 *   table[2] → Node(k2)
 *   ...
 *
 * 핵심 포인트
 *  - 키 → 인덱스: hash 를 섞고(hash ^ hash>>>16) 배열 크기로 나눈 나머지((n-1) & hash)
 *  - 같은 키 판단: hash 가 같고 equals 가 true
 *  - 원소 수가 capacity * 0.75 를 넘으면 배열을 2배로 늘리고 모든 노드를 다시 배치(resize)
 *  (실제 JDK 는 한 칸에 8개 이상 쌓이면 Red-Black 트리로 바꾸지만 여기서는 생략)
 */
public class MyHashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;      // 항상 2의 거듭제곱
    private static final float LOAD_FACTOR = 0.75f;

    static final class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Node<K, V>[] table;
    private int size;
    private int threshold;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        this.table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
        this.threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
    }

    /**
     * TODO 2-1. 해시 값 섞기
     *  - null 키는 0
     *  - 그 외에는 h = key.hashCode(); return h ^ (h >>> 16);
     *  - 왜? 인덱스를 (n-1) & hash 로 구하면 "하위 비트"만 쓰이므로, 상위 비트를 하위로 섞어 충돌을 줄인다.
     */
    static int hash(Object key) {
        throw new UnsupportedOperationException("TODO 2-1 hash");
    }

    /**
     * TODO 2-2. 버킷 인덱스 계산
     *  - length 가 2의 거듭제곱이면 hash % length 와 (length - 1) & hash 가 같다. (비트 연산이 더 빠름)
     */
    static int indexFor(int hash, int length) {
        throw new UnsupportedOperationException("TODO 2-2 indexFor");
    }

    /**
     * TODO 2-3. 저장
     *  1) hash, index 계산
     *  2) 해당 버킷의 연결 리스트를 따라가며 "같은 키"(hash 같고 Objects.equals)를 찾으면 값을 바꾸고 이전 값을 반환
     *  3) 없으면 새 Node 를 버킷 맨 앞에 추가, size 증가
     *  4) size 가 threshold 를 넘으면 resize()
     *  5) 새로 추가했으면 null 반환
     */
    public V put(K key, V value) {
        throw new UnsupportedOperationException("TODO 2-3 put");
    }

    /**
     * TODO 2-4. 조회
     *  - put 과 같은 방법으로 버킷을 찾고, 연결 리스트에서 같은 키를 찾아 값을 반환 (없으면 null)
     *  - hash 를 먼저 비교하는 이유: equals 보다 훨씬 싸다
     */
    public V get(Object key) {
        throw new UnsupportedOperationException("TODO 2-4 get");
    }

    /**
     * TODO 2-5. 배열 2배 확장 (rehash)
     *  - 새 배열(길이 2배)을 만들고, 기존 모든 노드를 "새 길이 기준 인덱스"로 다시 넣는다.
     *  - threshold 도 새 용량 * LOAD_FACTOR 로 갱신
     *  - 왜? 원소가 많아지면 한 버킷의 리스트가 길어져 조회가 O(1) → O(n) 으로 나빠진다.
     */
    @SuppressWarnings("unchecked")
    void resize() {
        throw new UnsupportedOperationException("TODO 2-5 resize");
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return table.length;
    }

    /** 가장 긴 버킷의 길이 (충돌 정도 확인용) */
    public int longestChain() {
        int max = 0;
        for (Node<K, V> head : table) {
            int len = 0;
            for (Node<K, V> e = head; e != null; e = e.next) len++;
            max = Math.max(max, len);
        }
        return max;
    }
}
