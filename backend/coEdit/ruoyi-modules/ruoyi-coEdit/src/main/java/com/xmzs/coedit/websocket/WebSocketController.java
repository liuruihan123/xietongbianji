package com.xmzs.coedit.websocket;

import com.xmzs.coedit.domain.dto.WebSocketMessage;
import com.xmzs.coedit.domain.vo.CoEditSessionVo;
import com.xmzs.coedit.service.ICoEditLogService;
import com.xmzs.coedit.service.ICoEditSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

/**
 * WebSocket控制器
 * 处理协同编辑的实时消息
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Controller
public class WebSocketController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ICoEditSessionService sessionService;
    private final ICoEditLogService logService;

    /**
     * 用户加入编辑
     * 客户端发送消息到: /app/file/{fileId}/join
     * 服务器广播消息到: /topic/file/{fileId}
     */
    @MessageMapping("/file/{fileId}/join")
    @SendTo("/topic/file/{fileId}")
    public WebSocketMessage handleUserJoin(
            @DestinationVariable Long fileId,
            @Payload WebSocketMessage message,
            SimpMessageHeaderAccessor headerAccessor) {

        log.info("用户加入编辑: fileId={}, userId={}, userName={}",
                 fileId, message.getUserId(), message.getUserName());

        // 创建编辑会话
        String ipAddress = (String) headerAccessor.getSessionAttributes().get("ip");
        String userAgent = (String) headerAccessor.getSessionAttributes().get("userAgent");

        CoEditSessionVo session = sessionService.createSession(
            fileId,
            message.getUserId(),
            message.getUserName(),
            message.getSessionKey(),
            ipAddress,
            userAgent
        );

        // 保存会话信息到WebSocket会话属性
        headerAccessor.getSessionAttributes().put("sessionKey", session.getSessionKey());
        headerAccessor.getSessionAttributes().put("fileId", fileId);
        headerAccessor.getSessionAttributes().put("userId", message.getUserId());
        headerAccessor.getSessionAttributes().put("userName", message.getUserName());

        // 记录用户加入日志
        logService.logUserJoin(fileId, message.getUserId(), message.getUserName(),
                               session.getSessionKey(), ipAddress, userAgent);

        // 返回用户加入消息
        return WebSocketMessage.createUserJoinMessage(
            fileId,
            message.getUserId(),
            message.getUserName(),
            session.getSessionKey()
        );
    }

    /**
     * 用户离开编辑
     * 客户端发送消息到: /app/file/{fileId}/leave
     * 服务器广播消息到: /topic/file/{fileId}
     */
    @MessageMapping("/file/{fileId}/leave")
    @SendTo("/topic/file/{fileId}")
    public WebSocketMessage handleUserLeave(
            @DestinationVariable Long fileId,
            @Payload WebSocketMessage message) {

        log.info("用户离开编辑: fileId={}, userId={}, userName={}",
                 fileId, message.getUserId(), message.getUserName());

        // 结束编辑会话
        if (message.getSessionKey() != null) {
            sessionService.endSession(message.getSessionKey());
        }

        // 返回用户离开消息
        return WebSocketMessage.createUserLeaveMessage(
            fileId,
            message.getUserId(),
            message.getUserName(),
            message.getSessionKey()
        );
    }

    /**
     * 编辑状态更新
     * 客户端发送消息到: /app/file/{fileId}/status
     * 服务器广播消息到: /topic/file/{fileId}
     */
    @MessageMapping("/file/{fileId}/status")
    @SendTo("/topic/file/{fileId}")
    public WebSocketMessage handleEditStatus(
            @DestinationVariable Long fileId,
            @Payload WebSocketMessage message) {

        log.debug("编辑状态更新: fileId={}, userId={}, content={}",
                  fileId, message.getUserId(), message.getContent());

        // 增加编辑次数
        if (message.getSessionKey() != null) {
            sessionService.incrementEditCount(message.getSessionKey());
        }

        // 返回编辑状态消息
        return WebSocketMessage.createEditStatusMessage(
            fileId,
            message.getUserId(),
            message.getUserName(),
            message.getContent()
        );
    }

    /**
     * 光标移动
     * 客户端发送消息到: /app/file/{fileId}/cursor
     * 服务器广播消息到: /topic/file/{fileId}
     */
    @MessageMapping("/file/{fileId}/cursor")
    @SendTo("/topic/file/{fileId}")
    public WebSocketMessage handleCursorMove(
            @DestinationVariable Long fileId,
            @Payload WebSocketMessage message) {

        log.trace("光标移动: fileId={}, userId={}", fileId, message.getUserId());

        // 返回光标移动消息
        return WebSocketMessage.createCursorMoveMessage(
            fileId,
            message.getUserId(),
            message.getUserName(),
            message.getData()
        );
    }

    /**
     * 内容变更通知
     * 客户端发送消息到: /app/file/{fileId}/change
     * 服务器广播消息到: /topic/file/{fileId}
     */
    @MessageMapping("/file/{fileId}/change")
    @SendTo("/topic/file/{fileId}")
    public WebSocketMessage handleContentChange(
            @DestinationVariable Long fileId,
            @Payload WebSocketMessage message) {

        log.debug("内容变更: fileId={}, userId={}", fileId, message.getUserId());

        // 增加编辑次数
        if (message.getSessionKey() != null) {
            sessionService.incrementEditCount(message.getSessionKey());
        }

        // 返回内容变更消息
        return WebSocketMessage.createContentChangeMessage(
            fileId,
            message.getUserId(),
            message.getUserName(),
            message.getContent()
        );
    }

    /**
     * 心跳消息
     * 客户端发送消息到: /app/heartbeat
     */
    @MessageMapping("/heartbeat")
    public void handleHeartbeat(@Payload WebSocketMessage message) {
        log.trace("收到心跳: userId={}, sessionKey={}", message.getUserId(), message.getSessionKey());

        // 更新会话活跃时间
        if (message.getSessionKey() != null) {
            sessionService.updateActiveTime(message.getSessionKey());
        }
    }

}

