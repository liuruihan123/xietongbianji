package com.xmzs.common.mail.config;

import com.xmzs.common.core.service.ConfigService;
import com.xmzs.common.mail.utils.MailAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * JavaMail 配置
 *
 * @author Michelle.Chung
 */

@RequiredArgsConstructor
@Configuration
@Slf4j
public class MailConfig {

    private final ConfigService configService;
    private MailAccount account;  // 缓存MailAccount实例

    @Bean
    @Scope("singleton")
    public MailAccount mailAccount() {
        if (account == null) {
            account = new MailAccount();
            // 延迟初始化：等待 Spring 容器完全启动后再加载配置
            // 避免 MapStruct Converter 未完全初始化导致的偶发性启动失败
        }
        return account;
    }

    /**
     * 在 Spring 容器完全启动后初始化邮件配置
     * 确保所有 Bean（包括 MapStruct 转换器）都已完全初始化
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("初始化邮件配置...");
        updateMailAccount();
        log.info("邮件配置初始化完成");
    }

    @Scheduled(fixedDelay = 10000)  // 每10秒检查一次
    public void updateMailAccount() {
        if (account == null) {
            return;
        }
        account.setHost(getKey("host"));
        account.setPort(NumberUtils.toInt(getKey("port"), 465));
        account.setAuth(true);
        account.setFrom(getKey("from"));
        account.setUser(getKey("user"));
        account.setPass(getKey("pass"));
        account.setSocketFactoryPort(NumberUtils.toInt(getKey("port"), 465));
        account.setStarttlsEnable(true);
        account.setSslEnable(true);
        account.setTimeout(0);
        account.setConnectionTimeout(0);
    }

    public String getKey(String key){
        return configService.getConfigValue("mail", key);
    }
}
