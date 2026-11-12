package com.study.coupon.domain;

public enum Grade {
    BRONZE(0), SILVER(5), GOLD(10);

    private final int discountRate;

    Grade(int discountRate) {
        this.discountRate = discountRate;
    }

    public int discountRate() {
        return discountRate;
    }
}
