# 文件管理数据库设计文档

## 设计概述

本文档描述了coEdit协同编辑系统中文件管理相关的数据库表设计，包括文件表、文件版本表和回收站表。

## 设计原则

1. **完整性**：记录文件的完整生命周期，从创建到删除
2. **可追溯性**：支持版本历史追踪和操作审计
3. **可恢复性**：支持软删除和文件恢复机制
4. **高性能**：合理的索引设计，支持高效查询
5. **OnlyOffice集成**：支持OnlyOffice协同编辑功能
6. **扩展性**：预留扩展字段，支持未来功能扩展

## 表结构设计

### 1. co_file（文件表）

#### 表说明
存储文件的基本信息、OnlyOffice编辑状态、权限信息和统计数据。

#### 字段说明

**基本信息字段**
- `id`: 文件ID，主键，自增
- `workspace_id`: 工作空间ID，必填
- `folder_id`: 文件夹ID，NULL表示根目录
- `file_name`: 文件名，必填
- `file_type`: 文件类型（docx, xlsx, pptx等）
- `file_size`: 文件大小（字节）
- `file_path`: 存储路径
- `file_extension`: 文件扩展名
- `mime_type`: MIME类型

**OnlyOffice相关字段**
- `document_key`: OnlyOffice文档Key，用于标识文档
- `current_version`: 当前版本号，默认1
- `edit_status`: 编辑状态（0-未编辑 1-编辑中 2-保存中 3-已保存）
- `current_editor_id`: 当前编辑者ID
- `current_editor_name`: 当前编辑者名称
- `edit_start_time`: 编辑开始时间
- `editor_count`: 当前编辑人数，支持协同编辑

**锁定相关字段**
- `is_locked`: 是否锁定（0-否 1-是）
- `locked_by`: 锁定人ID
- `locked_by_name`: 锁定人名称
- `locked_time`: 锁定时间
- `lock_reason`: 锁定原因

**权限相关字段**
- `is_private`: 是否私有（0-否 1-是）
- `owner_id`: 所有者ID
- `owner_name`: 所有者名称

**统计字段**
- `download_count`: 下载次数
- `view_count`: 查看次数
- `edit_count`: 编辑次数
- `share_count`: 分享次数

**编辑信息字段**
- `last_edit_time`: 最后编辑时间
- `last_edit_by`: 最后编辑人ID
- `last_edit_by_name`: 最后编辑人名称

**状态和排序字段**
- `status`: 状态（0-已删除 1-正常 2-归档）
- `sort_order`: 排序

**审计字段**
- `create_by`: 创建者
- `create_time`: 创建时间
- `update_by`: 更新者
- `update_time`: 更新时间
- `remark`: 备注

#### 索引设计
- PRIMARY KEY: `id`
- INDEX: `idx_workspace_id` - 查询工作空间下的文件
- INDEX: `idx_folder_id` - 查询文件夹下的文件
- INDEX: `idx_document_key` - OnlyOffice文档查询
- INDEX: `idx_owner_id` - 查询用户的文件
- INDEX: `idx_create_by` - 查询创建者的文件
- INDEX: `idx_status` - 按状态查询
- INDEX: `idx_create_time` - 按时间排序

#### 业务规则
1. 文件名在同一文件夹下不能重复
2. 删除文件时status设为0，同时在回收站创建记录
3. 编辑文件时更新edit_status和current_editor_id
4. 保存文件时创建新版本记录
5. 锁定文件后其他用户不能编辑
6. 私有文件只有所有者可以访问

---

### 2. co_file_version（文件版本表）

#### 表说明
存储文件的版本历史，支持版本回滚和对比功能。

#### 字段说明

**基本信息字段**
- `id`: 版本ID，主键，自增
- `file_id`: 文件ID，必填
- `version_number`: 版本号，必填
- `file_name`: 文件名
- `file_size`: 文件大小（字节）
- `file_path`: 存储路径
- `document_key`: OnlyOffice文档Key

**变更信息字段**
- `change_type`: 变更类型（1-创建 2-编辑 3-上传 4-替换）
- `change_description`: 变更描述
- `change_summary`: 变更摘要（详细内容）

**版本状态字段**
- `is_current`: 是否当前版本（0-否 1-是）
- `is_major`: 是否主要版本（0-否 1-是）

**编辑信息字段**
- `editor_id`: 编辑者ID
- `editor_name`: 编辑者名称
- `edit_duration`: 编辑时长（秒）

**审计字段**
- `create_by`: 创建者
- `create_time`: 创建时间
- `remark`: 备注

