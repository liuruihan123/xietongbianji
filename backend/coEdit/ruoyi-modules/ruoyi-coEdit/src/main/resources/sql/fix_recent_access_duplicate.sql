-- ========================================
-- 修复"最近访问"数据重复问题 - MySQL版本
-- 作者: AI Assistant
-- 日期: 2025-11-20
-- 说明: 
--   1. 添加 last_accessed_at 和 view_count 字段
--   2. 清理重复数据（保留最新访问记录）
--   3. 添加唯一索引防止未来重复
-- 数据库: MySQL 5.7+
-- ========================================

-- ========================================
-- 第一部分: 为 co_file_access_record 表添加新字段
-- ========================================

-- 1.1 添加 last_accessed_at 字段（最后访问时间）
ALTER TABLE `co_file_access_record`
    ADD COLUMN `last_accessed_at` DATETIME DEFAULT NULL COMMENT '最后访问时间' AFTER `access_time`;

-- 1.2 添加 view_count 字段（访问次数）
ALTER TABLE `co_file_access_record`
    ADD COLUMN `view_count` INT(11) DEFAULT 1 COMMENT '访问次数' AFTER `last_accessed_at`;

-- 1.3 初始化 last_accessed_at 字段（使用 access_time 的值）
UPDATE `co_file_access_record`
SET `last_accessed_at` = `access_time`
WHERE `last_accessed_at` IS NULL;

-- ========================================
-- 第二部分: 清理 co_file_access_record 表的重复数据
-- ========================================

