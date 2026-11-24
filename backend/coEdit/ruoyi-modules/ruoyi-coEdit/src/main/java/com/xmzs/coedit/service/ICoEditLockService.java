package com.xmzs.coedit.service;

/**
 * 编辑锁Service接口
 *
 * @author zuleed
 * @date 2025-11-04
 */
public interface ICoEditLockService {

    /**
     * 获取文档锁
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @param lockType 锁类型：0-读锁 1-写锁
     * @param expireMinutes 过期时间（分钟）
     * @return 锁Key，如果获取失败返回null
     */
    String acquireLock(Long fileId, Long userId, String userName, Integer lockType, Integer expireMinutes);

    /**
     * 释放锁
     *
     * @param lockKey 锁Key
     * @return 是否成功
     */
    Boolean releaseLock(String lockKey);

    /**
     * 检查文件是否被锁定
     *
     * @param fileId 文件ID
     * @return 是否被锁定
     */
    Boolean isLocked(Long fileId);

    /**
     * 检查用户是否持有锁
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 是否持有锁
     */
    Boolean hasLock(Long fileId, Long userId);

    /**
     * 获取文件的锁持有者
     *
     * @param fileId 文件ID
     * @return 锁持有者用户名，如果没有锁返回null
     */
    String getLockHolder(Long fileId);

    /**
     * 续期锁
     *
     * @param lockKey 锁Key
     * @param extendMinutes 延长时间（分钟）
     * @return 是否成功
     */
    Boolean renewLock(String lockKey, Integer extendMinutes);

    /**
     * 强制释放锁（管理员功能）
     *
     * @param fileId 文件ID
     * @return 是否成功
     */
    Boolean forceReleaseLock(Long fileId);

    /**
     * 清理过期锁
     *
     * @return 清理数量
     */
    Integer cleanExpiredLocks();

}

