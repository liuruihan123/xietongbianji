package com.xmzs.coedit.task;

import com.xmzs.coedit.service.ICoEditorPerformanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 性能数据清理定时任务
 * 每天凌晨4点执行，清理30天前的性能数据
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PerformanceCleanTask {

    private final ICoEditorPerformanceService performanceService;

    /**
     * 清理过期的性能数据
     * 每天凌晨4点执行
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void cleanExpiredPerformanceData() {
        log.info("开始清理过期的性能数据...");
        
        try {
            // 清理30天前的数据
            Integer count = performanceService.cleanExpiredData(30);
            log.info("清理过期性能数据完成，共清理{}条记录", count);
        } catch (Exception e) {
            log.error("清理过期性能数据失败", e);
        }
    }
}

