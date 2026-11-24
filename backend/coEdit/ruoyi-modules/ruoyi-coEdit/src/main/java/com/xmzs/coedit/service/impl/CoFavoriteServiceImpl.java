package com.xmzs.coedit.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.domain.*;
import com.xmzs.coedit.domain.bo.CoFavoriteBo;
import com.xmzs.coedit.domain.vo.CoFavoriteVo;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.domain.vo.CoFolderVo;
import com.xmzs.coedit.domain.vo.CoWorkspaceVo;
import com.xmzs.coedit.mapper.*;
import com.xmzs.coedit.service.ICoFavoriteService;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.satoken.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 收藏Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoFavoriteServiceImpl implements ICoFavoriteService {

    private final CoFavoriteMapper baseMapper;
    private final CoFileMapper fileMapper;
    private final CoFolderMapper folderMapper;
    private final CoWorkspaceMapper workspaceMapper;

    /**
     * 查询收藏
     */
    @Override
    public CoFavoriteVo queryById(Long id) {
        CoFavoriteVo vo = baseMapper.selectVoById(id);
        if (vo != null) {
            enrichTargetInfo(vo);
        }
        return vo;
    }

    /**
     * 查询收藏列表(分页)
     */
    @Override
    public TableDataInfo<CoFavoriteVo> queryPageList(CoFavoriteBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CoFavorite> wrapper = buildQueryWrapper(bo);
        Page<CoFavoriteVo> page = baseMapper.selectVoPage(pageQuery.build(), wrapper);

        // 填充目标详细信息
        page.getRecords().forEach(this::enrichTargetInfo);

        return TableDataInfo.build(page);
    }

    /**
     * 查询收藏列表
     */
    @Override
    public List<CoFavoriteVo> queryList(CoFavoriteBo bo) {
        LambdaQueryWrapper<CoFavorite> wrapper = buildQueryWrapper(bo);
        List<CoFavoriteVo> list = baseMapper.selectVoList(wrapper);

        // 填充目标详细信息
        list.forEach(this::enrichTargetInfo);

        return list;
    }

    /**
     * 添加收藏
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addFavorite(CoFavoriteBo bo) {
        CoFavorite favorite = MapstructUtils.convert(bo, CoFavorite.class);

        // 设置用户ID
        if (favorite.getUserId() == null) {
            favorite.setUserId(LoginHelper.getUserId());
        }

        // 检查是否已经收藏
        if (isFavorited(favorite.getUserId(), favorite.getTargetType(), favorite.getTargetId())) {
            String targetName = getTargetTypeName(favorite.getTargetType());
            log.info("该{}已经收藏过了: userId={}, targetType={}, targetId={}",
                    targetName, favorite.getUserId(), favorite.getTargetType(), favorite.getTargetId());
            throw new com.xmzs.common.core.exception.ServiceException("该" + targetName + "已经在收藏夹中了");
        }

        // 检查目标是否存在和有效
        if (!checkTargetExists(favorite.getTargetType(), favorite.getTargetId())) {
            String targetName = getTargetTypeName(favorite.getTargetType());
            log.warn("收藏目标不存在: targetType={}, targetId={}",
                     favorite.getTargetType(), favorite.getTargetId());
            throw new com.xmzs.common.core.exception.ServiceException(targetName + "不存在或已被删除");
        }

        // 设置默认值
        if (favorite.getIsValid() == null) {
            favorite.setIsValid(1);
        }
        if (favorite.getSortOrder() == null) {
            favorite.setSortOrder(0);
        }

        // 获取目标信息并设置快照
        setTargetSnapshot(favorite);

        int result = baseMapper.insert(favorite);
        log.info("添加收藏: userId={}, targetType={}, targetId={}",
                 favorite.getUserId(), favorite.getTargetType(), favorite.getTargetId());

        return result > 0;
    }

    /**
     * 删除收藏
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeFavorite(Long id) {
        int result = baseMapper.deleteById(id);
        log.info("删除收藏: id={}", id);
        return result > 0;
    }

    /**
     * 更新收藏
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateFavorite(CoFavoriteBo bo) {
        CoFavorite favorite = MapstructUtils.convert(bo, CoFavorite.class);
        int result = baseMapper.updateById(favorite);
        log.info("更新收藏: id={}", favorite.getId());
        return result > 0;
    }

    /**
     * 检查是否已收藏
     */
    @Override
    public Boolean isFavorited(Long userId, Integer targetType, Long targetId) {
        Long count = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoFavorite.class)
                .eq(CoFavorite::getUserId, userId)
                .eq(CoFavorite::getTargetType, targetType)
                .eq(CoFavorite::getTargetId, targetId)
        );
        return count > 0;
    }

    /**
     * 根据文件ID查询是否被收藏
     */
    @Override
    public Boolean isFileFavorited(Long fileId) {
        Long userId = LoginHelper.getUserId();
        return isFavorited(userId, 3, fileId);
    }

    /**
     * 根据目标获取收藏信息
     */
    @Override
    public CoFavoriteVo getFavoriteByTarget(Long userId, Integer targetType, Long targetId) {
        CoFavoriteVo vo = baseMapper.selectVoOne(
            Wrappers.lambdaQuery(CoFavorite.class)
                .eq(CoFavorite::getUserId, userId)
                .eq(CoFavorite::getTargetType, targetType)
                .eq(CoFavorite::getTargetId, targetId)
        );

        if (vo != null) {
            enrichTargetInfo(vo);
        }

        return vo;
    }

    /**
     * 批量添加收藏
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchAddFavorite(List<CoFavoriteBo> boList) {
        if (boList == null || boList.isEmpty()) {
            return false;
        }

        Long userId = LoginHelper.getUserId();
        int successCount = 0;
        int skipCount = 0;
        StringBuilder skipMessages = new StringBuilder();

        for (CoFavoriteBo bo : boList) {
            bo.setUserId(userId);
            try {
                if (addFavorite(bo)) {
                    successCount++;
                }
            } catch (com.xmzs.common.core.exception.ServiceException e) {
                // 捕获已收藏的异常，继续处理其他项
                skipCount++;
                if (skipMessages.length() > 0) {
                    skipMessages.append("; ");
                }
                skipMessages.append(e.getMessage());
                log.info("批量收藏跳过: {}", e.getMessage());
            }
        }

        log.info("批量添加收藏: 总数={}, 成功={}, 跳过={}", boList.size(), successCount, skipCount);

        if (successCount == 0 && skipCount > 0) {
            throw new com.xmzs.common.core.exception.ServiceException("所有项目均已在收藏夹中");
        }

        return successCount > 0;
    }

    /**
     * 批量删除收藏
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchRemoveFavorite(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        int result = baseMapper.deleteBatchIds(ids);
        log.info("批量删除收藏: 总数={}, 成功={}", ids.size(), result);
        return result > 0;
    }

    /**
     * 检查并更新收藏有效性
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean checkAndUpdateValidity(Long favoriteId) {
        CoFavorite favorite = baseMapper.selectById(favoriteId);
        if (favorite == null) {
            return false;
        }

        boolean isValid = checkTargetValidity(favorite);

        if (!isValid && favorite.getIsValid() == 1) {
            // 标记为失效
            favorite.setIsValid(0);
            favorite.setInvalidReason("目标已被删除或无权限访问");
            baseMapper.updateById(favorite);
            log.info("收藏已失效: id={}, targetType={}, targetId={}",
                     favoriteId, favorite.getTargetType(), favorite.getTargetId());
        } else if (isValid && favorite.getIsValid() == 0) {
            // 恢复有效
            favorite.setIsValid(1);
            favorite.setInvalidReason(null);
            baseMapper.updateById(favorite);
            log.info("收藏已恢复: id={}, targetType={}, targetId={}",
                     favoriteId, favorite.getTargetType(), favorite.getTargetId());
        }

        return isValid;
    }

    /**
     * 批量检查用户的所有收藏有效性
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchCheckValidity(Long userId) {
        List<CoFavorite> favorites = baseMapper.selectList(
            Wrappers.lambdaQuery(CoFavorite.class)
                .eq(CoFavorite::getUserId, userId)
        );

        int invalidCount = 0;
        for (CoFavorite favorite : favorites) {
            if (!checkAndUpdateValidity(favorite.getId())) {
                invalidCount++;
            }
        }

        log.info("批量检查收藏有效性: userId={}, 总数={}, 失效={}",
                 userId, favorites.size(), invalidCount);
        return invalidCount;
    }

    /**
     * 获取失效的收藏列表
     */
    @Override
    public List<CoFavoriteVo> getInvalidFavorites(Long userId) {
        return baseMapper.selectVoList(
            Wrappers.lambdaQuery(CoFavorite.class)
                .eq(CoFavorite::getUserId, userId)
                .eq(CoFavorite::getIsValid, 0)
                .orderByDesc(CoFavorite::getUpdateTime)
        );
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<CoFavorite> buildQueryWrapper(CoFavoriteBo bo) {
        return Wrappers.lambdaQuery(CoFavorite.class)
            .eq(ObjectUtil.isNotNull(bo.getUserId()), CoFavorite::getUserId, bo.getUserId())
            .eq(ObjectUtil.isNotNull(bo.getTargetType()), CoFavorite::getTargetType, bo.getTargetType())
            .eq(ObjectUtil.isNotNull(bo.getTargetId()), CoFavorite::getTargetId, bo.getTargetId())
            .eq(ObjectUtil.isNotNull(bo.getWorkspaceId()), CoFavorite::getWorkspaceId, bo.getWorkspaceId())
            .eq(ObjectUtil.isNotNull(bo.getIsValid()), CoFavorite::getIsValid, bo.getIsValid())
            .orderByDesc(CoFavorite::getSortOrder)
            .orderByDesc(CoFavorite::getCreateTime);
    }

    /**
     * 填充目标详细信息
     */
    private void enrichTargetInfo(CoFavoriteVo vo) {
        if (vo == null || vo.getTargetId() == null) {
            return;
        }

        switch (vo.getTargetType()) {
            case 1: // 工作空间
                CoWorkspaceVo workspaceVo = workspaceMapper.selectVoById(vo.getTargetId());
                vo.setTargetInfo(workspaceVo);
                break;
            case 2: // 文件夹
                CoFolderVo folderVo = folderMapper.selectVoById(vo.getTargetId());
                vo.setTargetInfo(folderVo);
                break;
            case 3: // 文件
                CoFileVo fileVo = fileMapper.selectVoById(vo.getTargetId());
                vo.setTargetInfo(fileVo);
                break;
            default:
                break;
        }
    }

    /**
     * 设置目标快照信息
     */
    private void setTargetSnapshot(CoFavorite favorite) {
        switch (favorite.getTargetType()) {
            case 1: // 工作空间
                CoWorkspace workspace = workspaceMapper.selectById(favorite.getTargetId());
                if (workspace != null) {
                    favorite.setFavoriteName(workspace.getWorkspaceName());
                    favorite.setWorkspaceId(workspace.getId());
                }
                break;
            case 2: // 文件夹
                CoFolder folder = folderMapper.selectById(favorite.getTargetId());
                if (folder != null) {
                    favorite.setFavoriteName(folder.getFolderName());
                    favorite.setFavoritePath(folder.getFolderPath());
                    favorite.setWorkspaceId(folder.getWorkspaceId());
                }
                break;
            case 3: // 文件
                CoFile file = fileMapper.selectById(favorite.getTargetId());
                if (file != null) {
                    favorite.setFavoriteName(file.getFileName());
                    favorite.setFavoritePath(file.getFilePath());
                    favorite.setWorkspaceId(file.getWorkspaceId());
                }
                break;
            default:
                break;
        }
    }

    /**
     * 检查目标是否存在
     */
    private boolean checkTargetExists(Integer targetType, Long targetId) {
        switch (targetType) {
            case 1: // 工作空间
                return workspaceMapper.selectById(targetId) != null;
            case 2: // 文件夹
                return folderMapper.selectById(targetId) != null;
            case 3: // 文件
                return fileMapper.selectById(targetId) != null;
            default:
                return false;
        }
    }

    /**
     * 检查目标有效性(是否存在且未被删除)
     */
    private boolean checkTargetValidity(CoFavorite favorite) {
        switch (favorite.getTargetType()) {
            case 1: // 工作空间
                CoWorkspace workspace = workspaceMapper.selectById(favorite.getTargetId());
                return workspace != null && workspace.getStatus() != null && workspace.getStatus() == 1;
            case 2: // 文件夹
                CoFolder folder = folderMapper.selectById(favorite.getTargetId());
                return folder != null && "0".equals(folder.getStatus());
            case 3: // 文件
                CoFile file = fileMapper.selectById(favorite.getTargetId());
                return file != null && "0".equals(file.getStatus());
            default:
                return false;
        }
    }

    /**
     * 获取目标类型的友好名称
     */
    private String getTargetTypeName(Integer targetType) {
        switch (targetType) {
            case 1:
                return "工作空间";
            case 2:
                return "文件夹";
            case 3:
                return "文件";
            default:
                return "目标";
        }
    }

}

