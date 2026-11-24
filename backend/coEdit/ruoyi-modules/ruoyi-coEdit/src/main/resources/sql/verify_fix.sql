-- ========================================
-- 验证"最近访问"修复是否成功
-- 作者: AI Assistant
-- 日期: 2025-11-20
-- 说明: 执行此脚本验证数据库迁移和修复是否成功
-- ========================================

-- ========================================
-- 1. 验证表结构
-- ========================================

-- 1.1 检查 co_file_access_record 表结构
SELECT 
    'co_file_access_record' AS table_name,
    COLUMN_NAME,
    DATA_TYPE,
    COLUMN_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'co_file_access_record'
  AND COLUMN_NAME IN ('last_accessed_at', 'view_count')
ORDER BY ORDINAL_POSITION;

-- 预期结果: 应返回2行，包含 last_accessed_at 和 view_count 字段

-- 1.2 检查 co_access_record 表结构
SELECT 
    'co_access_record' AS table_name,
    COLUMN_NAME,
    DATA_TYPE,
    COLUMN_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'co_access_record'
  AND COLUMN_NAME IN ('last_accessed_at', 'view_count')
ORDER BY ORDINAL_POSITION;

-- 预期结果: 应返回2行，包含 last_accessed_at 和 view_count 字段

-- ========================================
-- 2. 验证唯一索引
-- ========================================

-- 2.1 检查 co_file_access_record 唯一索引
SELECT 
    'co_file_access_record' AS table_name,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX,
    NON_UNIQUE
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'co_file_access_record'
  AND INDEX_NAME = 'ux_file_user'
ORDER BY SEQ_IN_INDEX;

-- 预期结果: 应返回2行 (file_id, user_id)，NON_UNIQUE=0

-- 2.2 检查 co_access_record 唯一索引
SELECT 
    'co_access_record' AS table_name,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX,
    NON_UNIQUE
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'co_access_record'
  AND INDEX_NAME = 'ux_target_user'
ORDER BY SEQ_IN_INDEX;

-- 预期结果: 应返回3行 (target_type, target_id, user_id)，NON_UNIQUE=0

-- 2.3 检查 last_accessed_at 索引
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    NON_UNIQUE
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME IN ('co_file_access_record', 'co_access_record')
  AND INDEX_NAME = 'idx_last_accessed_at'
ORDER BY TABLE_NAME;

-- 预期结果: 应返回2行，每个表一个索引

-- ========================================
-- 3. 验证数据完整性
-- ========================================

-- 3.1 检查 co_file_access_record 是否有重复数据
SELECT 
    '检查重复数据' AS check_type,
    'co_file_access_record' AS table_name,
    file_id,
    user_id,
    COUNT(*) AS duplicate_count
FROM co_file_access_record
GROUP BY file_id, user_id
HAVING COUNT(*) > 1;

-- 预期结果: 空结果集（无重复）

-- 3.2 检查 co_access_record 是否有重复数据
SELECT 
    '检查重复数据' AS check_type,
    'co_access_record' AS table_name,
    target_type,
    target_id,
    user_id,
    COUNT(*) AS duplicate_count
FROM co_access_record
GROUP BY target_type, target_id, user_id
HAVING COUNT(*) > 1;

-- 预期结果: 空结果集（无重复）

-- 3.3 检查 last_accessed_at 是否有空值
SELECT 
    '检查空值' AS check_type,
    'co_file_access_record' AS table_name,
    COUNT(*) AS null_count
FROM co_file_access_record
WHERE last_accessed_at IS NULL;

-- 预期结果: null_count = 0

SELECT 
    '检查空值' AS check_type,
    'co_access_record' AS table_name,
    COUNT(*) AS null_count
FROM co_access_record
WHERE last_accessed_at IS NULL;

-- 预期结果: null_count = 0

-- 3.4 检查 view_count 是否合理
SELECT 
    '检查view_count' AS check_type,
    'co_file_access_record' AS table_name,
    MIN(view_count) AS min_count,
    MAX(view_count) AS max_count,
    AVG(view_count) AS avg_count
FROM co_file_access_record;

-- 预期结果: min_count >= 1

SELECT 
    '检查view_count' AS check_type,
    'co_access_record' AS table_name,
    MIN(view_count) AS min_count,
    MAX(view_count) AS max_count,
    AVG(view_count) AS avg_count
FROM co_access_record;

-- 预期结果: min_count >= 1

-- ========================================
-- 4. 验证数据统计
-- ========================================

-- 4.1 统计 co_file_access_record 记录数
SELECT 
    '记录统计' AS stat_type,
    'co_file_access_record' AS table_name,
    COUNT(*) AS total_records,
    COUNT(DISTINCT file_id) AS unique_files,
    COUNT(DISTINCT user_id) AS unique_users,
    COUNT(DISTINCT CONCAT(file_id, '-', user_id)) AS unique_combinations
