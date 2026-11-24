package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.dto.WebSocketMessage;
import com.xmzs.coedit.domain.vo.CoEditSessionVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * WebSocket服务
 * 提供WebSocket消息发送功能
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WebSocketService {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ICoEditSessionService sessionService;

    /**
     * 向指定文件的所有编辑者广播消息
     *
     * @param fileId 文件ID
     * @param message 消息内容
     */
    public void sendToFile(Long fileId, WebSocketMessage message) {
        log.debug("向文件广播消息: fileId={}, type={}", fileId, message.getType());
        messagingTemplate.convertAndSend("/topic/file/" + fileId, message);
    }

    /**
     * 向指定用户发送消息
     *
     * @param userId 用户ID
     * @param message 消息内容
     */
    public void sendToUser(Long userId, WebSocketMessage message) {
        log.debug("向用户发送消息: userId={}, type={}", userId, message.getType());
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/message", message);
    }

    /**
     * 通知用户加入编辑
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @param sessionKey 会话Key
     */
    public void notifyUserJoin(Long fileId, Long userId, String userName, String sessionKey) {
        WebSocketMessage message = WebSocketMessage.createUserJoinMessage(fileId, userId, userName, sessionKey);
        sendToFile(fileId, message);
        log.info("通知用户加入: fileId={}, userId={}, userName={}", fileId, userId, userName);
    }

    /**
     * 通知用户离开编辑
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @param sessionKey 会话Key
     */
    public void notifyUserLeave(Long fileId, Long userId, String userName, String sessionKey) {
        WebSocketMessage message = WebSocketMessage.createUserLeaveMessage(fileId, userId, userName, sessionKey);
        sendToFile(fileId, message);
        log.info("通知用户离开: fileId={}, userId={}, userName={}", fileId, userId, userName);
    }

    /**
     * 通知编辑状态更新
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @param content 状态内容
     */
    public void notifyEditStatus(Long fileId, Long userId, String userName, String content) {
        WebSocketMessage message = WebSocketMessage.createEditStatusMessage(fileId, userId, userName, content);
        sendToFile(fileId, message);
        log.debug("通知编辑状态: fileId={}, userId={}, content={}", fileId, userId, content);
    }

    /**
     * 通知内容变更
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @param content 变更内容
     */
    public void notifyContentChange(Long fileId, Long userId, String userName, String content) {
        WebSocketMessage message = WebSocketMessage.createContentChangeMessage(fileId, userId, userName, content);
        sendToFile(fileId, message);
        log.debug("通知内容变更: fileId={}, userId={}", fileId, userId);
    }

    /**
     * 发送系统通知
     *
     * @param fileId 文件ID
     * @param content 通知内容
     */
    public void sendSystemNotify(Long fileId, String content) {
        WebSocketMessage message = WebSocketMessage.createSystemNotifyMessage(fileId, content);
        sendToFile(fileId, message);
        log.info("发送系统通知: fileId={}, content={}", fileId, content);
    }

    /**
     * 获取文件的在线用户列表
     *
     * @param fileId 文件ID
     * @return 在线用户列表
     */
    public List<CoEditSessionVo> getOnlineUsers(Long fileId) {
        return sessionService.queryActiveSessionsByFileId(fileId);
    }

    /**
     * 获取文件的在线用户数量
     *
     * @param fileId 文件ID
     * @return 在线用户数量
     */
    public Integer getOnlineUserCount(Long fileId) {
        return sessionService.getActiveSessionCount(fileId);
    }

}

