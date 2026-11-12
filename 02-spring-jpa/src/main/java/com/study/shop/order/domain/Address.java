package com.study.shop.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 값 타입: orders 테이블의 zipcode, street, detail 컬럼으로 펼쳐진다. 불변(setter 없음). */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Column(length = 10)
    private String zipcode;

    private String street;

    private String detail;

    public Address(String zipcode, String street, String detail) {
        this.zipcode = zipcode;
        this.street = street;
        this.detail = detail;
    }
}
