-- 文件版本表
CREATE TABLE IF NOT EXISTS `co_file_version` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '版本ID',
    `file_id` BIGINT(20) NOT NULL COMMENT '文件ID',
    `version_number` INT(11) NOT NULL COMMENT '版本号',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_size` BIGINT(20) NOT NULL COMMENT '文件大小（字节）',
    `file_path` VARCHAR(500) NOT NULL COMMENT '版本文件存储路径',
    `file_hash` VARCHAR(64) NOT NULL COMMENT '文件哈希值（MD5）',
    `version_desc` VARCHAR(500) DEFAULT NULL COMMENT '版本描述',
    `created_by` BIGINT(20) NOT NULL COMMENT '创建者ID',
    `created_name` VARCHAR(100) NOT NULL COMMENT '创建者名称',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_current` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否当前版本（0-否 1-是）',
    `change_type` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '变更类型（0-创建 1-修改 2-重命名 3-回滚）',
    PRIMARY KEY (`id`),
    KEY `idx_file_id` (`file_id`),
    KEY `idx_version_number` (`version_number`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_is_current` (`is_current`),
    KEY `idx_file_hash` (`file_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件版本表';

