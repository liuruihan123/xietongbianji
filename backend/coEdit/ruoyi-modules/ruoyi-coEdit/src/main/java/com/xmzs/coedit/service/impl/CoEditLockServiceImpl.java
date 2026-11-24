package com.xmzs.coedit.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xmzs.coedit.domain.CoEditLock;
import com.xmzs.coedit.mapper.CoEditLockMapper;
import com.xmzs.coedit.service.ICoEditLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 编辑锁Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoEditLockServiceImpl implements ICoEditLockService {

    private final CoEditLockMapper baseMapper;

    /**
     * 获取文档锁
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String acquireLock(Long fileId, Long userId, String userName, Integer lockType, Integer expireMinutes) {
        log.info("尝试获取锁: fileId={}, userId={}, lockType={}", fileId, userId, lockType);

        // 检查文件是否已被其他用户锁定
        CoEditLock existingLock = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoEditLock.class)
                .eq(CoEditLock::getFileId, fileId)
                .eq(CoEditLock::getStatus, 0) // 活跃状态
                .gt(CoEditLock::getExpireTime, new Date()) // 未过期
        );

        if (existingLock != null) {
            // 如果是同一个用户，允许重新获取锁
            if (existingLock.getUserId().equals(userId)) {
                log.info("用户已持有锁，返回现有锁: lockKey={}", existingLock.getLockKey());
                return existingLock.getLockKey();
            }

            // 如果是读锁，允许多个用户同时持有
            if (lockType == 0 && existingLock.getLockType() == 0) {
                log.info("读锁可以共享，创建新的读锁");
            } else {
                log.warn("文件已被其他用户锁定: fileId={}, holder={}", fileId, existingLock.getUserName());
                return null;
            }
        }

        // 创建新锁
        String lockKey = UUID.randomUUID().toString(true);
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + expireMinutes * 60 * 1000L);

        CoEditLock lock = new CoEditLock();
        lock.setFileId(fileId);
        lock.setUserId(userId);
        lock.setUserName(userName);
        lock.setLockKey(lockKey);
        lock.setLockType(lockType);
        lock.setLockScope(0); // 整个文档
        lock.setAcquireTime(now);
        lock.setExpireTime(expireTime);
        lock.setStatus(0); // 活跃

        baseMapper.insert(lock);

        log.info("成功获取锁: lockKey={}, fileId={}, userId={}", lockKey, fileId, userId);
        return lockKey;
    }

    /**
     * 释放锁
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean releaseLock(String lockKey) {
        log.info("释放锁: lockKey={}", lockKey);

        CoEditLock lock = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoEditLock.class)
                .eq(CoEditLock::getLockKey, lockKey)
        );

        if (lock == null) {
            log.warn("锁不存在: lockKey={}", lockKey);
            return false;
        }

        // 更新锁状态为已释放
        lock.setStatus(1);
        int result = baseMapper.updateById(lock);

        log.info("锁已释放: lockKey={}, result={}", lockKey, result > 0);
        return result > 0;
    }

    /**
     * 检查文件是否被锁定
     */
    @Override
    public Boolean isLocked(Long fileId) {
        Long count = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoEditLock.class)
                .eq(CoEditLock::getFileId, fileId)
                .eq(CoEditLock::getStatus, 0) // 活跃状态
                .gt(CoEditLock::getExpireTime, new Date()) // 未过期
        );
        return count > 0;
    }

    /**
     * 检查用户是否持有锁
     */
    @Override
    public Boolean hasLock(Long fileId, Long userId) {
        Long count = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoEditLock.class)
                .eq(CoEditLock::getFileId, fileId)
                .eq(CoEditLock::getUserId, userId)
                .eq(CoEditLock::getStatus, 0) // 活跃状态
                .gt(CoEditLock::getExpireTime, new Date()) // 未过期
        );
        return count > 0;
    }

    /**
     * 获取文件的锁持有者
     */
    @Override
    public String getLockHolder(Long fileId) {
        CoEditLock lock = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoEditLock.class)
                .eq(CoEditLock::getFileId, fileId)
                .eq(CoEditLock::getStatus, 0) // 活跃状态
                .gt(CoEditLock::getExpireTime, new Date()) // 未过期
                .orderByDesc(CoEditLock::getAcquireTime)
                .last("LIMIT 1")
        );
        return lock != null ? lock.getUserName() : null;
    }

    /**
     * 续期锁
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean renewLock(String lockKey, Integer extendMinutes) {
        log.info("续期锁: lockKey={}, extendMinutes={}", lockKey, extendMinutes);

        CoEditLock lock = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoEditLock.class)
                .eq(CoEditLock::getLockKey, lockKey)
        );

        if (lock == null) {
            log.warn("锁不存在: lockKey={}", lockKey);
            return false;
        }

        // 延长过期时间
        Date newExpireTime = new Date(lock.getExpireTime().getTime() + extendMinutes * 60 * 1000L);
        lock.setExpireTime(newExpireTime);

        int result = baseMapper.updateById(lock);

        log.info("锁已续期: lockKey={}, newExpireTime={}", lockKey, newExpireTime);
        return result > 0;
    }

    /**
     * 强制释放锁（管理员功能）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean forceReleaseLock(Long fileId) {
        log.info("强制释放锁: fileId={}", fileId);

        List<CoEditLock> locks = baseMapper.selectList(
            Wrappers.lambdaQuery(CoEditLock.class)
                .eq(CoEditLock::getFileId, fileId)
                .eq(CoEditLock::getStatus, 0) // 活跃状态
        );

        if (locks.isEmpty()) {
            log.info("没有需要释放的锁: fileId={}", fileId);
            return true;
        }

        // 批量更新锁状态为已释放
        for (CoEditLock lock : locks) {
            lock.setStatus(1);
            baseMapper.updateById(lock);
        }

        log.info("强制释放了{}个锁: fileId={}", locks.size(), fileId);
        return true;
    }

    /**
     * 清理过期锁
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanExpiredLocks() {
        log.info("开始清理过期锁");

        // 查询过期的活跃锁
        List<CoEditLock> expiredLocks = baseMapper.selectList(
            Wrappers.lambdaQuery(CoEditLock.class)
                .eq(CoEditLock::getStatus, 0) // 活跃状态
                .lt(CoEditLock::getExpireTime, new Date()) // 已过期
        );

        if (expiredLocks.isEmpty()) {
            log.info("没有过期的锁需要清理");
            return 0;
        }

        // 批量更新锁状态为已过期
        for (CoEditLock lock : expiredLocks) {
            lock.setStatus(2);
            baseMapper.updateById(lock);
        }

        log.info("清理了{}个过期锁", expiredLocks.size());
        return expiredLocks.size();
    }

}

