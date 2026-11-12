package com.study.java.ex02_collection;

import com.study.java.common.Check;

import java.util.List;

public class Ex02Main {

    public static void main(String[] args) {
        Check.run("2-1, 2-2 해시와 인덱스", () -> {
            Check.equal("null 키의 hash", 0, MyHashMap.hash(null));
            int h = MyHashMap.hash("order");
            Check.equal("(n-1) & hash == hash % n (양수일 때)", Math.floorMod(h, 16), MyHashMap.indexFor(h, 16));
        });

        Check.run("2-3, 2-4 put/get", () -> {
            MyHashMap<String, Integer> map = new MyHashMap<>();
            Check.equal("처음 put 은 null 반환", null, map.put("kim", 1));
            Check.equal("같은 키 put 은 이전 값 반환", 1, map.put("kim", 2));
            Check.equal("get(kim)", 2, map.get("kim"));
            Check.equal("없는 키", null, map.get("lee"));
            map.put(null, 99);
            Check.equal("null 키도 저장 가능", 99, map.get(null));
            Check.equal("size", 2, map.size());
        });

        Check.run("2-5 resize", () -> {
            MyHashMap<Integer, String> map = new MyHashMap<>();
            for (int i = 0; i < 1000; i++) {
                map.put(i, "v" + i);
            }
            Check.equal("1000개 저장 후 size", 1000, map.size());
            Check.isTrue("용량이 늘어났다 (16 → " + map.capacity() + ")", map.capacity() >= 2048);
            Check.equal("확장 후에도 조회 가능 get(777)", "v777", map.get(777));
            Check.isTrue("가장 긴 버킷 길이가 작다: " + map.longestChain(), map.longestChain() <= 3);
        });

        Check.run("2-6, 2-7 LRU 캐시", () -> {
            LruCache<String, String> cache = new LruCache<>(3);
            cache.put("a", "A");
            cache.put("b", "B");
            cache.put("c", "C");
            cache.get("a");          // a 를 최근에 사용
            cache.put("d", "D");     // 가득 찼으므로 가장 오래 안 쓴 b 가 빠져야 함
            Check.equal("남은 키 (오래된 순)", List.of("c", "a", "d"), List.copyOf(cache.keySet()));
        });
    }
}
