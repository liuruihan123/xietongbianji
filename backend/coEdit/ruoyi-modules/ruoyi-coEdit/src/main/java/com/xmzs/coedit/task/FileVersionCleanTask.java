package com.xmzs.coedit.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.mapper.CoFileMapper;
import com.xmzs.coedit.service.ICoFileVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文件版本清理定时任务
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class FileVersionCleanTask {

    private final ICoFileVersionService versionService;
    private final CoFileMapper fileMapper;

    /**
     * 清理旧版本
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanOldVersions() {
        log.info("开始执行文件版本清理任务");

        try {
            // 获取所有文件
            List<CoFile> files = fileMapper.selectList(Wrappers.emptyWrapper());

            int totalCleaned = 0;
            for (CoFile file : files) {
                try {
                    // 每个文件保留最近10个版本
                    Integer cleaned = versionService.cleanOldVersions(file.getId(), 10);
                    totalCleaned += cleaned;
                } catch (Exception e) {
                    log.error("清理文件版本失败: fileId={}", file.getId(), e);
                }
            }

            log.info("文件版本清理任务完成，共清理 {} 个旧版本", totalCleaned);
        } catch (Exception e) {
            log.error("文件版本清理任务执行失败", e);
        }
    }

}