#### 索引设计
- PRIMARY KEY: `id`
- UNIQUE KEY: `uk_file_version` (file_id, version_number) - 确保版本号唯一
- INDEX: `idx_file_id` - 查询文件的所有版本
- INDEX: `idx_is_current` - 查询当前版本
- INDEX: `idx_create_time` - 按时间排序

#### 业务规则
1. 每次保存文件时创建新版本
2. 版本号自动递增
3. 只有一个版本的is_current为1
4. 版本文件不能删除，只能标记为非当前版本
5. 支持回滚到历史版本

---

### 3. co_recycle_bin（回收站表）

#### 表说明
存储已删除的文件信息，支持文件恢复和自动清理功能。

#### 字段说明

**基本信息字段**
- `id`: 回收站ID，主键，自增
- `file_id`: 原文件ID，必填
- `workspace_id`: 工作空间ID，必填
- `folder_id`: 原文件夹ID
- `folder_path`: 原文件夹路径

**文件信息字段**
- `file_name`: 文件名，必填
- `file_type`: 文件类型
- `file_size`: 文件大小（字节）
- `file_extension`: 文件扩展名

**存储路径字段**
- `original_path`: 原存储路径
- `backup_path`: 备份路径

**删除信息字段**
- `deleted_by`: 删除人ID
- `deleted_by_name`: 删除人名称
- `deleted_time`: 删除时间
- `delete_reason`: 删除原因

**过期和恢复字段**
- `expire_time`: 过期时间（30天后自动清理）
- `can_restore`: 是否可恢复（0-否 1-是）
- `restore_time`: 恢复时间
- `restore_by`: 恢复人ID
- `restore_by_name`: 恢复人名称

**原文件信息字段**
- `original_owner_id`: 原所有者ID
- `original_owner_name`: 原所有者名称
- `original_create_time`: 原创建时间
- `original_update_time`: 原更新时间

**状态字段**
- `status`: 状态（0-已清理 1-待清理 2-已恢复）

**审计字段**
- `create_by`: 创建者
- `create_time`: 创建时间
- `update_by`: 更新者
- `update_time`: 更新时间
- `remark`: 备注

#### 索引设计
- PRIMARY KEY: `id`
- INDEX: `idx_file_id` - 查询原文件
- INDEX: `idx_workspace_id` - 查询工作空间的回收站
- INDEX: `idx_deleted_by` - 查询删除人的操作
- INDEX: `idx_deleted_time` - 按删除时间排序
- INDEX: `idx_expire_time` - 清理过期文件
- INDEX: `idx_status` - 按状态查询

#### 业务规则
1. 删除文件时自动创建回收站记录
2. 过期时间默认为删除时间+30天
3. 定时任务自动清理过期文件
4. 恢复文件时更新status为2
5. 清理文件时更新status为0并删除物理文件
6. 不可恢复的文件can_restore为0

---

## 表关系图

```
co_workspace (1) ----< (N) co_file
co_folder (1) ----< (N) co_file
co_file (1) ----< (N) co_file_version
co_file (1) ---- (1) co_recycle_bin
```

## 数据量估算

### 预期数据量（按1000个活跃用户计算）

**co_file表**
- 每用户平均文件数：100个
- 总文件数：100,000条
- 年增长率：50%
- 3年后预计：337,500条

**co_file_version表**
- 每文件平均版本数：10个
- 总版本数：1,000,000条
- 年增长率：100%
- 3年后预计：8,000,000条

**co_recycle_bin表**
- 回收站保留30天
- 每天删除文件数：100个
- 回收站文件数：3,000条
- 相对稳定

## 性能优化建议

### 1. 索引优化
- 为高频查询字段建立索引
- 定期分析索引使用情况
- 删除未使用的索引

### 2. 分区策略
- co_file_version表按create_time分区（按月）
- co_recycle_bin表按deleted_time分区（按月）

### 3. 归档策略
- 超过1年的历史版本归档到历史表
- 已清理的回收站记录定期归档

### 4. 缓存策略
- 文件基本信息使用Redis缓存
- 当前版本信息缓存
- 热门文件缓存

### 5. 查询优化
- 避免SELECT *，只查询需要的字段
- 使用覆盖索引
- 合理使用分页查询

## 安全考虑

1. **数据加密**：敏感字段加密存储
2. **访问控制**：基于工作空间和文件夹权限
3. **审计日志**：记录所有文件操作
4. **备份策略**：定期备份，支持恢复
5. **物理删除**：回收站过期后物理删除文件

## 变更日志

### v1.0.0 (2025-11-04)
- 初始版本
- 创建co_file、co_file_version、co_recycle_bin表
- 定义完整的字段和索引
- 编写设计文档

