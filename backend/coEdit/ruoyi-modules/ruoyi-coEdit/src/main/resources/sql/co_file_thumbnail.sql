-- ----------------------------
-- 文件缩略图表
-- ----------------------------
DROP TABLE IF EXISTS `co_file_thumbnail`;
CREATE TABLE `co_file_thumbnail` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '缩略图ID',
    `file_id` BIGINT(20) NOT NULL COMMENT '文件ID',
    `thumbnail_path` VARCHAR(500) DEFAULT NULL COMMENT '缩略图路径',
    `thumbnail_url` VARCHAR(500) DEFAULT NULL COMMENT '缩略图URL',
    `width` INT(11) NOT NULL COMMENT '缩略图宽度',
    `height` INT(11) NOT NULL COMMENT '缩略图高度',
    `size` BIGINT(20) DEFAULT NULL COMMENT '缩略图大小（字节）',
    `format` VARCHAR(20) DEFAULT NULL COMMENT '缩略图格式',
    `status` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '生成状态：0-生成中 1-成功 2-失败',
    `fail_reason` VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
    `access_count` INT(11) DEFAULT 0 COMMENT '访问次数',
    `last_access_time` DATETIME DEFAULT NULL COMMENT '最后访问时间',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_file_id` (`file_id`),
    KEY `idx_status` (`status`),
    KEY `idx_expire_time` (`expire_time`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件缩略图表';

