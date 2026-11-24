package com.xmzs.coedit.task;

import com.xmzs.coedit.service.ICoFilePreviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 缩略图清理定时任务
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ThumbnailCleanTask {

    private final ICoFilePreviewService filePreviewService;

    /**
     * 清理过期缩略图
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredThumbnails() {
        log.info("开始执行缩略图清理任务");
        try {
            Integer count = filePreviewService.cleanExpiredThumbnails();
            log.info("缩略图清理任务完成，清理数量: {}", count);
        } catch (Exception e) {
            log.error("缩略图清理任务执行失败", e);
        }
    }
}

