package com.xmzs.coedit.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.domain.CoAccessRecord;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.domain.CoFolder;
import com.xmzs.coedit.domain.CoWorkspace;
import com.xmzs.coedit.domain.vo.CoAccessRecordVo;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.domain.vo.CoFolderVo;
import com.xmzs.coedit.domain.vo.CoWorkspaceVo;
import com.xmzs.coedit.mapper.CoAccessRecordMapper;
import com.xmzs.coedit.mapper.CoFileMapper;
import com.xmzs.coedit.mapper.CoFolderMapper;
import com.xmzs.coedit.mapper.CoWorkspaceMapper;
import com.xmzs.coedit.service.ICoAccessRecordService;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 访问记录Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoAccessRecordServiceImpl implements ICoAccessRecordService {

    private final CoAccessRecordMapper baseMapper;
    private final CoWorkspaceMapper workspaceMapper;
    private final CoFolderMapper folderMapper;
    private final CoFileMapper fileMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 通用记录访问方法（使用Upsert，幂等且并发安全）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean recordAccess(Long userId, Integer targetType, Long targetId,
                               Long workspaceId, Integer accessType,
                               Integer accessDuration, String accessIp, String userAgent) {
        log.info("记录访问(Upsert): userId={}, targetType={}, targetId={}, workspaceId={}",
                userId, targetType, targetId, workspaceId);

        // 使用Upsert方法，自动处理插入或更新
        int result = baseMapper.upsertAccess(
            targetType,
            targetId,
            userId,
            workspaceId,
            accessType != null ? accessType : 1,
            accessDuration,
            accessIp,
            userAgent
        );

        log.debug("记录访问结果: result={}", result);
        return result > 0;
    }

    /**
     * 记录工作空间访问
     */
    @Override
    public Boolean recordWorkspaceAccess(Long userId, Long workspaceId) {
        return recordAccess(userId, 1, workspaceId, null, 1, null, null, null);
    }

    /**
     * 记录文件夹访问
     */
    @Override
    public Boolean recordFolderAccess(Long userId, Long folderId, Long workspaceId) {
        return recordAccess(userId, 2, folderId, workspaceId, 1, null, null, null);
    }

    /**
     * 记录文件访问
     */
    @Override
    public Boolean recordFileAccess(Long userId, Long fileId, Long workspaceId, Integer accessType) {
        return recordAccess(userId, 3, fileId, workspaceId, accessType, null, null, null);
    }

    /**
     * 获取最近访问记录(分页,已去重)
     * 使用last_accessed_at排序，每个目标只保留一条记录
     */
    @Override
    public TableDataInfo<CoAccessRecordVo> getRecentAccess(Long userId, Long workspaceId, PageQuery pageQuery) {
        log.info("获取最近访问记录(分页): userId={}, workspaceId={}", userId, workspaceId);

        LambdaQueryWrapper<CoAccessRecord> wrapper = Wrappers.lambdaQuery(CoAccessRecord.class)
            .eq(CoAccessRecord::getUserId, userId)
            .orderByDesc(CoAccessRecord::getLastAccessedAt);

        if (workspaceId != null) {
            wrapper.and(w -> w.eq(CoAccessRecord::getWorkspaceId, workspaceId)
                             .or()
                             .isNull(CoAccessRecord::getWorkspaceId));
        }

        // 先查询实体分页
        Page<CoAccessRecord> entityPage = baseMapper.selectPage(pageQuery.build(), wrapper);

        // 手动转换为 Vo 分页
        Page<CoAccessRecordVo> page = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<CoAccessRecordVo> voList = new ArrayList<>();

        for (CoAccessRecord entity : entityPage.getRecords()) {
            CoAccessRecordVo vo = new CoAccessRecordVo();
            vo.setId(entity.getId());
            vo.setTargetType(entity.getTargetType());
            vo.setTargetId(entity.getTargetId());
            vo.setUserId(entity.getUserId());
            vo.setWorkspaceId(entity.getWorkspaceId());
            vo.setAccessTime(entity.getAccessTime());
            vo.setAccessType(entity.getAccessType());
            vo.setAccessDuration(entity.getAccessDuration());
            vo.setAccessIp(entity.getAccessIp());
            vo.setUserAgent(entity.getUserAgent());
            vo.setCreateTime(entity.getCreateTime());

            // 填充目标详细信息
            enrichTargetInfo(vo);

            voList.add(vo);
        }

        page.setRecords(voList);
        return TableDataInfo.build(page);
    }

    /**
     * 获取最近访问记录列表(不分页,已去重,最多50条)
     * 使用last_accessed_at排序
     */
    @Override
    public List<CoAccessRecordVo> getRecentAccessList(Long userId, Long workspaceId, Integer limit) {
        log.info("获取最近访问记录列表: userId={}, workspaceId={}, limit={}", userId, workspaceId, limit);

        if (limit == null || limit <= 0) {
            limit = 20;
        }
        if (limit > 50) {
            limit = 50;  // 最多50条
        }

        LambdaQueryWrapper<CoAccessRecord> wrapper = Wrappers.lambdaQuery(CoAccessRecord.class)
            .eq(CoAccessRecord::getUserId, userId)
            .orderByDesc(CoAccessRecord::getLastAccessedAt)
            .last("LIMIT " + limit);

        if (workspaceId != null) {
            wrapper.and(w -> w.eq(CoAccessRecord::getWorkspaceId, workspaceId)
                             .or()
                             .isNull(CoAccessRecord::getWorkspaceId));
        }

        // 先查询实体列表
        List<CoAccessRecord> entityList = baseMapper.selectList(wrapper);

        // 手动转换为 Vo 列表
        List<CoAccessRecordVo> records = new ArrayList<>();
        for (CoAccessRecord entity : entityList) {
            CoAccessRecordVo vo = new CoAccessRecordVo();
            vo.setId(entity.getId());
            vo.setTargetType(entity.getTargetType());
            vo.setTargetId(entity.getTargetId());
            vo.setUserId(entity.getUserId());
            vo.setWorkspaceId(entity.getWorkspaceId());
            vo.setAccessTime(entity.getAccessTime());
            vo.setAccessType(entity.getAccessType());
            vo.setAccessDuration(entity.getAccessDuration());
            vo.setAccessIp(entity.getAccessIp());
            vo.setUserAgent(entity.getUserAgent());
            vo.setCreateTime(entity.getCreateTime());

            // 填充目标详细信息
            enrichTargetInfo(vo);

            records.add(vo);
        }

        log.info("获取到{}条最近访问记录", records.size());
        return records;
    }

    /**
     * 获取访问统计信息
     */
    @Override
    public Map<String, Object> getAccessStatistics(Long userId, Long workspaceId) {
        log.info("获取访问统计: userId={}, workspaceId={}", userId, workspaceId);

        LambdaQueryWrapper<CoAccessRecord> wrapper = Wrappers.lambdaQuery(CoAccessRecord.class)
            .eq(CoAccessRecord::getUserId, userId);

        if (workspaceId != null) {
            wrapper.and(w -> w.eq(CoAccessRecord::getWorkspaceId, workspaceId)
                             .or()
                             .isNull(CoAccessRecord::getWorkspaceId));
        }

        Long totalAccess = baseMapper.selectCount(wrapper);

        // 按目标类型统计
        Long workspaceAccess = baseMapper.selectCount(
            wrapper.clone().eq(CoAccessRecord::getTargetType, 1)
        );
        Long folderAccess = baseMapper.selectCount(
            wrapper.clone().eq(CoAccessRecord::getTargetType, 2)
        );
        Long fileAccess = baseMapper.selectCount(
            wrapper.clone().eq(CoAccessRecord::getTargetType, 3)
        );

        // 按访问类型统计(仅文件)
        Long viewCount = baseMapper.selectCount(
            wrapper.clone().eq(CoAccessRecord::getTargetType, 3)
                          .eq(CoAccessRecord::getAccessType, 1)
        );
        Long editCount = baseMapper.selectCount(
            wrapper.clone().eq(CoAccessRecord::getTargetType, 3)
                          .eq(CoAccessRecord::getAccessType, 2)
        );
        Long downloadCount = baseMapper.selectCount(
            wrapper.clone().eq(CoAccessRecord::getTargetType, 3)
                          .eq(CoAccessRecord::getAccessType, 3)
        );

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalAccess", totalAccess);
        statistics.put("workspaceAccess", workspaceAccess);
        statistics.put("folderAccess", folderAccess);
        statistics.put("fileAccess", fileAccess);
        statistics.put("viewCount", viewCount);
        statistics.put("editCount", editCount);
        statistics.put("downloadCount", downloadCount);

        return statistics;
    }

    /**
     * 获取访问最多的目标
     */
    @Override
    public List<Map<String, Object>> getTopAccessedTargets(Long userId, Long workspaceId, Integer targetType, Integer limit) {
        log.info("获取访问最多的目标: userId={}, workspaceId={}, targetType={}, limit={}",
                userId, workspaceId, targetType, limit);

        if (limit == null || limit <= 0) {
            limit = 10;
        }

        // 这里需要使用原生SQL或者分组查询
        // 简化实现:返回空列表,实际项目中可以使用MyBatis的原生SQL
        return new ArrayList<>();
    }

    /**
     * 填充目标详细信息
     */
    private void enrichTargetInfo(CoAccessRecordVo vo) {
        if (vo == null || vo.getTargetType() == null || vo.getTargetId() == null) {
            return;
        }

        try {
            switch (vo.getTargetType()) {
                case 1: // 工作空间
                    CoWorkspaceVo workspace = workspaceMapper.selectVoById(vo.getTargetId());
                    vo.setTargetInfo(workspace);
                    break;
                case 2: // 文件夹
                    CoFolderVo folder = folderMapper.selectVoById(vo.getTargetId());
                    vo.setTargetInfo(folder);
                    break;
                case 3: // 文件
                    CoFileVo file = fileMapper.selectVoById(vo.getTargetId());
                    if (file != null && !Boolean.TRUE.equals(file.getIsDeleted())) {
                        // 构建文件的层级路径
                        String locationPath = buildLocationPath(file);
                        file.setLocationPath(locationPath);

                        // 查询并设置所有者昵称
                        // 注意：直接使用 selectOne 避免跨模块 MapStruct 转换问题
                        if (file.getOwnerId() != null) {
                            try {
                                com.xmzs.system.domain.SysUser user = sysUserMapper.selectOne(
                                    Wrappers.lambdaQuery(com.xmzs.system.domain.SysUser.class)
                                        .eq(com.xmzs.system.domain.SysUser::getUserId, file.getOwnerId())
                                        .select(com.xmzs.system.domain.SysUser::getUserId,
                                               com.xmzs.system.domain.SysUser::getNickName)
                                );
                                if (user != null && user.getNickName() != null) {
                                    file.setOwnerNickName(user.getNickName());
                                }
                            } catch (Exception e) {
                                log.error("查询文件所有者昵称失败: fileId={}, ownerId={}",
                                         file.getId(), file.getOwnerId(), e);
                            }
                        }
                    }
                    vo.setTargetInfo(file);
                    break;
                default:
                    log.warn("未知的目标类型: {}", vo.getTargetType());
            }
        } catch (Exception e) {
            log.error("填充目标信息失败: targetType={}, targetId={}",
                    vo.getTargetType(), vo.getTargetId(), e);
        }
    }

    /**
     * 构建文件的层级路径
     * @param file 文件对象
     * @return 层级路径字符串，格式：工作空间名称/文件夹1/文件夹2
     */
    private String buildLocationPath(CoFileVo file) {
        if (file == null) {
            return "";
        }

        List<String> pathParts = new ArrayList<>();

        try {
            // 1. 获取工作空间名称
            if (file.getWorkspaceId() != null) {
                CoWorkspace workspace = workspaceMapper.selectById(file.getWorkspaceId());
                if (workspace != null && workspace.getWorkspaceName() != null) {
                    pathParts.add(workspace.getWorkspaceName());
                }
            }

            // 2. 递归获取文件夹路径
            if (file.getFolderId() != null && file.getFolderId() > 0) {
                List<String> folderPath = buildFolderPath(file.getFolderId());
                pathParts.addAll(folderPath);
            }

            // 3. 组合路径
            if (pathParts.isEmpty()) {
                return "根目录";
            }
            return String.join("/", pathParts);

        } catch (Exception e) {
            log.error("构建文件路径失败: fileId={}, workspaceId={}, folderId={}",
                    file.getId(), file.getWorkspaceId(), file.getFolderId(), e);
            return "未知位置";
        }
    }

    /**
     * 递归构建文件夹路径
     * @param folderId 文件夹ID
     * @return 文件夹路径列表（从根到当前文件夹）
     */
    private List<String> buildFolderPath(Long folderId) {
        List<String> path = new ArrayList<>();

        if (folderId == null || folderId <= 0) {
            return path;
        }

        try {
            CoFolder folder = folderMapper.selectById(folderId);
            if (folder == null) {
                return path;
            }

            // 递归获取父文件夹路径
            if (folder.getParentId() != null && folder.getParentId() > 0) {
                path.addAll(buildFolderPath(folder.getParentId()));
            }

            // 添加当前文件夹名称
            if (folder.getFolderName() != null) {
                path.add(folder.getFolderName());
            }

        } catch (Exception e) {
            log.error("构建文件夹路径失败: folderId={}", folderId, e);
        }

        return path;
    }

}

