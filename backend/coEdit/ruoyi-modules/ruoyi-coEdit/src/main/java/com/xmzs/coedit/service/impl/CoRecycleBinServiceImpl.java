package com.xmzs.coedit.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.constant.WorkspaceConstants;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.domain.CoRecycleBin;
import com.xmzs.coedit.domain.bo.CoRecycleBinBo;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.domain.vo.CoRecycleBinVo;
import com.xmzs.coedit.mapper.CoFileMapper;
import com.xmzs.coedit.mapper.CoRecycleBinMapper;
import com.xmzs.coedit.service.ICoFolderService;
import com.xmzs.coedit.service.ICoRecycleBinService;
import com.xmzs.coedit.service.ICoWorkspaceService;
import com.xmzs.common.core.exception.ServiceException;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 回收站Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoRecycleBinServiceImpl implements ICoRecycleBinService {

    private final CoRecycleBinMapper baseMapper;
    private final CoFileMapper fileMapper;
    private final ICoFolderService folderService;
    private final ICoWorkspaceService workspaceService;

    /**
     * 查询回收站
     */
    @Override
    public CoRecycleBinVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询回收站列表
     */
    @Override
    public TableDataInfo<CoRecycleBinVo> queryPageList(CoRecycleBinBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CoRecycleBin> lqw = buildQueryWrapper(bo);
        Page<CoRecycleBinVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询回收站列表
     */
    @Override
    public List<CoRecycleBinVo> queryList(CoRecycleBinBo bo) {
        LambdaQueryWrapper<CoRecycleBin> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<CoRecycleBin> buildQueryWrapper(CoRecycleBinBo bo) {
        LambdaQueryWrapper<CoRecycleBin> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getFileId() != null, CoRecycleBin::getFileId, bo.getFileId());
        lqw.eq(bo.getWorkspaceId() != null, CoRecycleBin::getWorkspaceId, bo.getWorkspaceId());
        lqw.eq(bo.getFolderId() != null, CoRecycleBin::getFolderId, bo.getFolderId());
        lqw.like(StringUtils.isNotBlank(bo.getFileName()), CoRecycleBin::getFileName, bo.getFileName());
        lqw.eq(StringUtils.isNotBlank(bo.getFileType()), CoRecycleBin::getFileType, bo.getFileType());
        lqw.eq(bo.getDeletedBy() != null, CoRecycleBin::getDeletedBy, bo.getDeletedBy());
        lqw.eq(bo.getStatus() != null, CoRecycleBin::getStatus, bo.getStatus());
        lqw.orderByDesc(CoRecycleBin::getDeletedTime);
        return lqw;
    }

    /**
     * 新增回收站
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(CoRecycleBinBo bo) {
        CoRecycleBin add = MapstructUtils.convert(bo, CoRecycleBin.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改回收站
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(CoRecycleBinBo bo) {
        CoRecycleBin update = MapstructUtils.convert(bo, CoRecycleBin.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(CoRecycleBin entity) {
        // 校验文件ID不能为空
        if (entity.getFileId() == null) {
            throw new ServiceException("文件ID不能为空");
        }

        // 校验工作空间ID不能为空
        if (entity.getWorkspaceId() == null) {
            throw new ServiceException("工作空间ID不能为空");
        }
    }

    /**
     * 批量删除回收站
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 将文件移动到回收站
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean moveFileToRecycleBin(Long fileId, String deleteReason) {
        // 查询文件信息
        CoFileVo file = fileMapper.selectVoById(fileId);
        if (file == null) {
            throw new ServiceException("文件不存在");
        }

        // 获取当前用户信息
//        Long userId = LoginHelper.getUserId();
//        String username = LoginHelper.getUserName();

        // 计算过期时间（30天后）
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, WorkspaceConstants.Default.RECYCLE_BIN_DAYS);
        Date expireTime = calendar.getTime();

        // 创建回收站记录
        CoRecycleBin recycleBin = new CoRecycleBin();
        recycleBin.setId(IdUtil.getSnowflakeNextId());
        recycleBin.setFileId(fileId);
        recycleBin.setWorkspaceId(file.getWorkspaceId());
        recycleBin.setFolderId(file.getFolderId());
        recycleBin.setFileName(file.getFileName());
        recycleBin.setFileType(file.getFileType());
        recycleBin.setFileSize(file.getFileSize());
        recycleBin.setFileExtension(file.getFileExtension());
        recycleBin.setOriginalPath(file.getFilePath());
        recycleBin.setDeletedBy(1l);
        recycleBin.setDeletedByName("admin");
        recycleBin.setDeletedTime(new Date());
        recycleBin.setDeleteReason(deleteReason);
        recycleBin.setExpireTime(expireTime);
        recycleBin.setCanRestore(1); // 可恢复
        recycleBin.setOriginalOwnerId(file.getOwnerId());
        recycleBin.setOriginalOwnerName(file.getOwnerName());
        recycleBin.setOriginalCreateTime(file.getCreateTime());
        recycleBin.setOriginalUpdateTime(file.getUpdateTime());
        recycleBin.setStatus(WorkspaceConstants.RecycleBinStatus.PENDING); // 待清理

        // 保存回收站记录
        if (baseMapper.insert(recycleBin) <= 0) {
            throw new ServiceException("移动到回收站失败");
        }

        // 更新文件状态为已删除
        LambdaUpdateWrapper<CoFile> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoFile::getId, fileId);
        updateWrapper.set(CoFile::getStatus, WorkspaceConstants.FileStatus.DELETED);
        updateWrapper.set(CoFile::getIsDeleted, true);
        fileMapper.update(null, updateWrapper);

        // 更新文件夹的文件数量和大小
        if (file.getFolderId() != null && file.getFolderId() > 0) {
            folderService.updateFileCount(file.getFolderId(), -1);
            folderService.updateTotalSize(file.getFolderId(), -file.getFileSize());
        }

        return true;
    }

    /**
     * 从回收站恢复文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean restoreFile(Long recycleBinId) {
        // 查询回收站记录
//        CoRecycleBinVo recycleBin = queryById(recycleBinId);
        CoRecycleBinVo recycleBin = queryByFileId(recycleBinId);
        if (recycleBin == null) {
            throw new ServiceException("回收站记录不存在");
        }

        // 检查是否可恢复
        if (recycleBin.getCanRestore() == null || recycleBin.getCanRestore() == 0) {
            throw new ServiceException("该文件不可恢复");
        }

        // 检查状态
        if (recycleBin.getStatus() != WorkspaceConstants.RecycleBinStatus.PENDING) {
            throw new ServiceException("该文件已被处理，无法恢复");
        }

        // 查询文件信息
        CoFileVo file = fileMapper.selectVoById(recycleBin.getFileId());
        if (file == null) {
            throw new ServiceException("原文件不存在");
        }

        // 检查原位置是否有同名文件
        LambdaQueryWrapper<CoFile> checkWrapper = Wrappers.lambdaQuery();
        checkWrapper.eq(CoFile::getWorkspaceId, recycleBin.getWorkspaceId());
        checkWrapper.eq(recycleBin.getFolderId() != null, CoFile::getFolderId, recycleBin.getFolderId());
        checkWrapper.isNull(recycleBin.getFolderId() == null, CoFile::getFolderId);
        checkWrapper.eq(CoFile::getFileName, recycleBin.getFileName());
        checkWrapper.eq(CoFile::getIsDeleted, false);
        if (fileMapper.selectCount(checkWrapper) > 0) {
            throw new ServiceException("原位置已存在同名文件，无法恢复");
        }

        // 恢复文件状态
        LambdaUpdateWrapper<CoFile> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoFile::getId, recycleBin.getFileId());
        updateWrapper.set(CoFile::getStatus, WorkspaceConstants.FileStatus.NORMAL);
        updateWrapper.set(CoFile::getIsDeleted, false);
        fileMapper.update(null, updateWrapper);

        // 直接删除回收站记录（避免再次删除同一文件时 fileId 重复）
        baseMapper.deleteById(recycleBin.getId());

        // 更新文件夹的文件数量和大小
        if (recycleBin.getFolderId() != null && recycleBin.getFolderId() > 0) {
            folderService.updateFileCount(recycleBin.getFolderId(), 1);
            folderService.updateTotalSize(recycleBin.getFolderId(), recycleBin.getFileSize());
        }

        return true;
    }

    /**
     * 从回收站永久删除文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean permanentlyDeleteFile(Long recycleBinId) {
        // 查询回收站记录
//        CoRecycleBinVo recycleBin = queryById(recycleBinId);
        CoRecycleBinVo recycleBin = queryByFileId(recycleBinId);
        if (recycleBin == null) {
            throw new ServiceException("回收站记录不存在");
        }

        // 查询文件信息
        CoFileVo file = fileMapper.selectVoById(recycleBin.getFileId());
        if (file != null) {
            // 删除物理文件
            try {
                File physicalFile = new File(file.getFilePath());
                if (physicalFile.exists()) {
                    FileUtil.del(physicalFile);
                    log.info("删除物理文件成功: {}", file.getFilePath());
                }
            } catch (Exception e) {
                log.error("删除物理文件失败: {}", file.getFilePath(), e);
            }

            // 删除文件记录
            fileMapper.deleteById(recycleBin.getFileId());

            // 更新工作空间存储使用量
            workspaceService.updateStorageUsage(recycleBin.getWorkspaceId(), -recycleBin.getFileSize());
        }

        // 更新回收站记录状态
        LambdaUpdateWrapper<CoRecycleBin> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoRecycleBin::getId, recycleBinId);
        updateWrapper.set(CoRecycleBin::getStatus, WorkspaceConstants.RecycleBinStatus.CLEANED);
        baseMapper.update(null, updateWrapper);

        return true;
    }

    /**
     * 批量从回收站恢复文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchRestoreFiles(List<Long> recycleBinIds) {
        for (Long recycleBinId : recycleBinIds) {
            if (!restoreFile(recycleBinId)) {
                throw new ServiceException("文件恢复失败：" + recycleBinId);
            }
        }
        return true;
    }

    /**
     * 批量从回收站永久删除文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchPermanentlyDeleteFiles(List<Long> recycleBinIds) {
        for (Long recycleBinId : recycleBinIds) {
            if (!permanentlyDeleteFile(recycleBinId)) {
                throw new ServiceException("文件删除失败：" + recycleBinId);
            }
        }
        return true;
    }

    /**
     * 清空回收站
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean emptyRecycleBin(Long workspaceId) {
        // 查询工作空间的所有回收站项目
        LambdaQueryWrapper<CoRecycleBin> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoRecycleBin::getWorkspaceId, workspaceId);
        lqw.eq(CoRecycleBin::getStatus, WorkspaceConstants.RecycleBinStatus.PENDING);
        List<CoRecycleBinVo> recycleBinList = baseMapper.selectVoList(lqw);

        // 永久删除所有项目
        for (CoRecycleBinVo recycleBin : recycleBinList) {
            permanentlyDeleteFile(recycleBin.getId());
        }

        return true;
    }

    /**
     * 自动清理过期的回收站项目
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer autoCleanExpiredItems() {
        // 查询所有过期的回收站项目
        LambdaQueryWrapper<CoRecycleBin> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoRecycleBin::getStatus, WorkspaceConstants.RecycleBinStatus.PENDING);
        lqw.le(CoRecycleBin::getExpireTime, new Date());
        List<CoRecycleBinVo> expiredList = baseMapper.selectVoList(lqw);

        int count = 0;
        for (CoRecycleBinVo recycleBin : expiredList) {
            try {
                permanentlyDeleteFile(recycleBin.getId());
                count++;
            } catch (Exception e) {
                log.error("自动清理回收站项目失败: recycleBinId={}", recycleBin.getId(), e);
            }
        }

        log.info("自动清理回收站完成，共清理{}个项目", count);
        return count;
    }

    /**
     * 根据文件ID查询回收站文件
     */
    @Override
    public CoRecycleBinVo queryByFileId(Long fileId) {
        LambdaQueryWrapper<CoRecycleBin> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoRecycleBin::getFileId, fileId);
        lqw.eq(CoRecycleBin::getStatus, WorkspaceConstants.RecycleBinStatus.PENDING);
        lqw.orderByDesc(CoRecycleBin::getDeletedTime);
        lqw.last("LIMIT 1");
        return baseMapper.selectVoOne(lqw);
    }

    /**
     * 查询工作空间的回收站列表
     */
    @Override
    public List<CoRecycleBinVo> queryByWorkspaceId(Long workspaceId) {
        LambdaQueryWrapper<CoRecycleBin> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoRecycleBin::getWorkspaceId, workspaceId);
        lqw.eq(CoRecycleBin::getStatus, WorkspaceConstants.RecycleBinStatus.PENDING);
        lqw.orderByDesc(CoRecycleBin::getDeletedTime);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询用户的回收站列表
     */
    @Override
    public List<CoRecycleBinVo> queryByUserId(Long userId) {
        LambdaQueryWrapper<CoRecycleBin> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoRecycleBin::getDeletedBy, userId);
        lqw.eq(CoRecycleBin::getStatus, WorkspaceConstants.RecycleBinStatus.PENDING);
        lqw.orderByDesc(CoRecycleBin::getDeletedTime);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 统计回收站大小
     */
    @Override
    public Long calculateRecycleBinSize(Long workspaceId) {
        LambdaQueryWrapper<CoRecycleBin> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoRecycleBin::getWorkspaceId, workspaceId);
        lqw.eq(CoRecycleBin::getStatus, WorkspaceConstants.RecycleBinStatus.PENDING);
        lqw.select(CoRecycleBin::getFileSize);

        List<CoRecycleBin> list = baseMapper.selectList(lqw);
        return list.stream()
            .mapToLong(item -> item.getFileSize() != null ? item.getFileSize() : 0L)
            .sum();
    }

    /**
     * 统计回收站文件数量
     */
    @Override
    public Integer countRecycleBinFiles(Long workspaceId) {
        LambdaQueryWrapper<CoRecycleBin> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoRecycleBin::getWorkspaceId, workspaceId);
        lqw.eq(CoRecycleBin::getStatus, WorkspaceConstants.RecycleBinStatus.PENDING);
        return Math.toIntExact(baseMapper.selectCount(lqw));
    }

}


