-- ----------------------------
-- 文件管理相关数据库表
-- Author: zuleed
-- Date: 2025-11-04
-- ----------------------------

-- ----------------------------
-- 1. 文件表
-- ----------------------------
DROP TABLE IF EXISTS `co_file`;
CREATE TABLE `co_file` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `workspace_id` BIGINT(20) NOT NULL COMMENT '工作空间ID',
    `folder_id` BIGINT(20) DEFAULT NULL COMMENT '文件夹ID，NULL表示根目录',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型（docx, xlsx, pptx等）',
    `file_size` BIGINT(20) DEFAULT 0 COMMENT '文件大小（字节）',
    `file_path` VARCHAR(500) DEFAULT NULL COMMENT '存储路径',
    `file_extension` VARCHAR(20) DEFAULT NULL COMMENT '文件扩展名',
    `mime_type` VARCHAR(100) DEFAULT NULL COMMENT 'MIME类型',
    
    -- OnlyOffice相关字段
    `document_key` VARCHAR(100) DEFAULT NULL COMMENT 'OnlyOffice文档Key',
    `current_version` INT(11) DEFAULT 1 COMMENT '当前版本号',
    `edit_status` TINYINT(4) DEFAULT 0 COMMENT '编辑状态：0-未编辑 1-编辑中 2-保存中 3-已保存',
    `current_editor_id` BIGINT(20) DEFAULT NULL COMMENT '当前编辑者ID',
    `current_editor_name` VARCHAR(100) DEFAULT NULL COMMENT '当前编辑者名称',
    `edit_start_time` DATETIME DEFAULT NULL COMMENT '编辑开始时间',
    `editor_count` INT(11) DEFAULT 0 COMMENT '当前编辑人数',
    
    -- 锁定相关字段
    `is_locked` TINYINT(1) DEFAULT 0 COMMENT '是否锁定：0-否 1-是',
    `locked_by` BIGINT(20) DEFAULT NULL COMMENT '锁定人ID',
    `locked_by_name` VARCHAR(100) DEFAULT NULL COMMENT '锁定人名称',
    `locked_time` DATETIME DEFAULT NULL COMMENT '锁定时间',
    `lock_reason` VARCHAR(500) DEFAULT NULL COMMENT '锁定原因',
    
    -- 权限相关字段
    `is_private` TINYINT(1) DEFAULT 0 COMMENT '是否私有：0-否 1-是',
    `owner_id` BIGINT(20) DEFAULT NULL COMMENT '所有者ID',
    `owner_name` VARCHAR(100) DEFAULT NULL COMMENT '所有者名称',
    
    -- 统计字段
    `download_count` INT(11) DEFAULT 0 COMMENT '下载次数',
    `view_count` INT(11) DEFAULT 0 COMMENT '查看次数',
    `edit_count` INT(11) DEFAULT 0 COMMENT '编辑次数',
    `share_count` INT(11) DEFAULT 0 COMMENT '分享次数',
    
    -- 编辑信息
    `last_edit_time` DATETIME DEFAULT NULL COMMENT '最后编辑时间',
    `last_edit_by` BIGINT(20) DEFAULT NULL COMMENT '最后编辑人ID',
    `last_edit_by_name` VARCHAR(100) DEFAULT NULL COMMENT '最后编辑人名称',
    
    -- 状态和排序
    `status` TINYINT(4) DEFAULT 1 COMMENT '状态：0-已删除 1-正常 2-归档',
    `sort_order` INT(11) DEFAULT 0 COMMENT '排序',
    
    -- 审计字段
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    
    PRIMARY KEY (`id`),
    KEY `idx_workspace_id` (`workspace_id`),
    KEY `idx_folder_id` (`folder_id`),
    KEY `idx_document_key` (`document_key`),
    KEY `idx_owner_id` (`owner_id`),
    KEY `idx_create_by` (`create_by`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

-- ----------------------------
-- 2. 文件版本表
-- ----------------------------
DROP TABLE IF EXISTS `co_file_version`;
CREATE TABLE `co_file_version` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '版本ID',
    `file_id` BIGINT(20) NOT NULL COMMENT '文件ID',
    `version_number` INT(11) NOT NULL COMMENT '版本号',
    `file_name` VARCHAR(255) DEFAULT NULL COMMENT '文件名',
    `file_size` BIGINT(20) DEFAULT 0 COMMENT '文件大小（字节）',
    `file_path` VARCHAR(500) DEFAULT NULL COMMENT '存储路径',
    `document_key` VARCHAR(100) DEFAULT NULL COMMENT 'OnlyOffice文档Key',
    
    -- 变更信息
    `change_type` TINYINT(4) DEFAULT 1 COMMENT '变更类型：1-创建 2-编辑 3-上传 4-替换',
    `change_description` VARCHAR(500) DEFAULT NULL COMMENT '变更描述',
    `change_summary` TEXT DEFAULT NULL COMMENT '变更摘要（详细内容）',
    
    -- 版本状态
    `is_current` TINYINT(1) DEFAULT 0 COMMENT '是否当前版本：0-否 1-是',
    `is_major` TINYINT(1) DEFAULT 0 COMMENT '是否主要版本：0-否 1-是',
    
    -- 编辑信息
    `editor_id` BIGINT(20) DEFAULT NULL COMMENT '编辑者ID',
    `editor_name` VARCHAR(100) DEFAULT NULL COMMENT '编辑者名称',
    `edit_duration` INT(11) DEFAULT 0 COMMENT '编辑时长（秒）',
    
    -- 审计字段
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_file_version` (`file_id`, `version_number`),
    KEY `idx_file_id` (`file_id`),
    KEY `idx_is_current` (`is_current`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件版本表';

-- ----------------------------
-- 3. 回收站表
-- ----------------------------
DROP TABLE IF EXISTS `co_recycle_bin`;
CREATE TABLE `co_recycle_bin` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '回收站ID',
    `file_id` BIGINT(20) NOT NULL COMMENT '原文件ID',
    `workspace_id` BIGINT(20) NOT NULL COMMENT '工作空间ID',
    `folder_id` BIGINT(20) DEFAULT NULL COMMENT '原文件夹ID',
    `folder_path` VARCHAR(1000) DEFAULT NULL COMMENT '原文件夹路径',
    
    -- 文件信息
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型',
    `file_size` BIGINT(20) DEFAULT 0 COMMENT '文件大小（字节）',
    `file_extension` VARCHAR(20) DEFAULT NULL COMMENT '文件扩展名',
    
    -- 存储路径
    `original_path` VARCHAR(500) DEFAULT NULL COMMENT '原存储路径',
    `backup_path` VARCHAR(500) DEFAULT NULL COMMENT '备份路径',
    
    -- 删除信息
    `deleted_by` BIGINT(20) DEFAULT NULL COMMENT '删除人ID',
    `deleted_by_name` VARCHAR(100) DEFAULT NULL COMMENT '删除人名称',
    `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
    `delete_reason` VARCHAR(500) DEFAULT NULL COMMENT '删除原因',
    
    -- 过期和恢复
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间（30天后自动清理）',
    `can_restore` TINYINT(1) DEFAULT 1 COMMENT '是否可恢复：0-否 1-是',
    `restore_time` DATETIME DEFAULT NULL COMMENT '恢复时间',
    `restore_by` BIGINT(20) DEFAULT NULL COMMENT '恢复人ID',
    `restore_by_name` VARCHAR(100) DEFAULT NULL COMMENT '恢复人名称',
    
    -- 原文件信息（用于恢复）
    `original_owner_id` BIGINT(20) DEFAULT NULL COMMENT '原所有者ID',
    `original_owner_name` VARCHAR(100) DEFAULT NULL COMMENT '原所有者名称',
    `original_create_time` DATETIME DEFAULT NULL COMMENT '原创建时间',
    `original_update_time` DATETIME DEFAULT NULL COMMENT '原更新时间',
    
    -- 状态
    `status` TINYINT(4) DEFAULT 1 COMMENT '状态：0-已清理 1-待清理 2-已恢复',
    
    -- 审计字段
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    
    PRIMARY KEY (`id`),
    KEY `idx_file_id` (`file_id`),
    KEY `idx_workspace_id` (`workspace_id`),
    KEY `idx_deleted_by` (`deleted_by`),
    KEY `idx_deleted_time` (`deleted_time`),
    KEY `idx_expire_time` (`expire_time`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回收站表';

-- ----------------------------
-- 初始化数据
-- ----------------------------
-- 无需初始化数据

