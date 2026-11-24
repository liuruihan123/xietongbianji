package com.xmzs.coedit.task;

import com.xmzs.coedit.service.ICoEditLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 编辑锁清理定时任务
 * 定期清理过期的编辑锁
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class EditLockCleanTask {

    private final ICoEditLockService lockService;

    /**
     * 清理过期锁
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void cleanExpiredLocks() {
        log.info("开始执行编辑锁清理任务");
        
        try {
            Integer count = lockService.cleanExpiredLocks();
            
            if (count > 0) {
                log.info("编辑锁清理任务完成: 清理了{}个过期锁", count);
            } else {
                log.debug("编辑锁清理任务完成: 没有过期锁需要清理");
            }
            
        } catch (Exception e) {
            log.error("编辑锁清理任务执行失败", e);
        }
    }

}

