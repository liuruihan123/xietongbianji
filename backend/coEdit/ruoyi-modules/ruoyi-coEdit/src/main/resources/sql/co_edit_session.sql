-- ========================================
-- RuoYi-coEdit 协同编辑会话表
-- 作者: zuleed
-- 日期: 2025-11-04
-- 版本: v1.0
-- ========================================

-- 协同编辑会话表
DROP TABLE IF EXISTS `co_edit_session`;
CREATE TABLE `co_edit_session` (
    -- ==================== 主键 ====================
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '会话ID',

    -- ==================== 基本信息 ====================
    `file_id` BIGINT(20) NOT NULL COMMENT '文件ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(100) DEFAULT NULL COMMENT '用户名称',
    `session_key` VARCHAR(100) NOT NULL COMMENT '会话唯一标识',
    `document_key` VARCHAR(100) DEFAULT NULL COMMENT '文档Key（OnlyOffice使用）',

    -- ==================== 时间信息 ====================
    `start_time` DATETIME NOT NULL COMMENT '会话开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '会话结束时间',
    `last_active_time` DATETIME NOT NULL COMMENT '最后活跃时间',
    `duration` INT(11) DEFAULT 0 COMMENT '编辑时长（秒）',

    -- ==================== 状态信息 ====================
    `status` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '会话状态：0-活跃 1-空闲 2-已结束',
    `edit_count` INT(11) DEFAULT 0 COMMENT '编辑次数',

    -- ==================== 客户端信息 ====================
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',

    -- ==================== 审计字段 ====================
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- ==================== 主键和索引 ====================
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_key` (`session_key`),
    KEY `idx_file_id` (`file_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_last_active_time` (`last_active_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='协同编辑会话表';

