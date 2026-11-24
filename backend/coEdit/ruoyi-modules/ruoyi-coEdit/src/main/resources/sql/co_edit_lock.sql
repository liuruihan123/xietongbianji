-- 编辑锁表
CREATE TABLE IF NOT EXISTS `co_edit_lock` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '锁ID',
    `file_id` BIGINT(20) NOT NULL COMMENT '文件ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '持有锁的用户ID',
    `user_name` VARCHAR(100) NOT NULL COMMENT '用户名称',
    `lock_key` VARCHAR(100) NOT NULL COMMENT '锁唯一标识',
    `lock_type` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '锁类型：0-读锁 1-写锁',
    `lock_scope` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '锁范围：0-整个文档 1-段落 2-区域',
    `scope_data` TEXT DEFAULT NULL COMMENT '范围数据（JSON格式）',
    `acquire_time` DATETIME NOT NULL COMMENT '获取锁时间',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `status` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '锁状态：0-活跃 1-已释放 2-已过期',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_lock_key` (`lock_key`),
    KEY `idx_file_id` (`file_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='编辑锁表';

