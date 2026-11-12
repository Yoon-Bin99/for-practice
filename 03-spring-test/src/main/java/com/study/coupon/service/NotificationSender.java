package com.study.coupon.service;

/** 알림 발송 (문자, 푸시). 반환값이 없는 외부 부수 효과 → 행위 검증(verify) 대상 */
public interface NotificationSender {

    void send(Long memberId, String message);
}
