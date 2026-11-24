package com.xmzs.coedit.task;

import com.xmzs.coedit.service.ICoFileConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 转换记录清理定时任务
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConversionCleanTask {

    private final ICoFileConversionService conversionService;

    /**
     * 保留天数，默认30天
     */
    @Value("${coedit.conversion.keep-days:30}")
    private Integer keepDays;

    /**
     * 清理过期的转换记录
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredConversions() {
        log.info("开始清理过期的转换记录，保留天数: {}", keepDays);
        
        try {
            Integer count = conversionService.cleanExpiredConversions(keepDays);
            log.info("清理过期转换记录完成，共清理 {} 条记录", count);
        } catch (Exception e) {
            log.error("清理过期转换记录失败", e);
        }
    }
}

