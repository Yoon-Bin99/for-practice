package com.study.middleware.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 비즈니스 데이터와 "같은 트랜잭션"으로 저장되는 발행 대기 이벤트 */
@Entity
@Table(indexes = @Index(name = "idx_outbox_status_id", columnList = "status, id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent {

    private static final int MAX_RETRY = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String eventId;

    @Column(nullable = false, length = 100)
    private String topic;

    /** 파티션 키 (같은 키 = 같은 파티션 = 순서 보장) */
    @Column(nullable = false, length = 50)
    private String aggregateId;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OutboxStatus status;

    private int retryCount;

    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    public OutboxEvent(String eventId, String topic, String aggregateId, String payload) {
        this.eventId = eventId;
        this.topic = topic;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void markSent() {
        this.status = OutboxStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void markRetry() {
        this.retryCount++;
        if (retryCount >= MAX_RETRY) {
            this.status = OutboxStatus.FAILED;      // 알림 대상
        }
    }
}
