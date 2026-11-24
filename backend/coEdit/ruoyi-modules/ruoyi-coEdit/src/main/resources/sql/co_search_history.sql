-- ========================================
-- 文件搜索历史表
-- ========================================

DROP TABLE IF EXISTS `co_search_history`;
CREATE TABLE `co_search_history` (
    -- ==================== 主键 ====================
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '搜索历史ID',

    -- ==================== 搜索信息 ====================
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `workspace_id` BIGINT(20) DEFAULT NULL COMMENT '工作空间ID',
    `keyword` VARCHAR(200) NOT NULL COMMENT '搜索关键词',
    `search_type` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '搜索类型：1-文件名 2-内容 3-高级搜索',

    -- ==================== 搜索条件（高级搜索） ====================
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型',
    `folder_id` BIGINT(20) DEFAULT NULL COMMENT '文件夹ID',
    `min_size` BIGINT(20) DEFAULT NULL COMMENT '最小文件大小（字节）',
    `max_size` BIGINT(20) DEFAULT NULL COMMENT '最大文件大小（字节）',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `creator_id` BIGINT(20) DEFAULT NULL COMMENT '创建者ID',

    -- ==================== 搜索结果 ====================
    `result_count` INT(11) DEFAULT 0 COMMENT '结果数量',
    `search_time` INT(11) DEFAULT 0 COMMENT '搜索耗时（毫秒）',

    -- ==================== 审计字段 ====================
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',

    -- ==================== 主键和索引 ====================
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_workspace_id` (`workspace_id`),
    KEY `idx_keyword` (`keyword`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_search_type` (`search_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件搜索历史表';

-- ========================================
-- 初始化数据
-- ========================================

-- 搜索类型说明：
-- 1-文件名搜索：根据文件名模糊匹配
-- 2-内容搜索：根据文件内容搜索（需要全文索引支持）
-- 3-高级搜索：多条件组合搜索

