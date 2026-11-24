package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.bo.CoFileHistoryBo;
import com.xmzs.coedit.domain.vo.CoFileChangeVo;
import com.xmzs.coedit.domain.vo.CoFileHistoryVo;

import java.util.List;
import java.util.Map;

/**
 * 文件历史Service接口
 *
 * @author zuleed
 * @date 2025-11-05
 */
public interface ICoFileHistoryService {

    /**
     * 创建文件历史记录
     *
     * @param historyBo 历史业务对象
     * @return 历史ID
     */
    Long createHistory(CoFileHistoryBo historyBo);

    /**
     * 获取文件历史列表
     *
     * @param fileId 文件ID
     * @return 历史列表
     */
    List<CoFileHistoryVo> getFileHistoryList(Long fileId);

    /**
     * 获取文件历史详情
     *
     * @param historyId 历史ID
     * @return 历史详情
     */
    CoFileHistoryVo getHistoryDetail(Long historyId);

    /**
     * 获取指定版本的历史
     *
     * @param fileId  文件ID
     * @param version 版本号
     * @return 历史详情
     */
    CoFileHistoryVo getHistoryByVersion(Long fileId, Integer version);

    /**
     * 获取当前版本的历史
     *
     * @param fileId 文件ID
     * @return 历史详情
     */
    CoFileHistoryVo getCurrentHistory(Long fileId);

    /**
     * 获取OnlyOffice历史配置
     *
     * @param fileId 文件ID
     * @return OnlyOffice历史配置
     */
    Map<String, Object> getOnlyOfficeHistoryConfig(Long fileId);

    /**
     * 获取OnlyOffice历史数据
     *
     * @param fileId  文件ID
     * @param version 版本号
     * @return OnlyOffice历史数据
     */
    Map<String, Object> getOnlyOfficeHistoryData(Long fileId, Integer version);

    /**
     * 对比两个版本
     *
     * @param fileId      文件ID
     * @param version1    版本1
     * @param version2    版本2
     * @return 对比结果
     */
    Map<String, Object> compareVersions(Long fileId, Integer version1, Integer version2);

    /**
     * 恢复到指定版本
     *
     * @param fileId  文件ID
     * @param version 版本号
     * @param userId  用户ID
     * @return 是否成功
     */
    Boolean restoreVersion(Long fileId, Integer version, Long userId);

    /**
     * 删除文件历史
     *
     * @param historyId 历史ID
     * @return 是否成功
     */
    Boolean deleteHistory(Long historyId);

    /**
     * 批量删除文件历史
     *
     * @param historyIds 历史ID数组
     * @return 是否成功
     */
    Boolean batchDeleteHistory(Long[] historyIds);

    /**
     * 删除文件的所有历史
     *
     * @param fileId 文件ID
     * @return 删除数量
     */
    Integer deleteFileHistory(Long fileId);

    /**
     * 获取文件的变更列表
     *
     * @param fileId 文件ID
     * @return 变更列表
     */
    List<CoFileChangeVo> getFileChanges(Long fileId);

    /**
     * 获取版本的变更列表
     *
     * @param historyId 历史ID
     * @return 变更列表
     */
    List<CoFileChangeVo> getVersionChanges(Long historyId);

    /**
     * 保存变更数据
     *
     * @param historyId   历史ID
     * @param changesData 变更数据（JSON）
     * @return 是否成功
     */
    Boolean saveChanges(Long historyId, String changesData);

    /**
     * 清理过期历史（保留最近N个版本）
     *
     * @param fileId       文件ID
     * @param keepVersions 保留版本数
     * @return 清理数量
     */
    Integer cleanOldHistory(Long fileId, Integer keepVersions);
}

