package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.bo.CoFileSearchBo;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.domain.vo.CoSearchHistoryVo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Map;

/**
 * 文件搜索Service接口
 *
 * @author zuleed
 * @date 2025-11-05
 */
public interface ICoFileSearchService {

    /**
     * 搜索文件
     *
     * @param searchBo 搜索条件
     * @param pageQuery 分页参数
     * @param userId 用户ID
     * @return 文件列表
     */
    TableDataInfo<CoFileVo> searchFiles(CoFileSearchBo searchBo, PageQuery pageQuery, Long userId);

    /**
     * 快速搜索（仅文件名）
     *
     * @param keyword 关键词
     * @param workspaceId 工作空间ID
     * @param userId 用户ID
     * @return 文件列表（最多10条）
     */
    List<CoFileVo> quickSearch(String keyword, Long workspaceId, Long userId);

    /**
     * 高级搜索
     *
     * @param searchBo 搜索条件
     * @param pageQuery 分页参数
     * @param userId 用户ID
     * @return 文件列表
     */
    TableDataInfo<CoFileVo> advancedSearch(CoFileSearchBo searchBo, PageQuery pageQuery, Long userId);

    /**
     * 获取搜索历史
     *
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 搜索历史列表
     */
    List<CoSearchHistoryVo> getSearchHistory(Long userId, Integer limit);

    /**
     * 清空搜索历史
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean clearSearchHistory(Long userId);

    /**
     * 删除搜索历史
     *
     * @param historyId 历史ID
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean deleteSearchHistory(Long historyId, Long userId);

    /**
     * 获取热门搜索关键词
     *
     * @param workspaceId 工作空间ID
     * @param limit 数量限制
     * @return 热门关键词列表
     */
    List<Map<String, Object>> getHotKeywords(Long workspaceId, Integer limit);

    /**
     * 获取搜索统计
     *
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getSearchStatistics(Long userId);

    /**
     * 获取搜索建议
     *
     * @param keyword 关键词前缀
     * @param workspaceId 工作空间ID
     * @param limit 数量限制
     * @return 建议列表
     */
    List<String> getSearchSuggestions(String keyword, Long workspaceId, Integer limit);
}

