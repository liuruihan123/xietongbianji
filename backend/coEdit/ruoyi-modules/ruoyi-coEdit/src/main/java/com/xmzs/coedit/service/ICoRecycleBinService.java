package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.bo.CoRecycleBinBo;
import com.xmzs.coedit.domain.vo.CoRecycleBinVo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 回收站Service接口
 *
 * @author zuleed
 * @date 2025-11-04
 */
public interface ICoRecycleBinService {

    /**
     * 查询回收站
     *
     * @param id 回收站主键
     * @return 回收站
     */
    CoRecycleBinVo queryById(Long id);

    /**
     * 查询回收站列表
     *
     * @param bo 回收站
     * @param pageQuery 分页参数
     * @return 回收站集合
     */
    TableDataInfo<CoRecycleBinVo> queryPageList(CoRecycleBinBo bo, PageQuery pageQuery);

    /**
     * 查询回收站列表
     *
     * @param bo 回收站
     * @return 回收站集合
     */
    List<CoRecycleBinVo> queryList(CoRecycleBinBo bo);

    /**
     * 新增回收站
     *
     * @param bo 回收站
     * @return 是否成功
     */
    Boolean insertByBo(CoRecycleBinBo bo);

    /**
     * 修改回收站
     *
     * @param bo 回收站
     * @return 是否成功
     */
    Boolean updateByBo(CoRecycleBinBo bo);

    /**
     * 校验并批量删除回收站信息
     *
     * @param ids 回收站主键集合
     * @param isValid 是否校验
     * @return 是否成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 将文件移动到回收站
     *
     * @param fileId 文件ID
     * @param deleteReason 删除原因
     * @return 是否成功
     */
    Boolean moveFileToRecycleBin(Long fileId, String deleteReason);

    /**
     * 从回收站恢复文件
     *
     * @param recycleBinId 回收站ID
     * @return 是否成功
     */
    Boolean restoreFile(Long recycleBinId);

    /**
     * 从回收站永久删除文件
     *
     * @param recycleBinId 回收站ID
     * @return 是否成功
     */
    Boolean permanentlyDeleteFile(Long recycleBinId);

    /**
     * 批量从回收站恢复文件
     *
     * @param recycleBinIds 回收站ID列表
     * @return 是否成功
     */
    Boolean batchRestoreFiles(List<Long> recycleBinIds);

    /**
     * 批量从回收站永久删除文件
     *
     * @param recycleBinIds 回收站ID列表
     * @return 是否成功
     */
    Boolean batchPermanentlyDeleteFiles(List<Long> recycleBinIds);

    /**
     * 清空回收站
     *
     * @param workspaceId 工作空间ID
     * @return 是否成功
     */
    Boolean emptyRecycleBin(Long workspaceId);

    /**
     * 自动清理过期的回收站项目
     *
     * @return 清理数量
     */
    Integer autoCleanExpiredItems();

    public CoRecycleBinVo queryByFileId(Long fileId);
    /**
     * 查询工作空间的回收站列表
     *
     * @param workspaceId 工作空间ID
     * @return 回收站集合
     */
    List<CoRecycleBinVo> queryByWorkspaceId(Long workspaceId);

    /**
     * 查询用户的回收站列表
     *
     * @param userId 用户ID
     * @return 回收站集合
     */
    List<CoRecycleBinVo> queryByUserId(Long userId);

    /**
     * 统计回收站大小
     *
     * @param workspaceId 工作空间ID
     * @return 总大小（字节）
     */
    Long calculateRecycleBinSize(Long workspaceId);

    /**
     * 统计回收站文件数量
     *
     * @param workspaceId 工作空间ID
     * @return 文件数量
     */
    Integer countRecycleBinFiles(Long workspaceId);

}

