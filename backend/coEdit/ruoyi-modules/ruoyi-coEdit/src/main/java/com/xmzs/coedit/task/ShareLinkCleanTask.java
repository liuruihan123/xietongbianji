package com.xmzs.coedit.task;

import com.xmzs.coedit.service.ICoShareLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 共享链接清理定时任务
 * 定期清理过期的共享链接
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ShareLinkCleanTask {

    private final ICoShareLinkService shareLinkService;

    /**
     * 清理过期共享链接
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanExpiredShareLinks() {
        log.info("开始执行共享链接清理任务");
        
        try {
            Integer count = shareLinkService.cleanExpiredLinks();
            
            if (count > 0) {
                log.info("共享链接清理任务完成: 清理了{}个过期链接", count);
            } else {
                log.debug("共享链接清理任务完成: 没有过期链接需要清理");
            }
            
        } catch (Exception e) {
            log.error("共享链接清理任务执行失败", e);
        }
    }

}

