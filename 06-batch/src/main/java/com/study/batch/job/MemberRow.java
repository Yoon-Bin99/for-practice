package com.study.batch.job;

import java.time.LocalDateTime;

public record MemberRow(Long id, String name, String email, LocalDateTime lastLoginAt) {

    public boolean isVip() {
        return email.endsWith("@vip.com");
    }
}
