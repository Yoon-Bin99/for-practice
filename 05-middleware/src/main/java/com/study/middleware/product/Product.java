package com.study.middleware.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private long price;

    private int stock;

    public Product(String name, long price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public void changePrice(long price) {
        this.price = price;
    }

    public void decreaseStock(int quantity) {
        if (stock < quantity) {
            throw new IllegalStateException("재고 부족");
        }
        stock -= quantity;
    }

    public void resetStock(int stock) {
        this.stock = stock;
    }
}
