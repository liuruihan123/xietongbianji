package com.xmzs.coedit.service;

import cn.hutool.core.lang.tree.Tree;
import com.xmzs.coedit.domain.bo.CoFolderBo;
import com.xmzs.coedit.domain.vo.CoFolderVo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 文件夹Service接口
 *
 * @author zuleed
 * @date 2025-11-04
 */
public interface ICoFolderService {

    /**
     * 查询文件夹
     *
     * @param id 文件夹主键
     * @return 文件夹
     */
    CoFolderVo queryById(Long id);

    /**
     * 查询文件夹列表
     *
     * @param bo 文件夹
     * @param pageQuery 分页参数
     * @return 文件夹集合
     */
    TableDataInfo<CoFolderVo> queryPageList(CoFolderBo bo, PageQuery pageQuery);

    /**
     * 查询文件夹列表
     *
     * @param bo 文件夹
     * @return 文件夹集合
     */
    List<CoFolderVo> queryList(CoFolderBo bo);

    /**
     * 新增文件夹
     *
     * @param bo 文件夹
     * @return 是否成功
     */
    Boolean insertByBo(CoFolderBo bo);

    /**
     * 修改文件夹
     *
     * @param bo 文件夹
     * @return 是否成功
     */
    Boolean updateByBo(CoFolderBo bo);

    /**
     * 校验并批量删除文件夹信息
     *
     * @param ids 文件夹主键集合
     * @param isValid 是否校验
     * @return 是否成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 查询文件夹树形结构
     *
     * @param workspaceId 工作空间ID
     * @return 文件夹树形结构
     */
    List<Tree<Long>> selectFolderTreeList(Long workspaceId);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param folders 文件夹列表
     * @return 下拉树结构列表
     */
    List<Tree<Long>> buildFolderTreeSelect(List<CoFolderVo> folders);

    /**
     * 创建文件夹
     *
     * @param workspaceId 工作空间ID
     * @param parentId 父文件夹ID
     * @param folderName 文件夹名称
     * @return 文件夹信息
     */
    CoFolderVo createFolder(Long workspaceId, Long parentId, String folderName);

    /**
     * 重命名文件夹
     *
     * @param folderId 文件夹ID
     * @param newName 新名称
     * @return 是否成功
     */
    Boolean renameFolder(Long folderId, String newName);

    /**
     * 移动文件夹
     *
     * @param folderId 文件夹ID
     * @param targetParentId 目标父文件夹ID
     * @return 是否成功
     */
    Boolean moveFolder(Long folderId, Long targetParentId);

    /**
     * 复制文件夹
     *
     * @param folderId 文件夹ID
     * @param targetParentId 目标父文件夹ID
     * @param newName 新名称
     * @return 新文件夹信息
     */
    CoFolderVo copyFolder(Long folderId, Long targetParentId, String newName);

    /**
     * 检查文件夹是否存在
     *
     * @param workspaceId 工作空间ID
     * @param parentId 父文件夹ID
     * @param folderName 文件夹名称
     * @return 是否存在
     */
    Boolean checkFolderExists(Long workspaceId, Long parentId, String folderName);

    /**
     * 检查文件夹是否有子文件夹
     *
     * @param folderId 文件夹ID
     * @return 是否有子文件夹
     */
    Boolean hasChildFolders(Long folderId);

    /**
     * 检查文件夹是否有文件
     *
     * @param folderId 文件夹ID
     * @return 是否有文件
     */
    Boolean hasFiles(Long folderId);

    /**
     * 更新文件夹文件数量
     *
     * @param folderId 文件夹ID
     * @param increment 增量（正数增加，负数减少）
     * @return 是否成功
     */
    Boolean updateFileCount(Long folderId, Integer increment);

    /**
     * 更新文件夹子文件夹数量
     *
     * @param folderId 文件夹ID
     * @param increment 增量（正数增加，负数减少）
     * @return 是否成功
     */
    Boolean updateSubfolderCount(Long folderId, Integer increment);

    /**
     * 更新文件夹总大小
     *
     * @param folderId 文件夹ID
     * @param increment 增量（正数增加，负数减少）
     * @return 是否成功
     */
    Boolean updateTotalSize(Long folderId, Long increment);

    /**
     * 查询工作空间下的文件夹列表
     *
     * @param workspaceId 工作空间ID
     * @return 文件夹集合
     */
    List<CoFolderVo> queryByWorkspaceId(Long workspaceId);

    /**
     * 查询父文件夹下的子文件夹列表
     *
     * @param parentId 父文件夹ID
     * @return 文件夹集合
     */
    List<CoFolderVo> queryByParentId(Long parentId);

    /**
     * 查询用户的文件夹列表
     *
     * @param userId 用户ID
     * @return 文件夹集合
     */
    List<CoFolderVo> queryByUserId(Long userId);

}

