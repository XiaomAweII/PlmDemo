package com.example.debounce.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 订单服务
 */
@Slf4j
@Service
public class OrderService {

    /**
     * 创建订单
     *
     * @param userId      用户ID
     * @param productId   商品ID
     * @param amount      数量
     * @param totalPrice  总价
     * @return 订单ID
     */
    public String createOrder(String userId, String productId, int amount, BigDecimal totalPrice) {
        // 模拟订单创建耗时
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String orderId = String.format("ORDER_%s_%s_%d", userId, productId, System.currentTimeMillis());
        log.info("Created order: {}, userId: {}, productId: {}, amount: {}, totalPrice: {}",
                orderId, userId, productId, amount, totalPrice);
        return orderId;
    }

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     */
    public void cancelOrder(String orderId, String userId) {
        // 模拟订单取消耗时
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("Cancelled order: {}, userId: {}", orderId, userId);
    }
}