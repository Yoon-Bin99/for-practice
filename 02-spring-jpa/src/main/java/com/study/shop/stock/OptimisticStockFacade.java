package com.study.shop.stock;

import com.study.shop.common.exception.BusinessException;
import com.study.shop.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * [실습 5-C] 낙관적 락 + 재시도
 *
 * 왜 Facade(별도 빈)인가?
 *  - 재시도는 "트랜잭션 바깥"에서 해야 매번 새 트랜잭션으로 최신 version 을 다시 읽는다.
 *  - 트랜잭션 안에서 다시 시도하면 같은 영속성 컨텍스트의 옛 값을 계속 보게 된다.
 */
@Component
@RequiredArgsConstructor
public class OptimisticStockFacade {

    private static final int MAX_RETRY = 100;

    private final StockService stockService;

    /**
     * TODO 5-5. 낙관적 락 충돌 시 재시도
     *  - for (attempt = 1..MAX_RETRY) { try { stockService.decreaseWithVersionCheck(...); return; }
     *                                   catch (OptimisticLockingFailureException e) { 잠깐 쉬고 다시 } }
     *  - 다 실패하면 BusinessException(STOCK_CONFLICT)
     *  - 쉬는 시간: 10~30ms 정도 랜덤 (ThreadLocalRandom) → 모두가 같은 순간에 다시 부딪히는 것을 피한다
     *  - 재고 부족(BusinessException)은 재시도해도 소용없으므로 잡지 않고 그대로 던진다.
     *  - 관찰: 충돌이 많은 상황에서는 비관적 락보다 훨씬 느리다 → "충돌이 드물 때" 쓰는 전략인 이유
     */
    public void decrease(Long productId, int quantity) {
        throw new UnsupportedOperationException("TODO 5-5 OptimisticStockFacade.decrease");
    }

    private static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
