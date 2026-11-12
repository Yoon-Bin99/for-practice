package com.study.middleware.practice;

import com.study.middleware.common.BusinessException;
import com.study.middleware.coupon.FirstComeCouponService;
import com.study.middleware.order.OrderService;
import com.study.middleware.ranking.RankingService;
import com.study.middleware.ratelimit.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final OrderService orderService;
    private final FirstComeCouponService couponService;
    private final RankingService rankingService;
    private final RateLimiter rateLimiter;

    /** [K1~K4] 주문 → Outbox → Kafka → 포인트 적립. amount 를 음수로 주면 DLT 실습 */
    @PostMapping("/api/orders")
    public Map<String, Long> order(@RequestParam Long memberId, @RequestParam long amount) {
        return Map.of("orderId", orderService.placeOrder(memberId, amount));
    }

    /** [R4] 선착순 쿠폰 (한 사람 기준 [R6] 레이트 리밋: 10초에 5번) */
    @PostMapping("/api/coupons/{couponId}/issue")
    public Map<String, String> issueCoupon(@PathVariable Long couponId, @RequestParam Long memberId) {
        if (!rateLimiter.tryAcquire("coupon:" + memberId, 5, 10)) {
            throw new BusinessException(HttpStatus.TOO_MANY_REQUESTS, "R001", "요청이 너무 많습니다. 잠시 후 다시 시도해 주세요.");
        }
        couponService.issue(couponId, memberId, 100);
        return Map.of("result", "발급 성공");
    }

    /** [R5] 랭킹 */
    @PostMapping("/api/ranking")
    public void addScore(@RequestParam String member, @RequestParam double score) {
        rankingService.addScore(member, score);
    }

    @GetMapping("/api/ranking")
    public List<RankingService.Rank> top(@RequestParam(defaultValue = "10") int n) {
        return rankingService.top(n);
    }

    @GetMapping("/api/ranking/{member}")
    public Map<String, Long> myRank(@PathVariable String member) {
        return Map.of("rank", rankingService.myRank(member));
    }
}
