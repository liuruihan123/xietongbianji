package com.xmzs.coedit.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.domain.CoFileAccessRecord;
import com.xmzs.coedit.domain.bo.CoFileAccessRecordBo;
import com.xmzs.coedit.domain.vo.CoFileAccessRecordVo;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.mapper.CoFileAccessRecordMapper;
import com.xmzs.coedit.mapper.CoFileMapper;
import com.xmzs.coedit.service.ICoFileAccessRecordService;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件访问记录Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoFileAccessRecordServiceImpl implements ICoFileAccessRecordService {

    private final CoFileAccessRecordMapper baseMapper;
    private final CoFileMapper fileMapper;

    /**
     * 记录文件访问（使用Upsert，幂等且并发安全）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean recordAccess(CoFileAccessRecordBo bo) {
        // 查询文件信息获取工作空间ID
        CoFile file = fileMapper.selectById(bo.getFileId());
        if (file == null) {
            log.warn("文件不存在: fileId={}", bo.getFileId());
            return false;
        }

        // 设置默认访问类型
        Integer accessType = bo.getAccessType() != null ? bo.getAccessType() : 1;

        // 使用Upsert方法，自动处理插入或更新
        int result = baseMapper.upsertFileAccess(
            bo.getFileId(),
            bo.getUserId(),
            file.getWorkspaceId(),
            accessType,
            bo.getAccessDuration(),
            bo.getAccessIp(),
            bo.getUserAgent()
        );

        log.info("记录文件访问(Upsert): fileId={}, userId={}, accessType={}, result={}",
                 bo.getFileId(), bo.getUserId(), accessType, result);

        return result > 0;
    }

    /**
     * 记录文件访问(简化版，使用Upsert)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean recordAccess(Long fileId, Long userId, Integer accessType) {
        // 查询文件信息获取工作空间ID
        CoFile file = fileMapper.selectById(fileId);
        if (file == null) {
            log.warn("文件不存在: fileId={}", fileId);
            return false;
        }

        // 使用Upsert方法
        int result = baseMapper.upsertFileAccess(
            fileId,
            userId,
            file.getWorkspaceId(),
            accessType != null ? accessType : 1,
            null,  // accessDuration
            null,  // accessIp
            null   // userAgent
        );

        log.info("记录文件访问(Upsert): fileId={}, userId={}, accessType={}, result={}",
                 fileId, userId, accessType, result);

        return result > 0;
    }

    /**
     * 获取最近访问的文件列表(分页,已去重)
     * 使用last_accessed_at排序，每个文件只保留一条记录
     */
    @Override
    public TableDataInfo<CoFileAccessRecordVo> getRecentAccess(Long userId, Long workspaceId, PageQuery pageQuery) {
        // 构建查询条件 - 使用last_accessed_at排序
        LambdaQueryWrapper<CoFileAccessRecord> wrapper = Wrappers.lambdaQuery(CoFileAccessRecord.class)
            .eq(CoFileAccessRecord::getUserId, userId)
            .eq(ObjectUtil.isNotNull(workspaceId), CoFileAccessRecord::getWorkspaceId, workspaceId)
            .orderByDesc(CoFileAccessRecord::getLastAccessedAt);

        // 分页查询（由于已通过唯一索引去重，无需额外处理）
        Page<CoFileAccessRecordVo> page = baseMapper.selectVoPage(pageQuery.build(), wrapper);

        // 填充文件详细信息
        for (CoFileAccessRecordVo vo : page.getRecords()) {
            enrichFileInfo(vo);
        }

        return TableDataInfo.build(page);
    }

    /**
     * 获取最近访问的文件列表(不分页,已去重)
     * 使用last_accessed_at排序
     */
    @Override
    public List<CoFileAccessRecordVo> getRecentAccessList(Long userId, Long workspaceId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 20;
        }

        LambdaQueryWrapper<CoFileAccessRecord> wrapper = Wrappers.lambdaQuery(CoFileAccessRecord.class)
            .eq(CoFileAccessRecord::getUserId, userId)
            .eq(ObjectUtil.isNotNull(workspaceId), CoFileAccessRecord::getWorkspaceId, workspaceId)
            .orderByDesc(CoFileAccessRecord::getLastAccessedAt)
            .last("LIMIT " + limit);

        List<CoFileAccessRecordVo> records = baseMapper.selectVoList(wrapper);

        // 填充文件详细信息
        for (CoFileAccessRecordVo vo : records) {
            enrichFileInfo(vo);
        }

        return records;
    }

    /**
     * 获取访问统计
     */
    @Override
    public Map<String, Object> getAccessStatistics(Long userId, Long workspaceId) {
        LambdaQueryWrapper<CoFileAccessRecord> wrapper = Wrappers.lambdaQuery(CoFileAccessRecord.class)
            .eq(CoFileAccessRecord::getUserId, userId)
            .eq(ObjectUtil.isNotNull(workspaceId), CoFileAccessRecord::getWorkspaceId, workspaceId);

        List<CoFileAccessRecord> records = baseMapper.selectList(wrapper);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalAccess", records.size());
        statistics.put("uniqueFiles", records.stream().map(CoFileAccessRecord::getFileId).distinct().count());
        statistics.put("viewCount", records.stream().filter(r -> r.getAccessType() == 1).count());
        statistics.put("editCount", records.stream().filter(r -> r.getAccessType() == 2).count());
        statistics.put("downloadCount", records.stream().filter(r -> r.getAccessType() == 3).count());

        return statistics;
    }

    /**
     * 获取访问最多的文件
     */
    @Override
    public List<Map<String, Object>> getTopAccessedFiles(Long userId, Long workspaceId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        LambdaQueryWrapper<CoFileAccessRecord> wrapper = Wrappers.lambdaQuery(CoFileAccessRecord.class)
            .eq(CoFileAccessRecord::getUserId, userId)
            .eq(ObjectUtil.isNotNull(workspaceId), CoFileAccessRecord::getWorkspaceId, workspaceId);

        List<CoFileAccessRecord> records = baseMapper.selectList(wrapper);

        // 统计每个文件的访问次数
        Map<Long, Long> fileAccessCount = records.stream()
            .collect(Collectors.groupingBy(CoFileAccessRecord::getFileId, Collectors.counting()));

        // 排序并取前N个
        List<Map<String, Object>> topFiles = fileAccessCount.entrySet().stream()
            .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> {
                Map<String, Object> fileMap = new HashMap<>();
                fileMap.put("fileId", entry.getKey());
                fileMap.put("accessCount", entry.getValue());

                // 获取文件信息
                CoFileVo fileVo = fileMapper.selectVoById(entry.getKey());
                if (fileVo != null) {
                    fileMap.put("fileName", fileVo.getFileName());
                    fileMap.put("fileType", fileVo.getFileType());
                }

                return fileMap;
            })
            .collect(Collectors.toList());

        return topFiles;
    }

    /**
     * 填充文件详细信息
     */
    private void enrichFileInfo(CoFileAccessRecordVo vo) {
        if (vo.getFileId() != null) {
            CoFileVo fileVo = fileMapper.selectVoById(vo.getFileId());
            vo.setFileInfo(fileVo);
        }
    }

}

