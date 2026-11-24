package com.xmzs.coedit.util;

import com.xmzs.common.mybatis.config.CustomSnowflakeIdGenerator;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ID生成测试工具类
 * 用于测试和验证新的ID生成策略
 * 
 * @author xmzs
 */
@Slf4j
public class IdTestUtil {
    
    private static final CustomSnowflakeIdGenerator generator = new CustomSnowflakeIdGenerator();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 生成测试ID并显示详细信息
     */
    public static void generateTestIds(int count) {
        log.info("=== ID生成测试 ===");
        log.info("起始时间(Epoch): {} ({})", 
                CustomSnowflakeIdGenerator.getEpoch(), 
                dateFormat.format(new Date(CustomSnowflakeIdGenerator.getEpoch())));
        
        for (int i = 0; i < count; i++) {
            long id = generator.nextId(null).longValue();
            
            // 解析ID组成部分
            long timestamp = CustomSnowflakeIdGenerator.parseTimestamp(id);
            long datacenterId = CustomSnowflakeIdGenerator.parseDatacenterId(id);
            long workerId = CustomSnowflakeIdGenerator.parseWorkerId(id);
            long sequence = CustomSnowflakeIdGenerator.parseSequence(id);
            
            log.info("ID[{}]: {} ({}位数字)", i + 1, id, String.valueOf(id).length());
            log.info("  - 时间戳: {} ({})", timestamp, dateFormat.format(new Date(timestamp)));
            log.info("  - 数据中心ID: {}", datacenterId);
            log.info("  - 机器ID: {}", workerId);
            log.info("  - 序列号: {}", sequence);
            log.info("  - JavaScript安全: {}", isJavaScriptSafe(id) ? "是" : "否");
            log.info("---");
        }
    }
    
    /**
     * 检查ID是否在JavaScript安全范围内
     */
    public static boolean isJavaScriptSafe(long id) {
        return id >= -9007199254740991L && id <= 9007199254740991L;
    }
    
    /**
     * 比较新旧ID大小
     */
    public static void compareWithOldId(long oldId) {
        long newId = generator.nextId(null).longValue();
        
        log.info("=== ID大小对比 ===");
        log.info("旧ID: {} ({}位数字)", oldId, String.valueOf(oldId).length());
        log.info("新ID: {} ({}位数字)", newId, String.valueOf(newId).length());
        
        double reduction = (double)(oldId - newId) / oldId * 100;
        log.info("减小幅度: {:.2f}%", reduction);
        
        log.info("旧ID JavaScript安全: {}", isJavaScriptSafe(oldId) ? "是" : "否");
        log.info("新ID JavaScript安全: {}", isJavaScriptSafe(newId) ? "是" : "否");
    }
    
    /**
     * 性能测试
     */
    public static void performanceTest(int count) {
        log.info("=== ID生成性能测试 ===");
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < count; i++) {
            generator.nextId(null);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        log.info("生成{}个ID耗时: {}ms", count, duration);
        log.info("平均每个ID耗时: {:.3f}ms", (double)duration / count);
        log.info("每秒可生成ID数: {}", count * 1000 / duration);
    }
    
    /**
     * 验证ID唯一性
     */
    public static void uniquenessTest(int count) {
        log.info("=== ID唯一性测试 ===");
        
        java.util.Set<Long> idSet = new java.util.HashSet<>();
        int duplicateCount = 0;
        
        for (int i = 0; i < count; i++) {
            long id = generator.nextId(null).longValue();
            if (!idSet.add(id)) {
                duplicateCount++;
                log.warn("发现重复ID: {}", id);
            }
        }
        
        log.info("生成{}个ID，重复数量: {}", count, duplicateCount);
        log.info("唯一性: {:.2f}%", (double)(count - duplicateCount) / count * 100);
    }
    
    /**
     * 主测试方法
     */
    public static void main(String[] args) {
        // 生成测试ID
        generateTestIds(5);
        
        // 与旧ID对比（使用您提到的大ID）
        compareWithOldId(1987153123470454800L);
        
        // 性能测试
        performanceTest(10000);
        
        // 唯一性测试
        uniquenessTest(1000);
    }
}
