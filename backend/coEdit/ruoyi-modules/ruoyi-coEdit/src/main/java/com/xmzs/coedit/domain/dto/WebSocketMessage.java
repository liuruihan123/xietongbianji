package com.xmzs.coedit.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * WebSocket消息DTO
 * 用于协同编辑的实时消息传递
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息类型
     */
    private MessageType type;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 会话Key
     */
    private String sessionKey;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息数据（扩展字段）
     */
    private Map<String, Object> data;

    /**
     * 时间戳
     */
    private Date timestamp;

    /**
     * 消息类型枚举
     */
    public enum MessageType {
        /**
         * 用户加入编辑
         */
        USER_JOIN,

        /**
         * 用户离开编辑
         */
        USER_LEAVE,

        /**
         * 编辑状态更新
         */
        EDIT_STATUS,

        /**
         * 光标移动
         */
        CURSOR_MOVE,

        /**
         * 内容变更通知
         */
        CONTENT_CHANGE,

        /**
         * 心跳消息
         */
        HEARTBEAT,

        /**
         * 系统通知
         */
        SYSTEM_NOTIFY,

        /**
         * 错误消息
         */
        ERROR
    }

    /**
     * 创建用户加入消息
     */
    public static WebSocketMessage createUserJoinMessage(Long fileId, Long userId, String userName, String sessionKey) {
        return WebSocketMessage.builder()
                .type(MessageType.USER_JOIN)
                .fileId(fileId)
                .userId(userId)
                .userName(userName)
                .sessionKey(sessionKey)
                .content(userName + " 加入了编辑")
                .timestamp(new Date())
                .build();
    }

    /**
     * 创建用户离开消息
     */
    public static WebSocketMessage createUserLeaveMessage(Long fileId, Long userId, String userName, String sessionKey) {
        return WebSocketMessage.builder()
                .type(MessageType.USER_LEAVE)
                .fileId(fileId)
                .userId(userId)
                .userName(userName)
                .sessionKey(sessionKey)
                .content(userName + " 离开了编辑")
                .timestamp(new Date())
                .build();
    }

    /**
     * 创建编辑状态消息
     */
    public static WebSocketMessage createEditStatusMessage(Long fileId, Long userId, String userName, String content) {
        return WebSocketMessage.builder()
                .type(MessageType.EDIT_STATUS)
                .fileId(fileId)
                .userId(userId)
                .userName(userName)
                .content(content)
                .timestamp(new Date())
                .build();
    }

    /**
     * 创建光标移动消息
     */
    public static WebSocketMessage createCursorMoveMessage(Long fileId, Long userId, String userName, Map<String, Object> data) {
        return WebSocketMessage.builder()
                .type(MessageType.CURSOR_MOVE)
                .fileId(fileId)
                .userId(userId)
                .userName(userName)
                .data(data)
                .timestamp(new Date())
                .build();
    }

    /**
     * 创建内容变更消息
     */
    public static WebSocketMessage createContentChangeMessage(Long fileId, Long userId, String userName, String content) {
        return WebSocketMessage.builder()
                .type(MessageType.CONTENT_CHANGE)
                .fileId(fileId)
                .userId(userId)
                .userName(userName)
                .content(content)
                .timestamp(new Date())
                .build();
    }

    /**
     * 创建心跳消息
     */
    public static WebSocketMessage createHeartbeatMessage(Long userId, String sessionKey) {
        return WebSocketMessage.builder()
                .type(MessageType.HEARTBEAT)
                .userId(userId)
                .sessionKey(sessionKey)
                .timestamp(new Date())
                .build();
    }

    /**
     * 创建系统通知消息
     */
    public static WebSocketMessage createSystemNotifyMessage(Long fileId, String content) {
        return WebSocketMessage.builder()
                .type(MessageType.SYSTEM_NOTIFY)
                .fileId(fileId)
                .content(content)
                .timestamp(new Date())
                .build();
    }

    /**
     * 创建错误消息
     */
    public static WebSocketMessage createErrorMessage(String content) {
        return WebSocketMessage.builder()
                .type(MessageType.ERROR)
                .content(content)
                .timestamp(new Date())
                .build();
    }

}

