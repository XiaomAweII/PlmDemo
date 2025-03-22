package com.example.debounce.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 支付服务
 */
@Slf4j
@Service
public class PaymentService {

    /**
     * 处理支付
     *
     * @param orderId    订单ID
     * @param userId     用户ID
     * @param amount     支付金额
     * @return 支付流水号
     */
    public String processPayment(String orderId, String userId, BigDecimal amount) {
        // 模拟支付处理耗时
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String transactionId = String.format("TXN_%s_%s_%d", orderId, userId, System.currentTimeMillis());
        log.info("Payment processed: orderId={}, userId={}, amount={}, transactionId={}",
                orderId, userId, amount, transactionId);
        return transactionId;
    }

    /**
     * 退款处理
     *
     * @param orderId       订单ID
     * @param userId        用户ID
     * @param transactionId 支付流水号
     * @param amount        退款金额
     * @return 退款流水号
     */
    public String processRefund(String orderId, String userId, String transactionId, BigDecimal amount) {
        // 模拟退款处理耗时
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String refundId = String.format("REFUND_%s_%s_%d", transactionId, userId, System.currentTimeMillis());
        log.info("Refund processed: orderId={}, userId={}, amount={}, transactionId={}, refundId={}",
                orderId, userId, amount, transactionId, refundId);
        return refundId;
    }
}