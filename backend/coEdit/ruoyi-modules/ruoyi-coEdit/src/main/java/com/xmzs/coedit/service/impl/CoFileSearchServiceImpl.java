package com.xmzs.coedit.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.constant.WorkspaceConstants;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.domain.CoFolder;
import com.xmzs.coedit.domain.CoSearchHistory;
import com.xmzs.coedit.domain.CoWorkspace;
import com.xmzs.coedit.domain.CoWorkspaceMember;
import com.xmzs.coedit.domain.bo.CoFileSearchBo;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.domain.vo.CoSearchHistoryVo;
import com.xmzs.coedit.domain.vo.CoWorkspaceVo;
import com.xmzs.coedit.mapper.CoFileMapper;
import com.xmzs.coedit.mapper.CoFolderMapper;
import com.xmzs.coedit.mapper.CoSearchHistoryMapper;
import com.xmzs.coedit.mapper.CoWorkspaceMapper;
import com.xmzs.coedit.mapper.CoWorkspaceMemberMapper;
import com.xmzs.coedit.service.ICoFileSearchService;
import com.xmzs.common.core.utils.ServletUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.satoken.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件搜索Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoFileSearchServiceImpl implements ICoFileSearchService {

    private final CoFileMapper fileMapper;
    private final CoFolderMapper folderMapper;
    private final CoSearchHistoryMapper searchHistoryMapper;
    private final CoWorkspaceMapper workspaceMapper;
    private final CoWorkspaceMemberMapper workspaceMemberMapper;

    /**
     * 搜索文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TableDataInfo<CoFileVo> searchFiles(CoFileSearchBo searchBo, PageQuery pageQuery, Long userId) {
        long startTime = System.currentTimeMillis();

        // 构建查询条件
        LambdaQueryWrapper<CoFile> wrapper = buildSearchWrapper(searchBo, userId);

        // 执行查询
        Page<CoFileVo> result = fileMapper.selectVoPage(pageQuery.build(), wrapper);

        // 记录搜索历史
        long searchTime = System.currentTimeMillis() - startTime;
        saveSearchHistory(searchBo, userId, result.getTotal(), searchTime);

        return TableDataInfo.build(result);
    }

    /**
     * 快速搜索（仅文件名）
     */
    @Override
    public List<CoFileVo> quickSearch(String keyword, Long workspaceId, Long userId) {
        if (StrUtil.isBlank(keyword)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<CoFile> wrapper = Wrappers.lambdaQuery(CoFile.class)
            .like(CoFile::getFileName, keyword)
            .eq(workspaceId != null, CoFile::getWorkspaceId, workspaceId)
            .eq(CoFile::getIsDeleted, false)
            .orderByDesc(CoFile::getUpdateTime)
            .last("LIMIT 10");

        // 如果没有指定工作空间，只搜索用户有权限访问的工作空间
        if (workspaceId == null) {
            List<Long> accessibleWorkspaceIds = getAccessibleWorkspaceIds(userId);
            if (CollUtil.isNotEmpty(accessibleWorkspaceIds)) {
                wrapper.in(CoFile::getWorkspaceId, accessibleWorkspaceIds);
            } else {
                return Collections.emptyList();
            }
        }

        return fileMapper.selectVoList(wrapper);
    }

    /**
     * 高级搜索
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TableDataInfo<CoFileVo> advancedSearch(CoFileSearchBo searchBo, PageQuery pageQuery, Long userId) {
        searchBo.setSearchType(3); // 高级搜索
        return searchFiles(searchBo, pageQuery, userId);
    }

    /**
     * 获取用户有权限访问的所有工作空间ID
     */
    private List<Long> getAccessibleWorkspaceIds(Long userId) {
        List<Long> accessibleIds = new ArrayList<>();

        // 1. 用户作为所有者的工作空间
        List<CoWorkspaceVo> ownedWorkspaces = workspaceMapper.selectVoList(
            Wrappers.lambdaQuery(CoWorkspace.class)
                .eq(CoWorkspace::getOwnerId, userId)
                .eq(CoWorkspace::getStatus, WorkspaceConstants.WorkspaceStatus.NORMAL)
        );
        accessibleIds.addAll(ownedWorkspaces.stream().map(CoWorkspaceVo::getId).collect(Collectors.toList()));

        // 2. 用户作为成员的工作空间
        List<CoWorkspaceMember> memberships = workspaceMemberMapper.selectList(
            Wrappers.lambdaQuery(CoWorkspaceMember.class)
                .eq(CoWorkspaceMember::getUserId, userId)
                .eq(CoWorkspaceMember::getStatus, WorkspaceConstants.MemberStatus.NORMAL)
        );
        accessibleIds.addAll(memberships.stream().map(CoWorkspaceMember::getWorkspaceId).collect(Collectors.toList()));

        // 3. 同部门的部门工作空间
        Long userDeptId = null;
        try {
            userDeptId = LoginHelper.getDeptId();
        } catch (Exception e) {
            log.warn("获取用户部门ID失败", e);
        }

        if (userDeptId != null) {
            List<CoWorkspaceVo> deptWorkspaces = workspaceMapper.selectVoList(
                Wrappers.lambdaQuery(CoWorkspace.class)
                    .eq(CoWorkspace::getWorkspaceType, 2)  // 2-部门空间
                    .eq(CoWorkspace::getDeptId, userDeptId)
                    .eq(CoWorkspace::getStatus, WorkspaceConstants.WorkspaceStatus.NORMAL)
            );
            accessibleIds.addAll(deptWorkspaces.stream().map(CoWorkspaceVo::getId).collect(Collectors.toList()));
        }

        // 去重
        return accessibleIds.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 构建搜索查询条件
     */
    private LambdaQueryWrapper<CoFile> buildSearchWrapper(CoFileSearchBo searchBo, Long userId) {
        LambdaQueryWrapper<CoFile> wrapper = Wrappers.lambdaQuery();

        // 只搜索用户有权限访问的工作空间中的文件
        List<Long> accessibleWorkspaceIds = getAccessibleWorkspaceIds(userId);
        if (CollUtil.isNotEmpty(accessibleWorkspaceIds)) {
            wrapper.in(CoFile::getWorkspaceId, accessibleWorkspaceIds);
        } else {
            // 用户没有可访问的工作空间，返回空结果
            wrapper.eq(CoFile::getId, -1);
            return wrapper;
        }

        // 关键词搜索（文件名）
        if (StrUtil.isNotBlank(searchBo.getKeyword())) {
            wrapper.like(CoFile::getFileName, searchBo.getKeyword());
        }

        // 工作空间
        if (searchBo.getWorkspaceId() != null) {
            wrapper.eq(CoFile::getWorkspaceId, searchBo.getWorkspaceId());
        }

        // 文件夹（包含子文件夹）
        if (searchBo.getFolderId() != null) {
            if (Boolean.TRUE.equals(searchBo.getIncludeSubFolders())) {
                // 获取所有子文件夹ID
                List<Long> folderIds = getAllSubFolderIds(searchBo.getFolderId());
                folderIds.add(searchBo.getFolderId());
                wrapper.in(CoFile::getFolderId, folderIds);
            } else {
                wrapper.eq(CoFile::getFolderId, searchBo.getFolderId());
            }
        }

        // 文件类型
        if (StrUtil.isNotBlank(searchBo.getFileType())) {
            wrapper.eq(CoFile::getFileType, searchBo.getFileType());
        }

        // 文件大小范围
        if (searchBo.getMinSize() != null) {
            wrapper.ge(CoFile::getFileSize, searchBo.getMinSize());
        }
        if (searchBo.getMaxSize() != null) {
            wrapper.le(CoFile::getFileSize, searchBo.getMaxSize());
        }

        // 时间范围
        if (searchBo.getStartTime() != null) {
            wrapper.ge(CoFile::getCreateTime, searchBo.getStartTime());
        }
        if (searchBo.getEndTime() != null) {
            wrapper.le(CoFile::getCreateTime, searchBo.getEndTime());
        }

        // 创建者
        if (searchBo.getCreatorId() != null) {
            wrapper.eq(CoFile::getCreateBy, searchBo.getCreatorId());
        }

        // 未删除的文件
        wrapper.eq(CoFile::getIsDeleted, false);

        // 排序
        applySorting(wrapper, searchBo);

        return wrapper;
    }

    /**
     * 应用排序
     */
    private void applySorting(LambdaQueryWrapper<CoFile> wrapper, CoFileSearchBo searchBo) {
        String sortField = searchBo.getSortField();
        String sortOrder = searchBo.getSortOrder();
        boolean isAsc = "asc".equalsIgnoreCase(sortOrder);

        switch (sortField) {
            case "name":
                wrapper.orderBy(true, isAsc, CoFile::getFileName);
                break;
            case "size":
                wrapper.orderBy(true, isAsc, CoFile::getFileSize);
                break;
            case "createTime":
                wrapper.orderBy(true, isAsc, CoFile::getCreateTime);
                break;
            case "updateTime":
            default:
                wrapper.orderBy(true, isAsc, CoFile::getUpdateTime);
                break;
        }
    }

    /**
     * 获取所有子文件夹ID（递归）
     */
    private List<Long> getAllSubFolderIds(Long folderId) {
        List<Long> result = new ArrayList<>();
        collectSubFolderIds(folderId, result);
        return result;
    }

    /**
     * 递归收集子文件夹ID
     */
    private void collectSubFolderIds(Long folderId, List<Long> result) {
        List<CoFolder> children = folderMapper.selectList(
            Wrappers.lambdaQuery(CoFolder.class)
                .eq(CoFolder::getParentId, folderId)
        );

        for (CoFolder child : children) {
            result.add(child.getId());
            collectSubFolderIds(child.getId(), result);
        }
    }

    /**
     * 保存搜索历史
     */
    private void saveSearchHistory(CoFileSearchBo searchBo, Long userId, long resultCount, long searchTime) {
        try {
            CoSearchHistory history = new CoSearchHistory();
            history.setUserId(userId);
            history.setWorkspaceId(searchBo.getWorkspaceId());
            history.setKeyword(searchBo.getKeyword());
            history.setSearchType(searchBo.getSearchType());
            history.setFileType(searchBo.getFileType());
            history.setFolderId(searchBo.getFolderId());
            history.setMinSize(searchBo.getMinSize());
            history.setMaxSize(searchBo.getMaxSize());
            history.setStartTime(searchBo.getStartTime());
            history.setEndTime(searchBo.getEndTime());
            history.setCreatorId(searchBo.getCreatorId());
            history.setResultCount((int) resultCount);
            history.setSearchTime((int) searchTime);
            history.setIpAddress(ServletUtils.getClientIP());
            history.setUserAgent(ServletUtils.getRequest().getHeader("User-Agent"));

            searchHistoryMapper.insert(history);
        } catch (Exception e) {
            log.error("保存搜索历史失败", e);
        }
    }

    /**
     * 获取搜索历史
     */
    @Override
    public List<CoSearchHistoryVo> getSearchHistory(Long userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        List<CoSearchHistoryVo> list = searchHistoryMapper.selectVoList(
            Wrappers.lambdaQuery(CoSearchHistory.class)
                .eq(CoSearchHistory::getUserId, userId)
                .orderByDesc(CoSearchHistory::getCreateTime)
                .last("LIMIT " + limit)
        );

        // 设置搜索类型描述
        list.forEach(vo -> {
            vo.setSearchTypeDesc(getSearchTypeDesc(vo.getSearchType()));
        });

        return list;
    }

    /**
     * 获取搜索类型描述
     */
    private String getSearchTypeDesc(Integer searchType) {
        if (searchType == null) {
            return "未知";
        }
        switch (searchType) {
            case 1:
                return "文件名搜索";
            case 2:
                return "内容搜索";
            case 3:
                return "高级搜索";
            default:
                return "未知";
        }
    }

    /**
     * 清空搜索历史
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean clearSearchHistory(Long userId) {
        return searchHistoryMapper.delete(
            Wrappers.lambdaQuery(CoSearchHistory.class)
                .eq(CoSearchHistory::getUserId, userId)
        ) >= 0;
    }

    /**
     * 删除搜索历史
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSearchHistory(Long historyId, Long userId) {
        return searchHistoryMapper.delete(
            Wrappers.lambdaQuery(CoSearchHistory.class)
                .eq(CoSearchHistory::getId, historyId)
                .eq(CoSearchHistory::getUserId, userId)
        ) > 0;
    }

    /**
     * 获取热门搜索关键词
     */
    @Override
    public List<Map<String, Object>> getHotKeywords(Long workspaceId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        // 查询最近7天的搜索记录
        Date sevenDaysAgo = new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L);

        List<CoSearchHistory> histories = searchHistoryMapper.selectList(
            Wrappers.lambdaQuery(CoSearchHistory.class)
                .eq(workspaceId != null, CoSearchHistory::getWorkspaceId, workspaceId)
                .ge(CoSearchHistory::getCreateTime, sevenDaysAgo)
                .isNotNull(CoSearchHistory::getKeyword)
        );

        // 统计关键词频率
        Map<String, Long> keywordCount = histories.stream()
            .filter(h -> StrUtil.isNotBlank(h.getKeyword()))
            .collect(Collectors.groupingBy(
                CoSearchHistory::getKeyword,
                Collectors.counting()
            ));

        // 排序并取前N个
        return keywordCount.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> {
                Map<String, Object> map = new HashMap<>();
                map.put("keyword", entry.getKey());
                map.put("count", entry.getValue());
                return map;
            })
            .collect(Collectors.toList());
    }

    /**
     * 获取搜索统计
     */
    @Override
    public Map<String, Object> getSearchStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();

        // 总搜索次数
        Long totalCount = searchHistoryMapper.selectCount(
            Wrappers.lambdaQuery(CoSearchHistory.class)
                .eq(CoSearchHistory::getUserId, userId)
        );
        statistics.put("totalCount", totalCount);

        // 今天搜索次数
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date todayStart = calendar.getTime();

        Long todayCount = searchHistoryMapper.selectCount(
            Wrappers.lambdaQuery(CoSearchHistory.class)
                .eq(CoSearchHistory::getUserId, userId)
                .ge(CoSearchHistory::getCreateTime, todayStart)
        );
        statistics.put("todayCount", todayCount);

        // 最近7天搜索次数
        Date sevenDaysAgo = new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L);
        Long weekCount = searchHistoryMapper.selectCount(
            Wrappers.lambdaQuery(CoSearchHistory.class)
                .eq(CoSearchHistory::getUserId, userId)
                .ge(CoSearchHistory::getCreateTime, sevenDaysAgo)
        );
        statistics.put("weekCount", weekCount);

        // 按搜索类型统计
        List<CoSearchHistory> histories = searchHistoryMapper.selectList(
            Wrappers.lambdaQuery(CoSearchHistory.class)
                .eq(CoSearchHistory::getUserId, userId)
        );

        Map<Integer, Long> typeCount = histories.stream()
            .collect(Collectors.groupingBy(
                CoSearchHistory::getSearchType,
                Collectors.counting()
            ));
        statistics.put("typeCount", typeCount);

        // 平均搜索耗时
        Double avgSearchTime = histories.stream()
            .filter(h -> h.getSearchTime() != null)
            .mapToInt(CoSearchHistory::getSearchTime)
            .average()
            .orElse(0.0);
        statistics.put("avgSearchTime", Math.round(avgSearchTime));

        // 平均结果数量
        Double avgResultCount = histories.stream()
            .filter(h -> h.getResultCount() != null)
            .mapToInt(CoSearchHistory::getResultCount)
            .average()
            .orElse(0.0);
        statistics.put("avgResultCount", Math.round(avgResultCount));

        return statistics;
    }

    /**
     * 获取搜索建议
     */
    @Override
    public List<String> getSearchSuggestions(String keyword, Long workspaceId, Integer limit) {
        if (StrUtil.isBlank(keyword)) {
            return Collections.emptyList();
        }

        if (limit == null || limit <= 0) {
            limit = 5;
        }

        // 从历史搜索中获取建议
        List<CoSearchHistory> histories = searchHistoryMapper.selectList(
            Wrappers.lambdaQuery(CoSearchHistory.class)
                .like(CoSearchHistory::getKeyword, keyword)
                .eq(workspaceId != null, CoSearchHistory::getWorkspaceId, workspaceId)
                .orderByDesc(CoSearchHistory::getCreateTime)
                .last("LIMIT " + (limit * 2))
        );

        // 去重并限制数量
        List<String> suggestions = histories.stream()
            .map(CoSearchHistory::getKeyword)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .limit(limit)
            .collect(Collectors.toList());

        // 如果历史建议不足，从文件名中获取
        if (suggestions.size() < limit) {
            List<CoFileVo> files = fileMapper.selectVoList(
                Wrappers.lambdaQuery(CoFile.class)
                    .like(CoFile::getFileName, keyword)
                    .eq(workspaceId != null, CoFile::getWorkspaceId, workspaceId)
                    .eq(CoFile::getIsDeleted, false)
                    .orderByDesc(CoFile::getUpdateTime)
                    .last("LIMIT " + (limit - suggestions.size()))
            );

            files.stream()
                .map(CoFileVo::getFileName)
                .filter(name -> !suggestions.contains(name))
                .forEach(suggestions::add);
        }

        return suggestions;
    }

}

