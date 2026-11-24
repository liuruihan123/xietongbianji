# RuoYi-coEdit 数据库设计文档

## 📋 概述

本文档描述了 RuoYi-coEdit 模块的数据库表结构设计，包括工作空间管理、成员管理、文件夹管理和权限控制等核心功能。

**设计原则：**
- 遵循 RuoYi 框架的数据库设计规范
- 使用 MyBatis-Plus 的 BaseEntity 基类
- 支持逻辑删除和软删除
- 合理设计索引以提升查询性能
- 支持数据审计和追溯

**作者：** zuleed  
**日期：** 2025-11-04  
**版本：** v1.0

---

## 📊 表结构设计

### 1. co_workspace（工作空间表）

**功能说明：** 存储工作空间的基本信息，支持个人空间、部门空间和团队空间三种类型。

**字段说明：**

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT(20) | 工作空间ID | 主键，自增 |
| workspace_name | VARCHAR(100) | 工作空间名称 | NOT NULL |
| workspace_type | TINYINT(4) | 工作空间类型 | NOT NULL, 1-个人 2-部门 3-团队 |
| workspace_desc | VARCHAR(500) | 工作空间描述 | NULL |
| workspace_icon | VARCHAR(255) | 工作空间图标 | NULL |
| workspace_color | VARCHAR(20) | 工作空间颜色标识 | NULL |
| owner_id | BIGINT(20) | 所有者ID | NOT NULL |
| owner_name | VARCHAR(100) | 所有者名称 | NULL |
| dept_id | BIGINT(20) | 部门ID | NULL |
| dept_name | VARCHAR(100) | 部门名称 | NULL |
| max_storage | BIGINT(20) | 最大存储空间（字节） | 默认10GB |
| used_storage | BIGINT(20) | 已使用存储空间（字节） | 默认0 |
| max_members | INT(11) | 最大成员数量 | 默认100 |
| member_count | INT(11) | 当前成员数量 | 默认1 |
| default_permission | VARCHAR(20) | 默认权限 | 默认'view' |
| allow_invite | TINYINT(1) | 是否允许成员邀请 | 默认1 |
| allow_share | TINYINT(1) | 是否允许外部分享 | 默认1 |
| allow_download | TINYINT(1) | 是否允许下载 | 默认1 |
| status | TINYINT(4) | 状态 | NOT NULL, 0-禁用 1-正常 2-归档 |
| is_default | TINYINT(1) | 是否默认空间 | 默认0 |
| sort_order | INT(11) | 排序顺序 | 默认0 |
| create_by | BIGINT(20) | 创建者 | NULL |
| create_time | DATETIME | 创建时间 | NOT NULL, 默认当前时间 |
| update_by | BIGINT(20) | 更新者 | NULL |
| update_time | DATETIME | 更新时间 | NOT NULL, 自动更新 |
| remark | VARCHAR(500) | 备注 | NULL |

**索引设计：**
- PRIMARY KEY (`id`)
- KEY `idx_owner_id` (`owner_id`)
- KEY `idx_dept_id` (`dept_id`)
- KEY `idx_workspace_type` (`workspace_type`)
- KEY `idx_status` (`status`)
- KEY `idx_create_time` (`create_time`)

**业务规则：**
1. 每个用户可以创建多个工作空间
2. 个人空间的 owner_id 为创建者用户ID
3. 部门空间的 dept_id 为所属部门ID
4. 团队空间可以自定义成员
5. 存储空间和成员数量有上限控制

---

### 2. co_workspace_member（工作空间成员表）

**功能说明：** 管理工作空间的成员关系和权限。

**字段说明：**

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT(20) | 成员ID | 主键，自增 |
| workspace_id | BIGINT(20) | 工作空间ID | NOT NULL |
| user_id | BIGINT(20) | 用户ID | NOT NULL |
| user_name | VARCHAR(100) | 用户名称 | NULL |
| dept_id | BIGINT(20) | 部门ID | NULL |
| dept_name | VARCHAR(100) | 部门名称 | NULL |
| member_role | VARCHAR(20) | 成员角色 | NOT NULL, 默认'member' |
| permissions | VARCHAR(500) | 权限列表（JSON） | NULL |
| invite_by | BIGINT(20) | 邀请人ID | NULL |
| invite_time | DATETIME | 邀请时间 | NULL |
| join_time | DATETIME | 加入时间 | NULL |
| join_type | TINYINT(4) | 加入方式 | 1-邀请 2-申请 3-系统 |
| status | TINYINT(4) | 状态 | NOT NULL, 0-移除 1-正常 2-禁用 |
| last_access_time | DATETIME | 最后访问时间 | NULL |
| access_count | INT(11) | 访问次数 | 默认0 |
| create_by | BIGINT(20) | 创建者 | NULL |
| create_time | DATETIME | 创建时间 | NOT NULL |
| update_by | BIGINT(20) | 更新者 | NULL |
| update_time | DATETIME | 更新时间 | NOT NULL |
| remark | VARCHAR(500) | 备注 | NULL |

