package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.vo.FileTreeNodeVo;
import com.xmzs.coedit.domain.vo.FileTreeResponseVo;

import java.util.List;

/**
 * 文件树Service接口
 *
 * @author zuleed
 * @date 2025-11-08
 */
public interface ICoFileTreeService {

    /**
     * 获取工作空间的文件树(支持懒加载)
     *
     * @param workspaceId 工作空间ID
     * @param userId 用户ID
     * @param parentId 父文件夹ID,为null时返回根级文件夹和文件
     * @return 当前层级的文件夹和文件列表
     */
    List<FileTreeNodeVo> getFileTree(Long workspaceId, Long userId, Long parentId);

    /**
     * 获取文件夹下的文件树
     *
     * @param folderId 文件夹ID
     * @param userId 用户ID
     * @param includeSubfolders 是否包含子文件夹
     * @return 文件树节点列表
     */
    List<FileTreeNodeVo> getFolderTree(Long folderId, Long userId, Boolean includeSubfolders);

    /**
     * 获取用户的所有文件树(跨工作空间)
     *
     * @param userId 用户ID
     * @return 文件树根节点列表(按工作空间分组)
     */
    List<FileTreeNodeVo> getUserFileTree(Long userId);

    /**
     * 获取文件树响应数据
     * 当 workspaceId 为 null 时，返回用户有权限访问的所有工作空间及其一级文件树（支持分页和筛选）
     * 当 workspaceId 有值时，返回该工作空间的文件树
     *
     * @param workspaceId 工作空间ID，为null时返回所有工作空间
     * @param userId 用户ID
     * @param parentId 父文件夹ID，为null时返回根级
     * @param workspaceType 工作空间类型: 1-个人空间 2-部门空间 3-团队空间, null或0时忽略此条件
     * @param ownershipType 所有权类型: 1-我创建的 2-我加入的, null或0时返回全部
     * @param pageNum 页码，默认1，仅当workspaceId为null时生效
     * @param pageSize 每页大小，默认10，仅当workspaceId为null时生效
     * @return 文件树响应数据
     */
    FileTreeResponseVo getFileTreeResponse(Long workspaceId, Long userId, Long parentId,
                                           Integer workspaceType, Integer ownershipType,
                                           Integer pageNum, Integer pageSize);

}

