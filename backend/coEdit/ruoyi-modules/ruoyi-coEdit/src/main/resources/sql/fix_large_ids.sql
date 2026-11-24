-- 修复大ID问题的数据库脚本
-- 用于检查和处理现有的大ID数据

-- 1. 检查当前数据库中的大ID情况
SELECT 
    'co_file' as table_name,
    COUNT(*) as total_count,
    COUNT(CASE WHEN id > 9007199254740991 THEN 1 END) as large_id_count,
    MIN(id) as min_id,
    MAX(id) as max_id
FROM co_file
UNION ALL
SELECT 
    'co_folder' as table_name,
    COUNT(*) as total_count,
    COUNT(CASE WHEN id > 9007199254740991 THEN 1 END) as large_id_count,
    MIN(id) as min_id,
    MAX(id) as max_id
FROM co_folder
UNION ALL
SELECT 
    'co_workspace' as table_name,
    COUNT(*) as total_count,
    COUNT(CASE WHEN id > 9007199254740991 THEN 1 END) as large_id_count,
    MIN(id) as min_id,
    MAX(id) as max_id
FROM co_workspace;

-- 2. 检查收藏表中引用的大ID
SELECT 
    f.id as favorite_id,
    f.target_type,
    f.target_id,
    f.favorite_name,
    CASE 
        WHEN f.target_type = 1 AND cf.id IS NULL THEN '文件不存在'
        WHEN f.target_type = 2 AND cfo.id IS NULL THEN '文件夹不存在'
        WHEN f.target_type = 3 AND cw.id IS NULL THEN '工作空间不存在'
        ELSE '目标存在'
    END as target_status
FROM co_favorite f
LEFT JOIN co_file cf ON f.target_type = 1 AND f.target_id = cf.id
LEFT JOIN co_folder cfo ON f.target_type = 2 AND f.target_id = cfo.id
LEFT JOIN co_workspace cw ON f.target_type = 3 AND f.target_id = cw.id
WHERE f.target_id > 9007199254740991
ORDER BY f.id;

-- 3. 清理无效的收藏记录（可选执行）
-- 注意：执行前请先备份数据
/*
DELETE FROM co_favorite 
WHERE target_type = 1 AND target_id NOT IN (SELECT id FROM co_file)
   OR target_type = 2 AND target_id NOT IN (SELECT id FROM co_folder)
   OR target_type = 3 AND target_id NOT IN (SELECT id FROM co_workspace);
*/

-- 4. 检查其他表中可能存在的大ID引用
SELECT 
    'co_file_history' as table_name,
    COUNT(CASE WHEN file_id > 9007199254740991 THEN 1 END) as large_file_id_count
FROM co_file_history
UNION ALL
SELECT 
    'co_edit_session' as table_name,
    COUNT(CASE WHEN file_id > 9007199254740991 THEN 1 END) as large_file_id_count
FROM co_edit_session
UNION ALL
SELECT 
    'co_share_link' as table_name,
    COUNT(CASE WHEN target_id > 9007199254740991 THEN 1 END) as large_target_id_count
FROM co_share_link;

-- 5. 显示新ID生成器生成的ID范围（执行后）
-- 新的ID应该明显小于旧的大ID
SELECT 
    '新ID范围预估' as info,
    'ID将从约400000000000000000开始' as description,
    '相比旧ID减小约80%' as improvement;
