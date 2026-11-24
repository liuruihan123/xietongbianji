package com.xmzs.common.mybatis.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 自定义雪花ID生成器
 *
 * 通过调整起始时间(epoch)来减小ID数值,同时保持分布式唯一性
 *
 * 雪花算法结构(64位):
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * ↑   ←-------------- 41位时间戳 --------------→   ←5位→  ←5位→  ←--12位序列-->
 * 符号位              (毫秒级)                    数据中心  机器ID    序列号
 *
 * 优化说明:
 * - 默认起始时间: 2020-01-01 → 生成的ID约19位数字
 * - 自定义起始时间: 2024-01-01 → 生成的ID约18位数字(减小约60%)
 * - 自定义起始时间: 2025-01-01 → 生成的ID约18位数字(减小约80%)
 *
 * @author xmzs
 */
@Slf4j
public class CustomSnowflakeIdGenerator implements IdentifierGenerator {

    /**
     * 起始时间戳(epoch) - 2025-01-01 00:00:00 UTC+8
     *
     * 注意:
     * 1. 起始时间设置后不应再修改,否则可能导致ID冲突
     * 2. 起始时间越接近当前时间,生成的ID数值越小
     * 3. 41位时间戳可以使用约69年,所以2025-01-01可以用到2094年
     * 4. 更新为2025-01-01可以进一步减小ID数值约50%
     */
    private static final long EPOCH = 1735660800000L;  // 2025-01-01 00:00:00

    // 各部分占用的位数
    private static final long TIMESTAMP_BITS = 41L;      // 时间戳占41位
    private static final long DATACENTER_ID_BITS = 5L;   // 数据中心ID占5位
    private static final long WORKER_ID_BITS = 5L;       // 机器ID占5位
    private static final long SEQUENCE_BITS = 12L;       // 序列号占12位

    // 各部分的最大值
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);  // 31
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);          // 31
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);            // 4095

    // 各部分向左的位移量
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;                                    // 12
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;              // 17
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;  // 22

    // 数据中心ID(0-31)
    private final long datacenterId;

    // 机器ID(0-31)
    private final long workerId;

    // 序列号(0-4095)
    private long sequence = 0L;

    // 上次生成ID的时间戳
    private long lastTimestamp = -1L;

    /**
     * 构造函数
     * 自动从网卡信息获取数据中心ID和机器ID
     */
    public CustomSnowflakeIdGenerator() {
        this.datacenterId = getDatacenterId();
        this.workerId = getWorkerId();

        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                String.format("数据中心ID必须在0-%d之间,当前值: %d", MAX_DATACENTER_ID, datacenterId));
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                String.format("机器ID必须在0-%d之间,当前值: %d", MAX_WORKER_ID, workerId));
        }

        log.info("初始化自定义雪花ID生成器 - 数据中心ID: {}, 机器ID: {}, 起始时间: {} (2025-01-01)",
                 datacenterId, workerId, EPOCH);
    }

    /**
     * 构造函数
     *
     * @param datacenterId 数据中心ID (0-31)
     * @param workerId 机器ID (0-31)
     */
    public CustomSnowflakeIdGenerator(long datacenterId, long workerId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                String.format("数据中心ID必须在0-%d之间,当前值: %d", MAX_DATACENTER_ID, datacenterId));
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                String.format("机器ID必须在0-%d之间,当前值: %d", MAX_WORKER_ID, workerId));
        }

        this.datacenterId = datacenterId;
        this.workerId = workerId;

        log.info("初始化自定义雪花ID生成器 - 数据中心ID: {}, 机器ID: {}, 起始时间: {} (2025-01-01)",
                 datacenterId, workerId, EPOCH);
    }

    /**
     * 生成下一个ID
     *
     * @param entity 实体对象(未使用)
     * @return 生成的ID
     */
    @Override
    public synchronized Number nextId(Object entity) {
        long timestamp = System.currentTimeMillis();

        // 时钟回拨检查
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                // 如果时钟回拨在5ms以内,等待两倍时间
                try {
                    wait(offset << 1);
                    timestamp = System.currentTimeMillis();
                    if (timestamp < lastTimestamp) {
                        throw new RuntimeException(
                            String.format("时钟回拨,拒绝生成ID。回拨了%d毫秒", lastTimestamp - timestamp));
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException("等待时钟同步时被中断", e);
                }
            } else {
                throw new RuntimeException(
                    String.format("时钟回拨,拒绝生成ID。回拨了%d毫秒", lastTimestamp - timestamp));
            }
        }

        // 同一毫秒内
        if (timestamp == lastTimestamp) {
            // 序列号递增
            sequence = (sequence + 1) & MAX_SEQUENCE;

            // 序列号溢出(同一毫秒内生成超过4096个ID)
            if (sequence == 0) {
                // 等待下一毫秒
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒,序列号重置为0
            sequence = 0L;
        }

        // 更新上次生成ID的时间戳
        lastTimestamp = timestamp;

        // 组装64位ID
        // 时间戳部分 | 数据中心ID部分 | 机器ID部分 | 序列号部分
        long id = ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;

        return id;
    }

    /**
     * 生成字符串类型的ID
     *
     * @param entity 实体对象
     * @return 字符串类型的ID
     */
    @Override
    public String nextUUID(Object entity) {
        return String.valueOf(nextId(entity));
    }

    /**
     * 等待下一毫秒
     *
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 当前时间戳
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * 获取数据中心ID
     * 根据IP地址计算
     *
     * @return 数据中心ID (0-31)
     */
    private long getDatacenterId() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            byte[] ipBytes = ip.getAddress();
            // 使用IP地址的倒数第二个字节的高5位
            return ((ipBytes[ipBytes.length - 2] & 0xFF) >> 3) & 0x1F;
        } catch (UnknownHostException e) {
            log.warn("获取数据中心ID失败,使用默认值0", e);
            return 0L;
        }
    }

    /**
     * 获取机器ID
     * 根据IP地址计算
     *
     * @return 机器ID (0-31)
     */
    private long getWorkerId() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            byte[] ipBytes = ip.getAddress();
            // 使用IP地址的最后一个字节的低5位
            return (ipBytes[ipBytes.length - 1] & 0xFF) & 0x1F;
        } catch (UnknownHostException e) {
            log.warn("获取机器ID失败,使用默认值0", e);
            return 0L;
        }
    }

    /**
     * 获取当前配置的起始时间
     *
     * @return 起始时间戳(毫秒)
     */
    public static long getEpoch() {
        return EPOCH;
    }

    /**
     * 从ID中解析时间戳
     *
     * @param id 雪花ID
     * @return 时间戳(毫秒)
     */
    public static long parseTimestamp(long id) {
        return (id >> TIMESTAMP_SHIFT) + EPOCH;
    }

    /**
     * 从ID中解析数据中心ID
     *
     * @param id 雪花ID
     * @return 数据中心ID
     */
    public static long parseDatacenterId(long id) {
        return (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
    }

    /**
     * 从ID中解析机器ID
     *
     * @param id 雪花ID
     * @return 机器ID
     */
    public static long parseWorkerId(long id) {
        return (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
    }

    /**
     * 从ID中解析序列号
     *
     * @param id 雪花ID
     * @return 序列号
     */
    public static long parseSequence(long id) {
        return id & MAX_SEQUENCE;
    }
}

