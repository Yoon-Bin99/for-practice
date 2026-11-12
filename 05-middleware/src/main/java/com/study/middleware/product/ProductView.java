package com.study.middleware.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 캐시에 저장할 DTO. 엔티티를 그대로 캐시하지 않는다(지연 로딩 프록시 직렬화 문제, 엔티티 변경에 캐시가 묶임).
 * @Cacheable 의 GenericJackson2JsonRedisSerializer 가 역직렬화할 수 있도록 기본 생성자가 있는 일반 클래스로 만든다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductView {

    private Long id;
    private String name;
    private long price;

    public static ProductView from(Product product) {
        return new ProductView(product.getId(), product.getName(), product.getPrice());
    }
}
