package com.study.middleware.point;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 이미 처리한 이벤트 ID (PK = eventId → 같은 이벤트를 두 번 저장할 수 없다) */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessedEvent {

    @Id
    private String eventId;

    private LocalDateTime processedAt;

    public ProcessedEvent(String eventId) {
        this.eventId = eventId;
        this.processedAt = LocalDateTime.now();
    }
}
