package com.example.debounce.web.controller;

import com.example.debounce.common.annotation.Debounce;
import com.example.debounce.web.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

/**
 * 支付控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 处理支付请求
     * 使用防抖注解防止重复支付
     */
    @Debounce(value = 10000, message = "支付正在处理中，请勿重复提交", prefix = "payment")
    @PostMapping("/pay")
    public ResponseEntity<Map<String, String>> processPayment(@RequestBody Map<String, Object> request) {
        String orderId = (String) request.get("orderId");
        String userId = (String) request.get("userId");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        String transactionId = paymentService.processPayment(orderId, userId, amount);
        return ResponseEntity.ok(Collections.singletonMap("transactionId", transactionId));
    }

    /**
     * 处理退款请求
     * 使用防抖注解防止重复退款
     */
    @Debounce(value = 8000, message = "退款正在处理中，请勿重复提交", prefix = "refund")
    @PostMapping("/refund")
    public ResponseEntity<Map<String, String>> processRefund(@RequestBody Map<String, Object> request) {
        String orderId = (String) request.get("orderId");
        String userId = (String) request.get("userId");
        String transactionId = (String) request.get("transactionId");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        String refundId = paymentService.processRefund(orderId, userId, transactionId, amount);
        return ResponseEntity.ok(Collections.singletonMap("refundId", refundId));
    }
}