FROM co_file_access_record;

-- 预期结果: total_records = unique_combinations

-- 4.2 统计 co_access_record 记录数
SELECT 
    '记录统计' AS stat_type,
    'co_access_record' AS table_name,
    COUNT(*) AS total_records,
    COUNT(DISTINCT target_id) AS unique_targets,
    COUNT(DISTINCT user_id) AS unique_users,
    COUNT(DISTINCT CONCAT(target_type, '-', target_id, '-', user_id)) AS unique_combinations
FROM co_access_record;

-- 预期结果: total_records = unique_combinations

-- ========================================
-- 5. 抽样检查数据
-- ========================================

-- 5.1 查看 co_file_access_record 最近10条记录
SELECT 
    id,
    file_id,
    user_id,
    workspace_id,
    access_time,
    last_accessed_at,
    view_count,
    create_time,
    update_time
FROM co_file_access_record
ORDER BY last_accessed_at DESC
LIMIT 10;

-- 5.2 查看 co_access_record 最近10条记录
SELECT 
    id,
    target_type,
    target_id,
    user_id,
    workspace_id,
    access_time,
    last_accessed_at,
    view_count,
    create_time,
    update_time
FROM co_access_record
ORDER BY last_accessed_at DESC
LIMIT 10;

-- ========================================
-- 6. 性能验证（可选）
-- ========================================

-- 6.1 验证索引是否生效 - co_file_access_record
EXPLAIN SELECT *
FROM co_file_access_record
WHERE file_id = 1 AND user_id = 1;

-- 预期结果: key = 'ux_file_user', type = 'const' 或 'ref'

-- 6.2 验证索引是否生效 - co_access_record
EXPLAIN SELECT *
FROM co_access_record
WHERE target_type = 3 AND target_id = 1 AND user_id = 1;

-- 预期结果: key = 'ux_target_user', type = 'const' 或 'ref'

-- 6.3 验证排序索引是否生效
EXPLAIN SELECT *
FROM co_file_access_record
WHERE user_id = 1
ORDER BY last_accessed_at DESC
LIMIT 50;

-- 预期结果: 应使用索引，避免 filesort

-- ========================================
-- 7. 总结报告
-- ========================================

SELECT '========== 验证总结 ==========' AS summary;

SELECT 
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS 
            WHERE TABLE_SCHEMA = DATABASE() 
              AND TABLE_NAME = 'co_file_access_record'
              AND COLUMN_NAME = 'last_accessed_at'
        ) THEN '✓ PASS'
        ELSE '✗ FAIL'
    END AS 'co_file_access_record.last_accessed_at';

SELECT 
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM INFORMATION_SCHEMA.STATISTICS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'co_file_access_record'
              AND INDEX_NAME = 'ux_file_user'
              AND NON_UNIQUE = 0
        ) THEN '✓ PASS'
        ELSE '✗ FAIL'
    END AS 'co_file_access_record.ux_file_user';

SELECT 
    CASE 
        WHEN NOT EXISTS (
            SELECT 1 FROM co_file_access_record
            GROUP BY file_id, user_id
            HAVING COUNT(*) > 1
        ) THEN '✓ PASS'
        ELSE '✗ FAIL'
    END AS 'co_file_access_record.no_duplicates';

SELECT 
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS 
            WHERE TABLE_SCHEMA = DATABASE() 
              AND TABLE_NAME = 'co_access_record'
              AND COLUMN_NAME = 'last_accessed_at'
        ) THEN '✓ PASS'
        ELSE '✗ FAIL'
    END AS 'co_access_record.last_accessed_at';

SELECT 
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM INFORMATION_SCHEMA.STATISTICS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'co_access_record'
              AND INDEX_NAME = 'ux_target_user'
              AND NON_UNIQUE = 0
        ) THEN '✓ PASS'
        ELSE '✗ FAIL'
    END AS 'co_access_record.ux_target_user';

SELECT 
    CASE 
        WHEN NOT EXISTS (
            SELECT 1 FROM co_access_record
            GROUP BY target_type, target_id, user_id
            HAVING COUNT(*) > 1
        ) THEN '✓ PASS'
        ELSE '✗ FAIL'
    END AS 'co_access_record.no_duplicates';

SELECT '========== 验证完成 ==========' AS summary;

-- ========================================
-- 使用说明
-- ========================================
-- 1. 在数据库管理工具中执行此脚本
-- 2. 检查每个查询的结果是否符合预期
-- 3. 如果所有检查都通过，说明修复成功
-- 4. 如果有失败项，请检查迁移脚本是否正确执行

