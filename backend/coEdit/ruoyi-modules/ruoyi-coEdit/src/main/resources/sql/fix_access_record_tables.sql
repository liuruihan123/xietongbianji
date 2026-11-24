-- ========================================
-- 修复访问记录表的 BaseEntity 字段问题
-- 作者: AI Assistant
-- 日期: 2025-11-13
-- 说明: 为 co_access_record 和 co_file_access_record 表添加缺失的审计字段
-- ========================================

-- 1. 修复 co_access_record 表
-- 添加缺失的 create_by, create_time, update_by, update_time 字段
ALTER TABLE `co_access_record`
    ADD COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者' AFTER `user_agent`,
    ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `create_by`,
    ADD COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`,
    ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `update_by`;

-- 2. 修复 co_file_access_record 表
-- 添加缺失的 create_by, create_time, update_by, update_time 字段
ALTER TABLE `co_file_access_record`
    ADD COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者' AFTER `user_agent`,
    ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `create_by`,
    ADD COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`,
    ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `update_by`;

-- ========================================
-- 验证修复结果
-- ========================================
-- 执行以下 SQL 验证表结构是否正确:
-- 
-- SELECT 
--     TABLE_NAME,
--     COLUMN_NAME,
--     DATA_TYPE,
--     COLUMN_TYPE,
--     COLUMN_COMMENT
-- FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_SCHEMA = DATABASE() 
--   AND TABLE_NAME IN ('co_access_record', 'co_file_access_record')
--   AND COLUMN_NAME IN ('create_by', 'create_time', 'update_by', 'update_time')
-- ORDER BY TABLE_NAME, ORDINAL_POSITION;
-- 
-- 预期结果:
-- - create_by 和 update_by 字段类型应为 bigint
-- - create_time 和 update_time 字段类型应为 datetime
-- ========================================

