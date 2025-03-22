package com.example.debounce.common.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 跨版本兼容的默认防抖策略实现
 * 支持基于用户ID、IP和请求参数的防抖
 * @author maxiaoweii
 */
public class CompatibleDefaultDebounceStrategy implements CompatibleDebounceStrategy {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String generateKey(Map<String, Object> request, String prefix) {
        StringBuilder keyBuilder = new StringBuilder("debounce:");
        
        // 添加前缀
        if (StringUtils.hasText(prefix)) {
            keyBuilder.append(prefix).append(":");
        }
        
        // 添加请求路径
        keyBuilder.append(request.get("requestURI")).append(":");
        
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
    private String generateParamsHash(Map<String, Object> request) {
        try {
            Map<?, ?> parameters = (Map<?, ?>) request.get("parameters");
            if (parameters != null && !parameters.isEmpty()) {
                String paramsJson = objectMapper.writeValueAsString(parameters);
                return DigestUtils.md5DigestAsHex(paramsJson.getBytes());
            }
        } catch (Exception e) {
            // 如果序列化失败，返回null
            return null;
        }
        return null;
    }
}