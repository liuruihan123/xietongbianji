package com.xmzs.common.config;

import com.xmzs.common.core.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 支付配置信息
 *
 * @author Admin
 */
@RequiredArgsConstructor
@Configuration
@Slf4j
public class PayInit {

    private final ConfigService configService;

    private PayConfig payConfig;

    @Bean
    @Scope("singleton")
    public PayConfig payConfig() {
        if (payConfig == null) {
            payConfig = new PayConfig();
            // 延迟初始化：等待 Spring 容器完全启动后再加载配置
            // 避免 MapStruct Converter 未完全初始化导致的偶发性启动失败
        }
        return payConfig;
    }

    /**
     * 在 Spring 容器完全启动后初始化支付配置
     * 确保所有 Bean（包括 MapStruct 转换器）都已完全初始化
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("初始化支付配置...");
        updatePayConfig();
        log.info("支付配置初始化完成");
    }

    @Scheduled(fixedDelay = 10000)  // 每10秒检查一次
    public void updatePayConfig() {
        if (payConfig == null) {
            return;
        }
        payConfig.setType("wxpay");
        payConfig.setDevice("pc");
        payConfig.setSign_type("MD5");
        payConfig.setPid(getKey("pid"));
        payConfig.setKey(getKey("key"));
        payConfig.setPayUrl(getKey("payUrl"));
        payConfig.setNotify_url(getKey("notify_url"));
        payConfig.setReturn_url(getKey("return_url"));
    }

    public String getKey(String key){
        return configService.getConfigValue("pay", key);
    }

}
