package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.bo.CoFolderPermissionBo;
import com.xmzs.coedit.domain.vo.CoFolderPermissionVo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 文件夹权限Service接口
 *
 * @author zuleed
 * @date 2025-11-04
 */
public interface ICoFolderPermissionService {

    /**
     * 查询文件夹权限
     *
     * @param id 权限ID
     * @return 文件夹权限
     */
    CoFolderPermissionVo queryById(Long id);

    /**
     * 查询文件夹权限列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 文件夹权限分页列表
     */
    TableDataInfo<CoFolderPermissionVo> queryPageList(CoFolderPermissionBo bo, PageQuery pageQuery);

    /**
     * 查询文件夹权限列表
     *
     * @param bo 查询条件
     * @return 文件夹权限列表
     */
    List<CoFolderPermissionVo> queryList(CoFolderPermissionBo bo);

    /**
     * 新增文件夹权限
     *
     * @param bo 文件夹权限业务对象
     * @return 是否成功
     */
    Boolean insertByBo(CoFolderPermissionBo bo);

    /**
     * 修改文件夹权限
     *
     * @param bo 文件夹权限业务对象
     * @return 是否成功
     */
    Boolean updateByBo(CoFolderPermissionBo bo);

    /**
     * 批量删除文件夹权限
     *
     * @param ids 权限ID集合
     * @return 是否成功
     */
    Boolean deleteByIds(Collection<Long> ids);

    /**
     * 查询文件夹的所有权限
     *
     * @param folderId 文件夹ID
     * @return 权限列表
     */
    List<CoFolderPermissionVo> queryByFolderId(Long folderId);

    /**
     * 查询用户对文件夹的权限
     *
     * @param folderId 文件夹ID
     * @param userId   用户ID
     * @return 权限信息
     */
    CoFolderPermissionVo queryUserPermission(Long folderId, Long userId);

    /**
     * 授予文件夹权限
     *
     * @param folderId        文件夹ID
     * @param targetId        目标ID
     * @param permissionType  权限类型
     * @param permissionLevel 权限级别
     * @return 是否成功
     */
    Boolean grantPermission(Long folderId, Long targetId, Integer permissionType, String permissionLevel);

    /**
     * 撤销文件夹权限
     *
     * @param folderId       文件夹ID
     * @param targetId       目标ID
     * @param permissionType 权限类型
     * @return 是否成功
     */
    Boolean revokePermission(Long folderId, Long targetId, Integer permissionType);

    /**
     * 检查用户是否有文件夹的读权限
     *
     * @param folderId 文件夹ID
     * @param userId   用户ID
     * @return 是否有权限
     */
    Boolean hasReadPermission(Long folderId, Long userId);

    /**
     * 检查用户是否有文件夹的写权限
     *
     * @param folderId 文件夹ID
     * @param userId   用户ID
     * @return 是否有权限
     */
    Boolean hasWritePermission(Long folderId, Long userId);

    /**
     * 检查用户是否有文件夹的删除权限
     *
     * @param folderId 文件夹ID
     * @param userId   用户ID
     * @return 是否有权限
     */
    Boolean hasDeletePermission(Long folderId, Long userId);

    /**
     * 检查用户是否有文件夹的管理权限
     *
     * @param folderId 文件夹ID
     * @param userId   用户ID
     * @return 是否有权限
     */
    Boolean hasManagePermission(Long folderId, Long userId);

    /**
     * 清理过期权限
     *
     * @return 清理数量
     */
    Integer cleanExpiredPermissions();

    /**
     * 继承父文件夹权限
     *
     * @param folderId 文件夹ID
     * @param parentFolderId 父文件夹ID
     * @param grantBy 授权人ID
     * @return 继承的权限数量
     */
    Integer inheritParentPermission(Long folderId, Long parentFolderId, Long grantBy);

    /**
     * 传播权限到子文件夹
     *
     * @param folderId 文件夹ID
     * @param recursive 是否递归
     * @param grantBy 授权人ID
     * @return 传播的权限数量
     */
    Integer propagatePermissionToChildren(Long folderId, Boolean recursive, Long grantBy);

    /**
     * 获取有效权限（考虑继承和冲突解决）
     *
     * @param folderId 文件夹ID
     * @param userId 用户ID
     * @return 有效权限
     */
    CoFolderPermissionVo getEffectivePermission(Long folderId, Long userId);

    /**
     * 批量设置文件夹权限
     *
     * @param folderId 文件夹ID
     * @param permissions 权限列表
     * @param grantBy 授权人ID
     * @return 成功数量
     */
    Integer batchSetPermissions(Long folderId, List<CoFolderPermissionBo> permissions, Long grantBy);

    /**
     * 复制权限到另一个文件夹
     *
     * @param sourceFolderId 源文件夹ID
     * @param targetFolderId 目标文件夹ID
     * @param grantBy 授权人ID
     * @return 复制的权限数量
     */
    Integer copyPermissions(Long sourceFolderId, Long targetFolderId, Long grantBy);

    /**
     * 检查用户对文件夹的特定操作权限
     *
     * @param folderId 文件夹ID
     * @param userId 用户ID
     * @param operation 操作类型（read/write/delete/share/download/upload/manage）
     * @return 是否有权限
     */
    Boolean checkPermission(Long folderId, Long userId, String operation);

    /**
     * 获取文件夹权限统计
     *
     * @param folderId 文件夹ID
     * @return 统计信息（用户数、部门数、角色数等）
     */
    java.util.Map<String, Object> getPermissionStatistics(Long folderId);
}

