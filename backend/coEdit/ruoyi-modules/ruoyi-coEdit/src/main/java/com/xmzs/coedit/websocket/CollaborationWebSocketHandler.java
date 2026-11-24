package com.xmzs.coedit.websocket;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmzs.coedit.domain.dto.CollaborationMessage;
import com.xmzs.coedit.domain.dto.UserPresence;
import com.xmzs.coedit.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实时协作WebSocket处理器
 * 处理文档协作中的实时通信，包括：
 * - 用户在线状态
 * - 光标位置同步
 * - 文档内容同步
 * - 用户加入/离开通知
 *
 * @author zuleed
 * @date 2025-11-17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CollaborationWebSocketHandler implements WebSocketHandler {

    private final CollaborationService collaborationService;
    private final ObjectMapper objectMapper;

    // 存储文档ID -> WebSocket会话映射
    private final Map<String, Map<String, WebSocketSession>> documentSessions = new ConcurrentHashMap<>();
    // 存储会话ID -> 用户信息映射
    private final Map<String, UserPresence> sessionUsers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket连接建立: sessionId={}", session.getId());
        
        // 从URL参数中获取文档ID和分享码
        String documentId = getParameterFromSession(session, "documentId");
        String shareCode = getParameterFromSession(session, "shareCode");
        
        if (documentId == null) {
            log.warn("文档ID为空，关闭连接: sessionId={}", session.getId());
            session.close(CloseStatus.BAD_DATA.withReason("Missing documentId"));
            return;
        }

        // 获取用户信息
        UserPresence userPresence = getUserPresenceFromSession(session, shareCode);
        if (userPresence == null) {
            log.warn("无法获取用户信息，关闭连接: sessionId={}", session.getId());
            session.close(CloseStatus.BAD_DATA.withReason("Invalid user"));
            return;
        }

        // 将会话添加到文档会话组
        documentSessions.computeIfAbsent(documentId, k -> new ConcurrentHashMap<>())
                        .put(session.getId(), session);
        sessionUsers.put(session.getId(), userPresence);

        log.info("用户加入文档协作: documentId={}, userId={}, userName={}, sessionId={}", 
                documentId, userPresence.getUserId(), userPresence.getUserName(), session.getId());

        // 通知其他用户有新用户加入
        broadcastUserJoined(documentId, userPresence, session.getId());
        
        // 发送当前在线用户列表给新用户
        sendOnlineUsers(session, documentId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        try {
            String payload = message.getPayload().toString();
            CollaborationMessage collaborationMessage = objectMapper.readValue(payload, CollaborationMessage.class);
            
            String documentId = getDocumentIdFromSession(session);
            if (documentId == null) {
                log.warn("会话中没有文档ID: sessionId={}", session.getId());
                return;
            }

            UserPresence userPresence = sessionUsers.get(session.getId());
            if (userPresence == null) {
                log.warn("会话中没有用户信息: sessionId={}", session.getId());
                return;
            }

            // 设置消息发送者信息
            collaborationMessage.setUserId(userPresence.getUserId());
            collaborationMessage.setUserName(userPresence.getUserName());
            collaborationMessage.setTimestamp(System.currentTimeMillis());

            log.debug("收到协作消息: documentId={}, type={}, userId={}", 
                    documentId, collaborationMessage.getType(), userPresence.getUserId());

            // 根据消息类型处理
            switch (collaborationMessage.getType()) {
                case "cursor":
                    handleCursorUpdate(documentId, collaborationMessage, session.getId());
                    break;
                case "selection":
                    handleSelectionUpdate(documentId, collaborationMessage, session.getId());
                    break;
                case "content":
                    handleContentUpdate(documentId, collaborationMessage, session.getId());
                    break;
                case "heartbeat":
                    handleHeartbeat(documentId, userPresence, session.getId());
                    break;
                default:
                    log.warn("未知的协作消息类型: {}", collaborationMessage.getType());
            }

        } catch (Exception e) {
            log.error("处理WebSocket消息失败: sessionId={}", session.getId(), e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误: sessionId={}", session.getId(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String sessionId = session.getId();
        log.info("WebSocket连接关闭: sessionId={}, status={}", sessionId, closeStatus);

        // 获取用户和文档信息
        UserPresence userPresence = sessionUsers.remove(sessionId);
        String documentId = getDocumentIdFromSession(session);

        if (documentId != null && userPresence != null) {
            // 从文档会话组中移除
            Map<String, WebSocketSession> sessions = documentSessions.get(documentId);
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    documentSessions.remove(documentId);
                }
            }

            log.info("用户离开文档协作: documentId={}, userId={}, userName={}", 
                    documentId, userPresence.getUserId(), userPresence.getUserName());

            // 通知其他用户有用户离开
            broadcastUserLeft(documentId, userPresence, sessionId);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 处理光标位置更新
     */
    private void handleCursorUpdate(String documentId, CollaborationMessage message, String senderSessionId) {
        broadcastToDocument(documentId, message, senderSessionId);
    }

    /**
     * 处理选择区域更新
     */
    private void handleSelectionUpdate(String documentId, CollaborationMessage message, String senderSessionId) {
        broadcastToDocument(documentId, message, senderSessionId);
    }

    /**
     * 处理内容更新
     */
    private void handleContentUpdate(String documentId, CollaborationMessage message, String senderSessionId) {
        // 可以在这里添加内容冲突检测和解决逻辑
        broadcastToDocument(documentId, message, senderSessionId);
    }

    /**
     * 处理心跳消息
     */
    private void handleHeartbeat(String documentId, UserPresence userPresence, String sessionId) {
        userPresence.setLastActiveTime(System.currentTimeMillis());
        // 心跳消息不需要广播
    }

    /**
     * 广播用户加入消息
     */
    private void broadcastUserJoined(String documentId, UserPresence userPresence, String excludeSessionId) {
        CollaborationMessage message = new CollaborationMessage();
        message.setType("user_joined");
        message.setUserId(userPresence.getUserId());
        message.setUserName(userPresence.getUserName());
        message.setData(userPresence);
        message.setTimestamp(System.currentTimeMillis());

        broadcastToDocument(documentId, message, excludeSessionId);
    }

    /**
     * 广播用户离开消息
     */
    private void broadcastUserLeft(String documentId, UserPresence userPresence, String excludeSessionId) {
        CollaborationMessage message = new CollaborationMessage();
        message.setType("user_left");
        message.setUserId(userPresence.getUserId());
        message.setUserName(userPresence.getUserName());
        message.setData(userPresence);
        message.setTimestamp(System.currentTimeMillis());

        broadcastToDocument(documentId, message, excludeSessionId);
    }

    /**
     * 发送在线用户列表
     */
    private void sendOnlineUsers(WebSocketSession session, String documentId) {
        Map<String, WebSocketSession> sessions = documentSessions.get(documentId);
        if (sessions == null) return;

        // 收集在线用户信息
        java.util.List<UserPresence> onlineUsers = sessions.keySet().stream()
                .map(sessionUsers::get)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());

        CollaborationMessage message = new CollaborationMessage();
        message.setType("online_users");
        message.setData(onlineUsers);
        message.setTimestamp(System.currentTimeMillis());

        sendMessage(session, message);
    }

    /**
     * 向文档的所有用户广播消息（排除发送者）
     */
    private void broadcastToDocument(String documentId, CollaborationMessage message, String excludeSessionId) {
        Map<String, WebSocketSession> sessions = documentSessions.get(documentId);
        if (sessions == null) return;

        String messageJson;
        try {
            messageJson = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error("序列化协作消息失败", e);
            return;
        }

        sessions.entrySet().parallelStream()
                .filter(entry -> !entry.getKey().equals(excludeSessionId))
                .forEach(entry -> {
                    try {
                        WebSocketSession session = entry.getValue();
                        if (session.isOpen()) {
                            session.sendMessage(new TextMessage(messageJson));
                        }
                    } catch (IOException e) {
                        log.error("发送WebSocket消息失败: sessionId={}", entry.getKey(), e);
                    }
                });
    }

    /**
     * 向指定会话发送消息
     */
    private void sendMessage(WebSocketSession session, CollaborationMessage message) {
        try {
            if (session.isOpen()) {
                String messageJson = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(messageJson));
            }
        } catch (Exception e) {
            log.error("发送WebSocket消息失败: sessionId={}", session.getId(), e);
        }
    }

    /**
     * 从会话中获取参数
     */
    private String getParameterFromSession(WebSocketSession session, String paramName) {
        try {
            String query = session.getUri().getQuery();
            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && paramName.equals(keyValue[0])) {
                        return java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析会话参数失败: paramName={}", paramName, e);
        }
        return null;
    }

    /**
     * 从会话中获取文档ID
     */
    private String getDocumentIdFromSession(WebSocketSession session) {
        return getParameterFromSession(session, "documentId");
    }

    /**
     * 从会话中获取用户信息
     */
    private UserPresence getUserPresenceFromSession(WebSocketSession session, String shareCode) {
        try {
            // 尝试从认证信息中获取用户
            if (StpUtil.isLogin()) {
                Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
                String userName = StpUtil.getLoginIdAsString(); // 实际应该从用户服务获取真实姓名
                
                UserPresence presence = new UserPresence();
                presence.setUserId(userId);
                presence.setUserName(userName);
                presence.setSessionId(session.getId());
                presence.setIsAuthenticated(true);
                presence.setJoinTime(System.currentTimeMillis());
                presence.setLastActiveTime(System.currentTimeMillis());
                return presence;
            } else {
                // 访客用户
                String visitorName = getParameterFromSession(session, "visitorName");
                if (visitorName == null) visitorName = "访客";
                
                UserPresence presence = new UserPresence();
                presence.setUserId(null);
                presence.setUserName(visitorName);
                presence.setSessionId(session.getId());
                presence.setIsAuthenticated(false);
                presence.setJoinTime(System.currentTimeMillis());
                presence.setLastActiveTime(System.currentTimeMillis());
                return presence;
            }
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return null;
        }
    }
}
