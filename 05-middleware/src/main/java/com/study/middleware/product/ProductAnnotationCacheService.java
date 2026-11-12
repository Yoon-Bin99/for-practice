package com.study.middleware.product;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [실습 R2] 같은 Cache-Aside 를 스프링 캐시 추상화로 (관련 문서: 미들웨어 01.Redis/05.SpringDataRedis.md)
 *
 * Redis 에 저장되는 키: study:product::{id}   (RedisConfig 의 prefix + 캐시 이름 + :: + key)
 */
@Service
@RequiredArgsConstructor
public class ProductAnnotationCacheService {

    private final ProductRepository productRepository;

    /**
     * TODO R2-1. @Cacheable(cacheNames = "product", key = "#id") 를 붙이세요.
     *  - 캐시에 있으면 메서드 본문이 "실행되지 않는다" → 로그 "DB 조회" 가 첫 호출에만 찍히는지 확인
     *  - AOP 프록시로 동작하므로 같은 클래스 내부에서 호출하면 적용되지 않는다.
     */
    // TODO R2-1
    @Transactional(readOnly = true)
    public ProductView get(Long id) {
        org.slf4j.LoggerFactory.getLogger(getClass()).info("📀 DB 조회 productId={}", id);
        return ProductView.from(productRepository.findById(id).orElseThrow(() -> ProductCacheService.notFound(id)));
    }

    /**
     * TODO R2-2. @CacheEvict(cacheNames = "product", key = "#id") 를 붙이세요.
     */
    // TODO R2-2
    @Transactional
    public void changePrice(Long id, long price) {
        productRepository.findById(id).orElseThrow(() -> ProductCacheService.notFound(id)).changePrice(price);
    }
}
