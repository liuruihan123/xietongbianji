# BaseEntity 字段修复说明

## 问题描述

运行时出现错误:
```
java.sql.SQLSyntaxErrorException: Unknown column 'create_by' in 'field list'
```

**根本原因**: 所有继承 `BaseEntity` 的实体类都包含以下字段:
- `createBy` (BIGINT)
- `createTime` (DATETIME)
- `updateBy` (BIGINT)
- `updateTime` (DATETIME)

但部分数据库表缺少这些字段或字段类型不匹配。

## 受影响的表

### 1. co_edit_session
- **问题**: 缺少 `create_by` 和 `update_by` 字段
- **状态**: ✅ 已修复

### 2. co_edit_lock
- **问题**: `create_by` 和 `update_by` 字段类型为 VARCHAR(64),应为 BIGINT(20)
- **状态**: ✅ 已修复

### 3. co_edit_history
- **问题**: 
  - `create_by` 字段类型为 VARCHAR(64),应为 BIGINT(20)
  - 缺少 `update_by` 和 `update_time` 字段
- **状态**: ✅ 已修复

### 4. co_edit_log
- **问题**: 
  - `create_by` 字段类型为 VARCHAR(64),应为 BIGINT(20)
  - 缺少 `update_by` 和 `update_time` 字段
- **状态**: ✅ 已修复

### 5. co_editor_performance
- **问题**: 缺少 `create_by` 和 `update_by` 字段
- **状态**: ✅ 已修复

### 6. co_share_link
- **问题**: 缺少 `create_by` 和 `update_by` 字段
- **状态**: ✅ 已修复

### 7. co_file_conversion
- **问题**: 缺少 `create_by` 和 `update_by` 字段
- **状态**: ✅ 已修复

### 8. co_search_history
- **问题**: 缺少 `create_by`、`update_by` 和 `update_time` 字段
- **状态**: ✅ 已修复

## 修复步骤

### 方式一: 执行迁移脚本(推荐)

如果数据库表已经创建,执行以下迁移脚本:

```bash
mysql -u your_username -p your_database < fix_base_entity_fields.sql
```

### 方式二: 重新创建表

如果是新环境或可以重建表,直接执行更新后的建表脚本:

1. `co_edit_session.sql`
2. `co_edit_lock.sql`
3. `co_edit_history.sql`
4. `co_edit_log.sql`
5. `co_editor_performance.sql`
6. `co_share_link.sql`
7. `co_file_conversion.sql`
8. `co_search_history.sql`

## BaseEntity 标准字段定义

所有继承 `BaseEntity` 的实体类都应该在数据库表中包含以下字段:

```sql
-- 审计字段
`create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
```

## 验证修复

执行以下 SQL 验证表结构是否正确:

```sql
-- 检查所有表的审计字段
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    COLUMN_TYPE
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME LIKE 'co_%'
  AND COLUMN_NAME IN ('create_by', 'create_time', 'update_by', 'update_time')
ORDER BY TABLE_NAME, ORDINAL_POSITION;
```

预期结果:
- `create_by` 和 `update_by` 字段类型应为 `bigint`
- `create_time` 和 `update_time` 字段类型应为 `datetime`

## 注意事项

1. **数据迁移**: 如果表中已有数据,`create_by` 和 `update_by` 字段会被设置为 NULL
2. **自动填充**: MyBatis-Plus 会通过 `@TableField(fill = FieldFill.INSERT)` 等注解自动填充这些字段
3. **类型一致性**: 确保所有审计字段类型与 `BaseEntity` 定义一致
4. **新表创建**: 创建新表时,请参考已修复的表结构,确保包含完整的审计字段

## 相关文件

- 迁移脚本: `fix_base_entity_fields.sql`
- BaseEntity 定义: `ruoyi-common/ruoyi-common-mybatis/src/main/java/com/xmzs/common/mybatis/core/domain/BaseEntity.java`
- 建表脚本目录: `ruoyi-modules/ruoyi-coEdit/src/main/resources/sql/`

## 修复日期

2025-11-06

