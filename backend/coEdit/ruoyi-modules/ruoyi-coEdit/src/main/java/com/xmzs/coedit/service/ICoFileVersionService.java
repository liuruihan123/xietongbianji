package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.bo.CoFileVersionBo;
import com.xmzs.coedit.domain.vo.CoFileVersionVo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

import java.util.Map;

/**
 * 文件版本Service接口
 *
 * @author zuleed
 * @date 2025-11-04
 */
public interface ICoFileVersionService {

    /**
     * 创建文件版本
     *
     * @param bo 文件版本业务对象
     * @param userId 用户ID
     * @param userName 用户名称
     * @return 版本信息
     */
    CoFileVersionVo createVersion(CoFileVersionBo bo, Long userId, String userName);

    /**
     * 根据文件ID查询版本列表
     *
     * @param fileId 文件ID
     * @param pageQuery 分页参数
     * @return 版本列表
     */
    TableDataInfo<CoFileVersionVo> getVersionListByFileId(Long fileId, PageQuery pageQuery);

    /**
     * 根据版本ID获取版本详情
     *
     * @param versionId 版本ID
     * @return 版本详情
     */
    CoFileVersionVo getVersionById(Long versionId);

    /**
     * 版本对比
     *
     * @param versionId1 版本1 ID
     * @param versionId2 版本2 ID
     * @return 对比结果
     */
    Map<String, Object> compareVersions(Long versionId1, Long versionId2);

    /**
     * 版本回滚
     *
     * @param versionId 要回滚到的版本ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @return 是否成功
     */
    Boolean rollbackToVersion(Long versionId, Long userId, String userName);

    /**
     * 删除指定版本
     *
     * @param versionId 版本ID
     * @return 是否成功
     */
    Boolean deleteVersion(Long versionId);

    /**
     * 清理文件的旧版本
     *
     * @param fileId 文件ID
     * @param keepCount 保留版本数量
     * @return 清理数量
     */
    Integer cleanOldVersions(Long fileId, Integer keepCount);

    /**
     * 获取文件的当前版本
     *
     * @param fileId 文件ID
     * @return 当前版本
     */
    CoFileVersionVo getCurrentVersion(Long fileId);

    /**
     * 获取文件的版本统计
     *
     * @param fileId 文件ID
     * @return 统计信息
     */
    Map<String, Object> getVersionStatistics(Long fileId);

    /**
     * 下载版本文件
     *
     * @param versionId 版本ID
     * @return 文件字节数组
     */
    byte[] downloadVersionFile(Long versionId);

}

