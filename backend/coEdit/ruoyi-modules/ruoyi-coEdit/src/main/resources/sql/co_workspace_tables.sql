-- ========================================
-- RuoYi-coEdit 工作空间相关数据库表
-- 作者: zuleed
-- 日期: 2025-11-04
-- 版本: v1.0
-- ========================================

-- 1. 工作空间表
DROP TABLE IF EXISTS `co_workspace`;
CREATE TABLE `co_workspace` (
    -- ==================== 主键 ====================
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '工作空间ID',
    
    -- ==================== 基本信息 ====================
    `workspace_name` VARCHAR(100) NOT NULL COMMENT '工作空间名称',
    `workspace_type` TINYINT(4) NOT NULL COMMENT '工作空间类型：1-个人空间 2-部门空间 3-团队空间',
    `workspace_desc` VARCHAR(500) DEFAULT NULL COMMENT '工作空间描述',
    `workspace_icon` VARCHAR(255) DEFAULT NULL COMMENT '工作空间图标',
    `workspace_color` VARCHAR(20) DEFAULT NULL COMMENT '工作空间颜色标识',
    
    -- ==================== 关联信息 ====================
    `owner_id` BIGINT(20) NOT NULL COMMENT '所有者ID（用户ID）',
    `owner_name` VARCHAR(100) DEFAULT NULL COMMENT '所有者名称',
    `dept_id` BIGINT(20) DEFAULT NULL COMMENT '部门ID（部门空间时使用）',
    `dept_name` VARCHAR(100) DEFAULT NULL COMMENT '部门名称',
    
    -- ==================== 配置信息 ====================
    `max_storage` BIGINT(20) DEFAULT 10737418240 COMMENT '最大存储空间（字节，默认10GB）',
    `used_storage` BIGINT(20) DEFAULT 0 COMMENT '已使用存储空间（字节）',
    `max_members` INT(11) DEFAULT 100 COMMENT '最大成员数量',
    `member_count` INT(11) DEFAULT 1 COMMENT '当前成员数量',
    
    -- ==================== 权限配置 ====================
    `default_permission` VARCHAR(20) DEFAULT 'view' COMMENT '默认权限：view-查看 edit-编辑 admin-管理',
    `allow_invite` TINYINT(1) DEFAULT 1 COMMENT '是否允许成员邀请：0-否 1-是',
    `allow_share` TINYINT(1) DEFAULT 1 COMMENT '是否允许外部分享：0-否 1-是',
    `allow_download` TINYINT(1) DEFAULT 1 COMMENT '是否允许下载：0-否 1-是',
    
    -- ==================== 状态信息 ====================
    `status` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-正常 2-归档',
    `is_default` TINYINT(1) DEFAULT 0 COMMENT '是否默认空间：0-否 1-是',
    `sort_order` INT(11) DEFAULT 0 COMMENT '排序顺序',
    
    -- ==================== 审计字段 ====================
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    
    -- ==================== 主键和索引 ====================
    PRIMARY KEY (`id`),
    KEY `idx_owner_id` (`owner_id`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_workspace_type` (`workspace_type`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作空间表';


-- 2. 工作空间成员表
DROP TABLE IF EXISTS `co_workspace_member`;
CREATE TABLE `co_workspace_member` (
    -- ==================== 主键 ====================
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '成员ID',
    
    -- ==================== 关联信息 ====================
    `workspace_id` BIGINT(20) NOT NULL COMMENT '工作空间ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(100) DEFAULT NULL COMMENT '用户名称',
    `dept_id` BIGINT(20) DEFAULT NULL COMMENT '部门ID',
    `dept_name` VARCHAR(100) DEFAULT NULL COMMENT '部门名称',
    
    -- ==================== 角色权限 ====================
    `member_role` VARCHAR(20) NOT NULL DEFAULT 'member' COMMENT '成员角色：owner-所有者 admin-管理员 editor-编辑者 viewer-查看者 member-普通成员',
    `permissions` VARCHAR(500) DEFAULT NULL COMMENT '权限列表（JSON格式）：["read","write","delete","share","invite"]',
    
    -- ==================== 邀请信息 ====================
    `invite_by` BIGINT(20) DEFAULT NULL COMMENT '邀请人ID',
    `invite_time` DATETIME DEFAULT NULL COMMENT '邀请时间',
    `join_time` DATETIME DEFAULT NULL COMMENT '加入时间',
    `join_type` TINYINT(4) DEFAULT 1 COMMENT '加入方式：1-邀请 2-申请 3-系统添加',
    
    -- ==================== 状态信息 ====================
    `status` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态：0-已移除 1-正常 2-已禁用',
    `last_access_time` DATETIME DEFAULT NULL COMMENT '最后访问时间',
    `access_count` INT(11) DEFAULT 0 COMMENT '访问次数',
    
    -- ==================== 审计字段 ====================
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    
    -- ==================== 主键和索引 ====================
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_workspace_user` (`workspace_id`, `user_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_workspace_id` (`workspace_id`),
    KEY `idx_member_role` (`member_role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作空间成员表';


-- 3. 文件夹表
DROP TABLE IF EXISTS `co_folder`;
CREATE TABLE `co_folder` (
    -- ==================== 主键 ====================
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '文件夹ID',
    
    -- ==================== 基本信息 ====================
    `folder_name` VARCHAR(255) NOT NULL COMMENT '文件夹名称',
    `folder_path` VARCHAR(1000) NOT NULL COMMENT '文件夹路径',
    `folder_level` INT(11) DEFAULT 1 COMMENT '文件夹层级',
    `folder_icon` VARCHAR(255) DEFAULT NULL COMMENT '文件夹图标',
    `folder_color` VARCHAR(20) DEFAULT NULL COMMENT '文件夹颜色',
    
    -- ==================== 关联信息 ====================
    `workspace_id` BIGINT(20) NOT NULL COMMENT '所属工作空间ID',
    `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父文件夹ID（0表示根目录）',
    `owner_id` BIGINT(20) NOT NULL COMMENT '所有者ID',
    `owner_name` VARCHAR(100) DEFAULT NULL COMMENT '所有者名称',
    
    -- ==================== 文件夹类型 ====================
    `folder_type` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '文件夹类型：1-普通文件夹 2-私密文件夹 3-共享文件夹 4-系统文件夹',
    `is_private` TINYINT(1) DEFAULT 0 COMMENT '是否私密：0-否 1-是（仅创建者可访问）',
    `is_system` TINYINT(1) DEFAULT 0 COMMENT '是否系统文件夹：0-否 1-是（不可删除）',
    
    -- ==================== 统计信息 ====================
    `file_count` INT(11) DEFAULT 0 COMMENT '文件数量',
    `subfolder_count` INT(11) DEFAULT 0 COMMENT '子文件夹数量',
    `total_size` BIGINT(20) DEFAULT 0 COMMENT '总大小（字节）',
    
    -- ==================== 权限配置 ====================
    `default_permission` VARCHAR(20) DEFAULT 'view' COMMENT '默认权限：view-查看 edit-编辑 admin-管理',
    `inherit_permission` TINYINT(1) DEFAULT 1 COMMENT '是否继承父文件夹权限：0-否 1-是',
    
    -- ==================== 状态信息 ====================
    `status` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态：0-已删除 1-正常 2-已锁定',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否在回收站：0-否 1-是',
    `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
    `sort_order` INT(11) DEFAULT 0 COMMENT '排序顺序',
    
    -- ==================== 审计字段 ====================
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    
    -- ==================== 主键和索引 ====================
    PRIMARY KEY (`id`),
    KEY `idx_workspace_id` (`workspace_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_owner_id` (`owner_id`),
    KEY `idx_folder_type` (`folder_type`),
    KEY `idx_is_private` (`is_private`),
    KEY `idx_is_deleted` (`is_deleted`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件夹表';


-- 4. 文件夹权限表
DROP TABLE IF EXISTS `co_folder_permission`;
CREATE TABLE `co_folder_permission` (
    -- ==================== 主键 ====================
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    
    -- ==================== 关联信息 ====================
    `folder_id` BIGINT(20) NOT NULL COMMENT '文件夹ID',
    `workspace_id` BIGINT(20) NOT NULL COMMENT '工作空间ID',
    
    -- ==================== 授权对象 ====================
    `permission_type` TINYINT(4) NOT NULL COMMENT '权限类型：1-用户 2-部门 3-角色 4-团队',
    `target_id` BIGINT(20) NOT NULL COMMENT '目标ID（用户ID/部门ID/角色ID/团队ID）',
    `target_name` VARCHAR(100) DEFAULT NULL COMMENT '目标名称',
    
    -- ==================== 权限配置 ====================
    `permission_level` VARCHAR(20) NOT NULL DEFAULT 'view' COMMENT '权限级别：view-查看 edit-编辑 admin-管理 owner-所有者',
    `can_read` TINYINT(1) DEFAULT 1 COMMENT '可读：0-否 1-是',
    `can_write` TINYINT(1) DEFAULT 0 COMMENT '可写：0-否 1-是',
    `can_delete` TINYINT(1) DEFAULT 0 COMMENT '可删除：0-否 1-是',
    `can_share` TINYINT(1) DEFAULT 0 COMMENT '可分享：0-否 1-是',
    `can_download` TINYINT(1) DEFAULT 1 COMMENT '可下载：0-否 1-是',
    `can_upload` TINYINT(1) DEFAULT 0 COMMENT '可上传：0-否 1-是',
    `can_manage_permission` TINYINT(1) DEFAULT 0 COMMENT '可管理权限：0-否 1-是',
    
    -- ==================== 授权信息 ====================
    `grant_by` BIGINT(20) DEFAULT NULL COMMENT '授权人ID',
    `grant_time` DATETIME DEFAULT NULL COMMENT '授权时间',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间（NULL表示永久）',
    
    -- ==================== 状态信息 ====================
    `status` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态：0-已撤销 1-正常 2-已过期',
    
    -- ==================== 审计字段 ====================
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    
    -- ==================== 主键和索引 ====================
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_folder_target` (`folder_id`, `permission_type`, `target_id`),
    KEY `idx_folder_id` (`folder_id`),
    KEY `idx_workspace_id` (`workspace_id`),
    KEY `idx_target` (`permission_type`, `target_id`),
    KEY `idx_permission_level` (`permission_level`),
    KEY `idx_status` (`status`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件夹权限表';


-- ========================================
-- 初始化数据
-- ========================================

-- 插入系统默认工作空间类型说明
-- workspace_type: 1-个人空间 2-部门空间 3-团队空间
-- member_role: owner-所有者 admin-管理员 editor-编辑者 viewer-查看者 member-普通成员
-- folder_type: 1-普通文件夹 2-私密文件夹 3-共享文件夹 4-系统文件夹
-- permission_type: 1-用户 2-部门 3-角色 4-团队
-- permission_level: view-查看 edit-编辑 admin-管理 owner-所有者

