package com.study.middleware.practice;

import com.study.middleware.product.Product;
import com.study.middleware.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(ApplicationArguments args) {
        productRepository.save(new Product("기계식 키보드", 89_000, 100));    // id = 1
        productRepository.save(new Product("무선 마우스", 39_000, 100));      // id = 2
    }
}
