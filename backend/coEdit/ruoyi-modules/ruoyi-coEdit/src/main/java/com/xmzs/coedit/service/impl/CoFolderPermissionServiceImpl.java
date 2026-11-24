package com.xmzs.coedit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.domain.CoFolder;
import com.xmzs.coedit.domain.CoFolderPermission;
import com.xmzs.coedit.domain.bo.CoFolderPermissionBo;
import com.xmzs.coedit.domain.vo.CoFolderPermissionVo;
import com.xmzs.coedit.mapper.CoFolderMapper;
import com.xmzs.coedit.mapper.CoFolderPermissionMapper;
import com.xmzs.coedit.service.ICoFolderPermissionService;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件夹权限Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoFolderPermissionServiceImpl implements ICoFolderPermissionService {

    private final CoFolderPermissionMapper baseMapper;
    private final CoFolderMapper folderMapper;

    /**
     * 查询文件夹权限
     */
    @Override
    public CoFolderPermissionVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询文件夹权限列表
     */
    @Override
    public TableDataInfo<CoFolderPermissionVo> queryPageList(CoFolderPermissionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CoFolderPermission> lqw = buildQueryWrapper(bo);
        Page<CoFolderPermissionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询文件夹权限列表
     */
    @Override
    public List<CoFolderPermissionVo> queryList(CoFolderPermissionBo bo) {
        LambdaQueryWrapper<CoFolderPermission> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<CoFolderPermission> buildQueryWrapper(CoFolderPermissionBo bo) {
        LambdaQueryWrapper<CoFolderPermission> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getFolderId() != null, CoFolderPermission::getFolderId, bo.getFolderId());
        lqw.eq(bo.getWorkspaceId() != null, CoFolderPermission::getWorkspaceId, bo.getWorkspaceId());
        lqw.eq(bo.getPermissionType() != null, CoFolderPermission::getPermissionType, bo.getPermissionType());
        lqw.eq(bo.getTargetId() != null, CoFolderPermission::getTargetId, bo.getTargetId());
        lqw.eq(bo.getPermissionLevel() != null, CoFolderPermission::getPermissionLevel, bo.getPermissionLevel());
        lqw.eq(bo.getStatus() != null, CoFolderPermission::getStatus, bo.getStatus());
        lqw.orderByDesc(CoFolderPermission::getCreateTime);
        return lqw;
    }

    /**
     * 新增文件夹权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(CoFolderPermissionBo bo) {
        CoFolderPermission add = BeanUtil.toBean(bo, CoFolderPermission.class);
        validEntityBeforeSave(add);

        // 设置默认值
        if (add.getStatus() == null) {
            add.setStatus(1); // 正常
        }
        if (add.getGrantTime() == null) {
            add.setGrantTime(new Date());
        }

        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改文件夹权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(CoFolderPermissionBo bo) {
        CoFolderPermission update = BeanUtil.toBean(bo, CoFolderPermission.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(CoFolderPermission entity) {
        // 检查文件夹是否存在
        if (entity.getFolderId() != null) {
            CoFolder folder = folderMapper.selectById(entity.getFolderId());
            if (folder == null) {
                throw new RuntimeException("文件夹不存在");
            }
            if (entity.getWorkspaceId() == null) {
                entity.setWorkspaceId(folder.getWorkspaceId());
            }
        }

        // 根据权限级别设置具体权限
        if (entity.getPermissionLevel() != null) {
            setPermissionsByLevel(entity, entity.getPermissionLevel());
        }
    }

    /**
     * 根据权限级别设置具体权限
     */
    private void setPermissionsByLevel(CoFolderPermission permission, String level) {
        switch (level) {
            case "owner":
                // 所有者：所有权限
                permission.setCanRead(true);
                permission.setCanWrite(true);
                permission.setCanDelete(true);
                permission.setCanShare(true);
                permission.setCanDownload(true);
                permission.setCanUpload(true);
                permission.setCanManagePermission(true);
                break;
            case "admin":
                // 管理员：除所有者权限外的所有权限
                permission.setCanRead(true);
                permission.setCanWrite(true);
                permission.setCanDelete(true);
                permission.setCanShare(true);
                permission.setCanDownload(true);
                permission.setCanUpload(true);
                permission.setCanManagePermission(true);
                break;
            case "edit":
                // 编辑者：读写权限
                permission.setCanRead(true);
                permission.setCanWrite(true);
                permission.setCanDelete(false);
                permission.setCanShare(false);
                permission.setCanDownload(true);
                permission.setCanUpload(true);
                permission.setCanManagePermission(false);
                break;
            case "view":
                // 查看者：只读权限
                permission.setCanRead(true);
                permission.setCanWrite(false);
                permission.setCanDelete(false);
                permission.setCanShare(false);
                permission.setCanDownload(true);
                permission.setCanUpload(false);
                permission.setCanManagePermission(false);
                break;
            default:
                throw new RuntimeException("不支持的权限级别: " + level);
        }
    }

    /**
     * 批量删除文件夹权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteByIds(Collection<Long> ids) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 查询文件夹的所有权限
     */
    @Override
    public List<CoFolderPermissionVo> queryByFolderId(Long folderId) {
        return baseMapper.selectVoList(
            Wrappers.lambdaQuery(CoFolderPermission.class)
                .eq(CoFolderPermission::getFolderId, folderId)
                .eq(CoFolderPermission::getStatus, 1)
                .orderByDesc(CoFolderPermission::getCreateTime)
        );
    }

    /**
     * 查询用户对文件夹的权限
     */
    @Override
    public CoFolderPermissionVo queryUserPermission(Long folderId, Long userId) {
        return baseMapper.selectVoOne(
            Wrappers.lambdaQuery(CoFolderPermission.class)
                .eq(CoFolderPermission::getFolderId, folderId)
                .eq(CoFolderPermission::getPermissionType, 1) // 用户类型
                .eq(CoFolderPermission::getTargetId, userId)
                .eq(CoFolderPermission::getStatus, 1)
        );
    }

    /**
     * 授予文件夹权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean grantPermission(Long folderId, Long targetId, Integer permissionType, String permissionLevel) {
        // 检查是否已存在权限
        CoFolderPermission existing = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoFolderPermission.class)
                .eq(CoFolderPermission::getFolderId, folderId)
                .eq(CoFolderPermission::getPermissionType, permissionType)
                .eq(CoFolderPermission::getTargetId, targetId)
        );

        if (existing != null) {
            // 更新现有权限
            existing.setPermissionLevel(permissionLevel);
            existing.setStatus(1);
            setPermissionsByLevel(existing, permissionLevel);
            return baseMapper.updateById(existing) > 0;
        } else {
            // 创建新权限
            CoFolderPermissionBo bo = new CoFolderPermissionBo();
            bo.setFolderId(folderId);
            bo.setTargetId(targetId);
            bo.setPermissionType(permissionType);
            bo.setPermissionLevel(permissionLevel);
            return insertByBo(bo);
        }
    }

    /**
     * 撤销文件夹权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean revokePermission(Long folderId, Long targetId, Integer permissionType) {
        return baseMapper.update(null,
            Wrappers.lambdaUpdate(CoFolderPermission.class)
                .eq(CoFolderPermission::getFolderId, folderId)
                .eq(CoFolderPermission::getPermissionType, permissionType)
                .eq(CoFolderPermission::getTargetId, targetId)
                .set(CoFolderPermission::getStatus, 0) // 已撤销
        ) > 0;
    }

    /**
     * 检查用户是否有文件夹的读权限
     */
    @Override
    public Boolean hasReadPermission(Long folderId, Long userId) {
        return checkPermission(folderId, userId, "read");
    }

    /**
     * 检查用户是否有文件夹的写权限
     */
    @Override
    public Boolean hasWritePermission(Long folderId, Long userId) {
        return checkPermission(folderId, userId, "write");
    }

    /**
     * 检查用户是否有文件夹的删除权限
     */
    @Override
    public Boolean hasDeletePermission(Long folderId, Long userId) {
        return checkPermission(folderId, userId, "delete");
    }

    /**
     * 检查用户是否有文件夹的管理权限
     */
    @Override
    public Boolean hasManagePermission(Long folderId, Long userId) {
        return checkPermission(folderId, userId, "manage");
    }

    /**
     * 清理过期权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanExpiredPermissions() {
        int count = baseMapper.update(null,
            Wrappers.lambdaUpdate(CoFolderPermission.class)
                .lt(CoFolderPermission::getExpireTime, new Date())
                .eq(CoFolderPermission::getStatus, 1)
                .set(CoFolderPermission::getStatus, 2) // 已过期
        );

        log.info("清理过期文件夹权限: count={}", count);
        return count;
    }

    /**
     * 继承父文件夹权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer inheritParentPermission(Long folderId, Long parentFolderId, Long grantBy) {
        // 获取父文件夹的所有权限
        List<CoFolderPermission> parentPermissions = baseMapper.selectList(
            Wrappers.lambdaQuery(CoFolderPermission.class)
                .eq(CoFolderPermission::getFolderId, parentFolderId)
                .eq(CoFolderPermission::getStatus, 1)
        );

        if (CollUtil.isEmpty(parentPermissions)) {
            return 0;
        }

        int count = 0;
        for (CoFolderPermission parentPerm : parentPermissions) {
            // 检查是否已存在相同的权限
            CoFolderPermission existing = baseMapper.selectOne(
                Wrappers.lambdaQuery(CoFolderPermission.class)
                    .eq(CoFolderPermission::getFolderId, folderId)
                    .eq(CoFolderPermission::getPermissionType, parentPerm.getPermissionType())
                    .eq(CoFolderPermission::getTargetId, parentPerm.getTargetId())
            );

            if (existing == null) {
                // 创建新权限
                CoFolderPermission newPerm = new CoFolderPermission();
                BeanUtil.copyProperties(parentPerm, newPerm);
                newPerm.setId(null);
                newPerm.setFolderId(folderId);
                newPerm.setGrantBy(grantBy);
                newPerm.setGrantTime(new Date());
                newPerm.setCreateTime(new Date());
                newPerm.setUpdateTime(new Date());

                baseMapper.insert(newPerm);
                count++;
            }
        }

        log.info("继承父文件夹权限: folderId={}, parentFolderId={}, count={}",
                 folderId, parentFolderId, count);
        return count;
    }

    /**
     * 传播权限到子文件夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer propagatePermissionToChildren(Long folderId, Boolean recursive, Long grantBy) {
        // 获取当前文件夹的所有权限
        List<CoFolderPermission> permissions = baseMapper.selectList(
            Wrappers.lambdaQuery(CoFolderPermission.class)
                .eq(CoFolderPermission::getFolderId, folderId)
                .eq(CoFolderPermission::getStatus, 1)
        );

        if (CollUtil.isEmpty(permissions)) {
            return 0;
        }

        // 获取子文件夹
        List<CoFolder> children = folderMapper.selectList(
            Wrappers.lambdaQuery(CoFolder.class)
                .eq(CoFolder::getParentId, folderId)
        );

        if (CollUtil.isEmpty(children)) {
            return 0;
        }

        int count = 0;
        for (CoFolder child : children) {
            // 为每个子文件夹设置权限
            for (CoFolderPermission perm : permissions) {
                CoFolderPermission existing = baseMapper.selectOne(
                    Wrappers.lambdaQuery(CoFolderPermission.class)
                        .eq(CoFolderPermission::getFolderId, child.getId())
                        .eq(CoFolderPermission::getPermissionType, perm.getPermissionType())
                        .eq(CoFolderPermission::getTargetId, perm.getTargetId())
                );

                if (existing == null) {
                    CoFolderPermission newPerm = new CoFolderPermission();
                    BeanUtil.copyProperties(perm, newPerm);
                    newPerm.setId(null);
                    newPerm.setFolderId(child.getId());
                    newPerm.setGrantBy(grantBy);
                    newPerm.setGrantTime(new Date());
                    newPerm.setCreateTime(new Date());
                    newPerm.setUpdateTime(new Date());

                    baseMapper.insert(newPerm);
                    count++;
                }
            }

            // 递归处理
            if (Boolean.TRUE.equals(recursive)) {
                count += propagatePermissionToChildren(child.getId(), true, grantBy);
            }
        }

        log.info("传播权限到子文件夹: folderId={}, recursive={}, count={}",
                 folderId, recursive, count);
        return count;
    }

    /**
     * 获取有效权限（考虑继承和冲突解决）
     */
    @Override
    public CoFolderPermissionVo getEffectivePermission(Long folderId, Long userId) {
        // 1. 获取用户直接权限
        CoFolderPermission directPerm = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoFolderPermission.class)
                .eq(CoFolderPermission::getFolderId, folderId)
                .eq(CoFolderPermission::getPermissionType, 1) // 用户类型
                .eq(CoFolderPermission::getTargetId, userId)
                .eq(CoFolderPermission::getStatus, 1)
        );

        if (directPerm != null) {
            return MapstructUtils.convert(directPerm, CoFolderPermissionVo.class);
        }

        // 2. 获取父文件夹权限（继承）
        CoFolder folder = folderMapper.selectById(folderId);
        if (folder != null && folder.getParentId() != null) {
            return getEffectivePermission(folder.getParentId(), userId);
        }

        // 3. 没有任何权限
        return null;
    }

    /**
     * 批量设置文件夹权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchSetPermissions(Long folderId, List<CoFolderPermissionBo> permissions, Long grantBy) {
        if (CollUtil.isEmpty(permissions)) {
            return 0;
        }

        int count = 0;
        for (CoFolderPermissionBo bo : permissions) {
            bo.setFolderId(folderId);
            bo.setGrantBy(grantBy);

            if (insertByBo(bo)) {
                count++;
            }
        }

        log.info("批量设置文件夹权限: folderId={}, count={}", folderId, count);
        return count;
    }

    /**
     * 复制权限到另一个文件夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer copyPermissions(Long sourceFolderId, Long targetFolderId, Long grantBy) {
        // 获取源文件夹的所有权限
        List<CoFolderPermission> sourcePermissions = baseMapper.selectList(
            Wrappers.lambdaQuery(CoFolderPermission.class)
                .eq(CoFolderPermission::getFolderId, sourceFolderId)
                .eq(CoFolderPermission::getStatus, 1)
        );

        if (CollUtil.isEmpty(sourcePermissions)) {
            return 0;
        }

        int count = 0;
        for (CoFolderPermission sourcePerm : sourcePermissions) {
            CoFolderPermission newPerm = new CoFolderPermission();
            BeanUtil.copyProperties(sourcePerm, newPerm);
            newPerm.setId(null);
            newPerm.setFolderId(targetFolderId);
            newPerm.setGrantBy(grantBy);
            newPerm.setGrantTime(new Date());
            newPerm.setCreateTime(new Date());
            newPerm.setUpdateTime(new Date());

            baseMapper.insert(newPerm);
            count++;
        }

        log.info("复制文件夹权限: sourceFolderId={}, targetFolderId={}, count={}",
                 sourceFolderId, targetFolderId, count);
        return count;
    }

    /**
     * 检查用户对文件夹的特定操作权限
     */
    @Override
    public Boolean checkPermission(Long folderId, Long userId, String operation) {
        // 获取有效权限
        CoFolderPermissionVo permission = getEffectivePermission(folderId, userId);

        if (permission == null) {
            return false;
        }

        // 根据操作类型检查权限
        switch (operation.toLowerCase()) {
            case "read":
                return Boolean.TRUE.equals(permission.getCanRead());
            case "write":
                return Boolean.TRUE.equals(permission.getCanWrite());
            case "delete":
                return Boolean.TRUE.equals(permission.getCanDelete());
            case "share":
                return Boolean.TRUE.equals(permission.getCanShare());
            case "download":
                return Boolean.TRUE.equals(permission.getCanDownload());
            case "upload":
                return Boolean.TRUE.equals(permission.getCanUpload());
            case "manage":
                return Boolean.TRUE.equals(permission.getCanManagePermission());
            default:
                return false;
        }
    }

    /**
     * 获取文件夹权限统计
     */
    @Override
    public Map<String, Object> getPermissionStatistics(Long folderId) {
        Map<String, Object> statistics = new HashMap<>();

        // 总权限数
        Long totalCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoFolderPermission.class)
                .eq(CoFolderPermission::getFolderId, folderId)
                .eq(CoFolderPermission::getStatus, 1)
        );
        statistics.put("totalCount", totalCount);

        // 用户权限数
        Long userCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoFolderPermission.class)
                .eq(CoFolderPermission::getFolderId, folderId)
                .eq(CoFolderPermission::getPermissionType, 1)
                .eq(CoFolderPermission::getStatus, 1)
        );
        statistics.put("userCount", userCount);

        // 部门权限数
        Long deptCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoFolderPermission.class)
                .eq(CoFolderPermission::getFolderId, folderId)
                .eq(CoFolderPermission::getPermissionType, 2)
                .eq(CoFolderPermission::getStatus, 1)
        );
        statistics.put("deptCount", deptCount);

        // 角色权限数
        Long roleCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoFolderPermission.class)
                .eq(CoFolderPermission::getFolderId, folderId)
                .eq(CoFolderPermission::getPermissionType, 3)
                .eq(CoFolderPermission::getStatus, 1)
        );
        statistics.put("roleCount", roleCount);

        // 团队权限数
        Long teamCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoFolderPermission.class)
                .eq(CoFolderPermission::getFolderId, folderId)
                .eq(CoFolderPermission::getPermissionType, 4)
                .eq(CoFolderPermission::getStatus, 1)
        );
        statistics.put("teamCount", teamCount);

        // 按权限级别统计
        List<CoFolderPermission> permissions = baseMapper.selectList(
            Wrappers.lambdaQuery(CoFolderPermission.class)
                .eq(CoFolderPermission::getFolderId, folderId)
                .eq(CoFolderPermission::getStatus, 1)
        );

        Map<String, Long> levelCount = permissions.stream()
            .collect(Collectors.groupingBy(
                CoFolderPermission::getPermissionLevel,
                Collectors.counting()
            ));
        statistics.put("levelCount", levelCount);

        return statistics;
    }

}

