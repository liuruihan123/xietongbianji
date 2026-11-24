package com.xmzs.coedit.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户在线状态DTO
 * 用于实时协作中的用户状态管理
 *
 * @author zuleed
 * @date 2025-11-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPresence {

    /**
     * 用户ID（认证用户有值，访客为null）
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 是否为认证用户
     */
    private Boolean isAuthenticated;

    /**
     * 用户头像URL
     */
    private String avatarUrl;

    /**
     * 用户颜色（用于光标和选择区域显示）
     */
    private String userColor;

    /**
     * 加入时间
     */
    private Long joinTime;

    /**
     * 最后活跃时间
     */
    private Long lastActiveTime;

    /**
     * 当前光标位置
     */
    private CursorPosition cursorPosition;

    /**
     * 当前选择区域
     */
    private SelectionRange selectionRange;

    /**
     * 用户状态
     * online: 在线
     * away: 离开
     * busy: 忙碌
     */
    private String status = "online";

    /**
     * 光标位置信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursorPosition {
        private Integer line;
        private Integer column;
        private Long timestamp;
    }

    /**
     * 选择区域信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectionRange {
        private Integer startLine;
        private Integer startColumn;
        private Integer endLine;
        private Integer endColumn;
        private Long timestamp;
    }
}
