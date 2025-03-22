package com.example.debounce.common.adapter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet请求适配器，用于将不同版本的HttpServletRequest转换为统一的Map格式
 * 支持Jakarta和Javax两个版本的Servlet API
 */
public class ServletRequestAdapter {
    /**
     * 将HttpServletRequest转换为Map格式
     *
     * @param request HttpServletRequest对象（可以是Jakarta或Javax版本）
     * @return 统一的Map格式表示
     */
    public static Map<String, Object> adapt(Object request) {
        Map<String, Object> requestMap = new HashMap<>();
        
        try {
            // 获取请求头
            Map<String, String> headers = new HashMap<>();
            Enumeration<?> headerNames = (Enumeration<?>) request.getClass().getMethod("getHeaderNames").invoke(request);
            while (headerNames.hasMoreElements()) {
                String name = (String) headerNames.nextElement();
                String value = (String) request.getClass().getMethod("getHeader", String.class).invoke(request, name);
                headers.put(name, value);
            }
            requestMap.put("headers", headers);
            
            // 获取请求参数
            Map<String, String[]> parameters = new HashMap<>();
            Map<?, ?> parameterMap = (Map<?, ?>) request.getClass().getMethod("getParameterMap").invoke(request);
            for (Map.Entry<?, ?> entry : parameterMap.entrySet()) {
                String name = (String) entry.getKey();
                String[] values = (String[]) entry.getValue();
                parameters.put(name, values);
            }
            requestMap.put("parameters", parameters);
            
            // 获取请求路径
            String requestURI = (String) request.getClass().getMethod("getRequestURI").invoke(request);
            requestMap.put("requestURI", requestURI);
            
            // 获取远程地址
            String remoteAddr = (String) request.getClass().getMethod("getRemoteAddr").invoke(request);
            requestMap.put("remoteAddr", remoteAddr);
            
        } catch (Exception e) {
            // 如果出现异常，返回一个包含基本信息的Map
            requestMap.put("headers", Collections.emptyMap());
            requestMap.put("parameters", Collections.emptyMap());
            requestMap.put("requestURI", "/");
            requestMap.put("remoteAddr", "unknown");
        }
        
        return requestMap;
    }
}