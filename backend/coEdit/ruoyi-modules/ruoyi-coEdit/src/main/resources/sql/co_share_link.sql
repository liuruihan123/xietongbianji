-- 共享链接表
CREATE TABLE IF NOT EXISTS `co_share_link` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '共享链接ID',
    `file_id` BIGINT(20) NOT NULL COMMENT '文件ID',
    `share_code` VARCHAR(20) NOT NULL COMMENT '共享码（唯一标识）',
    `share_url` VARCHAR(500) DEFAULT NULL COMMENT '完整的共享URL',
    `creator_id` BIGINT(20) NOT NULL COMMENT '创建者ID',
    `creator_name` VARCHAR(100) NOT NULL COMMENT '创建者名称',
    `share_type` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '共享类型：0-公开 1-密码保护',
    `share_password` VARCHAR(100) DEFAULT NULL COMMENT '访问密码（明文存储）',
    `permission_type` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '权限类型：0-只读 1-可编辑 2-可下载 3-完全权限',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `max_access_count` INT(11) DEFAULT NULL COMMENT '最大访问次数（NULL表示无限制）',
    `access_count` INT(11) NOT NULL DEFAULT 0 COMMENT '已访问次数',
    `status` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态：0-正常 1-已禁用 2-已过期',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_share_code` (`share_code`),
    KEY `idx_file_id` (`file_id`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_status_expire` (`status`, `expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='共享链接表';

