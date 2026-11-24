package com.xmzs.coedit.websocket;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * WebSocket连接管理器
 * 管理所有活跃的WebSocket连接，提供心跳检测和重连支持
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Slf4j
@Component
public class WebSocketConnectionManager {

    /**
     * 存储所有活跃连接
     * Key: sessionId, Value: ConnectionInfo
     */
    private final Map<String, ConnectionInfo> activeConnections = new ConcurrentHashMap<>();

    /**
     * 存储用户的所有会话
     * Key: userId, Value: Set of sessionIds
     */
    private final Map<Long, Map<String, ConnectionInfo>> userSessions = new ConcurrentHashMap<>();

    /**
     * 添加连接
     */
    public void addConnection(String sessionId, Long userId, String userName, Long fileId, String fileName) {
        ConnectionInfo info = new ConnectionInfo();
        info.setSessionId(sessionId);
        info.setUserId(userId);
        info.setUserName(userName);
        info.setFileId(fileId);
        info.setFileName(fileName);
        info.setConnectTime(new Date());
        info.setLastHeartbeatTime(new Date());

        activeConnections.put(sessionId, info);
        
        userSessions.computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                    .put(sessionId, info);

        log.info("添加WebSocket连接: sessionId={}, userId={}, fileId={}", sessionId, userId, fileId);
    }

    /**
     * 移除连接
     */
    public ConnectionInfo removeConnection(String sessionId) {
        ConnectionInfo info = activeConnections.remove(sessionId);
        if (info != null) {
            Map<String, ConnectionInfo> sessions = userSessions.get(info.getUserId());
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    userSessions.remove(info.getUserId());
                }
            }
            info.setDisconnectTime(new Date());
            log.info("移除WebSocket连接: sessionId={}, userId={}", sessionId, info.getUserId());
        }
        return info;
    }

    /**
     * 获取连接信息
     */
    public ConnectionInfo getConnection(String sessionId) {
        return activeConnections.get(sessionId);
    }

    /**
     * 更新心跳时间
     */
    public void updateHeartbeat(String sessionId) {
        ConnectionInfo info = activeConnections.get(sessionId);
        if (info != null) {
            info.setLastHeartbeatTime(new Date());
            info.incrementHeartbeatCount();
        }
    }

    /**
     * 记录消息发送
     */
    public void recordMessageSent(String sessionId) {
        ConnectionInfo info = activeConnections.get(sessionId);
        if (info != null) {
            info.incrementMessagesSent();
        }
    }

    /**
     * 记录消息接收
     */
    public void recordMessageReceived(String sessionId) {
        ConnectionInfo info = activeConnections.get(sessionId);
        if (info != null) {
            info.incrementMessagesReceived();
        }
    }

    /**
     * 记录延迟
     */
    public void recordLatency(String sessionId, long latency) {
        ConnectionInfo info = activeConnections.get(sessionId);
        if (info != null) {
            info.recordLatency(latency);
        }
    }

    /**
     * 记录错误
     */
    public void recordError(String sessionId) {
        ConnectionInfo info = activeConnections.get(sessionId);
        if (info != null) {
            info.incrementErrorCount();
        }
    }

    /**
     * 记录重连
     */
    public void recordReconnect(String sessionId) {
        ConnectionInfo info = activeConnections.get(sessionId);
        if (info != null) {
            info.incrementReconnectCount();
        }
    }

    /**
     * 获取用户的所有会话
     */
    public Map<String, ConnectionInfo> getUserSessions(Long userId) {
        return userSessions.getOrDefault(userId, new ConcurrentHashMap<>());
    }

    /**
     * 获取所有活跃连接
     */
    public Map<String, ConnectionInfo> getAllConnections() {
        return new ConcurrentHashMap<>(activeConnections);
    }

    /**
     * 获取活跃连接数
     */
    public int getActiveConnectionCount() {
        return activeConnections.size();
    }

    /**
     * 获取在线用户数
     */
    public int getOnlineUserCount() {
        return userSessions.size();
    }

    /**
     * 检查连接是否存活
     * 如果超过指定时间没有心跳，则认为连接已断开
     */
    public boolean isConnectionAlive(String sessionId, long timeoutMillis) {
        ConnectionInfo info = activeConnections.get(sessionId);
        if (info == null) {
            return false;
        }
        long timeSinceLastHeartbeat = System.currentTimeMillis() - info.getLastHeartbeatTime().getTime();
        return timeSinceLastHeartbeat < timeoutMillis;
    }

    /**
     * 连接信息类
     */
    @Data
    public static class ConnectionInfo {
        private String sessionId;
        private Long userId;
        private String userName;
        private Long fileId;
        private String fileName;
        private Date connectTime;
        private Date disconnectTime;
        private Date lastHeartbeatTime;
        
        private AtomicInteger messagesSent = new AtomicInteger(0);
        private AtomicInteger messagesReceived = new AtomicInteger(0);
        private AtomicInteger heartbeatCount = new AtomicInteger(0);
        private AtomicInteger errorCount = new AtomicInteger(0);
        private AtomicInteger reconnectCount = new AtomicInteger(0);
        
        private AtomicLong totalLatency = new AtomicLong(0);
        private AtomicInteger latencyCount = new AtomicInteger(0);
        private AtomicLong maxLatency = new AtomicLong(0);
        private AtomicLong minLatency = new AtomicLong(Long.MAX_VALUE);
        
        private String deviceType;
        private String browserType;
        private String networkType;

        public void incrementMessagesSent() {
            messagesSent.incrementAndGet();
        }

        public void incrementMessagesReceived() {
            messagesReceived.incrementAndGet();
        }

        public void incrementHeartbeatCount() {
            heartbeatCount.incrementAndGet();
        }

        public void incrementErrorCount() {
            errorCount.incrementAndGet();
        }

        public void incrementReconnectCount() {
            reconnectCount.incrementAndGet();
        }

        public void recordLatency(long latency) {
            totalLatency.addAndGet(latency);
            latencyCount.incrementAndGet();
            
            // 更新最大延迟
            long currentMax = maxLatency.get();
            while (latency > currentMax) {
                if (maxLatency.compareAndSet(currentMax, latency)) {
                    break;
                }
                currentMax = maxLatency.get();
            }
            
            // 更新最小延迟
            long currentMin = minLatency.get();
            while (latency < currentMin) {
                if (minLatency.compareAndSet(currentMin, latency)) {
                    break;
                }
                currentMin = minLatency.get();
            }
        }

        public long getAverageLatency() {
            int count = latencyCount.get();
            return count > 0 ? totalLatency.get() / count : 0;
        }

        public long getConnectionDuration() {
            Date endTime = disconnectTime != null ? disconnectTime : new Date();
            return endTime.getTime() - connectTime.getTime();
        }

        public int getMessagesSentValue() {
            return messagesSent.get();
        }

        public int getMessagesReceivedValue() {
            return messagesReceived.get();
        }

        public int getErrorCountValue() {
            return errorCount.get();
        }

        public int getReconnectCountValue() {
            return reconnectCount.get();
        }

        public long getMaxLatencyValue() {
            long max = maxLatency.get();
            return max == 0 ? 0 : max;
        }

        public long getMinLatencyValue() {
            long min = minLatency.get();
            return min == Long.MAX_VALUE ? 0 : min;
        }
    }
}

