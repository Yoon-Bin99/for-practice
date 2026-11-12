package com.study.shop.stock;

import com.study.shop.common.exception.BusinessException;
import com.study.shop.common.exception.ErrorCode;
import com.study.shop.product.Product;
import com.study.shop.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [실습 5-B] 재고 차감 동시성 (관련 문서: Spring JPA 03.JPA실전/05.동시성과락.md)
 *
 * POST /practice/stock/{strategy} 를 호출하면 100명이 동시에 1개씩 주문하는 상황을 시뮬레이션한다.
 * 재고 100 → 기대 결과 0. 전략별로 결과와 걸린 시간을 비교해 보자.
 */
@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepository;

    /** strategy=unsafe: 조회 → 차감 → 변경 감지. 동시에 같은 값을 읽어 갱신 분실 발생 (완성됨: 문제 관찰용) */
    @Transactional
    public void decreaseUnsafe(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        product.decreaseStock(quantity);
    }

    /**
     * TODO 5-3. strategy=pessimistic: 비관적 락
     *  - productRepository.findByIdForUpdate(...) 로 조회 (TODO 5-1 을 먼저 완성)
     *  - 이후는 unsafe 와 같다. 다른 요청은 내 트랜잭션이 커밋될 때까지 FOR UPDATE 에서 대기한다.
     *  - 포인트: 락은 "트랜잭션이 끝날 때" 풀린다 → @Transactional 범위가 곧 락 범위. 짧게 유지할 것.
     */
    @Transactional
    public void decreaseWithPessimisticLock(Long productId, int quantity) {
        throw new UnsupportedOperationException("TODO 5-3 decreaseWithPessimisticLock");
    }

    /**
     * TODO 5-4. strategy=atomic: 원자적 UPDATE
     *  - productRepository.decreaseStockAtomically(...) 호출 (TODO 5-2 를 먼저 완성)
     *  - 반환값이 0 이면 재고 부족 → BusinessException(OUT_OF_STOCK)
     */
    @Transactional
    public void decreaseAtomically(Long productId, int quantity) {
        throw new UnsupportedOperationException("TODO 5-4 decreaseAtomically");
    }

    /**
     * strategy=optimistic 에서 OptimisticStockFacade 가 호출하는 "한 번의 시도" (완성됨)
     *  - Product 의 @Version 덕분에 UPDATE ... WHERE version = ? 가 0건이면 커밋 시 예외가 난다.
     */
    @Transactional
    public void decreaseWithVersionCheck(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        product.decreaseStock(quantity);
    }
}
