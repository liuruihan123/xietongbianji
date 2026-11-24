package com.xmzs.coedit.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xmzs.coedit.domain.CoSearchHistory;
import com.xmzs.coedit.mapper.CoSearchHistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 搜索历史清理定时任务
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SearchHistoryCleanTask {

    private final CoSearchHistoryMapper searchHistoryMapper;

    /**
     * 清理30天前的搜索历史
     * 每天凌晨4点执行
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void cleanOldSearchHistory() {
        log.info("开始执行搜索历史清理任务");
        try {
            // 计算30天前的时间
            Date thirtyDaysAgo = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);

            // 删除30天前的搜索历史
            int count = searchHistoryMapper.delete(
                Wrappers.lambdaQuery(CoSearchHistory.class)
                    .lt(CoSearchHistory::getCreateTime, thirtyDaysAgo)
            );

            log.info("搜索历史清理任务完成，清理数量: {}", count);
        } catch (Exception e) {
            log.error("搜索历史清理任务执行失败", e);
        }
    }
}

