package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.vo.CoAccessRecordVo;
import com.xmzs.coedit.domain.bo.CoAccessRecordBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;

import java.util.List;
import java.util.Map;

/**
 * 访问记录Service接口
 *
 * @author zuleed
 * @date 2025-11-08
 */
public interface ICoAccessRecordService {

    /**
     * 通用记录访问方法
     *
     * @param userId 用户ID
     * @param targetType 目标类型:1-工作空间 2-文件夹 3-文件
     * @param targetId 目标ID
     * @param workspaceId 工作空间ID(目标为工作空间时可为NULL)
     * @param accessType 访问类型:1-查看 2-编辑 3-下载
     * @param accessDuration 访问时长(秒)
     * @param accessIp 访问IP
     * @param userAgent 用户代理
     * @return 是否成功
     */
    Boolean recordAccess(Long userId, Integer targetType, Long targetId,
                        Long workspaceId, Integer accessType,
                        Integer accessDuration, String accessIp, String userAgent);

    /**
     * 记录工作空间访问
     *
     * @param userId 用户ID
     * @param workspaceId 工作空间ID
     * @return 是否成功
     */
    Boolean recordWorkspaceAccess(Long userId, Long workspaceId);

    /**
     * 记录文件夹访问
     *
     * @param userId 用户ID
     * @param folderId 文件夹ID
     * @param workspaceId 工作空间ID
     * @return 是否成功
     */
    Boolean recordFolderAccess(Long userId, Long folderId, Long workspaceId);

    /**
     * 记录文件访问
     *
     * @param userId 用户ID
     * @param fileId 文件ID
     * @param workspaceId 工作空间ID
     * @param accessType 访问类型:1-查看 2-编辑 3-下载
     * @return 是否成功
     */
    Boolean recordFileAccess(Long userId, Long fileId, Long workspaceId, Integer accessType);

    /**
     * 获取最近访问记录(分页,不去重)
     *
     * @param userId 用户ID
     * @param workspaceId 工作空间ID(可选)
     * @param pageQuery 分页参数
     * @return 访问记录列表
     */
    TableDataInfo<CoAccessRecordVo> getRecentAccess(Long userId, Long workspaceId, PageQuery pageQuery);

    /**
     * 获取最近访问记录列表(不分页,不去重,最多50条)
     *
     * @param userId 用户ID
     * @param workspaceId 工作空间ID(可选)
     * @param limit 限制数量(最多50)
     * @return 访问记录列表
     */
    List<CoAccessRecordVo> getRecentAccessList(Long userId, Long workspaceId, Integer limit);

    /**
     * 获取访问统计信息
     *
     * @param userId 用户ID
     * @param workspaceId 工作空间ID(可选)
     * @return 统计信息
     */
    Map<String, Object> getAccessStatistics(Long userId, Long workspaceId);

    /**
     * 获取访问最多的目标
     *
     * @param userId 用户ID
     * @param workspaceId 工作空间ID(可选)
     * @param targetType 目标类型(可选)
     * @param limit 限制数量
     * @return 访问统计列表
     */
    List<Map<String, Object>> getTopAccessedTargets(Long userId, Long workspaceId, Integer targetType, Integer limit);

}

