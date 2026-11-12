package com.study.shop.practice;

import com.study.shop.member.Member;
import com.study.shop.member.MemberRepository;
import com.study.shop.order.domain.Address;
import com.study.shop.order.domain.Order;
import com.study.shop.order.domain.OrderItem;
import com.study.shop.order.repository.OrderRepository;
import com.study.shop.product.Product;
import com.study.shop.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 실습용 초기 데이터 (회원 10명, 상품 10개, 주문 30건).
 * TODO 1-1 ~ 1-3 을 구현하기 전에는 주문 데이터를 만들 수 없어 회원·상품만 생성된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void run(ApplicationArguments args) {
        List<Member> members = transactionTemplate.execute(status -> {
            List<Member> list = new ArrayList<>();
            String[] names = {"김민준", "이서연", "박도윤", "최하은", "정시우", "강지유", "조예준", "윤수아", "장주원", "임지호"};
            for (int i = 0; i < names.length; i++) {
                list.add(memberRepository.save(Member.create(names[i], "user" + i + "@shop.com")));
            }
            return list;
        });
        List<Product> products = transactionTemplate.execute(status -> {
            List<Product> list = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                list.add(productRepository.save(Product.create("상품" + i, i % 2 == 0 ? "book" : "digital",
                        1_000L * i, 1_000)));
            }
            return list;
        });

        try {
            transactionTemplate.executeWithoutResult(status -> {
                for (int i = 0; i < 30; i++) {
                    Member member = memberRepository.getReferenceById(members.get(i % members.size()).getId());
                    Product p1 = productRepository.findById(products.get(i % 10).getId()).orElseThrow();
                    Product p2 = productRepository.findById(products.get((i + 3) % 10).getId()).orElseThrow();
                    List<OrderItem> items = List.of(OrderItem.create(p1, 1 + i % 3), OrderItem.create(p2, 1));
                    orderRepository.save(Order.create(member, new Address("0612" + i % 10, "서울시 강남구", i + "호"), items));
                }
            });
            log.info("✅ 초기 데이터 생성: 회원 {}, 상품 {}, 주문 30", members.size(), products.size());
        } catch (UnsupportedOperationException e) {
            log.warn("⏳ 주문 초기 데이터를 만들지 못했습니다. 먼저 TODO 를 구현하세요: {}", e.getMessage());
        }
    }
}
