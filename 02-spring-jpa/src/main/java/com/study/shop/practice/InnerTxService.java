package com.study.shop.practice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class InnerTxService {

    @Transactional
    public String inner() {
        return "트랜잭션 활성화=" + TransactionSynchronizationManager.isActualTransactionActive();
    }
}