**索引设计：**
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_workspace_user` (`workspace_id`, `user_id`)
- KEY `idx_user_id` (`user_id`)
- KEY `idx_workspace_id` (`workspace_id`)
- KEY `idx_member_role` (`member_role`)
- KEY `idx_status` (`status`)

**成员角色说明：**
- **owner**: 所有者，拥有所有权限
- **admin**: 管理员，可以管理成员和权限
- **editor**: 编辑者，可以编辑文档
- **viewer**: 查看者，只能查看文档
- **member**: 普通成员，根据具体权限配置

**业务规则：**
1. 一个用户在一个工作空间中只能有一个成员记录
2. 工作空间所有者自动成为成员，角色为 owner
3. 成员权限可以通过 permissions 字段进行细粒度控制
4. 记录成员的访问统计信息

---

### 3. co_folder（文件夹表）

**功能说明：** 管理文件夹结构，支持树形层级和私密文件夹。

**字段说明：**

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT(20) | 文件夹ID | 主键，自增 |
| folder_name | VARCHAR(255) | 文件夹名称 | NOT NULL |
| folder_path | VARCHAR(1000) | 文件夹路径 | NOT NULL |
| folder_level | INT(11) | 文件夹层级 | 默认1 |
| folder_icon | VARCHAR(255) | 文件夹图标 | NULL |
| folder_color | VARCHAR(20) | 文件夹颜色 | NULL |
| workspace_id | BIGINT(20) | 所属工作空间ID | NOT NULL |
| parent_id | BIGINT(20) | 父文件夹ID | 默认0（根目录） |
| owner_id | BIGINT(20) | 所有者ID | NOT NULL |
| owner_name | VARCHAR(100) | 所有者名称 | NULL |
| folder_type | TINYINT(4) | 文件夹类型 | NOT NULL, 1-普通 2-私密 3-共享 4-系统 |
| is_private | TINYINT(1) | 是否私密 | 默认0 |
| is_system | TINYINT(1) | 是否系统文件夹 | 默认0 |
| file_count | INT(11) | 文件数量 | 默认0 |
| subfolder_count | INT(11) | 子文件夹数量 | 默认0 |
| total_size | BIGINT(20) | 总大小（字节） | 默认0 |
| default_permission | VARCHAR(20) | 默认权限 | 默认'view' |
| inherit_permission | TINYINT(1) | 是否继承父文件夹权限 | 默认1 |
| status | TINYINT(4) | 状态 | NOT NULL, 0-删除 1-正常 2-锁定 |
| is_deleted | TINYINT(1) | 是否在回收站 | 默认0 |
| deleted_time | DATETIME | 删除时间 | NULL |
| sort_order | INT(11) | 排序顺序 | 默认0 |
| create_by | BIGINT(20) | 创建者 | NULL |
| create_time | DATETIME | 创建时间 | NOT NULL |
| update_by | BIGINT(20) | 更新者 | NULL |
| update_time | DATETIME | 更新时间 | NOT NULL |
| remark | VARCHAR(500) | 备注 | NULL |

**索引设计：**
- PRIMARY KEY (`id`)
- KEY `idx_workspace_id` (`workspace_id`)
- KEY `idx_parent_id` (`parent_id`)
- KEY `idx_owner_id` (`owner_id`)
- KEY `idx_folder_type` (`folder_type`)
- KEY `idx_is_private` (`is_private`)
- KEY `idx_is_deleted` (`is_deleted`)
- KEY `idx_status` (`status`)
- KEY `idx_create_time` (`create_time`)

**文件夹类型说明：**
- **普通文件夹**: 常规文件夹，权限可配置
- **私密文件夹**: 仅创建者可访问，其他用户无权限
- **共享文件夹**: 可以生成分享链接的文件夹
- **系统文件夹**: 系统自动创建，不可删除

**业务规则：**
1. 文件夹采用树形结构，parent_id 为 0 表示根目录
2. folder_path 记录完整路径，便于查询和展示
3. 私密文件夹只有创建者可以访问
4. 系统文件夹不能被删除
5. 删除文件夹时移入回收站（is_deleted=1）
6. 文件夹统计信息（文件数量、大小）需要定期更新

---

### 4. co_folder_permission（文件夹权限表）

**功能说明：** 实现细粒度的文件夹权限控制。

**字段说明：**

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT(20) | 权限ID | 主键，自增 |
| folder_id | BIGINT(20) | 文件夹ID | NOT NULL |
| workspace_id | BIGINT(20) | 工作空间ID | NOT NULL |
| permission_type | TINYINT(4) | 权限类型 | NOT NULL, 1-用户 2-部门 3-角色 4-团队 |
| target_id | BIGINT(20) | 目标ID | NOT NULL |
| target_name | VARCHAR(100) | 目标名称 | NULL |
| permission_level | VARCHAR(20) | 权限级别 | NOT NULL, 默认'view' |
| can_read | TINYINT(1) | 可读 | 默认1 |
| can_write | TINYINT(1) | 可写 | 默认0 |
| can_delete | TINYINT(1) | 可删除 | 默认0 |
| can_share | TINYINT(1) | 可分享 | 默认0 |
| can_download | TINYINT(1) | 可下载 | 默认1 |
| can_upload | TINYINT(1) | 可上传 | 默认0 |
| can_manage_permission | TINYINT(1) | 可管理权限 | 默认0 |
| grant_by | BIGINT(20) | 授权人ID | NULL |
| grant_time | DATETIME | 授权时间 | NULL |
| expire_time | DATETIME | 过期时间 | NULL（永久） |
| status | TINYINT(4) | 状态 | NOT NULL, 0-撤销 1-正常 2-过期 |
| create_by | BIGINT(20) | 创建者 | NULL |
| create_time | DATETIME | 创建时间 | NOT NULL |
| update_by | BIGINT(20) | 更新者 | NULL |
| update_time | DATETIME | 更新时间 | NOT NULL |
| remark | VARCHAR(500) | 备注 | NULL |

**索引设计：**
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_folder_target` (`folder_id`, `permission_type`, `target_id`)
- KEY `idx_folder_id` (`folder_id`)
- KEY `idx_workspace_id` (`workspace_id`)
- KEY `idx_target` (`permission_type`, `target_id`)
- KEY `idx_permission_level` (`permission_level`)
- KEY `idx_status` (`status`)
- KEY `idx_expire_time` (`expire_time`)

