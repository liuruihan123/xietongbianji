package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.bo.CoFileAccessRecordBo;
import com.xmzs.coedit.domain.vo.CoFileAccessRecordVo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Map;

/**
 * 文件访问记录Service接口
 *
 * @author zuleed
 * @date 2025-11-08
 */
public interface ICoFileAccessRecordService {

    /**
     * 记录文件访问
     *
     * @param bo 访问记录
     * @return 是否成功
     */
    Boolean recordAccess(CoFileAccessRecordBo bo);

    /**
     * 记录文件访问(简化版)
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @param accessType 访问类型
     * @return 是否成功
     */
    Boolean recordAccess(Long fileId, Long userId, Integer accessType);

    /**
     * 获取最近访问的文件列表
     *
     * @param userId 用户ID
     * @param workspaceId 工作空间ID(可选)
     * @param pageQuery 分页参数
     * @return 最近访问的文件列表
     */
    TableDataInfo<CoFileAccessRecordVo> getRecentAccess(Long userId, Long workspaceId, PageQuery pageQuery);

    /**
     * 获取最近访问的文件列表(不分页)
     *
     * @param userId 用户ID
     * @param workspaceId 工作空间ID(可选)
     * @param limit 限制数量
     * @return 最近访问的文件列表
     */
    List<CoFileAccessRecordVo> getRecentAccessList(Long userId, Long workspaceId, Integer limit);

    /**
     * 获取访问统计
     *
     * @param userId 用户ID
     * @param workspaceId 工作空间ID(可选)
     * @return 统计数据
     */
    Map<String, Object> getAccessStatistics(Long userId, Long workspaceId);

    /**
     * 获取访问最多的文件
     *
     * @param userId 用户ID
     * @param workspaceId 工作空间ID(可选)
     * @param limit 限制数量
     * @return 访问最多的文件列表
     */
    List<Map<String, Object>> getTopAccessedFiles(Long userId, Long workspaceId, Integer limit);

}

