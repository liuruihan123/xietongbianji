package com.xmzs.coedit.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.domain.CoEditSession;
import com.xmzs.coedit.domain.bo.CoEditSessionBo;
import com.xmzs.coedit.domain.vo.CoEditSessionVo;
import com.xmzs.coedit.mapper.CoEditSessionMapper;
import com.xmzs.coedit.service.ICoEditSessionService;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 协同编辑会话Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoEditSessionServiceImpl implements ICoEditSessionService {

    private final CoEditSessionMapper baseMapper;

    /**
     * 查询协同编辑会话
     */
    @Override
    public CoEditSessionVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询协同编辑会话列表
     */
    @Override
    public TableDataInfo<CoEditSessionVo> queryPageList(CoEditSessionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CoEditSession> lqw = buildQueryWrapper(bo);
        Page<CoEditSessionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询协同编辑会话列表
     */
    @Override
    public List<CoEditSessionVo> queryList(CoEditSessionBo bo) {
        LambdaQueryWrapper<CoEditSession> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<CoEditSession> buildQueryWrapper(CoEditSessionBo bo) {
        LambdaQueryWrapper<CoEditSession> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getFileId() != null, CoEditSession::getFileId, bo.getFileId());
        lqw.eq(bo.getUserId() != null, CoEditSession::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getUserName()), CoEditSession::getUserName, bo.getUserName());
        lqw.eq(StringUtils.isNotBlank(bo.getSessionKey()), CoEditSession::getSessionKey, bo.getSessionKey());
        lqw.eq(bo.getStatus() != null, CoEditSession::getStatus, bo.getStatus());
        lqw.orderByDesc(CoEditSession::getStartTime);
        return lqw;
    }

    /**
     * 创建编辑会话
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoEditSessionVo createSession(Long fileId, Long userId, String userName, 
                                         String documentKey, String ipAddress, String userAgent) {
        log.info("创建编辑会话: fileId={}, userId={}, userName={}", fileId, userId, userName);

        // 生成会话Key
        String sessionKey = IdUtil.getSnowflakeNextIdStr();

        // 创建会话对象
        CoEditSession session = new CoEditSession();
        session.setFileId(fileId);
        session.setUserId(userId);
        session.setUserName(userName);
        session.setSessionKey(sessionKey);
        session.setDocumentKey(documentKey);
        session.setStartTime(new Date());
        session.setLastActiveTime(new Date());
        session.setStatus(0); // 活跃
        session.setEditCount(0);
        session.setDuration(0);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);

        // 保存会话
        baseMapper.insert(session);

        log.info("编辑会话创建成功: sessionKey={}", sessionKey);
        return baseMapper.selectVoById(session.getId());
    }

    /**
     * 更新会话活跃时间（心跳）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateActiveTime(String sessionKey) {
        CoEditSession session = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoEditSession.class)
                .eq(CoEditSession::getSessionKey, sessionKey)
        );

        if (session == null) {
            log.warn("会话不存在: sessionKey={}", sessionKey);
            return false;
        }

        // 更新最后活跃时间
        session.setLastActiveTime(new Date());
        
        // 如果状态是空闲，更新为活跃
        if (session.getStatus() == 1) {
            session.setStatus(0);
        }

        return baseMapper.updateById(session) > 0;
    }

    /**
     * 增加编辑次数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean incrementEditCount(String sessionKey) {
        CoEditSession session = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoEditSession.class)
                .eq(CoEditSession::getSessionKey, sessionKey)
        );

        if (session == null) {
            log.warn("会话不存在: sessionKey={}", sessionKey);
            return false;
        }

        // 增加编辑次数
        session.setEditCount(session.getEditCount() + 1);
        session.setLastActiveTime(new Date());

        return baseMapper.updateById(session) > 0;
    }

    /**
     * 结束编辑会话
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean endSession(String sessionKey) {
        log.info("结束编辑会话: sessionKey={}", sessionKey);

        CoEditSession session = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoEditSession.class)
                .eq(CoEditSession::getSessionKey, sessionKey)
        );

        if (session == null) {
            log.warn("会话不存在: sessionKey={}", sessionKey);
            return false;
        }

        // 计算编辑时长
        Date endTime = new Date();
        long duration = (endTime.getTime() - session.getStartTime().getTime()) / 1000;

        // 更新会话状态
        session.setEndTime(endTime);
        session.setDuration((int) duration);
        session.setStatus(2); // 已结束

        boolean result = baseMapper.updateById(session) > 0;
        
        if (result) {
            log.info("编辑会话结束成功: sessionKey={}, duration={}秒", sessionKey, duration);
        }

        return result;
    }

    /**
     * 查询文件的活跃会话列表
     */
    @Override
    public List<CoEditSessionVo> queryActiveSessionsByFileId(Long fileId) {
        return baseMapper.selectVoList(
            Wrappers.lambdaQuery(CoEditSession.class)
                .eq(CoEditSession::getFileId, fileId)
                .in(CoEditSession::getStatus, 0, 1) // 活跃或空闲
                .orderByDesc(CoEditSession::getLastActiveTime)
        );
    }

    /**
     * 查询用户的活跃会话列表
     */
    @Override
    public List<CoEditSessionVo> queryActiveSessionsByUserId(Long userId) {
        return baseMapper.selectVoList(
            Wrappers.lambdaQuery(CoEditSession.class)
                .eq(CoEditSession::getUserId, userId)
                .in(CoEditSession::getStatus, 0, 1) // 活跃或空闲
                .orderByDesc(CoEditSession::getLastActiveTime)
        );
    }

    /**
     * 查询用户的编辑历史
     */
    @Override
    public TableDataInfo<CoEditSessionVo> queryUserHistory(Long userId, PageQuery pageQuery) {
        Page<CoEditSessionVo> result = baseMapper.selectVoPage(
            pageQuery.build(),
            Wrappers.lambdaQuery(CoEditSession.class)
                .eq(CoEditSession::getUserId, userId)
                .orderByDesc(CoEditSession::getStartTime)
        );
        return TableDataInfo.build(result);
    }

    /**
     * 查询文件的编辑历史
     */
    @Override
    public TableDataInfo<CoEditSessionVo> queryFileHistory(Long fileId, PageQuery pageQuery) {
        Page<CoEditSessionVo> result = baseMapper.selectVoPage(
            pageQuery.build(),
            Wrappers.lambdaQuery(CoEditSession.class)
                .eq(CoEditSession::getFileId, fileId)
                .orderByDesc(CoEditSession::getStartTime)
        );
        return TableDataInfo.build(result);
    }

    /**
     * 检查文件是否有活跃会话
     */
    @Override
    public Boolean hasActiveSessions(Long fileId) {
        Long count = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoEditSession.class)
                .eq(CoEditSession::getFileId, fileId)
                .in(CoEditSession::getStatus, 0, 1) // 活跃或空闲
        );
        return count > 0;
    }

    /**
     * 获取文件的活跃会话数量
     */
    @Override
    public Integer getActiveSessionCount(Long fileId) {
        Long count = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoEditSession.class)
                .eq(CoEditSession::getFileId, fileId)
                .in(CoEditSession::getStatus, 0, 1) // 活跃或空闲
        );
        return count.intValue();
    }

    /**
     * 清理超时会话
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanTimeoutSessions(Integer timeoutMinutes) {
        log.info("开始清理超时会话: timeoutMinutes={}", timeoutMinutes);

        // 计算超时时间点
        Date timeoutTime = new Date(System.currentTimeMillis() - timeoutMinutes * 60 * 1000L);

        // 查询超时的活跃会话
        List<CoEditSession> timeoutSessions = baseMapper.selectList(
            Wrappers.lambdaQuery(CoEditSession.class)
                .in(CoEditSession::getStatus, 0, 1) // 活跃或空闲
                .lt(CoEditSession::getLastActiveTime, timeoutTime)
        );

        if (timeoutSessions.isEmpty()) {
            log.info("没有超时会话需要清理");
            return 0;
        }

        // 批量结束超时会话
        Date now = new Date();
        for (CoEditSession session : timeoutSessions) {
            long duration = (now.getTime() - session.getStartTime().getTime()) / 1000;
            session.setEndTime(now);
            session.setDuration((int) duration);
            session.setStatus(2); // 已结束
            baseMapper.updateById(session);
        }

        log.info("清理超时会话完成: count={}", timeoutSessions.size());
        return timeoutSessions.size();
    }

    /**
     * 批量删除协同编辑会话
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteByIds(Collection<Long> ids) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }

}

