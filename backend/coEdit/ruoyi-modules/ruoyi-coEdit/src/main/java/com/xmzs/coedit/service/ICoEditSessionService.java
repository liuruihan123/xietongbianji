package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.bo.CoEditSessionBo;
import com.xmzs.coedit.domain.vo.CoEditSessionVo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 协同编辑会话Service接口
 *
 * @author zuleed
 * @date 2025-11-04
 */
public interface ICoEditSessionService {

    /**
     * 查询协同编辑会话
     *
     * @param id 会话ID
     * @return 协同编辑会话
     */
    CoEditSessionVo queryById(Long id);

    /**
     * 查询协同编辑会话列表
     *
     * @param bo 查询条件
     * @param pageQuery 分页参数
     * @return 协同编辑会话分页列表
     */
    TableDataInfo<CoEditSessionVo> queryPageList(CoEditSessionBo bo, PageQuery pageQuery);

    /**
     * 查询协同编辑会话列表
     *
     * @param bo 查询条件
     * @return 协同编辑会话列表
     */
    List<CoEditSessionVo> queryList(CoEditSessionBo bo);

    /**
     * 创建编辑会话
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @param documentKey 文档Key
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @return 会话信息
     */
    CoEditSessionVo createSession(Long fileId, Long userId, String userName, 
                                   String documentKey, String ipAddress, String userAgent);

    /**
     * 更新会话活跃时间（心跳）
     *
     * @param sessionKey 会话Key
     * @return 是否成功
     */
    Boolean updateActiveTime(String sessionKey);

    /**
     * 增加编辑次数
     *
     * @param sessionKey 会话Key
     * @return 是否成功
     */
    Boolean incrementEditCount(String sessionKey);

    /**
     * 结束编辑会话
     *
     * @param sessionKey 会话Key
     * @return 是否成功
     */
    Boolean endSession(String sessionKey);

    /**
     * 查询文件的活跃会话列表
     *
     * @param fileId 文件ID
     * @return 活跃会话列表
     */
    List<CoEditSessionVo> queryActiveSessionsByFileId(Long fileId);

    /**
     * 查询用户的活跃会话列表
     *
     * @param userId 用户ID
     * @return 活跃会话列表
     */
    List<CoEditSessionVo> queryActiveSessionsByUserId(Long userId);

    /**
     * 查询用户的编辑历史
     *
     * @param userId 用户ID
     * @param pageQuery 分页参数
     * @return 编辑历史分页列表
     */
    TableDataInfo<CoEditSessionVo> queryUserHistory(Long userId, PageQuery pageQuery);

    /**
     * 查询文件的编辑历史
     *
     * @param fileId 文件ID
     * @param pageQuery 分页参数
     * @return 编辑历史分页列表
     */
    TableDataInfo<CoEditSessionVo> queryFileHistory(Long fileId, PageQuery pageQuery);

    /**
     * 检查文件是否有活跃会话
     *
     * @param fileId 文件ID
     * @return 是否有活跃会话
     */
    Boolean hasActiveSessions(Long fileId);

    /**
     * 获取文件的活跃会话数量
     *
     * @param fileId 文件ID
     * @return 活跃会话数量
     */
    Integer getActiveSessionCount(Long fileId);

    /**
     * 清理超时会话
     *
     * @param timeoutMinutes 超时时间（分钟）
     * @return 清理的会话数量
     */
    Integer cleanTimeoutSessions(Integer timeoutMinutes);

    /**
     * 批量删除协同编辑会话
     *
     * @param ids 会话ID集合
     * @return 是否成功
     */
    Boolean deleteByIds(Collection<Long> ids);

}

