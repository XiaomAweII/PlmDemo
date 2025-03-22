package com.example.debounce.common.aspect;

import com.example.debounce.common.annotation.Debounce;
import com.example.debounce.common.strategy.DebounceStrategy;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 防抖切面，使用AOP实现接口防抖功能
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DebounceAspect implements ApplicationContextAware {

    private final StringRedisTemplate redisTemplate;
    private ApplicationContext applicationContext;

    @Around("@annotation(com.example.debounce.common.annotation.Debounce)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        HttpServletRequest request = attributes.getRequest();

        // 获取防抖注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Debounce debounce = method.getAnnotation(Debounce.class);

        // 判断是否启用防抖
        if (!debounce.enabled()) {
            return joinPoint.proceed();
        }

        // 获取防抖策略
        DebounceStrategy strategy;
        try {
            strategy = applicationContext.getBean(debounce.strategy());
        } catch (BeansException e) {
            strategy = debounce.strategy().getDeclaredConstructor().newInstance();
        }

        // 生成防抖key
        String key = strategy.generateKey(request, debounce.prefix());
        log.debug("Debounce key: {}", key);

        // 尝试获取分布式锁
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(key, "1", debounce.value(), TimeUnit.MILLISECONDS);
        if (locked == null || !locked) {
            log.debug("Request is debounced: {}", key);
            throw new IllegalStateException(debounce.message());
        }

        try {
            // 执行目标方法
            return joinPoint.proceed();
        } finally {
            // 释放锁
            redisTemplate.delete(key);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}