package com.xmzs.coedit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.constant.WorkspaceConstants;
import com.xmzs.coedit.domain.CoFolder;
import com.xmzs.coedit.domain.bo.CoFolderBo;
import com.xmzs.coedit.domain.vo.CoFolderVo;
import com.xmzs.coedit.mapper.CoFolderMapper;
import com.xmzs.coedit.service.ICoFolderService;
import com.xmzs.common.core.exception.ServiceException;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.core.utils.TreeBuildUtils;
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
 * 文件夹Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoFolderServiceImpl implements ICoFolderService {

    private final CoFolderMapper baseMapper;

    /**
     * 查询文件夹
     */
    @Override
    public CoFolderVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询文件夹列表
     */
    @Override
    public TableDataInfo<CoFolderVo> queryPageList(CoFolderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CoFolder> lqw = buildQueryWrapper(bo);
        Page<CoFolderVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询文件夹列表
     */
    @Override
    public List<CoFolderVo> queryList(CoFolderBo bo) {
        LambdaQueryWrapper<CoFolder> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<CoFolder> buildQueryWrapper(CoFolderBo bo) {
        LambdaQueryWrapper<CoFolder> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getWorkspaceId() != null, CoFolder::getWorkspaceId, bo.getWorkspaceId());
        lqw.eq(bo.getParentId() != null, CoFolder::getParentId, bo.getParentId());
        lqw.like(StringUtils.isNotBlank(bo.getFolderName()), CoFolder::getFolderName, bo.getFolderName());
        lqw.eq(bo.getFolderType() != null, CoFolder::getFolderType, bo.getFolderType());
        lqw.eq(bo.getOwnerId() != null, CoFolder::getOwnerId, bo.getOwnerId());
        lqw.eq(bo.getStatus() != null, CoFolder::getStatus, bo.getStatus());
        lqw.eq(bo.getIsDeleted() != null, CoFolder::getIsDeleted, bo.getIsDeleted());
        lqw.orderByAsc(CoFolder::getSortOrder);
        lqw.orderByDesc(CoFolder::getCreateTime);
        return lqw;
    }

    /**
     * 新增文件夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(CoFolderBo bo) {
        CoFolder add = MapstructUtils.convert(bo, CoFolder.class);
        validEntityBeforeSave(add);

        // 设置默认值
        if (add.getFolderLevel() == null) {
            // 计算层级
            if (add.getParentId() == null || add.getParentId() == 0) {
                add.setFolderLevel(0);
            } else {
                CoFolderVo parent = queryById(add.getParentId());
                if (parent != null) {
                    add.setFolderLevel(parent.getFolderLevel() + 1);
                }
            }
        }

        if (add.getFolderType() == null) {
            add.setFolderType(1); // 默认普通文件夹
        }
        if (add.getIsPrivate() == null) {
            add.setIsPrivate(false);
        }
        if (add.getIsSystem() == null) {
            add.setIsSystem(false);
        }
        if (add.getFileCount() == null) {
            add.setFileCount(0);
        }
        if (add.getSubfolderCount() == null) {
            add.setSubfolderCount(0);
        }
        if (add.getTotalSize() == null) {
            add.setTotalSize(0L);
        }
        if (add.getInheritPermission() == null) {
            add.setInheritPermission(true);
        }
        if (add.getStatus() == null) {
            add.setStatus(1); // 默认正常状态
        }
        if (add.getIsDeleted() == null) {
            add.setIsDeleted(false);
        }

        // 构建文件夹路径
        if (StringUtils.isBlank(add.getFolderPath())) {
            add.setFolderPath(buildFolderPath(add.getParentId(), add.getFolderName()));
        }

        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
            // 更新父文件夹的子文件夹数量
            if (add.getParentId() != null && add.getParentId() > 0) {
                updateSubfolderCount(add.getParentId(), 1);
            }
        }
        return flag;
    }

    /**
     * 修改文件夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(CoFolderBo bo) {
        CoFolder update = MapstructUtils.convert(bo, CoFolder.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(CoFolder entity) {
        // 校验文件夹名称不能为空
        if (StringUtils.isBlank(entity.getFolderName())) {
            throw new ServiceException("文件夹名称不能为空");
        }

        // 校验工作空间ID不能为空
        if (entity.getWorkspaceId() == null) {
            throw new ServiceException("工作空间ID不能为空");
        }

        // 校验文件夹名称是否重复
        if (entity.getId() == null) {
            // 新增时检查
            if (checkFolderExists(entity.getWorkspaceId(), entity.getParentId(), entity.getFolderName())) {
                throw new ServiceException("文件夹名称已存在");
            }
        }
    }

    /**
     * 批量删除文件夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务校验
            for (Long id : ids) {
                CoFolderVo folder = queryById(id);
                if (folder == null) {
                    throw new ServiceException("文件夹不存在");
                }
                // 检查是否为系统文件夹
                if (folder.getIsSystem() != null && folder.getIsSystem()) {
                    throw new ServiceException("系统文件夹不能删除");
                }
                // 检查是否有子文件夹
                if (hasChildFolders(id)) {
                    throw new ServiceException("文件夹下有子文件夹，不能删除");
                }
                // 检查是否有文件
                if (hasFiles(id)) {
                    throw new ServiceException("文件夹下有文件，不能删除");
                }
            }
        }

        // 软删除：更新状态为已删除
        for (Long id : ids) {
            CoFolderVo folder = queryById(id);
            if (folder != null) {
                LambdaUpdateWrapper<CoFolder> updateWrapper = Wrappers.lambdaUpdate();
                updateWrapper.eq(CoFolder::getId, id);
                updateWrapper.set(CoFolder::getIsDeleted, true);
                updateWrapper.set(CoFolder::getStatus, 0);
                baseMapper.update(null, updateWrapper);

                // 更新父文件夹的子文件夹数量
                if (folder.getParentId() != null && folder.getParentId() > 0) {
                    updateSubfolderCount(folder.getParentId(), -1);
                }
            }
        }

        return true;
    }

    /**
     * 查询文件夹树形结构
     */
    @Override
    public List<Tree<Long>> selectFolderTreeList(Long workspaceId) {
        CoFolderBo bo = new CoFolderBo();
        bo.setWorkspaceId(workspaceId);
        bo.setIsDeleted(false);
        List<CoFolderVo> folders = queryList(bo);
        return buildFolderTreeSelect(folders);
    }

    /**
     * 构建前端所需要下拉树结构
     */
    @Override
    public List<Tree<Long>> buildFolderTreeSelect(List<CoFolderVo> folders) {
        if (CollUtil.isEmpty(folders)) {
            return CollUtil.newArrayList();
        }
        return TreeBuildUtils.build(folders, (folder, tree) ->
            tree.setId(folder.getId())
                .setParentId(folder.getParentId())
                .setName(folder.getFolderName())
                .setWeight(folder.getSortOrder()));
    }

    /**
     * 创建文件夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoFolderVo createFolder(Long workspaceId, Long parentId, String folderName) {
        // 检查文件夹名称是否重复
        if (checkFolderExists(workspaceId, parentId, folderName)) {
            throw new ServiceException("文件夹名称已存在");
        }

        // 获取当前用户信息
        Long userId = LoginHelper.getUserId();
        String username = LoginHelper.getUserName();

        // 创建文件夹
        CoFolderBo folderBo = new CoFolderBo();
        folderBo.setWorkspaceId(workspaceId);
        folderBo.setParentId(parentId);
        folderBo.setFolderName(folderName);
        folderBo.setOwnerId(userId);
        folderBo.setOwnerName(username);

        if (!insertByBo(folderBo)) {
            throw new ServiceException("文件夹创建失败");
        }

        return queryById(folderBo.getId());
    }

    /**
     * 重命名文件夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean renameFolder(Long folderId, String newName) {
        CoFolderVo folder = queryById(folderId);
        if (folder == null) {
            throw new ServiceException("文件夹不存在");
        }

        // 检查新名称是否重复
        if (checkFolderExists(folder.getWorkspaceId(), folder.getParentId(), newName)) {
            throw new ServiceException("文件夹名称已存在");
        }

        // 更新文件夹名称和路径
        LambdaUpdateWrapper<CoFolder> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoFolder::getId, folderId);
        updateWrapper.set(CoFolder::getFolderName, newName);
        updateWrapper.set(CoFolder::getFolderPath, buildFolderPath(folder.getParentId(), newName));

        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 移动文件夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean moveFolder(Long folderId, Long targetParentId) {
        CoFolderVo folder = queryById(folderId);
        if (folder == null) {
            throw new ServiceException("文件夹不存在");
        }

        // 检查目标父文件夹是否存在
        if (targetParentId != null && targetParentId > 0) {
            CoFolderVo targetParent = queryById(targetParentId);
            if (targetParent == null) {
                throw new ServiceException("目标文件夹不存在");
            }
            // 检查是否移动到自己的子文件夹
            if (isChildFolder(targetParentId, folderId)) {
                throw new ServiceException("不能移动到自己的子文件夹");
            }
        }

        // 检查目标位置是否有同名文件夹
        if (checkFolderExists(folder.getWorkspaceId(), targetParentId, folder.getFolderName())) {
            throw new ServiceException("目标位置已存在同名文件夹");
        }

        // 更新文件夹父ID、层级和路径
        Long oldParentId = folder.getParentId();
        Integer newLevel = 0;
        if (targetParentId != null && targetParentId > 0) {
            CoFolderVo targetParent = queryById(targetParentId);
            newLevel = targetParent.getFolderLevel() + 1;
        }

        LambdaUpdateWrapper<CoFolder> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoFolder::getId, folderId);
        updateWrapper.set(CoFolder::getParentId, targetParentId);
        updateWrapper.set(CoFolder::getFolderLevel, newLevel);
        updateWrapper.set(CoFolder::getFolderPath, buildFolderPath(targetParentId, folder.getFolderName()));

        boolean result = baseMapper.update(null, updateWrapper) > 0;

        if (result) {
            // 更新原父文件夹的子文件夹数量
            if (oldParentId != null && oldParentId > 0) {
                updateSubfolderCount(oldParentId, -1);
            }
            // 更新新父文件夹的子文件夹数量
            if (targetParentId != null && targetParentId > 0) {
                updateSubfolderCount(targetParentId, 1);
            }
        }

        return result;
    }

    /**
     * 复制文件夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoFolderVo copyFolder(Long folderId, Long targetParentId, String newName) {
        CoFolderVo sourceFolder = queryById(folderId);
        if (sourceFolder == null) {
            throw new ServiceException("源文件夹不存在");
        }

        // 检查目标父文件夹是否存在
        if (targetParentId != null && targetParentId > 0) {
            CoFolderVo targetParent = queryById(targetParentId);
            if (targetParent == null) {
                throw new ServiceException("目标文件夹不存在");
            }
        }

        // 使用新名称或原名称
        String folderName = StringUtils.isNotBlank(newName) ? newName : sourceFolder.getFolderName();

        // 检查目标位置是否有同名文件夹
        if (checkFolderExists(sourceFolder.getWorkspaceId(), targetParentId, folderName)) {
            throw new ServiceException("目标位置已存在同名文件夹");
        }

        // 创建新文件夹
        CoFolderBo newFolderBo = new CoFolderBo();
        BeanUtil.copyProperties(sourceFolder, newFolderBo);
        newFolderBo.setId(null);
        newFolderBo.setParentId(targetParentId);
        newFolderBo.setFolderName(folderName);
        newFolderBo.setFileCount(0);
        newFolderBo.setSubfolderCount(0);
        newFolderBo.setTotalSize(0L);

        if (!insertByBo(newFolderBo)) {
            throw new ServiceException("文件夹复制失败");
        }

        return queryById(newFolderBo.getId());
    }

    /**
     * 检查文件夹是否存在
     */
    @Override
    public Boolean checkFolderExists(Long workspaceId, Long parentId, String folderName) {
        LambdaQueryWrapper<CoFolder> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoFolder::getWorkspaceId, workspaceId);
        lqw.eq(parentId != null, CoFolder::getParentId, parentId);
        lqw.isNull(parentId == null, CoFolder::getParentId);
        lqw.eq(CoFolder::getFolderName, folderName);
        lqw.eq(CoFolder::getIsDeleted, false);
        return baseMapper.selectCount(lqw) > 0;
    }

    /**
     * 检查文件夹是否有子文件夹
     */
    @Override
    public Boolean hasChildFolders(Long folderId) {
        LambdaQueryWrapper<CoFolder> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoFolder::getParentId, folderId);
        lqw.eq(CoFolder::getIsDeleted, false);
        return baseMapper.selectCount(lqw) > 0;
    }

    /**
     * 检查文件夹是否有文件
     */
    @Override
    public Boolean hasFiles(Long folderId) {
        CoFolderVo folder = queryById(folderId);
        return folder != null && folder.getFileCount() != null && folder.getFileCount() > 0;
    }

    /**
     * 更新文件夹文件数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateFileCount(Long folderId, Integer increment) {
        LambdaUpdateWrapper<CoFolder> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoFolder::getId, folderId);
        updateWrapper.setSql("file_count = file_count + " + increment);
        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 更新文件夹子文件夹数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSubfolderCount(Long folderId, Integer increment) {
        LambdaUpdateWrapper<CoFolder> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoFolder::getId, folderId);
        updateWrapper.setSql("subfolder_count = subfolder_count + " + increment);
        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 更新文件夹总大小
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTotalSize(Long folderId, Long increment) {
        LambdaUpdateWrapper<CoFolder> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoFolder::getId, folderId);
        updateWrapper.setSql("total_size = total_size + " + increment);
        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 查询工作空间下的文件夹列表
     */
    @Override
    public List<CoFolderVo> queryByWorkspaceId(Long workspaceId) {
        LambdaQueryWrapper<CoFolder> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoFolder::getWorkspaceId, workspaceId);
        lqw.eq(CoFolder::getIsDeleted, false);
        lqw.orderByAsc(CoFolder::getSortOrder);
        lqw.orderByDesc(CoFolder::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询父文件夹下的子文件夹列表
     */
    @Override
    public List<CoFolderVo> queryByParentId(Long parentId) {
        LambdaQueryWrapper<CoFolder> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoFolder::getParentId, parentId);
        lqw.eq(CoFolder::getIsDeleted, false);
        lqw.orderByAsc(CoFolder::getSortOrder);
        lqw.orderByDesc(CoFolder::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询用户的文件夹列表
     */
    @Override
    public List<CoFolderVo> queryByUserId(Long userId) {
        LambdaQueryWrapper<CoFolder> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoFolder::getOwnerId, userId);
        lqw.eq(CoFolder::getIsDeleted, false);
        lqw.orderByAsc(CoFolder::getSortOrder);
        lqw.orderByDesc(CoFolder::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 构建文件夹路径
     */
    private String buildFolderPath(Long parentId, String folderName) {
        if (parentId == null || parentId == 0) {
            return "/" + folderName;
        }

        CoFolderVo parent = queryById(parentId);
        if (parent == null) {
            return "/" + folderName;
        }

        return parent.getFolderPath() + "/" + folderName;
    }

    /**
     * 检查是否为子文件夹
     */
    private boolean isChildFolder(Long childId, Long parentId) {
        if (childId == null || parentId == null) {
            return false;
        }

        if (childId.equals(parentId)) {
            return true;
        }

        CoFolderVo child = queryById(childId);
        if (child == null || child.getParentId() == null) {
            return false;
        }

        return isChildFolder(child.getParentId(), parentId);
    }

}


