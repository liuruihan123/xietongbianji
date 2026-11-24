-- 共享安全控制表
DROP TABLE IF EXISTS `co_share_security`;
CREATE TABLE `co_share_security` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '安全配置ID',
    `share_link_id` BIGINT(20) NOT NULL COMMENT '共享链接ID',
    `ip_whitelist` TEXT DEFAULT NULL COMMENT 'IP白名单（JSON数组）',
    `ip_blacklist` TEXT DEFAULT NULL COMMENT 'IP黑名单（JSON数组）',
    `max_access_per_hour` INT(11) DEFAULT NULL COMMENT '每小时最大访问次数',
    `max_access_per_day` INT(11) DEFAULT NULL COMMENT '每天最大访问次数',
    `is_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用安全控制：0-否 1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_share_link_id` (`share_link_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='共享安全控制表';

