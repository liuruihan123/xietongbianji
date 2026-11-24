package com.xmzs.coedit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.constant.WorkspaceConstants;
import com.xmzs.coedit.domain.CoWorkspace;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.domain.CoFolder;
import com.xmzs.coedit.domain.CoWorkspaceMember;
import com.xmzs.coedit.domain.bo.CoWorkspaceBo;
import com.xmzs.coedit.domain.vo.CoWorkspaceVo;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.domain.vo.CoWorkspaceMemberVo;
import com.xmzs.coedit.mapper.CoWorkspaceMapper;
import com.xmzs.coedit.mapper.CoFileMapper;
import com.xmzs.coedit.mapper.CoFolderMapper;
import com.xmzs.coedit.mapper.CoWorkspaceMemberMapper;
import com.xmzs.coedit.service.ICoWorkspaceService;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.satoken.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 工作空间Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoWorkspaceServiceImpl implements ICoWorkspaceService {

    private final CoWorkspaceMapper baseMapper;
    private final CoFileMapper fileMapper;
    private final CoFolderMapper folderMapper;
    private final CoWorkspaceMemberMapper memberMapper;

    /**
     * 查询工作空间
     */
    @Override
    public CoWorkspaceVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询工作空间列表（分页）
     */
    @Override
    public TableDataInfo<CoWorkspaceVo> queryPageList(CoWorkspaceBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CoWorkspace> lqw = buildQueryWrapper(bo);
        Page<CoWorkspaceVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询工作空间列表
     */
    @Override
    public List<CoWorkspaceVo> queryList(CoWorkspaceBo bo) {
        LambdaQueryWrapper<CoWorkspace> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<CoWorkspace> buildQueryWrapper(CoWorkspaceBo bo) {
        LambdaQueryWrapper<CoWorkspace> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getWorkspaceName()), CoWorkspace::getWorkspaceName, bo.getWorkspaceName());
        lqw.eq(ObjectUtil.isNotNull(bo.getWorkspaceType()), CoWorkspace::getWorkspaceType, bo.getWorkspaceType());
        lqw.eq(ObjectUtil.isNotNull(bo.getOwnerId()), CoWorkspace::getOwnerId, bo.getOwnerId());
        lqw.eq(ObjectUtil.isNotNull(bo.getDeptId()), CoWorkspace::getDeptId, bo.getDeptId());
        lqw.eq(ObjectUtil.isNotNull(bo.getStatus()), CoWorkspace::getStatus, bo.getStatus());
        lqw.eq(ObjectUtil.isNotNull(bo.getIsDefault()), CoWorkspace::getIsDefault, bo.getIsDefault());
        lqw.orderByDesc(CoWorkspace::getIsDefault);
        lqw.orderByAsc(CoWorkspace::getSortOrder);
        lqw.orderByDesc(CoWorkspace::getCreateTime);
        return lqw;
    }

    /**
     * 新增工作空间
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(CoWorkspaceBo bo) {
        CoWorkspace add = MapstructUtils.convert(bo, CoWorkspace.class);
        validEntityBeforeSave(add);

        // 设置默认值
        if (add.getOwnerId() == null) {
            add.setOwnerId(LoginHelper.getUserId());
        }

        // 如果是部门空间，自动设置部门信息
        if (add.getWorkspaceType() != null && add.getWorkspaceType() == 2) {
            try {
                Long deptId = LoginHelper.getDeptId();
                String deptName = LoginHelper.getLoginUser().getDeptName();
                log.info("创建部门空间，用户部门ID: {}, 部门名称: {}", deptId, deptName);
                if (deptId != null && add.getDeptId() == null) {
                    add.setDeptId(deptId);
                }
                if (deptName != null && add.getDeptName() == null) {
                    add.setDeptName(deptName);
                }
            } catch (Exception e) {
                log.warn("获取用户部门信息失败", e);
            }
        }

        if (add.getMaxStorage() == null) {
            add.setMaxStorage(WorkspaceConstants.Default.MAX_STORAGE);
        }
        if (add.getUsedStorage() == null) {
            add.setUsedStorage(0L);
        }
        if (add.getMaxMembers() == null) {
            add.setMaxMembers(WorkspaceConstants.Default.MAX_MEMBERS);
        }
        if (add.getMemberCount() == null) {
            add.setMemberCount(1);
        }
        if (add.getStatus() == null) {
            add.setStatus(WorkspaceConstants.WorkspaceStatus.NORMAL);
        }
        if (add.getIsDefault() == null) {
            add.setIsDefault(false);
        }
        if (add.getSortOrder() == null) {
            add.setSortOrder(0);
        }
        if (add.getAllowInvite() == null) {
            add.setAllowInvite(true);
        }
        if (add.getAllowShare() == null) {
            add.setAllowShare(true);
        }
        if (add.getAllowDownload() == null) {
            add.setAllowDownload(true);
        }

        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改工作空间
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(CoWorkspaceBo bo) {
        CoWorkspace update = MapstructUtils.convert(bo, CoWorkspace.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(CoWorkspace entity) {
        // 校验工作空间名称唯一性
        if (StringUtils.isNotBlank(entity.getWorkspaceName())) {
            LambdaQueryWrapper<CoWorkspace> lqw = Wrappers.lambdaQuery();
            lqw.eq(CoWorkspace::getWorkspaceName, entity.getWorkspaceName());
            lqw.eq(CoWorkspace::getOwnerId, entity.getOwnerId());
            lqw.ne(entity.getId() != null, CoWorkspace::getId, entity.getId());
            if (baseMapper.selectCount(lqw) > 0) {
                throw new RuntimeException("工作空间名称已存在");
            }
        }
    }

    /**
     * 批量删除工作空间
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // TODO: 校验是否有文件或文件夹，如果有则不允许删除
            for (Long id : ids) {
                CoWorkspaceVo workspace = queryById(id);
                if (workspace == null) {
                    throw new RuntimeException("工作空间不存在");
                }
                // 检查是否是默认工作空间
                if (workspace.getIsDefault()) {
                    throw new RuntimeException("默认工作空间不允许删除");
                }
                // 检查是否有成员
                if (workspace.getMemberCount() > 1) {
                    throw new RuntimeException("工作空间还有其他成员，不允许删除");
                }
            }
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 查询用户的工作空间列表
     * 包括：
     * 1. 用户自己创建的工作空间
     * 2. 同部门的部门工作空间（workspaceType=2）
     */
    @Override
    public List<CoWorkspaceVo> queryUserWorkspaces(Long userId) {
        Long tempDeptId = null;
        try {
            tempDeptId = LoginHelper.getDeptId();
            log.info("查询用户[{}]的工作空间，用户部门ID: {}", userId, tempDeptId);
        } catch (Exception e) {
            log.warn("获取用户部门ID失败", e);
        }
        final Long deptId = tempDeptId;

        LambdaQueryWrapper<CoWorkspace> lqw = Wrappers.lambdaQuery();
        // 查询条件：用户自己的工作空间 或者 同部门的部门工作空间
        if (deptId != null) {
            lqw.and(wrapper -> wrapper
                .eq(CoWorkspace::getOwnerId, userId)
                .or(w -> w
                    .eq(CoWorkspace::getWorkspaceType, 2)  // 2-部门空间
                    .eq(CoWorkspace::getDeptId, deptId)
                )
            );
            log.info("查询条件: userId={} 或 (workspaceType=2 AND deptId={})", userId, deptId);
        } else {
            lqw.eq(CoWorkspace::getOwnerId, userId);
            log.info("查询条件: userId={} (无部门信息)", userId);
        }
        lqw.eq(CoWorkspace::getStatus, WorkspaceConstants.WorkspaceStatus.NORMAL);
        lqw.orderByDesc(CoWorkspace::getIsDefault);
        lqw.orderByAsc(CoWorkspace::getSortOrder);
        lqw.orderByDesc(CoWorkspace::getCreateTime);
        List<CoWorkspaceVo> result = baseMapper.selectVoList(lqw);
        log.info("查询到工作空间数量: {}", result.size());
        return result;
    }

    /**
     * 查询用户的默认工作空间
     */
    @Override
    public CoWorkspaceVo queryUserDefaultWorkspace(Long userId) {
        LambdaQueryWrapper<CoWorkspace> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoWorkspace::getOwnerId, userId);
        lqw.eq(CoWorkspace::getIsDefault, true);
        lqw.eq(CoWorkspace::getStatus, WorkspaceConstants.WorkspaceStatus.NORMAL);
        lqw.last("LIMIT 1");
        return baseMapper.selectVoOne(lqw);
    }

    /**
     * 设置默认工作空间
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setDefaultWorkspace(Long userId, Long workspaceId) {
        // 取消当前用户的所有默认工作空间
        LambdaUpdateWrapper<CoWorkspace> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoWorkspace::getOwnerId, userId);
        updateWrapper.set(CoWorkspace::getIsDefault, false);
        baseMapper.update(null, updateWrapper);

        // 设置新的默认工作空间
        CoWorkspace workspace = new CoWorkspace();
        workspace.setId(workspaceId);
        workspace.setIsDefault(true);
        return baseMapper.updateById(workspace) > 0;
    }

    /**
     * 更新工作空间存储使用量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateStorageUsage(Long workspaceId, Long sizeChange) {
        CoWorkspace workspace = baseMapper.selectById(workspaceId);
        if (workspace == null) {
            throw new RuntimeException("工作空间不存在");
        }

        Long newUsedStorage = workspace.getUsedStorage() + sizeChange;
        if (newUsedStorage < 0) {
            newUsedStorage = 0L;
        }

        // 检查是否超过最大存储空间
        if (newUsedStorage > workspace.getMaxStorage()) {
            throw new RuntimeException("存储空间不足");
        }

        LambdaUpdateWrapper<CoWorkspace> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoWorkspace::getId, workspaceId);
        updateWrapper.set(CoWorkspace::getUsedStorage, newUsedStorage);
        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 更新工作空间成员数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateMemberCount(Long workspaceId, Integer memberChange) {
        CoWorkspace workspace = baseMapper.selectById(workspaceId);
        if (workspace == null) {
            throw new RuntimeException("工作空间不存在");
        }

        Integer newMemberCount = workspace.getMemberCount() + memberChange;
        if (newMemberCount < 1) {
            newMemberCount = 1;
        }

        // 检查是否超过最大成员数量
        if (newMemberCount > workspace.getMaxMembers()) {
            throw new RuntimeException("成员数量已达上限");
        }

        LambdaUpdateWrapper<CoWorkspace> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoWorkspace::getId, workspaceId);
        updateWrapper.set(CoWorkspace::getMemberCount, newMemberCount);
        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 检查工作空间名称是否唯一
     */
    @Override
    public Boolean checkWorkspaceNameUnique(CoWorkspaceBo bo) {
        LambdaQueryWrapper<CoWorkspace> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoWorkspace::getWorkspaceName, bo.getWorkspaceName());
        lqw.eq(CoWorkspace::getOwnerId, bo.getOwnerId());
        lqw.ne(bo.getId() != null, CoWorkspace::getId, bo.getId());
        return baseMapper.selectCount(lqw) == 0;
    }

    /**
     * 检查存储空间是否足够
     */
    @Override
    public Boolean checkStorageAvailable(Long workspaceId, Long requiredSize) {
        CoWorkspace workspace = baseMapper.selectById(workspaceId);
        if (workspace == null) {
            return false;
        }
        Long availableSize = workspace.getMaxStorage() - workspace.getUsedStorage();
        return availableSize >= requiredSize;
    }

    /**
     * 检查成员数量是否已满
     */
    @Override
    public Boolean checkMemberLimitReached(Long workspaceId) {
        CoWorkspace workspace = baseMapper.selectById(workspaceId);
        if (workspace == null) {
            return true;
        }
        return workspace.getMemberCount() >= workspace.getMaxMembers();
    }

    /**
     * 根据用户ID获取默认工作空间
     * <p>
     * 查询条件:
     * - owner_id = userId (用户作为所有者)
     * - is_default = 1 (标记为默认工作空间)
     * - status = NORMAL (状态正常)
     * </p>
     *
     * @param userId 用户ID
     * @return 默认工作空间,如果不存在返回null
     */
    @Override
    public CoWorkspaceVo getDefaultWorkspaceByUserId(Long userId) {
        log.debug("查询用户[{}]的默认工作空间", userId);

        LambdaQueryWrapper<CoWorkspace> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoWorkspace::getOwnerId, userId);           // owner_id = userId
        lqw.eq(CoWorkspace::getIsDefault, true);           // is_default = 1
        lqw.eq(CoWorkspace::getStatus, WorkspaceConstants.WorkspaceStatus.NORMAL);  // status = NORMAL
        lqw.orderByDesc(CoWorkspace::getCreateTime);
        lqw.last("LIMIT 1");

        CoWorkspaceVo workspace = baseMapper.selectVoOne(lqw);

        if (workspace == null) {
            log.debug("用户[{}]没有默认工作空间", userId);
            return null;
        }

        log.debug("用户[{}]的默认工作空间ID:{}", userId, workspace.getId());
        return workspace;
    }

    /**
     * 创建工作空间并返回Vo对象
     * 包含完整的工作空间初始化流程
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoWorkspaceVo createWorkspace(CoWorkspaceBo bo) {
        log.info("开始创建工作空间,名称:{}, 所有者ID:{}", bo.getWorkspaceName(), bo.getOwnerId());

        // 1. 转换为实体对象
        CoWorkspace workspace = MapstructUtils.convert(bo, CoWorkspace.class);

        // 2. 数据验证
        validEntityBeforeSave(workspace);

        // 3. 设置默认值
        if (workspace.getOwnerId() == null) {
            workspace.setOwnerId(LoginHelper.getUserId());
        }

        // 如果是部门空间，自动设置部门信息
        if (workspace.getWorkspaceType() != null && workspace.getWorkspaceType() == 2) {
            try {
                Long deptId = LoginHelper.getDeptId();
                String deptName = LoginHelper.getLoginUser().getDeptName();
                log.info("创建部门空间，用户部门ID: {}, 部门名称: {}", deptId, deptName);
                if (deptId != null && workspace.getDeptId() == null) {
                    workspace.setDeptId(deptId);
                }
                if (deptName != null && workspace.getDeptName() == null) {
                    workspace.setDeptName(deptName);
                }
            } catch (Exception e) {
                log.warn("获取用户部门信息失败", e);
            }
        }

        if (workspace.getMaxStorage() == null) {
            workspace.setMaxStorage(WorkspaceConstants.Default.MAX_STORAGE);
        }
        if (workspace.getUsedStorage() == null) {
            workspace.setUsedStorage(0L);
        }
        if (workspace.getMaxMembers() == null) {
            workspace.setMaxMembers(WorkspaceConstants.Default.MAX_MEMBERS);
        }
        if (workspace.getMemberCount() == null) {
            workspace.setMemberCount(1);
        }
        if (workspace.getStatus() == null) {
            workspace.setStatus(WorkspaceConstants.WorkspaceStatus.NORMAL);
        }
        if (workspace.getIsDefault() == null) {
            workspace.setIsDefault(false);
        }
        if (workspace.getSortOrder() == null) {
            workspace.setSortOrder(0);
        }
        if (workspace.getAllowInvite() == null) {
            workspace.setAllowInvite(true);
        }
        if (workspace.getAllowShare() == null) {
            workspace.setAllowShare(true);
        }
        if (workspace.getAllowDownload() == null) {
            workspace.setAllowDownload(true);
        }

        // 4. 插入工作空间记录
        boolean insertSuccess = baseMapper.insert(workspace) > 0;
        if (!insertSuccess) {
            throw new RuntimeException("创建工作空间失败");
        }

        log.info("工作空间创建成功,ID:{}", workspace.getId());

        // 5. 返回创建的工作空间
        return baseMapper.selectVoById(workspace.getId());
    }

    @Override
    public java.util.Map<String, Object> getWorkspaceStats(Long workspaceId) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        // 获取工作空间基本信息
        CoWorkspaceVo workspace = baseMapper.selectVoById(workspaceId);
        if (workspace == null) {
            throw new RuntimeException("工作空间不存在");
        }

        // 统计文件数
        Long fileCount = fileMapper.selectCount(
            Wrappers.lambdaQuery(CoFile.class)
                .eq(CoFile::getWorkspaceId, workspaceId)
                .eq(CoFile::getStatus, WorkspaceConstants.FileStatus.NORMAL)
        );
        stats.put("fileCount", fileCount);

        // 统计文件夹数
        Long folderCount = folderMapper.selectCount(
            Wrappers.lambdaQuery(CoFolder.class)
                .eq(CoFolder::getWorkspaceId, workspaceId)
                .eq(CoFolder::getStatus, WorkspaceConstants.FolderStatus.NORMAL)
        );
        stats.put("folderCount", folderCount);

        // 统计成员数（普通成员 + 所有者）
        Long memberCount = memberMapper.selectCount(
            Wrappers.lambdaQuery(CoWorkspaceMember.class)
                .eq(CoWorkspaceMember::getWorkspaceId, workspaceId)
                .eq(CoWorkspaceMember::getStatus, WorkspaceConstants.MemberStatus.NORMAL)
        ) + 1;
        stats.put("memberCount", memberCount);

        // 存储信息
        stats.put("usedStorage", workspace.getUsedStorage());
        stats.put("maxStorage", workspace.getMaxStorage());

        // 最近文件（最多5个）
        List<CoFileVo> recentFiles = fileMapper.selectVoList(
            Wrappers.lambdaQuery(CoFile.class)
                .eq(CoFile::getWorkspaceId, workspaceId)
                .eq(CoFile::getStatus, WorkspaceConstants.FileStatus.NORMAL)
                .orderByDesc(CoFile::getUpdateTime)
                .last("LIMIT 5")
        );
        stats.put("recentFiles", recentFiles.stream().map(file -> {
            java.util.Map<String, Object> fileInfo = new java.util.HashMap<>();
            fileInfo.put("id", file.getId());
            fileInfo.put("name", file.getFileName());
            fileInfo.put("path", file.getFilePath());
            fileInfo.put("size", file.getFileSize());
            fileInfo.put("updatedAt", file.getUpdateTime());
            return fileInfo;
        }).collect(java.util.stream.Collectors.toList()));

        // 最近活跃成员（最多5个）
        List<CoWorkspaceMemberVo> recentMembers = memberMapper.selectVoList(
            Wrappers.lambdaQuery(CoWorkspaceMember.class)
                .eq(CoWorkspaceMember::getWorkspaceId, workspaceId)
                .eq(CoWorkspaceMember::getStatus, WorkspaceConstants.MemberStatus.NORMAL)
                .orderByDesc(CoWorkspaceMember::getUpdateTime)
                .last("LIMIT 5")
        );
        stats.put("recentMembers", recentMembers.stream().map(member -> {
            java.util.Map<String, Object> memberInfo = new java.util.HashMap<>();
            memberInfo.put("id", member.getUserId());
            memberInfo.put("username", member.getUserId().toString());
            memberInfo.put("avatar", null);
            memberInfo.put("lastActiveAt", member.getCreateTime());
            return memberInfo;
        }).collect(java.util.stream.Collectors.toList()));

        return stats;
    }
}

