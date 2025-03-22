package com.example.debounce.common.annotation;

import com.example.debounce.common.strategy.DebounceStrategy;
import com.example.debounce.common.strategy.DefaultDebounceStrategy;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防抖注解，用于标记需要进行防抖处理的接口
 * @author maxiaoweii
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Debounce {
    /**
     * 防抖时间，单位毫秒
     */
    long value() default 1000;

    /**
     * 提示信息
     */
    String message() default "请求正在处理中，请稍后再试";

    /**
     * 防抖策略，默认使用DefaultDebounceStrategy
     */
    Class<? extends DebounceStrategy> strategy() default DefaultDebounceStrategy.class;

    /**
     * 是否启用防抖，可以通过配置动态控制
     */
    boolean enabled() default true;

    /**
     * 防抖key的前缀，用于区分不同业务场景
     */
    String prefix() default "";
}