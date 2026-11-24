package com.xmzs.coedit.task;

import com.xmzs.coedit.service.ICoEditSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 编辑会话清理定时任务
 * 定期清理超时的编辑会话
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class EditSessionCleanTask {

    private final ICoEditSessionService sessionService;

    /**
     * 清理超时会话
     * 每10分钟执行一次，清理30分钟无活动的会话
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void cleanTimeoutSessions() {
        log.info("开始执行编辑会话清理任务");
        
        try {
            // 清理30分钟无活动的会话
            Integer count = sessionService.cleanTimeoutSessions(30);
            
            if (count > 0) {
                log.info("编辑会话清理任务完成: 清理了{}个超时会话", count);
            } else {
                log.debug("编辑会话清理任务完成: 没有超时会话需要清理");
            }
            
        } catch (Exception e) {
            log.error("编辑会话清理任务执行失败", e);
        }
    }

}

