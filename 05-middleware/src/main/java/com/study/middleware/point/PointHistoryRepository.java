package com.study.middleware.point;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    @Query("select coalesce(sum(p.point), 0) from PointHistory p where p.memberId = :memberId")
    long sumByMemberId(@Param("memberId") Long memberId);

    long countByOrderId(Long orderId);
}
