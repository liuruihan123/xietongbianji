package com.xmzs.coedit.task;

import com.xmzs.coedit.service.ICoRecycleBinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 回收站自动清理定时任务
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RecycleBinCleanTask {

    private final ICoRecycleBinService recycleBinService;

    /**
     * 自动清理过期的回收站项目
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void autoCleanExpiredItems() {
        log.info("开始执行回收站自动清理任务");
        try {
            Integer count = recycleBinService.autoCleanExpiredItems();
            log.info("回收站自动清理任务完成，共清理{}个项目", count);
        } catch (Exception e) {
            log.error("回收站自动清理任务执行失败", e);
        }
    }

}

