package com.study.java.ex04_stream;

import java.time.LocalDate;

public record Order(long id, long memberId, String category, long amount, Status status, LocalDate orderedAt) {

    public enum Status { PAID, CANCELED }

    public boolean isPaid() {
        return status == Status.PAID;
    }
}
