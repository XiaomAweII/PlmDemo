package com.example.debounce.common.strategy;

import java.util.Map;

/**
 * 跨版本兼容的防抖策略接口
 * 通过适配器模式实现对不同版本Servlet API的支持
 */
public interface CompatibleDebounceStrategy {
    /**
     * 生成防抖key
     *
     * @param request HTTP请求对象的Map表示，包含请求相关的所有信息
     * @param prefix  防抖key前缀
     * @return 防抖key
     */
    String generateKey(Map<String, Object> request, String prefix);

    /**
     * 获取用户标识
     *
     * @param request HTTP请求对象的Map表示
     * @return 用户标识
     */
    default String getUserIdentifier(Map<String, Object> request) {
        // 从请求头中获取用户标识
        Object userId = ((Map<?, ?>) request.get("headers")).get("X-User-Id");
        if (userId != null && !userId.toString().isEmpty()) {
            return userId.toString();
        }
        // 如果请求头中没有用户标识，则使用IP地址
        return getClientIp(request);
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求对象的Map表示
     * @return IP地址
     */
    default String getClientIp(Map<String, Object> request) {
        Map<?, ?> headers = (Map<?, ?>) request.get("headers");
        String ip = getHeader(headers, "X-Forwarded-For");
        if (isInvalidIp(ip)) {
            ip = getHeader(headers, "Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            ip = getHeader(headers, "WL-Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            ip = getHeader(headers, "HTTP_CLIENT_IP");
        }
        if (isInvalidIp(ip)) {
            ip = getHeader(headers, "HTTP_X_FORWARDED_FOR");
        }
        if (isInvalidIp(ip)) {
            ip = request.get("remoteAddr").toString();
        }
        return ip;
    }

    /**
     * 从请求头中获取指定的值
     */
    default String getHeader(Map<?, ?> headers, String name) {
        Object value = headers.get(name);
        return value != null ? value.toString() : null;
    }

    /**
     * 判断IP是否无效
     */
    default boolean isInvalidIp(String ip) {
        return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
    }
}