package com.xmzs.coedit.task;

import com.xmzs.coedit.service.ICoFolderPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 文件夹权限清理定时任务
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FolderPermissionCleanTask {

    private final ICoFolderPermissionService folderPermissionService;

    /**
     * 清理过期权限
     * 每天凌晨3点执行
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanExpiredPermissions() {
        log.info("开始执行文件夹权限清理任务");
        try {
            Integer count = folderPermissionService.cleanExpiredPermissions();
            log.info("文件夹权限清理任务完成，清理数量: {}", count);
        } catch (Exception e) {
            log.error("文件夹权限清理任务执行失败", e);
        }
    }
}

