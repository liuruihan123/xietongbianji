package com.xmzs.coedit.task;

import com.xmzs.coedit.websocket.WebSocketConnectionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 心跳检测定时任务
 * 每30秒检查一次所有连接的心跳状态
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HeartbeatCheckTask {

    private final WebSocketConnectionManager connectionManager;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * 心跳超时时间（毫秒）
     * 如果60秒内没有收到心跳，则认为连接已断开
     */
    private static final long HEARTBEAT_TIMEOUT = 60000;

    /**
     * 检查心跳状态
     * 每30秒执行一次
     */
    @Scheduled(fixedRate = 30000)
    public void checkHeartbeat() {
        Map<String, WebSocketConnectionManager.ConnectionInfo> connections = connectionManager.getAllConnections();
        
        int timeoutCount = 0;
        for (Map.Entry<String, WebSocketConnectionManager.ConnectionInfo> entry : connections.entrySet()) {
            String sessionId = entry.getKey();
            WebSocketConnectionManager.ConnectionInfo info = entry.getValue();
            
            // 检查连接是否超时
            if (!connectionManager.isConnectionAlive(sessionId, HEARTBEAT_TIMEOUT)) {
                log.warn("检测到心跳超时: sessionId={}, userId={}, fileId={}", 
                        sessionId, info.getUserId(), info.getFileId());
                
                // 发送心跳超时通知
                sendHeartbeatTimeoutNotification(info);
                
                timeoutCount++;
            }
        }
        
        if (timeoutCount > 0) {
            log.info("心跳检测完成，发现{}个超时连接", timeoutCount);
        }
    }

    /**
     * 发送心跳超时通知
     */
    private void sendHeartbeatTimeoutNotification(WebSocketConnectionManager.ConnectionInfo info) {
        try {
            // 向用户发送心跳超时通知
            messagingTemplate.convertAndSendToUser(
                    info.getUserId().toString(),
                    "/queue/heartbeat",
                    Map.of(
                            "type", "HEARTBEAT_TIMEOUT",
                            "sessionId", info.getSessionId(),
                            "fileId", info.getFileId(),
                            "message", "连接心跳超时，请检查网络连接"
                    )
            );
        } catch (Exception e) {
            log.error("发送心跳超时通知失败: sessionId={}", info.getSessionId(), e);
        }
    }
}

