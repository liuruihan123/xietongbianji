package com.xmzs.coedit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.domain.CoFileChange;
import com.xmzs.coedit.domain.CoFileHistory;
import com.xmzs.coedit.domain.bo.CoFileHistoryBo;
import com.xmzs.coedit.domain.vo.CoFileChangeVo;
import com.xmzs.coedit.domain.vo.CoFileHistoryVo;
import com.xmzs.coedit.mapper.CoFileChangeMapper;
import com.xmzs.coedit.mapper.CoFileHistoryMapper;
import com.xmzs.coedit.mapper.CoFileMapper;
import com.xmzs.coedit.service.ICoFileHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件历史Service实现
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CoFileHistoryServiceImpl implements ICoFileHistoryService {

    private final CoFileHistoryMapper historyMapper;
    private final CoFileChangeMapper changeMapper;
    private final CoFileMapper fileMapper;

    @Value("${file.upload.path:/upload}")
    private String uploadPath;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createHistory(CoFileHistoryBo historyBo) {
        // 查询文件信息
        CoFile file = fileMapper.selectById(historyBo.getFileId());
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }

        // 获取当前最大版本号
        Integer maxVersion = historyMapper.selectList(
            new LambdaQueryWrapper<CoFileHistory>()
                .eq(CoFileHistory::getFileId, historyBo.getFileId())
                .orderByDesc(CoFileHistory::getVersion)
                .last("LIMIT 1")
        ).stream().findFirst().map(CoFileHistory::getVersion).orElse(0);

        // 将之前的版本设置为非当前版本
        historyMapper.update(null,
            new LambdaUpdateWrapper<CoFileHistory>()
                .eq(CoFileHistory::getFileId, historyBo.getFileId())
                .set(CoFileHistory::getIsCurrent, false)
        );

        // 创建新版本
        CoFileHistory history = BeanUtil.copyProperties(historyBo, CoFileHistory.class);
        history.setVersion(maxVersion + 1);
        history.setIsCurrent(true);

        // 如果没有指定文档Key，使用文件ID和版本号生成
        if (StrUtil.isBlank(history.getDocumentKey())) {
            history.setDocumentKey(file.getId() + "_" + history.getVersion() + "_" + System.currentTimeMillis());
        }

        historyMapper.insert(history);
        return history.getId();
    }

    @Override
    public List<CoFileHistoryVo> getFileHistoryList(Long fileId) {
        List<CoFileHistory> historyList = historyMapper.selectList(
            new LambdaQueryWrapper<CoFileHistory>()
                .eq(CoFileHistory::getFileId, fileId)
                .orderByDesc(CoFileHistory::getVersion)
        );

        return historyList.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    public CoFileHistoryVo getHistoryDetail(Long historyId) {
        CoFileHistory history = historyMapper.selectById(historyId);
        if (history == null) {
            return null;
        }

        CoFileHistoryVo vo = convertToVo(history);

        // 获取变更列表
        List<CoFileChangeVo> changes = getVersionChanges(historyId);
        vo.setChanges(changes);

        return vo;
    }

    @Override
    public CoFileHistoryVo getHistoryByVersion(Long fileId, Integer version) {
        CoFileHistory history = historyMapper.selectOne(
            new LambdaQueryWrapper<CoFileHistory>()
                .eq(CoFileHistory::getFileId, fileId)
                .eq(CoFileHistory::getVersion, version)
        );

        return history != null ? convertToVo(history) : null;
    }

    @Override
    public CoFileHistoryVo getCurrentHistory(Long fileId) {
        CoFileHistory history = historyMapper.selectOne(
            new LambdaQueryWrapper<CoFileHistory>()
                .eq(CoFileHistory::getFileId, fileId)
                .eq(CoFileHistory::getIsCurrent, true)
        );

        return history != null ? convertToVo(history) : null;
    }

    @Override
    public Map<String, Object> getOnlyOfficeHistoryConfig(Long fileId) {
        List<CoFileHistoryVo> historyList = getFileHistoryList(fileId);

        Map<String, Object> config = new HashMap<>();
        
        // 当前版本号
        CoFileHistoryVo currentHistory = historyList.stream()
            .filter(CoFileHistoryVo::getIsCurrent)
            .findFirst()
            .orElse(null);
        
        if (currentHistory != null) {
            config.put("currentVersion", currentHistory.getVersion());
        }

        // 历史列表
        List<Map<String, Object>> history = new ArrayList<>();
        for (CoFileHistoryVo vo : historyList) {
            Map<String, Object> item = new HashMap<>();
            item.put("version", vo.getVersion());
            item.put("key", vo.getDocumentKey());
            item.put("created", vo.getCreateTime());
            
            // 用户信息
            Map<String, Object> user = new HashMap<>();
            user.put("id", String.valueOf(vo.getCreatorId()));
            user.put("name", vo.getCreatorName());
            item.put("user", user);

            // 如果有变更数据和服务器版本，添加到配置中
            if (StrUtil.isNotBlank(vo.getChangesUrl()) && vo.getServerVersion() != null) {
                item.put("serverVersion", vo.getServerVersion());
            }

            history.add(item);
        }
        config.put("history", history);

        return config;
    }

    @Override
    public Map<String, Object> getOnlyOfficeHistoryData(Long fileId, Integer version) {
        CoFileHistoryVo history = getHistoryByVersion(fileId, version);
        if (history == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "版本不存在");
            error.put("version", version);
            return error;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("version", history.getVersion());
        data.put("key", history.getDocumentKey());
        data.put("url", history.getFileUrl());
        
        CoFile file = fileMapper.selectById(fileId);
        if (file != null) {
            data.put("fileType", file.getFileType());
        }

        // 如果有变更数据URL，添加到配置中
        if (StrUtil.isNotBlank(history.getChangesUrl())) {
            data.put("changesUrl", history.getChangesUrl());

            // 获取上一个版本的信息
            CoFileHistoryVo previousHistory = getHistoryByVersion(fileId, version - 1);
            if (previousHistory != null) {
                Map<String, Object> previous = new HashMap<>();
                previous.put("key", previousHistory.getDocumentKey());
                previous.put("url", previousHistory.getFileUrl());
                if (file != null) {
                    previous.put("fileType", file.getFileType());
                }
                data.put("previous", previous);
            }
        }

        return data;
    }

    @Override
    public Map<String, Object> compareVersions(Long fileId, Integer version1, Integer version2) {
        CoFileHistoryVo history1 = getHistoryByVersion(fileId, version1);
        CoFileHistoryVo history2 = getHistoryByVersion(fileId, version2);

        if (history1 == null || history2 == null) {
            throw new RuntimeException("版本不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("version1", history1);
        result.put("version2", history2);

        // 获取两个版本之间的变更
        List<CoFileChangeVo> changes = changeMapper.selectList(
            new LambdaQueryWrapper<CoFileChange>()
                .eq(CoFileChange::getFileId, fileId)
                .between(CoFileChange::getVersion, Math.min(version1, version2), Math.max(version1, version2))
                .orderByAsc(CoFileChange::getVersion)
        ).stream().map(this::convertChangeToVo).collect(Collectors.toList());

        result.put("changes", changes);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean restoreVersion(Long fileId, Integer version, Long userId) {
        // 获取要恢复的版本
        CoFileHistoryVo history = getHistoryByVersion(fileId, version);
        if (history == null) {
            throw new RuntimeException("版本不存在");
        }

        // 复制历史版本文件到当前文件
        CoFile file = fileMapper.selectById(fileId);
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }

        try {
            // 复制文件
            File sourceFile = new File(history.getFileUrl());
            File targetFile = new File(file.getFilePath());
            FileUtil.copy(sourceFile, targetFile, true);

            // 创建新的历史记录
            CoFileHistoryBo historyBo = new CoFileHistoryBo();
            historyBo.setFileId(fileId);
            historyBo.setFilePath(file.getFilePath());
            historyBo.setFileUrl(file.getFileUrl());
            historyBo.setFileSize(file.getFileSize());
            historyBo.setChangeType(5); // 5-恢复
            historyBo.setChangeDescription("恢复到版本" + version);
            historyBo.setCreatorId(userId);

            createHistory(historyBo);

            return true;
        } catch (Exception e) {
            log.error("恢复版本失败", e);
            throw new RuntimeException("恢复版本失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteHistory(Long historyId) {
        CoFileHistory history = historyMapper.selectById(historyId);
        if (history == null) {
            return false;
        }

        // 不允许删除当前版本
        if (history.getIsCurrent()) {
            throw new RuntimeException("不能删除当前版本");
        }

        // 删除变更记录
        changeMapper.delete(
            new LambdaQueryWrapper<CoFileChange>()
                .eq(CoFileChange::getHistoryId, historyId)
        );

        // 删除历史文件
        if (StrUtil.isNotBlank(history.getFilePath())) {
            FileUtil.del(history.getFilePath());
        }

        // 删除变更数据文件
        if (StrUtil.isNotBlank(history.getChangesPath())) {
            FileUtil.del(history.getChangesPath());
        }

        // 删除历史记录
        return historyMapper.deleteById(historyId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteHistory(Long[] historyIds) {
        for (Long historyId : historyIds) {
            deleteHistory(historyId);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteFileHistory(Long fileId) {
        List<CoFileHistory> historyList = historyMapper.selectList(
            new LambdaQueryWrapper<CoFileHistory>()
                .eq(CoFileHistory::getFileId, fileId)
                .eq(CoFileHistory::getIsCurrent, false)
        );

        int count = 0;
        for (CoFileHistory history : historyList) {
            if (deleteHistory(history.getId())) {
                count++;
            }
        }

        return count;
    }

    @Override
    public List<CoFileChangeVo> getFileChanges(Long fileId) {
        List<CoFileChange> changes = changeMapper.selectList(
            new LambdaQueryWrapper<CoFileChange>()
                .eq(CoFileChange::getFileId, fileId)
                .orderByDesc(CoFileChange::getVersion)
        );

        return changes.stream().map(this::convertChangeToVo).collect(Collectors.toList());
    }

    @Override
    public List<CoFileChangeVo> getVersionChanges(Long historyId) {
        List<CoFileChange> changes = changeMapper.selectList(
            new LambdaQueryWrapper<CoFileChange>()
                .eq(CoFileChange::getHistoryId, historyId)
                .orderByAsc(CoFileChange::getCreateTime)
        );

        return changes.stream().map(this::convertChangeToVo).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveChanges(Long historyId, String changesData) {
        CoFileHistory history = historyMapper.selectById(historyId);
        if (history == null) {
            return false;
        }

        try {
            // 解析变更数据
            Map<String, Object> changesMap = JSONUtil.toBean(changesData, Map.class);
            
            // 保存变更记录
            if (changesMap.containsKey("changes")) {
                List<Map<String, Object>> changesList = (List<Map<String, Object>>) changesMap.get("changes");
                
                for (Map<String, Object> changeItem : changesList) {
                    CoFileChange change = new CoFileChange();
                    change.setHistoryId(historyId);
                    change.setFileId(history.getFileId());
                    change.setVersion(history.getVersion());
                    change.setChangeData(JSONUtil.toJsonStr(changeItem));
                    
                    // 提取变更信息
                    if (changeItem.containsKey("type")) {
                        change.setChangeType(String.valueOf(changeItem.get("type")));
                    }
                    if (changeItem.containsKey("user")) {
                        Map<String, Object> user = (Map<String, Object>) changeItem.get("user");
                        if (user.containsKey("id")) {
                            change.setUserId(Long.valueOf(String.valueOf(user.get("id"))));
                        }
                        if (user.containsKey("name")) {
                            change.setUserName(String.valueOf(user.get("name")));
                        }
                    }
                    
                    changeMapper.insert(change);
                }
            }

            return true;
        } catch (Exception e) {
            log.error("保存变更数据失败", e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanOldHistory(Long fileId, Integer keepVersions) {
        List<CoFileHistory> historyList = historyMapper.selectList(
            new LambdaQueryWrapper<CoFileHistory>()
                .eq(CoFileHistory::getFileId, fileId)
                .eq(CoFileHistory::getIsCurrent, false)
                .orderByDesc(CoFileHistory::getVersion)
        );

        if (historyList.size() <= keepVersions) {
            return 0;
        }

        // 保留最近的N个版本，删除其他版本
        List<CoFileHistory> toDelete = historyList.subList(keepVersions, historyList.size());
        int count = 0;
        for (CoFileHistory history : toDelete) {
            if (deleteHistory(history.getId())) {
                count++;
            }
        }

        return count;
    }

    /**
     * 转换为Vo对象
     */
    private CoFileHistoryVo convertToVo(CoFileHistory history) {
        CoFileHistoryVo vo = BeanUtil.copyProperties(history, CoFileHistoryVo.class);
        
        // 设置变更类型描述
        if (history.getChangeType() != null) {
            String[] types = {"", "创建", "编辑", "重命名", "移动", "恢复"};
            if (history.getChangeType() > 0 && history.getChangeType() < types.length) {
                vo.setChangeTypeDesc(types[history.getChangeType()]);
            }
        }

        // 设置OnlyOffice用户对象
        if (history.getCreatorId() != null) {
            CoFileHistoryVo.HistoryUser user = new CoFileHistoryVo.HistoryUser();
            user.setId(String.valueOf(history.getCreatorId()));
            user.setName(history.getCreatorName());
            vo.setUser(user);
        }

        return vo;
    }

    /**
     * 转换变更为Vo对象
     */
    private CoFileChangeVo convertChangeToVo(CoFileChange change) {
        return BeanUtil.copyProperties(change, CoFileChangeVo.class);
    }
}

