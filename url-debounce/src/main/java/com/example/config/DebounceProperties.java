package com.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author maxiaoweii
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "debounce")
public class DebounceProperties {
    /**
     * 需要防抖的URL配置
     */
    private Map<String, UrlConfig> urls = new HashMap<>();

    @Data
    public static class UrlConfig {
        /**
         * 防抖时间(毫秒)
         */
        private long time = 1000;
        
        /**
         * 提示信息
         */
        private String message = "请求过于频繁，请稍后再试";
        
        /**
         * 是否启用
         */
        private boolean enabled = true;
    }
}