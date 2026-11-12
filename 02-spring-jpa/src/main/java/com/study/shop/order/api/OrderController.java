package com.study.shop.order.api;

import com.study.shop.order.domain.Order;
import com.study.shop.order.repository.OrderQueryRepository;
import com.study.shop.order.repository.OrderSearchCondition;
import com.study.shop.order.repository.OrderSummary;
import com.study.shop.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderQueryRepository orderQueryRepository;

    @PostMapping
    public ResponseEntity<Map<String, Long>> create(@RequestBody @Valid OrderCreateRequest request) {
        Long orderId = orderService.placeOrder(request);
        return ResponseEntity.created(URI.create("/api/orders/" + orderId)).body(Map.of("orderId", orderId));
    }

    @GetMapping("/{orderId}")
    public OrderResponse get(@PathVariable Long orderId) {
        return orderService.find(orderId);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long orderId) {
        orderService.cancel(orderId);
        return ResponseEntity.noContent().build();
    }

    /** /api/orders?status=ORDERED&memberName=김&minPrice=10000&from=2026-01-01&page=0&size=10 */
    @GetMapping
    public Page<OrderSummary> search(@ModelAttribute OrderSearchCondition cond, Pageable pageable) {
        return orderQueryRepository.search(cond, pageable);
    }

    /** OSIV 관찰용: 서비스 밖(트랜잭션 종료 후)에서 지연 로딩 → LazyInitializationException → 500 */
    @GetMapping("/{orderId}/osiv-test")
    public Map<String, String> osivTest(@PathVariable Long orderId) {
        Order order = orderService.findEntity(orderId);
        return Map.of("memberName", order.getMember().getName());
    }
}
