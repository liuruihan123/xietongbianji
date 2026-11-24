package com.xmzs.web.config;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

/**
 * 应用程序WebSocket配置
 * 设置WebSocket相关参数
 *
 * @author xmzs
 */
@Configuration
public class ApplicationWebSocketConfig {

    /**
     * ServletContext初始化器
     * 设置WebSocket相关参数，不干扰自动配置
     */
}
