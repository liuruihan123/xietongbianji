package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.CoEditLog;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 协同编辑日志Service接口
 *
 * @author zuleed
 * @date 2025-11-04
 */
public interface ICoEditLogService {

    /**
     * 记录用户加入日志
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @param sessionKey 会话Key
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     */
    void logUserJoin(Long fileId, Long userId, String userName, String sessionKey, String ipAddress, String userAgent);

    /**
     * 记录用户离开日志
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @param sessionKey 会话Key
     */
    void logUserLeave(Long fileId, Long userId, String userName, String sessionKey);

    /**
     * 记录编辑日志
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @param sessionKey 会话Key
     * @param content 编辑内容
     */
    void logEdit(Long fileId, Long userId, String userName, String sessionKey, String content);

    /**
     * 记录加锁日志
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @param lockKey 锁Key
     */
    void logLock(Long fileId, Long userId, String userName, String lockKey);

    /**
     * 记录解锁日志
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @param lockKey 锁Key
     */
    void logUnlock(Long fileId, Long userId, String userName, String lockKey);

    /**
     * 记录冲突日志
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @param sessionKey 会话Key
     * @param conflictData 冲突数据
     */
    void logConflict(Long fileId, Long userId, String userName, String sessionKey, String conflictData);

    /**
     * 查询文件的编辑日志
     *
     * @param fileId 文件ID
     * @param pageQuery 分页参数
     * @return 日志列表
     */
    TableDataInfo<CoEditLog> queryFileLog(Long fileId, PageQuery pageQuery);

    /**
     * 查询用户的编辑日志
     *
     * @param userId 用户ID
     * @param pageQuery 分页参数
     * @return 日志列表
     */
    TableDataInfo<CoEditLog> queryUserLog(Long userId, PageQuery pageQuery);

    /**
     * 查询会话的编辑日志
     *
     * @param sessionKey 会话Key
     * @return 日志列表
     */
    List<CoEditLog> querySessionLog(String sessionKey);

}

