package com.example.debounce.common.strategy;

import javax.servlet.http.HttpServletRequest;

/**
 * 防抖策略接口，用于定义不同的防抖策略
 */
public interface DebounceStrategy {
    /**
     * 生成防抖key
     *
     * @param request HTTP请求
     * @param prefix  防抖key前缀
     * @return 防抖key
     */
    String generateKey(HttpServletRequest request, String prefix);

    /**
     * 获取用户标识
     *
     * @param request HTTP请求
     * @return 用户标识
     */
    default String getUserIdentifier(HttpServletRequest request) {
        // 默认从请求头中获取用户标识
        String userId = request.getHeader("X-User-Id");
        if (userId != null && !userId.isEmpty()) {
            return userId;
        }
        // 如果请求头中没有用户标识，则使用IP地址
        return getClientIp(request);
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求
     * @return IP地址
     */
    default String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，取第一个IP地址
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}