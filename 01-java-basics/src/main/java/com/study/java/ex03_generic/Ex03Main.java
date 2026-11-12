package com.study.java.ex03_generic;

import com.study.java.common.Check;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Ex03Main {

    public static void main(String[] args) {
        Check.run("3-1 copy (PECS)", () -> {
            List<Integer> integers = List.of(1, 2, 3);
            List<Number> numbers = new ArrayList<>(List.of(0.5));
            List<Object> objects = new ArrayList<>();

            // TODO 3-1 의 시그니처를 고친 뒤 아래 두 줄의 주석을 풀어 보세요.
            // CollectionUtils.copy(numbers, integers);   CollectionUtils.copy(objects, integers);

            Check.equal("numbers", List.of(0.5, 1, 2, 3), numbers);
            Check.equal("objects", List.of(1, 2, 3), objects);
        });

        Check.run("3-2 max", () -> {
            Comparator<Number> byDouble = Comparator.comparingDouble(Number::doubleValue);
            Integer max = CollectionUtils.max(List.of(3, 9, 4), byDouble);   // Comparator<Number> 로 Integer 비교
            Check.equal("max", 9, max);
            Check.equal("문자열 길이 기준 max", "banana",
                    CollectionUtils.max(List.of("kiwi", "banana", "fig"), Comparator.comparingInt(String::length)));
        });
    }
}
