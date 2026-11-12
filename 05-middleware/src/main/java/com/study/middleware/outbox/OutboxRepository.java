package com.study.middleware.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop100ByStatusOrderByIdAsc(OutboxStatus status);

    long countByStatus(OutboxStatus status);
}
