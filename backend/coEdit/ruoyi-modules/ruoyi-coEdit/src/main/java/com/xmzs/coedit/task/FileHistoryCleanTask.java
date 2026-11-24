package com.xmzs.coedit.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.mapper.CoFileMapper;
import com.xmzs.coedit.service.ICoFileHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文件历史清理定时任务
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileHistoryCleanTask {

    private final ICoFileHistoryService historyService;
    private final CoFileMapper fileMapper;

    @Value("${coedit.history.keep-versions:10}")
    private Integer keepVersions;

    /**
     * 清理过期历史
     * 每天凌晨3点执行
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanOldHistory() {
        log.info("开始执行文件历史清理任务，保留版本数: {}", keepVersions);
        try {
            // 获取所有文件
            List<CoFile> files = fileMapper.selectList(new LambdaQueryWrapper<>());
            
            int totalCleaned = 0;
            for (CoFile file : files) {
                try {
                    Integer count = historyService.cleanOldHistory(file.getId(), keepVersions);
                    totalCleaned += count;
                } catch (Exception e) {
                    log.error("清理文件历史失败，文件ID: {}", file.getId(), e);
                }
            }
            
            log.info("文件历史清理任务完成，清理数量: {}", totalCleaned);
        } catch (Exception e) {
            log.error("文件历史清理任务执行失败", e);
        }
    }
}