**权限级别说明：**
- **view**: 查看权限，只能查看和下载
- **edit**: 编辑权限，可以编辑和上传
- **admin**: 管理权限，可以删除和分享
- **owner**: 所有者权限，拥有所有权限

**业务规则：**
1. 同一个目标在同一个文件夹中只能有一条权限记录
2. 权限可以授予用户、部门、角色或团队
3. 支持权限过期时间设置
4. 细粒度权限控制（读、写、删除、分享、下载、上传、管理权限）
5. 权限继承：子文件夹可以继承父文件夹的权限

---

## 🔗 表关系图

```
co_workspace (工作空间)
    ├── co_workspace_member (成员)
    │   └── sys_user (用户)
    └── co_folder (文件夹)
        ├── co_folder_permission (权限)
        └── co_file (文件) [待实现]
```

---

## 📈 数据量估算

| 表名 | 预估数据量 | 增长速度 | 备注 |
|------|-----------|---------|------|
| co_workspace | 1万 | 慢 | 每个用户1-5个工作空间 |
| co_workspace_member | 10万 | 中 | 平均每个空间10个成员 |
| co_folder | 50万 | 快 | 平均每个空间50个文件夹 |
| co_folder_permission | 100万 | 快 | 平均每个文件夹2条权限 |

---

## 🔧 性能优化建议

### 1. 索引优化
- 为高频查询字段添加索引
- 使用复合索引优化多条件查询
- 定期分析慢查询并优化

### 2. 分区策略
- 对于大表可以考虑按时间分区
- co_folder_permission 可以按 workspace_id 分区

### 3. 缓存策略
- 工作空间信息可以缓存
- 权限信息可以缓存，设置合理的过期时间
- 文件夹树结构可以缓存

### 4. 归档策略
- 定期归档已删除的数据
- 回收站数据超过30天自动清理

---

## 📝 变更日志

### v1.0 (2025-11-04)
- 初始版本
- 创建工作空间相关4张表
- 完成基础字段设计和索引设计

