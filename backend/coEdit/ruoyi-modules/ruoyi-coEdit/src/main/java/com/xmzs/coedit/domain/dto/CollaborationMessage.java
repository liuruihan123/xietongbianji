package com.xmzs.coedit.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 协作消息DTO
 * 用于WebSocket实时协作通信
 *
 * @author zuleed
 * @date 2025-11-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollaborationMessage {

    /**
     * 消息类型
     * cursor: 光标位置更新
     * selection: 选择区域更新
     * content: 内容更新
     * user_joined: 用户加入
     * user_left: 用户离开
     * online_users: 在线用户列表
     * heartbeat: 心跳消息
     */
    private String type;

    /**
     * 发送者用户ID
     */
    private Long userId;

    /**
     * 发送者用户名
     */
    private String userName;

    /**
     * 消息数据（JSON格式）
     */
    private Object data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 文档ID
     */
    private String documentId;

    /**
     * 会话ID
     */
    private String sessionId;
}
