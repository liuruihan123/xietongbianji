-- 协同编辑日志表
CREATE TABLE IF NOT EXISTS `co_edit_log` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `file_id` BIGINT(20) NOT NULL COMMENT '文件ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(100) NOT NULL COMMENT '用户名称',
    `session_key` VARCHAR(100) DEFAULT NULL COMMENT '会话Key',
    `log_type` VARCHAR(50) NOT NULL COMMENT '日志类型：JOIN-加入 LEAVE-离开 EDIT-编辑 LOCK-加锁 UNLOCK-解锁 CONFLICT-冲突',
    `log_content` VARCHAR(500) NOT NULL COMMENT '日志内容',
    `log_data` TEXT DEFAULT NULL COMMENT '日志数据（JSON格式）',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    `log_time` DATETIME NOT NULL COMMENT '日志时间',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_file_id` (`file_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_session_key` (`session_key`),
    KEY `idx_log_type` (`log_type`),
    KEY `idx_log_time` (`log_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='协同编辑日志表';

