package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.dto.CollaborationMessage;
import com.xmzs.coedit.domain.dto.UserPresence;

import java.util.List;

/**
 * 协作服务接口
 * 处理实时协作相关的业务逻辑
 *
 * @author zuleed
 * @date 2025-11-17
 */
public interface CollaborationService {

    /**
     * 获取文档的在线用户列表
     *
     * @param documentId 文档ID
     * @return 在线用户列表
     */
    List<UserPresence> getOnlineUsers(String documentId);

    /**
     * 记录用户活动
     *
     * @param documentId 文档ID
     * @param userId 用户ID
     * @param activity 活动类型
     */
    void recordUserActivity(String documentId, Long userId, String activity);

    /**
     * 处理协作消息
     *
     * @param message 协作消息
     */
    void handleCollaborationMessage(CollaborationMessage message);

    /**
     * 清理过期的用户会话
     *
     * @param documentId 文档ID
     */
    void cleanupExpiredSessions(String documentId);

    /**
     * 获取文档的协作统计信息
     *
     * @param documentId 文档ID
     * @return 协作统计信息
     */
    CollaborationStats getCollaborationStats(String documentId);

    /**
     * 协作统计信息
     */
    class CollaborationStats {
        private Integer onlineUserCount;
        private Integer totalUserCount;
        private Long lastActivityTime;
        private List<String> recentActivities;

        // getters and setters
        public Integer getOnlineUserCount() { return onlineUserCount; }
        public void setOnlineUserCount(Integer onlineUserCount) { this.onlineUserCount = onlineUserCount; }

        public Integer getTotalUserCount() { return totalUserCount; }
        public void setTotalUserCount(Integer totalUserCount) { this.totalUserCount = totalUserCount; }

        public Long getLastActivityTime() { return lastActivityTime; }
        public void setLastActivityTime(Long lastActivityTime) { this.lastActivityTime = lastActivityTime; }

        public List<String> getRecentActivities() { return recentActivities; }
        public void setRecentActivities(List<String> recentActivities) { this.recentActivities = recentActivities; }
    }
}
