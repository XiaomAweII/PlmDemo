package com.example.debounce.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 分布式防抖示例应用
 * @author maxiaoweii
 */
@SpringBootApplication
@ComponentScan("com.example.debounce")
public class DebounceWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(DebounceWebApplication.class, args);
    }
}