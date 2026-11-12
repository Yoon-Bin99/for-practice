package com.study.coupon.api;

import com.study.coupon.service.CouponService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    public record IssueRequest(@NotNull @Positive Long memberId) { }

    @PostMapping("/{code}/issue")
    @ResponseStatus(HttpStatus.CREATED)
    public CouponService.IssueResult issue(@PathVariable String code, @RequestBody @Valid IssueRequest request) {
        return couponService.issue(code, request.memberId());
    }

    @GetMapping("/issuable")
    public List<CouponService.CouponView> issuable() {
        return couponService.findIssuable();
    }
}