-- 2.1 创建临时表存储聚合结果
CREATE TEMPORARY TABLE IF NOT EXISTS `temp_file_access_agg` (
    `file_id` BIGINT(20) NOT NULL,
    `user_id` BIGINT(20) NOT NULL,
    `max_access_time` DATETIME NOT NULL,
    `total_view_count` INT(11) NOT NULL,
    `keep_id` BIGINT(20) NOT NULL,
    PRIMARY KEY (`file_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2.2 计算每组的聚合数据（最大访问时间、总访问次数、要保留的记录ID）
INSERT INTO `temp_file_access_agg` (`file_id`, `user_id`, `max_access_time`, `total_view_count`, `keep_id`)
SELECT 
    `file_id`,
    `user_id`,
    MAX(`access_time`) AS `max_access_time`,
    SUM(COALESCE(`view_count`, 1)) AS `total_view_count`,
    MAX(`id`) AS `keep_id`  -- 保留ID最大的记录（通常是最新的）
FROM `co_file_access_record`
GROUP BY `file_id`, `user_id`
HAVING COUNT(*) >= 1;  -- 包括所有记录，即使没有重复

-- 2.3 更新要保留的记录，设置聚合后的值
UPDATE `co_file_access_record` t
INNER JOIN `temp_file_access_agg` a 
    ON t.`id` = a.`keep_id`
SET 
    t.`last_accessed_at` = a.`max_access_time`,
    t.`view_count` = a.`total_view_count`;

-- 2.4 删除重复记录（保留 keep_id 指定的记录）
DELETE t FROM `co_file_access_record` t
LEFT JOIN `temp_file_access_agg` a 
    ON t.`id` = a.`keep_id`
WHERE a.`keep_id` IS NULL
  AND EXISTS (
      SELECT 1 FROM `temp_file_access_agg` a2
      WHERE a2.`file_id` = t.`file_id` 
        AND a2.`user_id` = t.`user_id`
  );

-- 2.5 清理临时表
DROP TEMPORARY TABLE IF EXISTS `temp_file_access_agg`;

-- ========================================
-- 第三部分: 为 co_file_access_record 添加唯一索引
-- ========================================

-- 3.1 添加唯一索引（file_id, user_id）
-- 注意：如果索引已存在，MySQL会报错，可以先检查或使用 IF NOT EXISTS（MySQL 8.0+）
ALTER TABLE `co_file_access_record`
    ADD UNIQUE KEY `ux_file_user` (`file_id`, `user_id`);

-- 3.2 添加 last_accessed_at 索引以优化查询
ALTER TABLE `co_file_access_record`
    ADD KEY `idx_last_accessed_at` (`last_accessed_at`);

-- ========================================
-- 第四部分: 为 co_access_record 表添加新字段
-- ========================================

-- 4.1 添加 last_accessed_at 字段
ALTER TABLE `co_access_record`
    ADD COLUMN `last_accessed_at` DATETIME DEFAULT NULL COMMENT '最后访问时间' AFTER `access_time`;

-- 4.2 添加 view_count 字段
ALTER TABLE `co_access_record`
    ADD COLUMN `view_count` INT(11) DEFAULT 1 COMMENT '访问次数' AFTER `last_accessed_at`;

-- 4.3 初始化 last_accessed_at 字段
UPDATE `co_access_record`
SET `last_accessed_at` = `access_time`
WHERE `last_accessed_at` IS NULL;

-- ========================================
-- 第五部分: 清理 co_access_record 表的重复数据
-- ========================================

-- 5.1 创建临时表存储聚合结果
CREATE TEMPORARY TABLE IF NOT EXISTS `temp_access_agg` (
    `target_type` TINYINT(1) NOT NULL,
    `target_id` BIGINT(20) NOT NULL,
    `user_id` BIGINT(20) NOT NULL,
    `max_access_time` DATETIME NOT NULL,
    `total_view_count` INT(11) NOT NULL,
    `keep_id` BIGINT(20) NOT NULL,
    PRIMARY KEY (`target_type`, `target_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5.2 计算每组的聚合数据
INSERT INTO `temp_access_agg` (`target_type`, `target_id`, `user_id`, `max_access_time`, `total_view_count`, `keep_id`)
SELECT 
    `target_type`,
    `target_id`,
    `user_id`,
    MAX(`access_time`) AS `max_access_time`,
    SUM(COALESCE(`view_count`, 1)) AS `total_view_count`,
    MAX(`id`) AS `keep_id`
FROM `co_access_record`
GROUP BY `target_type`, `target_id`, `user_id`
HAVING COUNT(*) >= 1;

-- 5.3 更新要保留的记录
UPDATE `co_access_record` t
INNER JOIN `temp_access_agg` a 
    ON t.`id` = a.`keep_id`
SET 
    t.`last_accessed_at` = a.`max_access_time`,
    t.`view_count` = a.`total_view_count`;

-- 5.4 删除重复记录
DELETE t FROM `co_access_record` t
LEFT JOIN `temp_access_agg` a 
    ON t.`id` = a.`keep_id`
WHERE a.`keep_id` IS NULL
  AND EXISTS (
      SELECT 1 FROM `temp_access_agg` a2
      WHERE a2.`target_type` = t.`target_type`
        AND a2.`target_id` = t.`target_id`
        AND a2.`user_id` = t.`user_id`
  );

-- 5.5 清理临时表
DROP TEMPORARY TABLE IF EXISTS `temp_access_agg`;

-- ========================================
-- 第六部分: 为 co_access_record 添加唯一索引
-- ========================================

-- 6.1 添加唯一索引（target_type, target_id, user_id）
ALTER TABLE `co_access_record`
    ADD UNIQUE KEY `ux_target_user` (`target_type`, `target_id`, `user_id`);

-- 6.2 添加 last_accessed_at 索引
ALTER TABLE `co_access_record`
    ADD KEY `idx_last_accessed_at` (`last_accessed_at`);

-- ========================================
-- 验证修复结果
-- ========================================

-- 验证 co_file_access_record 表
-- 检查是否还有重复数据
SELECT 
    `file_id`, 
    `user_id`, 
    COUNT(*) AS `duplicate_count`
FROM `co_file_access_record`
GROUP BY `file_id`, `user_id`
HAVING COUNT(*) > 1;
-- 预期结果：空结果集（无重复）

-- 验证 co_access_record 表
SELECT 
    `target_type`,
    `target_id`, 
    `user_id`, 
    COUNT(*) AS `duplicate_count`
FROM `co_access_record`
GROUP BY `target_type`, `target_id`, `user_id`
HAVING COUNT(*) > 1;
-- 预期结果：空结果集（无重复）

-- 查看表结构
SHOW CREATE TABLE `co_file_access_record`;
SHOW CREATE TABLE `co_access_record`;

-- ========================================
-- 注意事项
-- ========================================
-- 1. 执行前请备份数据库
-- 2. 建议在低峰期执行，避免影响业务
-- 3. 对于大表（>100万行），建议分批执行删除操作
-- 4. 如果表数据量很大，创建唯一索引可能需要较长时间
-- 5. MySQL 5.7 不支持 CREATE INDEX IF NOT EXISTS，如果索引已存在会报错
-- 6. 执行完成后，应用代码需要同步更新以使用新的 Upsert 逻辑

-- ========================================
-- 回滚方案（如需回滚）
-- ========================================
-- 注意：回滚会丢失聚合的 view_count 数据

-- 回滚 co_file_access_record
-- ALTER TABLE `co_file_access_record` DROP INDEX `ux_file_user`;
-- ALTER TABLE `co_file_access_record` DROP INDEX `idx_last_accessed_at`;
-- ALTER TABLE `co_file_access_record` DROP COLUMN `last_accessed_at`;
-- ALTER TABLE `co_file_access_record` DROP COLUMN `view_count`;

-- 回滚 co_access_record
-- ALTER TABLE `co_access_record` DROP INDEX `ux_target_user`;
-- ALTER TABLE `co_access_record` DROP INDEX `idx_last_accessed_at`;
-- ALTER TABLE `co_access_record` DROP COLUMN `last_accessed_at`;
-- ALTER TABLE `co_access_record` DROP COLUMN `view_count`;

