# 最近访问记录重复问题修复文档

## 📋 问题描述

### 现象
- 同一用户访问同一文件/目标时，数据库中产生多条访问记录
- "最近访问"列表中出现重复项
- 并发访问时可能产生竞态条件，导致数据不一致

### 根本原因
1. **缺少唯一约束**：表中没有唯一索引防止重复插入
2. **非原子操作**：使用"先查询后插入"的逻辑，在并发场景下不安全
3. **缺少字段**：缺少 `last_accessed_at` 和 `view_count` 字段来记录最后访问时间和访问次数

## 🎯 解决方案

### 核心改进
1. **添加新字段**
   - `last_accessed_at`: 最后访问时间（用于排序）
   - `view_count`: 访问次数（累计统计）

2. **添加唯一索引**
   - `co_file_access_record`: `(file_id, user_id)` 唯一
   - `co_access_record`: `(target_type, target_id, user_id)` 唯一

3. **使用 Upsert 操作**
   - 利用 MySQL 的 `INSERT ... ON DUPLICATE KEY UPDATE` 语法
   - 实现原子性的插入或更新操作
   - 保证幂等性和并发安全

## 📦 涉及的表

### 1. co_file_access_record（文件访问记录表）

**唯一性约束**: `(file_id, user_id)`

**新增字段**:
```sql
last_accessed_at DATETIME      -- 最后访问时间
view_count       INT(11)        -- 访问次数
```

**Upsert 行为**:
- **首次访问**: 插入新记录，`last_accessed_at=NOW()`, `view_count=1`
- **重复访问**: 更新 `last_accessed_at=NOW()`, `view_count=view_count+1`

### 2. co_access_record（访问记录表）

**唯一性约束**: `(target_type, target_id, user_id)`

**新增字段**:
```sql
last_accessed_at DATETIME      -- 最后访问时间
view_count       INT(11)        -- 访问次数
```

**Upsert 行为**:
- **首次访问**: 插入新记录，`last_accessed_at=NOW()`, `view_count=1`
- **重复访问**: 更新 `last_accessed_at=NOW()`, `view_count=view_count+1`

## 🔧 实施步骤

### 步骤 1: 数据库迁移

**执行脚本**: `fix_recent_access_duplicate.sql`

**包含内容**:
1. 为两张表添加 `last_accessed_at` 和 `view_count` 字段
2. 清理现有重复数据（保留最新记录，汇总访问次数）
3. 添加唯一索引防止未来重复

**执行方式**:
```bash
# 方式1: MySQL 命令行
mysql -u username -p database_name < fix_recent_access_duplicate.sql

# 方式2: 使用数据库管理工具（推荐）
# 打开 Navicat/MySQL Workbench，执行脚本
```

**注意事项**:
- ⚠️ **执行前务必备份数据库**
- 建议在低峰期执行
- 对于大表（>100万行），建议分批执行
- 执行时间取决于数据量，可能需要几分钟到几十分钟

### 步骤 2: 代码部署

**涉及文件**:
1. **实体类**:
   - `CoFileAccessRecord.java` - 添加 `lastAccessedAt`, `viewCount` 字段
   - `CoAccessRecord.java` - 添加 `lastAccessedAt`, `viewCount` 字段
   - `CoFileAccessRecordVo.java` - 添加对应字段
   - `CoAccessRecordVo.java` - 添加对应字段

2. **Mapper 层**:
   - `CoFileAccessRecordMapper.java` - 添加 `upsertFileAccess` 方法
   - `CoFileAccessRecordMapper.xml` - 实现 Upsert SQL
   - `CoAccessRecordMapper.java` - 添加 `upsertAccess` 方法
   - `CoAccessRecordMapper.xml` - 实现 Upsert SQL

3. **Service 层**:
   - `CoFileAccessRecordServiceImpl.java` - 使用 Upsert 替代 insert
   - `CoAccessRecordServiceImpl.java` - 使用 Upsert 替代 insert

**部署步骤**:
1. 停止应用服务
2. 部署新代码
3. 重启应用服务
4. 验证功能正常

### 步骤 3: 验证

#### 3.1 数据验证

**检查重复数据**:
```sql
-- 检查 co_file_access_record 是否还有重复
SELECT file_id, user_id, COUNT(*) AS cnt
FROM co_file_access_record
GROUP BY file_id, user_id
HAVING COUNT(*) > 1;
-- 预期: 空结果集

-- 检查 co_access_record 是否还有重复
SELECT target_type, target_id, user_id, COUNT(*) AS cnt
FROM co_access_record
GROUP BY target_type, target_id, user_id
HAVING COUNT(*) > 1;
-- 预期: 空结果集
```

**检查新字段**:
```sql
-- 查看表结构
SHOW CREATE TABLE co_file_access_record;
SHOW CREATE TABLE co_access_record;

-- 检查字段值
SELECT id, file_id, user_id, last_accessed_at, view_count
FROM co_file_access_record
ORDER BY last_accessed_at DESC
LIMIT 10;
```

