package com.example.debounce.web.controller;

import com.example.debounce.common.annotation.Debounce;
import com.example.debounce.web.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     * 使用防抖注解防止重复提交订单
     */
    @Debounce(value = 5000, message = "订单正在处理中，请勿重复提交", prefix = "order")
    @PostMapping
    public ResponseEntity<Map<String, String>> createOrder(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        String productId = (String) request.get("productId");
        int amount = (Integer) request.get("amount");
        BigDecimal totalPrice = new BigDecimal(request.get("totalPrice").toString());

        String orderId = orderService.createOrder(userId, productId, amount, totalPrice);
        return ResponseEntity.ok(Collections.singletonMap("订单处理成功 orderId", orderId));
    }

    /**
     * 取消订单
     * 使用防抖注解防止重复取消订单
     */
    @Debounce(value = 3000, message = "订单取消请求正在处理中，请勿重复操作", prefix = "order")
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderId,
                                          @RequestHeader("X-User-Id") String userId) {
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.ok().build();
    }
}