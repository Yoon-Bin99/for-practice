package com.study.auth.api;

import com.study.auth.jwt.LoginMember;
import com.study.auth.member.MemberService;
import com.study.auth.order.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final OrderQueryService orderQueryService;

    /** 로그인한 사용자 정보 (필터가 넣은 LoginMember 를 그대로 받는다) */
    @GetMapping("/api/me")
    public MemberService.MemberView me(@AuthenticationPrincipal LoginMember loginMember) {
        return memberService.me(loginMember.memberId());
    }

    /** 메서드 보안 실습: 본인 또는 관리자만 */
    @GetMapping("/api/members/{memberId}/orders")
    public List<OrderQueryService.OrderView> orders(@PathVariable Long memberId) {
        return orderQueryService.findOrders(memberId);
    }

    /** URL 보안 실습: /api/admin/** 는 ADMIN 만 */
    @GetMapping("/api/admin/members")
    public List<MemberService.MemberView> allMembers() {
        return memberService.findAll();
    }

    /** 블라인드 인덱스 실습: 암호화된 전화번호로 검색 */
    @GetMapping("/api/admin/members/search")
    public List<MemberService.MemberView> searchByPhone(@RequestParam String phone) {
        return memberService.searchByPhone(phone);
    }
}
