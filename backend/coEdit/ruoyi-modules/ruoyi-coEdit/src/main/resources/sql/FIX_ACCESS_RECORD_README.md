# 访问记录表字段修复说明

## 问题描述

运行时出现错误:
```
java.sql.SQLSyntaxErrorException: Unknown column 'create_by' in 'field list'
```

**错误位置**: `/coedit/access-record/recent/list` 接口

**根本原因**: 
- `CoAccessRecord` 和 `CoFileAccessRecord` 实体类继承了 `BaseEntity`
- `BaseEntity` 包含审计字段: `create_by`, `create_time`, `update_by`, `update_time`
- 但数据库表 `co_access_record` 和 `co_file_access_record` 缺少这些字段

## 受影响的表

### 1. co_access_record
- **问题**: 缺少 `create_by`, `create_time`, `update_by`, `update_time` 字段
- **状态**: ⚠️ 待修复

### 2. co_file_access_record
- **问题**: 缺少 `create_by`, `create_time`, `update_by`, `update_time` 字段
- **状态**: ⚠️ 待修复

## 修复步骤

### 方式一: 执行迁移脚本(推荐)

在数据库中执行以下命令:

```bash
# 方法1: 使用 MySQL 命令行
mysql -u your_username -p your_database < fix_access_record_tables.sql

# 方法2: 使用 MySQL Workbench 或其他数据库工具
# 打开 fix_access_record_tables.sql 文件并执行
```

### 方式二: 手动执行 SQL

如果无法执行脚本文件,可以手动执行以下 SQL:

```sql
-- 修复 co_access_record 表
ALTER TABLE `co_access_record`
    ADD COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者' AFTER `user_agent`,
    ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `create_by`,
    ADD COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`,
    ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `update_by`;

-- 修复 co_file_access_record 表
ALTER TABLE `co_file_access_record`
    ADD COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者' AFTER `user_agent`,
    ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `create_by`,
    ADD COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`,
    ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `update_by`;
```

## 验证修复

执行以下 SQL 验证表结构是否正确:

```sql
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    COLUMN_TYPE,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME IN ('co_access_record', 'co_file_access_record')
  AND COLUMN_NAME IN ('create_by', 'create_time', 'update_by', 'update_time')
ORDER BY TABLE_NAME, ORDINAL_POSITION;
```

**预期结果**:
- 每个表应该有 4 个字段: `create_by`, `create_time`, `update_by`, `update_time`
- `create_by` 和 `update_by` 字段类型应为 `bigint(20)`
- `create_time` 和 `update_time` 字段类型应为 `datetime`

## 修复后测试

修复完成后,重启应用并测试以下接口:

1. **获取最近访问记录**
   ```
   GET /coedit/access-record/recent/list?limit=50
   ```

2. **获取工作空间文件树**
   ```
   GET /coedit/file-tree/workspace?workspaceId=1
   ```

## BaseEntity 标准字段定义

所有继承 `BaseEntity` 的实体类都应该在数据库表中包含以下字段:

```sql
-- 审计字段
`create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
```

## 注意事项

1. **数据迁移**: 如果表中已有数据,`create_by` 和 `update_by` 字段会被设置为 NULL
2. **自动填充**: MyBatis-Plus 会通过 `@TableField(fill = FieldFill.INSERT)` 等注解自动填充这些字段
3. **类型一致性**: 确保所有审计字段类型与 `BaseEntity` 定义一致
4. **新表创建**: 创建新表时,请确保包含完整的审计字段

## 相关文件

- 迁移脚本: `fix_access_record_tables.sql`
- BaseEntity 定义: `ruoyi-common/ruoyi-common-mybatis/src/main/java/com/xmzs/common/mybatis/core/domain/BaseEntity.java`
- 实体类: 
  - `CoAccessRecord.java`
  - `CoFileAccessRecord.java`

## 修复日期

2025-11-13

## 参考

- 类似问题的修复: `fix_base_entity_fields.sql`
- 修复说明文档: `README_FIX.md`

