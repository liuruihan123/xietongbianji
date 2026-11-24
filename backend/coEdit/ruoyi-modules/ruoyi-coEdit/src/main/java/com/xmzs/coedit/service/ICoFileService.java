package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.bo.CoFileBo;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

/**
 * 文件Service接口
 *
 * @author zuleed
 * @date 2025-11-04
 */
public interface ICoFileService {

    /**
     * 查询文件
     *
     * @param id 文件主键
     * @return 文件
     */
    CoFileVo queryById(Long id);

    /**
     * 查询文件列表
     *
     * @param bo 文件
     * @param pageQuery 分页参数
     * @return 文件集合
     */
    TableDataInfo<CoFileVo> queryPageList(CoFileBo bo, PageQuery pageQuery);

    /**
     * 查询文件列表
     *
     * @param bo 文件
     * @return 文件集合
     */
    List<CoFileVo> queryList(CoFileBo bo);

    /**
     * 新增文件
     *
     * @param bo 文件
     * @return 是否成功
     */
    Boolean insertByBo(CoFileBo bo);

    /**
     * 修改文件
     *
     * @param bo 文件
     * @return 是否成功
     */
    Boolean updateByBo(CoFileBo bo);

    /**
     * 校验并批量删除文件信息
     *
     * @param ids 文件主键集合
     * @param isValid 是否校验
     * @return 是否成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 上传文件
     *
     * @param workspaceId 工作空间ID
     * @param folderId 文件夹ID
     * @param file 文件
     * @return 文件信息
     */
    CoFileVo uploadFile(Long workspaceId, Long folderId, MultipartFile file);

    /**
     * 下载文件
     *
     * @param id 文件ID
     * @return 文件字节数组
     */
    byte[] downloadFile(Long id);

    /**
     * 获取文件物理路径
     *
     * @param id 文件ID
     * @return 文件物理路径
     */
    String getFilePath(Long id);

    /**
     * 更新文件编辑状态
     *
     * @param id 文件ID
     * @param editStatus 编辑状态
     * @param editorId 编辑者ID
     * @param editorName 编辑者名称
     * @return 是否成功
     */
    Boolean updateEditStatus(Long id, Integer editStatus, Long editorId, String editorName);

    /**
     * 更新文件锁定状态
     *
     * @param id 文件ID
     * @param isLocked 是否锁定
     * @param lockedBy 锁定人ID
     * @param lockedByName 锁定人名称
     * @param lockReason 锁定原因
     * @return 是否成功
     */
    Boolean updateLockStatus(Long id, Integer isLocked, Long lockedBy, String lockedByName, String lockReason);

    /**
     * 更新文件下载次数
     *
     * @param id 文件ID
     * @return 是否成功
     */
    Boolean incrementDownloadCount(Long id);

    /**
     * 更新文件查看次数
     *
     * @param id 文件ID
     * @return 是否成功
     */
    Boolean incrementViewCount(Long id);

    /**
     * 更新文件编辑次数
     *
     * @param id 文件ID
     * @return 是否成功
     */
    Boolean incrementEditCount(Long id);

    /**
     * 检查文件是否存在
     *
     * @param workspaceId 工作空间ID
     * @param folderId 文件夹ID
     * @param fileName 文件名
     * @return 是否存在
     */
    Boolean checkFileExists(Long workspaceId, Long folderId, String fileName);

    /**
     * 检查文件是否被锁定
     *
     * @param id 文件ID
     * @return 是否锁定
     */
    Boolean checkFileLocked(Long id);

    /**
     * 查询工作空间下的文件列表
     *
     * @param workspaceId 工作空间ID
     * @return 文件集合
     */
    List<CoFileVo> queryByWorkspaceId(Long workspaceId);

    /**
     * 查询文件夹下的文件列表
     *
     * @param folderId 文件夹ID
     * @return 文件集合
     */
    List<CoFileVo> queryByFolderId(Long folderId);

    /**
     * 查询用户的文件列表
     *
     * @param userId 用户ID
     * @return 文件集合
     */
    List<CoFileVo> queryByUserId(Long userId);

    /**
     * 移动文件
     *
     * @param fileId 文件ID
     * @param targetFolderId 目标文件夹ID
     * @return 是否成功
     */
    Boolean moveFile(Long fileId, Long targetFolderId);

    /**
     * 复制文件
     *
     * @param fileId 文件ID
     * @param targetFolderId 目标文件夹ID
     * @param newFileName 新文件名
     * @return 新文件信息
     */
    CoFileVo copyFile(Long fileId, Long targetFolderId, String newFileName);

    /**
     * 重命名文件
     *
     * @param fileId 文件ID
     * @param newFileName 新文件名
     * @return 是否成功
     */
    Boolean renameFile(Long fileId, String newFileName);

    /**
     * 批量移动文件
     *
     * @param fileIds 文件ID列表
     * @param targetFolderId 目标文件夹ID
     * @return 是否成功
     */
    Boolean batchMoveFiles(List<Long> fileIds, Long targetFolderId);

    /**
     * 批量复制文件
     *
     * @param fileIds 文件ID列表
     * @param targetFolderId 目标文件夹ID
     * @return 是否成功
     */
    Boolean batchCopyFiles(List<Long> fileIds, Long targetFolderId);

    /**
     * 创建在线文档
     *
     * @param workspaceId 工作空间ID
     * @param folderId 文件夹ID
     * @param fileName 文件名
     * @param fileType 文件类型（docx/xlsx/pptx/txt）
     * @return 文件信息
     */
    CoFileVo createDocument(Long workspaceId, Long folderId, String fileName, String fileType);

    /**
     * 清空文件表（物理删除）
     */
    void clearAll();

    /**
     * 更新文件路径和大小
     * 用于OnlyOffice保存文档后同步数据库信息
     *
     * @param id 文件ID
     * @param filePath 新的文件路径
     * @param fileSize 新的文件大小
     * @return 是否成功
     */
    Boolean updateFilePathAndSize(Long id, String filePath, Long fileSize);

}



