package com.xmzs.coedit.websocket;

import com.xmzs.coedit.domain.CoEditorPerformance;
import com.xmzs.coedit.domain.dto.WebSocketMessage;
import com.xmzs.coedit.mapper.CoEditorPerformanceMapper;
import com.xmzs.coedit.service.ICoEditLogService;
import com.xmzs.coedit.service.ICoEditSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Map;

/**
 * WebSocket事件监听器
 * 监听WebSocket连接和断开事件，并记录性能数据
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Component("coEditWebSocketEventListener")
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ICoEditSessionService sessionService;
    private final ICoEditLogService logService;
    private final WebSocketConnectionManager connectionManager;
    private final CoEditorPerformanceMapper performanceMapper;

    /**
     * 监听WebSocket连接事件
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        log.info("WebSocket连接建立: sessionId={}", sessionId);

        // 从header中获取用户信息并添加到连接管理器
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes != null) {
            Long userId = (Long) sessionAttributes.get("userId");
            String userName = (String) sessionAttributes.get("userName");
            Long fileId = (Long) sessionAttributes.get("fileId");
            String fileName = (String) sessionAttributes.get("fileName");
            String deviceType = (String) sessionAttributes.get("deviceType");
            String browserType = (String) sessionAttributes.get("browserType");
            String networkType = (String) sessionAttributes.get("networkType");

            if (userId != null && fileId != null) {
                connectionManager.addConnection(sessionId, userId, userName, fileId, fileName);

                // 设置设备信息
                WebSocketConnectionManager.ConnectionInfo info = connectionManager.getConnection(sessionId);
                if (info != null) {
                    info.setDeviceType(deviceType);
                    info.setBrowserType(browserType);
                    info.setNetworkType(networkType);
                }
            }
        }
    }

    /**
     * 监听WebSocket断开事件
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        log.info("WebSocket连接断开: sessionId={}", sessionId);

        // 从会话属性中获取用户信息
        String sessionKey = (String) headerAccessor.getSessionAttributes().get("sessionKey");
        Long fileId = (Long) headerAccessor.getSessionAttributes().get("fileId");
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        String userName = (String) headerAccessor.getSessionAttributes().get("userName");

        if (sessionKey != null) {
            // 结束编辑会话
            sessionService.endSession(sessionKey);

            // 发送用户离开消息
            if (fileId != null && userId != null && userName != null) {
                WebSocketMessage message = WebSocketMessage.createUserLeaveMessage(
                    fileId, userId, userName, sessionKey
                );
                messagingTemplate.convertAndSend("/topic/file/" + fileId, message);

                // 记录用户离开日志
                logService.logUserLeave(fileId, userId, userName, sessionKey);

                log.info("用户离开编辑: fileId={}, userId={}, userName={}", fileId, userId, userName);
            }
        }

        // 移除连接并保存性能数据
        WebSocketConnectionManager.ConnectionInfo info = connectionManager.removeConnection(sessionId);
        if (info != null) {
            savePerformanceData(info, event.getCloseStatus() != null && event.getCloseStatus().getCode() == 1000);
        }
    }

    /**
     * 监听WebSocket订阅事件
     */
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();

        log.info("WebSocket订阅: sessionId={}, destination={}", sessionId, destination);
    }

    /**
     * 保存性能数据
     */
    private void savePerformanceData(WebSocketConnectionManager.ConnectionInfo info, boolean normalDisconnect) {
        try {
            CoEditorPerformance performance = new CoEditorPerformance();
            performance.setFileId(info.getFileId());
            performance.setFileName(info.getFileName());
            performance.setSessionId(info.getSessionId());
            performance.setUserId(info.getUserId());
            performance.setUserName(info.getUserName());
            performance.setConnectionDuration(info.getConnectionDuration());
            performance.setMessagesSent(info.getMessagesSentValue());
            performance.setMessagesReceived(info.getMessagesReceivedValue());
            performance.setAverageLatency(info.getAverageLatency());
            performance.setMaxLatency(info.getMaxLatencyValue());
            performance.setMinLatency(info.getMinLatencyValue());
            performance.setErrorCount(info.getErrorCountValue());
            performance.setReconnectCount(info.getReconnectCountValue());
            performance.setDeviceType(info.getDeviceType());
            performance.setBrowserType(info.getBrowserType());
            performance.setNetworkType(info.getNetworkType());
            performance.setConnectTime(info.getConnectTime());
            performance.setDisconnectTime(info.getDisconnectTime());
            performance.setNormalDisconnect(normalDisconnect ? 1 : 0);

            performanceMapper.insert(performance);
            log.info("保存性能数据成功: sessionId={}", info.getSessionId());
        } catch (Exception e) {
            log.error("保存性能数据失败: sessionId={}", info.getSessionId(), e);
        }
    }

}

