-- 编辑历史表
CREATE TABLE IF NOT EXISTS `co_edit_history` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '历史ID',
    `file_id` BIGINT(20) NOT NULL COMMENT '文件ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(100) NOT NULL COMMENT '用户名称',
    `session_key` VARCHAR(100) DEFAULT NULL COMMENT '会话Key',
    `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型：INSERT-插入 DELETE-删除 UPDATE-更新 FORMAT-格式化',
    `content_before` TEXT DEFAULT NULL COMMENT '修改前内容',
    `content_after` TEXT DEFAULT NULL COMMENT '修改后内容',
    `position_data` TEXT DEFAULT NULL COMMENT '位置数据（JSON格式）',
    `operation_time` DATETIME NOT NULL COMMENT '操作时间',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_file_id` (`file_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_session_key` (`session_key`),
    KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='编辑历史表';

