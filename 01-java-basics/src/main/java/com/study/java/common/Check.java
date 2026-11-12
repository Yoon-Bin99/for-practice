package com.study.java.common;

import java.util.Objects;

/**
 * 실습 확인용 출력 도우미 (테스트 프레임워크 대신 main 메서드에서 결과를 눈으로 확인).
 */
public final class Check {

    private Check() {
    }

    public static void equal(String label, Object expected, Object actual) {
        boolean ok = Objects.equals(expected, actual);
        System.out.printf("%s %s%n    기대: %s%n    실제: %s%n", ok ? "✅" : "❌", label, expected, actual);
    }

    public static void isTrue(String label, boolean condition) {
        System.out.printf("%s %s%n", condition ? "✅" : "❌", label);
    }

    /** TODO를 아직 구현하지 않아 UnsupportedOperationException이 나면 안내만 출력하고 넘어간다. */
    public static void run(String title, Runnable exercise) {
        System.out.println("\n=== " + title + " ===");
        try {
            exercise.run();
        } catch (UnsupportedOperationException e) {
            System.out.println("⏳ 아직 구현하지 않은 TODO가 있습니다: " + e.getMessage());
        }
    }
}
