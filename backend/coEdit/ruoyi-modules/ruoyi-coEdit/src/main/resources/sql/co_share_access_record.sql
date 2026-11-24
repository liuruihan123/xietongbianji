-- 共享访问记录表
DROP TABLE IF EXISTS `co_share_access_record`;
CREATE TABLE `co_share_access_record` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '访问记录ID',
    `share_link_id` BIGINT(20) NOT NULL COMMENT '共享链接ID',
    `share_code` VARCHAR(50) NOT NULL COMMENT '共享码',
    `file_id` BIGINT(20) NOT NULL COMMENT '文件ID',
    `visitor_id` BIGINT(20) DEFAULT NULL COMMENT '访问者ID（如果已登录）',
    `visitor_name` VARCHAR(100) DEFAULT NULL COMMENT '访问者名称',
    `visitor_type` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '访问者类型：0-匿名 1-已登录',
    `access_ip` VARCHAR(50) NOT NULL COMMENT '访问IP地址',
    `access_location` VARCHAR(200) DEFAULT NULL COMMENT '访问地理位置',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    `access_time` DATETIME NOT NULL COMMENT '访问时间',
    `access_action` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '访问操作：0-查看 1-编辑 2-下载',
    `access_result` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '访问结果：0-成功 1-失败',
    `fail_reason` VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
    `session_id` VARCHAR(100) DEFAULT NULL COMMENT '会话ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_share_link_id` (`share_link_id`),
    KEY `idx_share_code` (`share_code`),
    KEY `idx_file_id` (`file_id`),
    KEY `idx_visitor_id` (`visitor_id`),
    KEY `idx_access_time` (`access_time`),
    KEY `idx_access_ip` (`access_ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='共享访问记录表';

