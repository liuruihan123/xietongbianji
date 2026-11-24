-- ----------------------------
-- 文件历史表
-- ----------------------------
DROP TABLE IF EXISTS `co_file_history`;
CREATE TABLE `co_file_history` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '历史ID',
    `file_id` BIGINT(20) NOT NULL COMMENT '文件ID',
    `version` INT(11) NOT NULL COMMENT '版本号',
    `document_key` VARCHAR(200) NOT NULL COMMENT '文档Key（OnlyOffice）',
    `file_path` VARCHAR(500) DEFAULT NULL COMMENT '文件路径',
    `file_url` VARCHAR(500) DEFAULT NULL COMMENT '文件URL',
    `file_size` BIGINT(20) DEFAULT NULL COMMENT '文件大小',
    `changes_url` VARCHAR(500) DEFAULT NULL COMMENT '变更数据URL',
    `changes_path` VARCHAR(500) DEFAULT NULL COMMENT '变更数据路径',
    `server_version` INT(11) DEFAULT NULL COMMENT '服务器版本号',
    `change_description` VARCHAR(500) DEFAULT NULL COMMENT '变更描述',
    `change_type` TINYINT(4) DEFAULT NULL COMMENT '变更类型：1-创建 2-编辑 3-重命名 4-移动 5-恢复',
    `is_current` TINYINT(1) DEFAULT 0 COMMENT '是否当前版本',
    `creator_id` BIGINT(20) DEFAULT NULL COMMENT '创建者ID',
    `creator_name` VARCHAR(100) DEFAULT NULL COMMENT '创建者名称',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_file_id` (`file_id`),
    KEY `idx_version` (`version`),
    KEY `idx_document_key` (`document_key`),
    KEY `idx_is_current` (`is_current`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件历史表';

-- ----------------------------
-- 文件变更记录表
-- ----------------------------
DROP TABLE IF EXISTS `co_file_change`;
CREATE TABLE `co_file_change` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '变更ID',
    `history_id` BIGINT(20) NOT NULL COMMENT '历史ID',
    `file_id` BIGINT(20) NOT NULL COMMENT '文件ID',
    `version` INT(11) NOT NULL COMMENT '版本号',
    `change_type` VARCHAR(50) DEFAULT NULL COMMENT '变更类型：insert-插入 delete-删除 modify-修改',
    `position` VARCHAR(200) DEFAULT NULL COMMENT '变更位置',
    `content` TEXT COMMENT '变更内容',
    `old_content` TEXT COMMENT '变更前内容',
    `new_content` TEXT COMMENT '变更后内容',
    `user_id` BIGINT(20) DEFAULT NULL COMMENT '变更用户ID',
    `user_name` VARCHAR(100) DEFAULT NULL COMMENT '变更用户名称',
    `change_data` TEXT COMMENT '变更数据（JSON）',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_history_id` (`history_id`),
    KEY `idx_file_id` (`file_id`),
    KEY `idx_version` (`version`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件变更记录表';