#### 3.2 功能测试

**测试用例 1: 首次访问**
```
操作: 用户A首次访问文件1
预期: 
  - 数据库插入1条记录
  - last_accessed_at = 当前时间
  - view_count = 1
```

**测试用例 2: 重复访问**
```
操作: 用户A再次访问文件1
预期:
  - 数据库仍只有1条记录
  - last_accessed_at 更新为最新时间
  - view_count = 2
```

**测试用例 3: 并发访问**
```
操作: 用户A同时发起10次访问文件1的请求
预期:
  - 数据库仍只有1条记录
  - view_count = 原值 + 10
  - 无报错，无死锁
```

**测试用例 4: 最近访问列表**
```
操作: 获取用户A的最近访问列表
预期:
  - 每个文件只出现一次
  - 按 last_accessed_at 降序排列
  - 显示正确的 view_count
```

#### 3.3 性能测试

**Upsert 性能**:
```
测试: 连续执行1000次 Upsert 操作
指标: P95 响应时间 < 50ms
工具: JMeter 或 Postman
```

**查询性能**:
```
测试: 查询最近访问列表（50条）
指标: 响应时间 < 100ms
验证: 索引是否生效（EXPLAIN 分析）
```

##[object Object]t 实现细节

### MySQL Upsert 语法

**co_file_access_record 示例**:
```sql
INSERT INTO co_file_access_record (
    file_id, user_id, workspace_id,
    access_time, last_accessed_at,
    access_type, view_count, create_time
) VALUES (
    ?, ?, ?,
    NOW(), NOW(),
    ?, 1, NOW()
)
ON DUPLICATE KEY UPDATE
    access_time = NOW(),
    last_accessed_at = NOW(),
    access_type = VALUES(access_type),
    view_count = view_count + 1,
    update_time = NOW();
```

**co_access_record 示例**:
```sql
INSERT INTO co_access_record (
    target_type, target_id, user_id, workspace_id,
    access_time, last_accessed_at,
    access_type, view_count, create_time
) VALUES (
    ?, ?, ?, ?,
    NOW(), NOW(),
    ?, 1, NOW()
)
ON DUPLICATE KEY UPDATE
    access_time = NOW(),
    last_accessed_at = NOW(),
    workspace_id = VALUES(workspace_id),
    access_type = VALUES(access_type),
    view_count = view_count + 1,
    update_time = NOW();
```

### 关键点说明

1. **唯一索引触发**: 当插入的记录违反唯一索引时，执行 UPDATE 部分
2. **view_count 递增**: `view_count = view_count + 1` 实现累加
3. **时间更新**: `last_accessed_at = NOW()` 记录最新访问时间
4. **原子性**: 整个操作在一个 SQL 语句中完成，保证原子性

## 🔄 回滚方案

### 回滚数据库

**注意**: 回滚会丢失聚合的 `view_count` 数据和 `last_accessed_at` 信息

```sql
-- 回滚 co_file_access_record
ALTER TABLE co_file_access_record DROP INDEX ux_file_user;
ALTER TABLE co_file_access_record DROP INDEX idx_last_accessed_at;
ALTER TABLE co_file_access_record DROP COLUMN last_accessed_at;
ALTER TABLE co_file_access_record DROP COLUMN view_count;

-- 回滚 co_access_record
ALTER TABLE co_access_record DROP INDEX ux_target_user;
ALTER TABLE co_access_record DROP INDEX idx_last_accessed_at;
ALTER TABLE co_access_record DROP COLUMN last_accessed_at;
ALTER TABLE co_access_record DROP COLUMN view_count;
```

### 回滚代码

1. 恢复到之前的代码版本
2. 重启应用服务

## 📈 性能优化建议

### 索引优化
- ✅ 已添加 `(file_id, user_id)` 唯一索引
- ✅ 已添加 `last_accessed_at` 索引用于排序
- 建议定期分析索引使用情况: `SHOW INDEX FROM co_file_access_record;`

### 查询优化
- 使用 `last_accessed_at` 而非 `access_time` 排序
- 限制查询结果数量（如最多50条）
- 避免全表扫描

### 并发优化
- Upsert 操作已实现行级锁，避免表锁
- 高并发场景下，可考虑将 `last_accessed_at` 精度降低到秒级

## ⚠️ 注意事项

1. **数据备份**: 执行迁移前务必备份数据库
2. **执行时机**: 建议在低峰期执行，避免影响业务
3. **大表处理**: 数据量超过100万行时，建议分批处理
4. **监控告警**: 部署后密切监控错误日志和性能指标
5. **兼容性**: 确认 MySQL 版本 >= 5.7

## 📞 支持

如遇到问题，请联系：
- 技术支持: [技术团队邮箱]
- 文档维护: AI Assistant
- 更新日期: 2025-11-20

---

**版本历史**:
- v1.0 (2025-11-20): 初始版本，实现 Upsert 逻辑和重复数据清理

