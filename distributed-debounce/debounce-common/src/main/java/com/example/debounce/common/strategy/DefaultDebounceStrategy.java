package com.example.debounce.common.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认防抖策略实现
 * 支持基于用户ID、IP和请求参数的防抖
 * @author maxiaoweii
 */
public class DefaultDebounceStrategy implements DebounceStrategy {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String generateKey(HttpServletRequest request, String prefix) {
        StringBuilder keyBuilder = new StringBuilder("debounce:");
        
        // 添加前缀
        if (StringUtils.hasText(prefix)) {
            keyBuilder.append(prefix).append(":");
        }
        
        // 添加请求路径
        keyBuilder.append(request.getRequestURI()).append(":");
        
        // 添加用户标识
        keyBuilder.append(getUserIdentifier(request)).append(":");
        
        // 添加请求参数的哈希值
        String paramsHash = generateParamsHash(request);
        if (paramsHash != null) {
            keyBuilder.append(paramsHash);
        }
        
        return keyBuilder.toString();
    }

    /**
     * 生成请求参数的哈希值
     */
    private String generateParamsHash(HttpServletRequest request) {
        try {
            Map<String, Object> params = new HashMap<>();
            
            // 获取URL参数
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement();
                params.put(name, request.getParameter(name));
            }
            
            // 获取请求体参数
            String contentType = request.getContentType();
            if (contentType != null && contentType.contains("application/json")) {
                try {
                    Object bodyParams = objectMapper.readValue(request.getInputStream(), Object.class);
                    params.put("body", bodyParams);
                } catch (IOException e) {
                    // 忽略请求体解析错误
                }
            }
            
            // 生成参数哈希值
            String paramsJson = objectMapper.writeValueAsString(params);
            return DigestUtils.md5DigestAsHex(paramsJson.getBytes());
        } catch (Exception e) {
            return null;
        }
    }
}