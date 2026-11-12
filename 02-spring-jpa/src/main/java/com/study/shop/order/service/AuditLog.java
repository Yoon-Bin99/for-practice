package com.study.shop.order.service;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 주문 시도 기록. 주문이 실패(롤백)해도 "시도했다"는 기록은 남아야 한다. */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;

    private LocalDateTime createdAt;

    public AuditLog(String action) {
        this.action = action;
        this.createdAt = LocalDateTime.now();
    }
}
