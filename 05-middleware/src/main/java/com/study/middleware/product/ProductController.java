package com.study.middleware.product;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductCacheService cacheService;
    private final ProductAnnotationCacheService annotationCacheService;

    @GetMapping("/{id}/cache-aside")
    public Map<String, Object> cacheAside(@PathVariable Long id) {
        long start = System.currentTimeMillis();
        ProductCacheService.CacheResult result = cacheService.get(id);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("source", result.source());
        body.put("elapsedMs", System.currentTimeMillis() - start);
        body.put("product", result.product());
        return body;
    }

    @PostMapping("/{id}/cache-aside/price")
    public void changePrice(@PathVariable Long id, @RequestParam long price) {
        cacheService.changePrice(id, price);
    }

    @GetMapping("/{id}/cacheable")
    public ProductView cacheable(@PathVariable Long id) {
        return annotationCacheService.get(id);
    }

    @PostMapping("/{id}/cacheable/price")
    public void changePriceCacheable(@PathVariable Long id, @RequestParam long price) {
        annotationCacheService.changePrice(id, price);
    }
}
