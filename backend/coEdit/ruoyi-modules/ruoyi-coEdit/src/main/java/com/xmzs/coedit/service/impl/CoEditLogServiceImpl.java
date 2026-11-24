package com.xmzs.coedit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.domain.CoEditLog;
import com.xmzs.coedit.mapper.CoEditLogMapper;
import com.xmzs.coedit.service.ICoEditLogService;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 协同编辑日志Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoEditLogServiceImpl implements ICoEditLogService {

    private final CoEditLogMapper baseMapper;

    /**
     * 记录用户加入日志
     */
    @Override
    public void logUserJoin(Long fileId, Long userId, String userName, String sessionKey, String ipAddress, String userAgent) {
        CoEditLog editLog = new CoEditLog();
        editLog.setFileId(fileId);
        editLog.setUserId(userId);
        editLog.setUserName(userName);
        editLog.setSessionKey(sessionKey);
        editLog.setLogType("JOIN");
        editLog.setLogContent(userName + " 加入了编辑");
        editLog.setIpAddress(ipAddress);
        editLog.setUserAgent(userAgent);
        editLog.setLogTime(new Date());

        baseMapper.insert(editLog);
        log.info("记录用户加入日志: fileId={}, userId={}, userName={}", fileId, userId, userName);
    }

    /**
     * 记录用户离开日志
     */
    @Override
    public void logUserLeave(Long fileId, Long userId, String userName, String sessionKey) {
        CoEditLog editLog = new CoEditLog();
        editLog.setFileId(fileId);
        editLog.setUserId(userId);
        editLog.setUserName(userName);
        editLog.setSessionKey(sessionKey);
        editLog.setLogType("LEAVE");
        editLog.setLogContent(userName + " 离开了编辑");
        editLog.setLogTime(new Date());

        baseMapper.insert(editLog);
        log.info("记录用户离开日志: fileId={}, userId={}, userName={}", fileId, userId, userName);
    }

    /**
     * 记录编辑日志
     */
    @Override
    public void logEdit(Long fileId, Long userId, String userName, String sessionKey, String content) {
        CoEditLog editLog = new CoEditLog();
        editLog.setFileId(fileId);
        editLog.setUserId(userId);
        editLog.setUserName(userName);
        editLog.setSessionKey(sessionKey);
        editLog.setLogType("EDIT");
        editLog.setLogContent(content);
        editLog.setLogTime(new Date());

        baseMapper.insert(editLog);
        log.debug("记录编辑日志: fileId={}, userId={}", fileId, userId);
    }

    /**
     * 记录加锁日志
     */
    @Override
    public void logLock(Long fileId, Long userId, String userName, String lockKey) {
        CoEditLog editLog = new CoEditLog();
        editLog.setFileId(fileId);
        editLog.setUserId(userId);
        editLog.setUserName(userName);
        editLog.setLogType("LOCK");
        editLog.setLogContent(userName + " 获取了编辑锁");
        editLog.setLogData("{\"lockKey\":\"" + lockKey + "\"}");
        editLog.setLogTime(new Date());

        baseMapper.insert(editLog);
        log.info("记录加锁日志: fileId={}, userId={}, lockKey={}", fileId, userId, lockKey);
    }

    /**
     * 记录解锁日志
     */
    @Override
    public void logUnlock(Long fileId, Long userId, String userName, String lockKey) {
        CoEditLog editLog = new CoEditLog();
        editLog.setFileId(fileId);
        editLog.setUserId(userId);
        editLog.setUserName(userName);
        editLog.setLogType("UNLOCK");
        editLog.setLogContent(userName + " 释放了编辑锁");
        editLog.setLogData("{\"lockKey\":\"" + lockKey + "\"}");
        editLog.setLogTime(new Date());

        baseMapper.insert(editLog);
        log.info("记录解锁日志: fileId={}, userId={}, lockKey={}", fileId, userId, lockKey);
    }

    /**
     * 记录冲突日志
     */
    @Override
    public void logConflict(Long fileId, Long userId, String userName, String sessionKey, String conflictData) {
        CoEditLog editLog = new CoEditLog();
        editLog.setFileId(fileId);
        editLog.setUserId(userId);
        editLog.setUserName(userName);
        editLog.setSessionKey(sessionKey);
        editLog.setLogType("CONFLICT");
        editLog.setLogContent("检测到编辑冲突");
        editLog.setLogData(conflictData);
        editLog.setLogTime(new Date());

        baseMapper.insert(editLog);
        log.warn("记录冲突日志: fileId={}, userId={}", fileId, userId);
    }

    /**
     * 查询文件的编辑日志
     */
    @Override
    public TableDataInfo<CoEditLog> queryFileLog(Long fileId, PageQuery pageQuery) {
        LambdaQueryWrapper<CoEditLog> wrapper = Wrappers.lambdaQuery(CoEditLog.class)
            .eq(CoEditLog::getFileId, fileId)
            .orderByDesc(CoEditLog::getLogTime);

        Page<CoEditLog> page = baseMapper.selectPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(page);
    }

    /**
     * 查询用户的编辑日志
     */
    @Override
    public TableDataInfo<CoEditLog> queryUserLog(Long userId, PageQuery pageQuery) {
        LambdaQueryWrapper<CoEditLog> wrapper = Wrappers.lambdaQuery(CoEditLog.class)
            .eq(CoEditLog::getUserId, userId)
            .orderByDesc(CoEditLog::getLogTime);

        Page<CoEditLog> page = baseMapper.selectPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(page);
    }

    /**
     * 查询会话的编辑日志
     */
    @Override
    public List<CoEditLog> querySessionLog(String sessionKey) {
        return baseMapper.selectList(
            Wrappers.lambdaQuery(CoEditLog.class)
                .eq(CoEditLog::getSessionKey, sessionKey)
                .orderByAsc(CoEditLog::getLogTime)
        );
    }

}

