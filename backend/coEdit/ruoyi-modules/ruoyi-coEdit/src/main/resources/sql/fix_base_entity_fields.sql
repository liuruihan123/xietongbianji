-- ========================================
-- 修复 BaseEntity 字段问题
-- 作者: AI Assistant
-- 日期: 2025-11-06
-- 说明: 修复所有继承 BaseEntity 的表,确保包含正确的审计字段
-- ========================================

-- 1. 修复 co_edit_session 表
-- 添加缺失的 create_by 和 update_by 字段
ALTER TABLE `co_edit_session`
    ADD COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者' AFTER `user_agent`,
    ADD COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`;

-- 调整字段顺序,将审计字段移到最后
ALTER TABLE `co_edit_session`
    MODIFY COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者' AFTER `user_agent`,
    MODIFY COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `create_by`,
    MODIFY COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`,
    MODIFY COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `update_by`;

-- 2. 修复 co_edit_lock 表
-- 修改 create_by 和 update_by 字段类型从 VARCHAR 改为 BIGINT
ALTER TABLE `co_edit_lock`
    MODIFY COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    MODIFY COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者';

-- 3. 修复 co_edit_history 表
-- 修改 create_by 类型并添加 update_by 和 update_time
ALTER TABLE `co_edit_history`
    MODIFY COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    ADD COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`,
    ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `update_by`;

-- 4. 修复 co_edit_log 表
-- 修改 create_by 类型并添加 update_by 和 update_time
ALTER TABLE `co_edit_log`
    MODIFY COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    ADD COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`,
    ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `update_by`;

-- 5. 修复 co_editor_performance 表
-- 添加缺失的 create_by 和 update_by 字段
ALTER TABLE `co_editor_performance`
    ADD COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者' AFTER `normal_disconnect`,
    ADD COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`;

-- 调整字段顺序
ALTER TABLE `co_editor_performance`
    MODIFY COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者' AFTER `normal_disconnect`,
    MODIFY COLUMN `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `create_by`,
    MODIFY COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`,
    MODIFY COLUMN `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `update_by`;

-- 6. 修复 co_share_link 表
-- 添加缺失的 create_by 和 update_by 字段
ALTER TABLE `co_share_link`
    ADD COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者' AFTER `status`,
    ADD COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`;

-- 调整字段顺序
ALTER TABLE `co_share_link`
    MODIFY COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者' AFTER `status`,
    MODIFY COLUMN `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `create_by`,
    MODIFY COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`,
    MODIFY COLUMN `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `update_by`;

-- 7. 修复 co_file_conversion 表
-- 添加缺失的 create_by 和 update_by 字段
ALTER TABLE `co_file_conversion`
    ADD COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者' AFTER `user_name`,
    ADD COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`;

-- 调整字段顺序
ALTER TABLE `co_file_conversion`
    MODIFY COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者' AFTER `user_name`,
    MODIFY COLUMN `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `create_by`,
    MODIFY COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`,
    MODIFY COLUMN `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `update_by`;

-- 8. 修复 co_search_history 表
-- 添加缺失的 create_by 和 update_by 字段
ALTER TABLE `co_search_history`
    ADD COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者' AFTER `search_time`,
    ADD COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`,
    ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `update_by`;

-- 调整字段顺序
ALTER TABLE `co_search_history`
    MODIFY COLUMN `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者' AFTER `search_time`,
    MODIFY COLUMN `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `create_by`,
    MODIFY COLUMN `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者' AFTER `create_time`,
    MODIFY COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `update_by`,
    MODIFY COLUMN `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址' AFTER `update_time`,
    MODIFY COLUMN `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理' AFTER `ip_address`;

-- ========================================
-- 验证修复结果
-- ========================================
-- 执行以下查询验证表结构是否正确

-- SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT
-- FROM INFORMATION_SCHEMA.COLUMNS
-- WHERE TABLE_SCHEMA = DATABASE()
--   AND TABLE_NAME = 'co_edit_session'
--   AND COLUMN_NAME IN ('create_by', 'create_time', 'update_by', 'update_time')
-- ORDER BY ORDINAL_POSITION;

-- SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT
-- FROM INFORMATION_SCHEMA.COLUMNS
-- WHERE TABLE_SCHEMA = DATABASE()
--   AND TABLE_NAME = 'co_edit_lock'
--   AND COLUMN_NAME IN ('create_by', 'create_time', 'update_by', 'update_time')
-- ORDER BY ORDINAL_POSITION;

-- SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT
-- FROM INFORMATION_SCHEMA.COLUMNS
-- WHERE TABLE_SCHEMA = DATABASE()
--   AND TABLE_NAME = 'co_edit_history'
--   AND COLUMN_NAME IN ('create_by', 'create_time', 'update_by', 'update_time')
-- ORDER BY ORDINAL_POSITION;

-- SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT
-- FROM INFORMATION_SCHEMA.COLUMNS
-- WHERE TABLE_SCHEMA = DATABASE()
--   AND TABLE_NAME = 'co_edit_log'
--   AND COLUMN_NAME IN ('create_by', 'create_time', 'update_by', 'update_time')
-- ORDER BY ORDINAL_POSITION;

-- SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT
-- FROM INFORMATION_SCHEMA.COLUMNS
-- WHERE TABLE_SCHEMA = DATABASE()
--   AND TABLE_NAME = 'co_editor_performance'
--   AND COLUMN_NAME IN ('create_by', 'create_time', 'update_by', 'update_time')
-- ORDER BY ORDINAL_POSITION;

