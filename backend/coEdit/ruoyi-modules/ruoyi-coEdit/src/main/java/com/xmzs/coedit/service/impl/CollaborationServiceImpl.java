package com.xmzs.coedit.service.impl;

import com.xmzs.coedit.domain.dto.CollaborationMessage;
import com.xmzs.coedit.domain.dto.UserPresence;
import com.xmzs.coedit.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 协作服务实现类
 *
 * @author zuleed
 * @date 2025-11-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollaborationServiceImpl implements CollaborationService {

    // 存储文档ID -> 用户在线状态映射
    private final Map<String, Map<String, UserPresence>> documentUsers = new ConcurrentHashMap<>();
    
    // 存储文档活动记录
    private final Map<String, List<String>> documentActivities = new ConcurrentHashMap<>();

    @Override
    public List<UserPresence> getOnlineUsers(String documentId) {
        Map<String, UserPresence> users = documentUsers.get(documentId);
        if (users == null) {
            return new ArrayList<>();
        }

        long currentTime = System.currentTimeMillis();
        long timeoutThreshold = 5 * 60 * 1000; // 5分钟超时

        return users.values().stream()
                .filter(user -> (currentTime - user.getLastActiveTime()) < timeoutThreshold)
                .collect(Collectors.toList());
    }

    @Override
    public void recordUserActivity(String documentId, Long userId, String activity) {
        // 记录活动
        documentActivities.computeIfAbsent(documentId, k -> new ArrayList<>())
                .add(String.format("[%s] 用户 %s %s", 
                    new Date().toString(), 
                    userId != null ? userId.toString() : "访客", 
                    activity));

        // 限制活动记录数量
        List<String> activities = documentActivities.get(documentId);
        if (activities.size() > 100) {
            activities.subList(0, activities.size() - 100).clear();
        }

        log.debug("记录用户活动: documentId={}, userId={}, activity={}", documentId, userId, activity);
    }

    @Override
    public void handleCollaborationMessage(CollaborationMessage message) {
        String documentId = message.getDocumentId();
        if (documentId == null) {
            log.warn("协作消息缺少文档ID");
            return;
        }

        // 更新用户活动时间
        Map<String, UserPresence> users = documentUsers.get(documentId);
        if (users != null) {
            UserPresence user = users.values().stream()
                    .filter(u -> Objects.equals(u.getUserId(), message.getUserId()))
                    .findFirst()
                    .orElse(null);
            
            if (user != null) {
                user.setLastActiveTime(System.currentTimeMillis());
                
                // 根据消息类型更新用户状态
                switch (message.getType()) {
                    case "cursor":
                        updateUserCursor(user, message);
                        break;
                    case "selection":
                        updateUserSelection(user, message);
                        break;
                }
            }
        }

        // 记录活动
        recordUserActivity(documentId, message.getUserId(), 
                String.format("执行了 %s 操作", message.getType()));
    }

    @Override
    public void cleanupExpiredSessions(String documentId) {
        Map<String, UserPresence> users = documentUsers.get(documentId);
        if (users == null) return;

        long currentTime = System.currentTimeMillis();
        long timeoutThreshold = 10 * 60 * 1000; // 10分钟超时

        List<String> expiredSessions = users.entrySet().stream()
                .filter(entry -> (currentTime - entry.getValue().getLastActiveTime()) > timeoutThreshold)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (String sessionId : expiredSessions) {
            UserPresence removedUser = users.remove(sessionId);
            if (removedUser != null) {
                log.info("清理过期会话: documentId={}, userId={}, sessionId={}", 
                        documentId, removedUser.getUserId(), sessionId);
                recordUserActivity(documentId, removedUser.getUserId(), "会话超时离开");
            }
        }

        // 如果文档没有在线用户，清理文档记录
        if (users.isEmpty()) {
            documentUsers.remove(documentId);
        }
    }

    @Override
    public CollaborationStats getCollaborationStats(String documentId) {
        CollaborationStats stats = new CollaborationStats();
        
        List<UserPresence> onlineUsers = getOnlineUsers(documentId);
        stats.setOnlineUserCount(onlineUsers.size());
        
        Map<String, UserPresence> allUsers = documentUsers.get(documentId);
        stats.setTotalUserCount(allUsers != null ? allUsers.size() : 0);
        
        if (!onlineUsers.isEmpty()) {
            stats.setLastActivityTime(onlineUsers.stream()
                    .mapToLong(UserPresence::getLastActiveTime)
                    .max()
                    .orElse(0L));
        }
        
        List<String> activities = documentActivities.get(documentId);
        if (activities != null && !activities.isEmpty()) {
            // 返回最近10条活动
            int fromIndex = Math.max(0, activities.size() - 10);
            stats.setRecentActivities(new ArrayList<>(activities.subList(fromIndex, activities.size())));
        } else {
            stats.setRecentActivities(new ArrayList<>());
        }
        
        return stats;
    }

    /**
     * 添加用户到文档协作
     */
    public void addUserToDocument(String documentId, UserPresence userPresence) {
        documentUsers.computeIfAbsent(documentId, k -> new ConcurrentHashMap<>())
                .put(userPresence.getSessionId(), userPresence);
        
        recordUserActivity(documentId, userPresence.getUserId(), "加入文档协作");
        log.info("用户加入文档协作: documentId={}, userId={}, userName={}", 
                documentId, userPresence.getUserId(), userPresence.getUserName());
    }

    /**
     * 从文档协作中移除用户
     */
    public void removeUserFromDocument(String documentId, String sessionId) {
        Map<String, UserPresence> users = documentUsers.get(documentId);
        if (users != null) {
            UserPresence removedUser = users.remove(sessionId);
            if (removedUser != null) {
                recordUserActivity(documentId, removedUser.getUserId(), "离开文档协作");
                log.info("用户离开文档协作: documentId={}, userId={}, userName={}", 
                        documentId, removedUser.getUserId(), removedUser.getUserName());
            }
            
            // 如果没有用户了，清理文档记录
            if (users.isEmpty()) {
                documentUsers.remove(documentId);
            }
        }
    }

    /**
     * 更新用户光标位置
     */
    private void updateUserCursor(UserPresence user, CollaborationMessage message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            if (data != null) {
                UserPresence.CursorPosition cursor = new UserPresence.CursorPosition();
                cursor.setLine((Integer) data.get("line"));
                cursor.setColumn((Integer) data.get("column"));
                cursor.setTimestamp(System.currentTimeMillis());
                user.setCursorPosition(cursor);
            }
        } catch (Exception e) {
            log.warn("更新用户光标位置失败: userId={}", user.getUserId(), e);
        }
    }

    /**
     * 更新用户选择区域
     */
    private void updateUserSelection(UserPresence user, CollaborationMessage message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            if (data != null) {
                UserPresence.SelectionRange selection = new UserPresence.SelectionRange();
                selection.setStartLine((Integer) data.get("startLine"));
                selection.setStartColumn((Integer) data.get("startColumn"));
                selection.setEndLine((Integer) data.get("endLine"));
                selection.setEndColumn((Integer) data.get("endColumn"));
                selection.setTimestamp(System.currentTimeMillis());
                user.setSelectionRange(selection);
            }
        } catch (Exception e) {
            log.warn("更新用户选择区域失败: userId={}", user.getUserId(), e);
        }
    }

    /**
     * 获取文档的用户颜色映射
     */
    public Map<String, String> getUserColors(String documentId) {
        Map<String, UserPresence> users = documentUsers.get(documentId);
        if (users == null) return new HashMap<>();

        Map<String, String> colors = new HashMap<>();
        String[] colorPalette = {
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7",
            "#DDA0DD", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E9"
        };

        int colorIndex = 0;
        for (UserPresence user : users.values()) {
            String userKey = user.getUserId() != null ? user.getUserId().toString() : user.getSessionId();
            if (user.getUserColor() == null) {
                user.setUserColor(colorPalette[colorIndex % colorPalette.length]);
                colorIndex++;
            }
            colors.put(userKey, user.getUserColor());
        }

        return colors;
    }
}
