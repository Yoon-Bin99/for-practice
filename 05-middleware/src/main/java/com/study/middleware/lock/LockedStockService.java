package com.study.middleware.lock;

import com.study.middleware.common.BusinessException;
import com.study.middleware.product.Product;
import com.study.middleware.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;

/**
 * [실습 R3-3] 분산 락 + 트랜잭션의 순서
 *
 * POST /practice/lock-stock?requests=100 → 재고 100 상품에 100명 동시 주문, finalStock 0 이어야 한다.
 */
@Service
@RequiredArgsConstructor
public class LockedStockService {

    private final RedisLock redisLock;
    private final ProductRepository productRepository;
    private final TransactionTemplate transactionTemplate;

    /**
     * TODO R3-3. 락 획득 → 트랜잭션(조회·차감·커밋) → 락 해제
     *  1) redisLock.tryLock("stock:" + productId, 3초, 5초) — 실패하면 BusinessException(429, "L001", "잠시 후 다시 시도")
     *  2) try { transactionTemplate.executeWithoutResult(... findById → decreaseStock ...) }
     *     finally { redisLock.unlock(...) }
     *  - 포인트: 메서드에 @Transactional 을 붙이고 그 안에서 락을 풀면?
     *           → 락 해제 "후"에 커밋되므로, 커밋 전 틈에 다른 요청이 옛 재고를 읽는다. 락은 커밋보다 바깥에 있어야 한다.
     */
    public void decrease(Long productId, int quantity) {
        transactionTemplate.executeWithoutResult(status -> productRepository.findById(productId).orElseThrow().decreaseStock(quantity)); // TODO R3-3: 락 없음
    }
}
