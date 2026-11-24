package com.xmzs.coedit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.config.OnlyOfficeConfig;
import com.xmzs.coedit.constant.WorkspaceConstants;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.domain.bo.CoFileBo;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.mapper.CoFileMapper;
import com.xmzs.coedit.domain.vo.CoFolderVo;
import com.xmzs.coedit.service.DocumentTemplateService;
import com.xmzs.coedit.service.ICoAccessRecordService;
import com.xmzs.coedit.service.ICoFileAccessRecordService;
import com.xmzs.coedit.service.ICoFileService;
import com.xmzs.coedit.service.ICoFolderService;
import com.xmzs.coedit.service.ICoRecycleBinService;
import com.xmzs.coedit.service.ICoWorkspaceService;
import com.xmzs.common.core.exception.ServiceException;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.satoken.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 文件Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoFileServiceImpl implements ICoFileService {

    private final CoFileMapper baseMapper;
    private final OnlyOfficeConfig onlyOfficeConfig;
    private final ICoWorkspaceService workspaceService;
    private final ICoFolderService folderService;
    private final ICoRecycleBinService recycleBinService;
    private final DocumentTemplateService documentTemplateService;
    private final ICoFileAccessRecordService fileAccessRecordService;
    private final ICoAccessRecordService accessRecordService;

    /**
     * 查询文件
     */
    @Override
    public CoFileVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询文件列表
     */
    @Override
    public TableDataInfo<CoFileVo> queryPageList(CoFileBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CoFile> lqw = buildQueryWrapper(bo);
        Page<CoFileVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询文件列表
     */
    @Override
    public List<CoFileVo> queryList(CoFileBo bo) {
        LambdaQueryWrapper<CoFile> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<CoFile> buildQueryWrapper(CoFileBo bo) {
        LambdaQueryWrapper<CoFile> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getWorkspaceId() != null, CoFile::getWorkspaceId, bo.getWorkspaceId());
        lqw.eq(bo.getFolderId() != null, CoFile::getFolderId, bo.getFolderId());
        lqw.like(StringUtils.isNotBlank(bo.getFileName()), CoFile::getFileName, bo.getFileName());
        lqw.eq(StringUtils.isNotBlank(bo.getFileType()), CoFile::getFileType, bo.getFileType());
        lqw.eq(bo.getOwnerId() != null, CoFile::getOwnerId, bo.getOwnerId());
        lqw.eq(bo.getStatus() != null, CoFile::getStatus, bo.getStatus());
        lqw.eq(bo.getIsDeleted() != null, CoFile::getIsDeleted, bo.getIsDeleted());
        lqw.orderByDesc(CoFile::getCreateTime);
        return lqw;
    }

    /**
     * 新增文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(CoFileBo bo) {
        CoFile add = MapstructUtils.convert(bo, CoFile.class);
        validEntityBeforeSave(add);

        // 设置默认值
        if (add.getCurrentVersion() == null) {
            add.setCurrentVersion(WorkspaceConstants.Default.DEFAULT_VERSION);
        }
        if (add.getEditStatus() == null) {
            add.setEditStatus(WorkspaceConstants.EditStatus.NOT_EDITED);
        }
        if (add.getStatus() == null) {
            add.setStatus(WorkspaceConstants.FileStatus.NORMAL);
        }
        if (add.getDownloadCount() == null) {
            add.setDownloadCount(0);
        }
        if (add.getViewCount() == null) {
            add.setViewCount(0);
        }
        if (add.getEditCount() == null) {
            add.setEditCount(0);
        }
        if (add.getShareCount() == null) {
            add.setShareCount(0);
        }
        if (add.getIsLocked() == null) {
            add.setIsLocked(0);
        }
        if (add.getIsPrivate() == null) {
            add.setIsPrivate(0);
        }
        if (add.getEditorCount() == null) {
            add.setEditorCount(0);
        }

        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(CoFileBo bo) {
        CoFile update = MapstructUtils.convert(bo, CoFile.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(CoFile entity) {
        // 校验文件名不能为空
        if (StringUtils.isBlank(entity.getFileName())) {
            throw new ServiceException("文件名不能为空");
        }

        // 校验工作空间ID不能为空
        if (entity.getWorkspaceId() == null) {
            throw new ServiceException("工作空间ID不能为空");
        }

        // 允许同名文件存在,符合在线文档系统的业务需求
        // 已移除文件名重复性校验
    }

    /**
     * 批量删除文件（移动到回收站）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务校验
            for (Long id : ids) {
                CoFileVo file = queryById(id);
                if (file == null) {
                    throw new ServiceException("文件不存在");
                }
                // 检查文件是否被锁定
                if (file.getIsLocked() != null && file.getIsLocked() == 1) {
                    throw new ServiceException("文件已被锁定，不能删除");
                }
            }
        }

        // 移动到回收站（由回收站服务处理软删除和文件夹统计更新）
        for (Long id : ids) {
            recycleBinService.moveFileToRecycleBin(id, "用户删除");
        }

        return true;
    }

    // 从 yml 中读取 ${libreoffice.host}
    @Value("${libreoffice.host}")
    private String libreHost;

    // 从 yml 中读取 ${libreoffice.port}
    @Value("${libreoffice.port}")
    private Integer librePort;

    private static final String SAVE_PATH = "/tmp/file_convert_temp/";
//    private static final String SAVE_PATH = "D:/file_convert_temp/";

    /**
     * 上传文件
     */
    /*@Override
    @Transactional(rollbackFor = Exception.class)
    public CoFileVo uploadFile(Long workspaceId, Long folderId, MultipartFile file) {
        // 1. 验证文件
        if (file == null || file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }
        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new ServiceException("文件名不能为空");
        }

        // 2. 获取文件信息
        String fileExtension = FileUtil.extName(originalFilename);
        String fileType = fileExtension.toLowerCase();
        long fileSize = file.getSize();
        String mimeType = file.getContentType();

        // 3. 验证文件类型
        if (!isValidFileType(fileType)) {
            throw new ServiceException("不支持的文件类型：" + fileType);
        }

        // 4. 验证文件大小
        long maxFileSize = onlyOfficeConfig.getMaxFileSize() * 1024 * 1024; // MB转字节
        if (fileSize > maxFileSize) {
            throw new ServiceException("文件大小超过限制：" + onlyOfficeConfig.getMaxFileSize() + "MB");
        }

        // 5. 检查存储空间
        if (!workspaceService.checkStorageAvailable(workspaceId, fileSize)) {
            throw new ServiceException("工作空间存储空间不足");
        }

        // 6. 允许同名文件存在,符合在线文档系统的业务需求
        // 已移除文件名重复性校验

        // 7. 生成文件ID和存储路径
        Long fileId = IdUtil.getSnowflakeNextId();
        String storagePath = buildStoragePath(workspaceId, folderId, fileId, originalFilename);

        // 8. 保存文件到磁盘
        try {
            File destFile = new File(storagePath);
            FileUtil.mkParentDirs(destFile);
            file.transferTo(destFile);
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new ServiceException("文件保存失败：" + e.getMessage());
        }

        // 9. 生成documentKey
        String documentKey = generateDocumentKey(fileId, 1);

        // 10. 获取当前用户信息
        Long userId = LoginHelper.getUserId();
        String username = LoginHelper.getUserName();

        // 11. 创建文件记录
        CoFileBo fileBo = new CoFileBo();
        fileBo.setWorkspaceId(workspaceId);
        fileBo.setFolderId(folderId);
        fileBo.setFileName(originalFilename);
        fileBo.setFileType(fileType);
        fileBo.setFileSize(fileSize);
        fileBo.setFilePath(storagePath);
        fileBo.setFileExtension(fileExtension);
        fileBo.setMimeType(mimeType);
        fileBo.setDocumentKey(documentKey);
        fileBo.setCurrentVersion(1);
        fileBo.setEditStatus(WorkspaceConstants.EditStatus.NOT_EDITED);
        fileBo.setOwnerId(userId);
        fileBo.setOwnerName(username);
        fileBo.setStatus(WorkspaceConstants.FileStatus.NORMAL);

        // 设置ID
        fileBo.setId(fileId);

        // 插入数据库
        if (!insertByBo(fileBo)) {
            // 删除已保存的文件
            FileUtil.del(storagePath);
            throw new ServiceException("文件记录创建失败");
        }

        // 12. 更新工作空间存储使用量
        workspaceService.updateStorageUsage(workspaceId, fileSize);

        // 13. 记录文件访问 - 创建后查看
        try {
            // 记录到文件访问记录表
            fileAccessRecordService.recordAccess(fileId, userId, 1); // 1-查看
            // 记录到通用访问记录表
            accessRecordService.recordFileAccess(userId, fileId, workspaceId, 1);
            log.debug("记录文件上传访问: fileId={}, userId={}, accessType=1(查看)", fileId, userId);
        } catch (Exception e) {
            log.error("记录文件上传访问失败: fileId={}", fileId, e);
        }

        // 14. 返回文件信息
        return queryById(fileId);
    }*/

    /**
     * 上传文件 转换（wps、et、dps）
     * @param file
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoFileVo uploadFile(Long workspaceId, Long folderId, MultipartFile file) {
        // 1. 验证文件
        if (file == null || file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }
        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new ServiceException("文件名不能为空");
        }

        // 2. 获取文件信息
        String fileExtension = FileUtil.extName(originalFilename);
        String fileType = fileExtension.toLowerCase();
        long fileSize = file.getSize();
        String mimeType = file.getContentType();

        // 3. 验证文件类型
        if (!isValidFileType(fileType)) {
            throw new ServiceException("不支持的文件类型：" + fileType);
        }

        // 4. 验证文件大小
        long maxFileSize = onlyOfficeConfig.getMaxFileSize() * 1024 * 1024; // MB转字节
        if (fileSize > maxFileSize) {
            throw new ServiceException("文件大小超过限制：" + onlyOfficeConfig.getMaxFileSize() + "MB");
        }

        // 5. 检查存储空间
        if (!workspaceService.checkStorageAvailable(workspaceId, fileSize)) {
            throw new ServiceException("工作空间存储空间不足");
        }

        // 6. 允许同名文件存在,符合在线文档系统的业务需求
        // 已移除文件名重复性校验

        // 7. 生成文件ID和存储路径
        Long fileId = IdUtil.getSnowflakeNextId();
        String storagePath = buildStoragePath(workspaceId, folderId, fileId, originalFilename);

        // ==============================================
        // 【核心：判断是否需要转换 —— 只有 wps/et/dps 才转】
        // ==============================================
        boolean needConvert = Arrays.asList("wps", "et", "dps").contains(fileType);

        File convertedFile = null;
        String convertedExt = fileType; // 默认使用原后缀

        if (needConvert) {
            try {
                convertedFile = fileTo(file);
                convertedExt = FileUtil.extName(convertedFile.getName());
                // 替换路径后缀
                storagePath = storagePath.replaceFirst("\\.[^.]+$", "." + convertedExt);
            } catch (Exception e) {
                log.error("文件格式转换失败", e);
                throw new ServiceException("文件格式转换失败：" + e.getMessage());
            }
        }

        // 8. 保存文件（转换后的 或 原始文件）
        try {
            File destFile = new File(storagePath);
            FileUtil.mkParentDirs(destFile);

            if (needConvert) {
                // 保存转换后的文件
                FileUtil.copyFile(convertedFile, destFile);
                FileUtil.del(convertedFile); // 删除临时文件
            } else {
                // 直接保存原始文件
                file.transferTo(destFile);
            }
        } catch (Exception e) {
            log.error("文件保存失败", e);
            throw new ServiceException("文件保存失败：" + e.getMessage());
        }

        // 最终文件信息
        String finalFileName = needConvert ? (FileUtil.mainName(originalFilename) + "." + convertedExt) : originalFilename;
        String finalFileType = convertedExt.toLowerCase();
        String finalMimeType = needConvert ? getConvertedMimeType(finalFileType) : mimeType;

        // 9. 生成documentKey
        String documentKey = generateDocumentKey(fileId, 1);

        // 10. 获取当前用户信息
        Long userId = LoginHelper.getUserId();
        String username = LoginHelper.getUserName();

        // 11. 创建文件记录
        CoFileBo fileBo = new CoFileBo();
        fileBo.setWorkspaceId(workspaceId);
        fileBo.setFolderId(folderId);
        fileBo.setFileName(finalFileName); // 正确的文件名
        fileBo.setFileType(fileType); // 正确类型
        fileBo.setFileSize(fileSize);
        fileBo.setFilePath(storagePath);
        fileBo.setFileExtension(finalFileType);
        fileBo.setMimeType(finalMimeType);
        fileBo.setDocumentKey(documentKey);
        fileBo.setCurrentVersion(1);
        fileBo.setEditStatus(WorkspaceConstants.EditStatus.NOT_EDITED);
        fileBo.setOwnerId(userId);
        fileBo.setOwnerName(username);
        fileBo.setStatus(WorkspaceConstants.FileStatus.NORMAL);

        fileBo.setId(fileId);

        // 插入数据库
        if (!insertByBo(fileBo)) {
            FileUtil.del(storagePath);
            throw new ServiceException("文件记录创建失败");
        }

        // 12. 更新工作空间存储使用量
        workspaceService.updateStorageUsage(workspaceId, fileSize);

        // 13. 记录文件访问
        try {
            fileAccessRecordService.recordAccess(fileId, userId, 1);
            accessRecordService.recordFileAccess(userId, fileId, workspaceId, 1);
            log.debug("记录文件上传访问: fileId={}, userId={}", fileId, userId);
        } catch (Exception e) {
            log.error("记录文件上传访问失败: fileId={}", fileId, e);
        }

        return queryById(fileId);
    }

    /**
     * 进行转换
     * @param file 文件
     * @return
     * @throws Exception
     */
    private File fileTo(MultipartFile file) throws Exception {
        // 1. 创建保存目录
        File saveDir = new File(SAVE_PATH);
        if (!saveDir.exists()) {
            saveDir.mkdirs(); // 自动创建文件夹
        }

        // 2. 获取文件信息
        String originalFilename = file.getOriginalFilename();
        String suffix = getFileSuffix(originalFilename).toLowerCase();
        String targetExt = getTargetExtension(suffix);

        if (targetExt == null) {
            throw new RuntimeException("仅支持 wps / et / dps 格式");
        }

        // 3. 新文件名（带时间戳，不重复）
        String fileNameNoSuffix = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        String newFileName = fileNameNoSuffix + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "." + targetExt;

        // ✅【最终文件路径】你要的路径在这里！
        File outputFile = new File(saveDir, newFileName);

        System.out.println("📁 转换后文件将保存到：" + outputFile.getAbsolutePath());

        // 4. 连通服务器
        try (Socket socket = new Socket(libreHost, librePort)) {
            System.out.println("✅ 转换服务连接成功");
        }

        // 5. 执行转换（真实内容）
        try (InputStream in = file.getInputStream();
             FileOutputStream out = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }

        System.out.println("🎉 转换完成！文件路径：" + outputFile.getAbsolutePath());
        return outputFile;
    }


    /**
     * 根据转换后的文件类型获取正确的MIME类型
     */
    private String getConvertedMimeType(String fileType) {
        return switch (fileType) {
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default -> "application/octet-stream";
        };
    }
    // 获取文件后缀
    private String getFileSuffix(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    // 获取目标格式
    private String getTargetExtension(String suffix) {
        return switch (suffix) {
            case "wps" -> "docx";
            case "et" -> "xlsx";
            case "dps" -> "pptx";
            default -> null;
        };
    }

    /**
     * 下载文件
     */
    @Override
    public byte[] downloadFile(Long id) {
        CoFileVo file = queryById(id);
        if (file == null) {
            throw new ServiceException("文件不存在");
        }

        // 检查文件物理路径是否存在
        File physicalFile = new File(file.getFilePath());
        if (!physicalFile.exists()) {
            throw new ServiceException("文件不存在");
        }

        // 读取文件内容
        byte[] fileBytes = FileUtil.readBytes(physicalFile);

        // 更新下载次数
        incrementDownloadCount(id);

        return fileBytes;
    }

    /**
     * 获取文件物理路径
     */
    @Override
    public String getFilePath(Long id) {
        CoFileVo file = queryById(id);
        if (file == null) {
            throw new ServiceException("文件不存在");
        }
        return file.getFilePath();
    }

    /**
     * 更新文件编辑状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateEditStatus(Long id, Integer editStatus, Long editorId, String editorName) {
        LambdaUpdateWrapper<CoFile> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoFile::getId, id);
        updateWrapper.set(CoFile::getEditStatus, editStatus);
        updateWrapper.set(CoFile::getCurrentEditorId, editorId);
        updateWrapper.set(CoFile::getCurrentEditorName, editorName);

        if (editStatus == WorkspaceConstants.EditStatus.EDITING) {
            updateWrapper.set(CoFile::getEditStartTime, new Date());
        }

        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 更新文件路径和大小
     * 用于OnlyOffice保存文档后同步数据库信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateFilePathAndSize(Long id, String filePath, Long fileSize) {
        LambdaUpdateWrapper<CoFile> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoFile::getId, id);
        updateWrapper.set(CoFile::getFilePath, filePath);
        updateWrapper.set(CoFile::getFileSize, fileSize);
        updateWrapper.set(CoFile::getLastEditTime, new Date());

        int result = baseMapper.update(null, updateWrapper);
        log.debug("更新文件路径和大小: fileId={}, filePath={}, fileSize={}, result={}",
                  id, filePath, fileSize, result);
        return result > 0;
    }

    /**
     * 更新文件锁定状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateLockStatus(Long id, Integer isLocked, Long lockedBy, String lockedByName, String lockReason) {
        LambdaUpdateWrapper<CoFile> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoFile::getId, id);
        updateWrapper.set(CoFile::getIsLocked, isLocked);
        updateWrapper.set(CoFile::getLockedBy, lockedBy);
        updateWrapper.set(CoFile::getLockedByName, lockedByName);
        updateWrapper.set(CoFile::getLockReason, lockReason);

        if (isLocked == 1) {
            updateWrapper.set(CoFile::getLockedTime, new Date());
        } else {
            updateWrapper.set(CoFile::getLockedTime, null);
        }

        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 更新文件下载次数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean incrementDownloadCount(Long id) {
        LambdaUpdateWrapper<CoFile> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoFile::getId, id);
        updateWrapper.setSql("download_count = download_count + 1");
        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 更新文件查看次数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean incrementViewCount(Long id) {
        LambdaUpdateWrapper<CoFile> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoFile::getId, id);
        updateWrapper.setSql("view_count = view_count + 1");
        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 更新文件编辑次数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean incrementEditCount(Long id) {
        LambdaUpdateWrapper<CoFile> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoFile::getId, id);
        updateWrapper.setSql("edit_count = edit_count + 1");
        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 检查文件是否存在
     */
    @Override
    public Boolean checkFileExists(Long workspaceId, Long folderId, String fileName) {
        LambdaQueryWrapper<CoFile> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoFile::getWorkspaceId, workspaceId);
        lqw.eq(folderId != null, CoFile::getFolderId, folderId);
        lqw.isNull(folderId == null, CoFile::getFolderId);
        lqw.eq(CoFile::getFileName, fileName);
        lqw.eq(CoFile::getStatus, WorkspaceConstants.FileStatus.NORMAL);
        return baseMapper.selectCount(lqw) > 0;
    }

    /**
     * 检查文件是否被锁定
     */
    @Override
    public Boolean checkFileLocked(Long id) {
        CoFileVo file = queryById(id);
        if (file == null) {
            return false;
        }
        return file.getIsLocked() != null && file.getIsLocked() == 1;
    }

    /**
     * 查询工作空间下的文件列表
     */
    @Override
    public List<CoFileVo> queryByWorkspaceId(Long workspaceId) {
        LambdaQueryWrapper<CoFile> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoFile::getWorkspaceId, workspaceId);
        lqw.eq(CoFile::getStatus, WorkspaceConstants.FileStatus.NORMAL);
        lqw.orderByDesc(CoFile::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询文件夹下的文件列表
     */
    @Override
    public List<CoFileVo> queryByFolderId(Long folderId) {
        LambdaQueryWrapper<CoFile> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoFile::getFolderId, folderId);
        lqw.eq(CoFile::getStatus, WorkspaceConstants.FileStatus.NORMAL);
        lqw.orderByDesc(CoFile::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询用户的文件列表
     */
    @Override
    public List<CoFileVo> queryByUserId(Long userId) {
        LambdaQueryWrapper<CoFile> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoFile::getOwnerId, userId);
        lqw.eq(CoFile::getStatus, WorkspaceConstants.FileStatus.NORMAL);
        lqw.orderByDesc(CoFile::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 构建文件存储路径
     */
    private String buildStoragePath(Long workspaceId, Long folderId, Long fileId, String fileName) {
        String basePath = onlyOfficeConfig.getStoragePath();
        String folderPath = folderId != null ? folderId.toString() : "root";
        return basePath + File.separator + workspaceId + File.separator + folderPath +
               File.separator + fileId + File.separator + fileName;
    }

    /**
     * 生成OnlyOffice documentKey
     */
    private String generateDocumentKey(Long fileId, Integer version) {
        return fileId + "_" + version + "_" + System.currentTimeMillis();
    }

    /**
     * 验证文件类型是否支持
     */
    private boolean isValidFileType(String fileType) {
        // 支持的文件类型
        String[] supportedTypes = {
            "docx", "doc", "docm", "dot", "dotx", "dotm", "odt", "fodt", "ott", "rtf", "txt",
            "xlsx", "xls", "xlsm", "xlt", "xltx", "xltm", "ods", "fods", "ots", "csv",
            "pptx", "ppt", "pptm", "pps", "ppsx", "ppsm", "pot", "potx", "potm", "odp", "fodp", "otp",
            "pdf",
            "wps",          // WPS文字格式
            "et", "ett",    // WPS表格格式
            "dps", "dpt"    // WPS演示格式
        };

        for (String type : supportedTypes) {
            if (type.equalsIgnoreCase(fileType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 移动文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean moveFile(Long fileId, Long targetFolderId) {
        CoFileVo file = queryById(fileId);
        if (file == null) {
            throw new ServiceException("文件不存在");
        }

        // 检查目标文件夹是否存在
        if (targetFolderId != null && targetFolderId > 0) {
            CoFolderVo targetFolder = folderService.queryById(targetFolderId);
            if (targetFolder == null) {
                throw new ServiceException("目标文件夹不存在");
            }
        }

        // 允许目标位置存在同名文件,符合在线文档系统的业务需求
        // 已移除文件名重复性校验

        // 更新文件的文件夹ID
        Long oldFolderId = file.getFolderId();
        LambdaUpdateWrapper<CoFile> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoFile::getId, fileId);
        updateWrapper.set(CoFile::getFolderId, targetFolderId);

        boolean result = baseMapper.update(null, updateWrapper) > 0;

        if (result) {
            // 更新原文件夹的文件数量和大小
            if (oldFolderId != null && oldFolderId > 0) {
                folderService.updateFileCount(oldFolderId, -1);
                folderService.updateTotalSize(oldFolderId, -file.getFileSize());
            }
            // 更新新文件夹的文件数量和大小
            if (targetFolderId != null && targetFolderId > 0) {
                folderService.updateFileCount(targetFolderId, 1);
                folderService.updateTotalSize(targetFolderId, file.getFileSize());
            }
        }

        return result;
    }

    /**
     * 复制文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoFileVo copyFile(Long fileId, Long targetFolderId, String newFileName) {
        CoFileVo sourceFile = queryById(fileId);
        if (sourceFile == null) {
            throw new ServiceException("源文件不存在");
        }

        // 检查目标文件夹是否存在
        if (targetFolderId != null && targetFolderId > 0) {
            CoFolderVo targetFolder = folderService.queryById(targetFolderId);
            if (targetFolder == null) {
                throw new ServiceException("目标文件夹不存在");
            }
        }

        // 使用新文件名或原文件名
        String fileName = StringUtils.isNotBlank(newFileName) ? newFileName : sourceFile.getFileName();

        // 允许目标位置存在同名文件,符合在线文档系统的业务需求
        // 已移除文件名重复性校验

        // 检查存储空间
        if (!workspaceService.checkStorageAvailable(sourceFile.getWorkspaceId(), sourceFile.getFileSize())) {
            throw new ServiceException("工作空间存储空间不足");
        }

        // 生成新文件ID和存储路径
        Long newFileId = IdUtil.getSnowflakeNextId();
        String newStoragePath = buildStoragePath(sourceFile.getWorkspaceId(), targetFolderId, newFileId, fileName);

        // 复制物理文件
        try {
            File sourcePhysicalFile = new File(sourceFile.getFilePath());
            File destPhysicalFile = new File(newStoragePath);
            FileUtil.mkParentDirs(destPhysicalFile);
            FileUtil.copy(sourcePhysicalFile, destPhysicalFile, true);
        } catch (Exception e) {
            log.error("文件复制失败", e);
            throw new ServiceException("文件复制失败：" + e.getMessage());
        }

        // 生成新的documentKey
        String newDocumentKey = generateDocumentKey(newFileId, 1);

        // 创建新文件记录
        CoFileBo newFileBo = new CoFileBo();
        BeanUtil.copyProperties(sourceFile, newFileBo);
        newFileBo.setId(newFileId);
        newFileBo.setFolderId(targetFolderId);
        newFileBo.setFileName(fileName);
        newFileBo.setFilePath(newStoragePath);
        newFileBo.setDocumentKey(newDocumentKey);
        newFileBo.setCurrentVersion(1);
        newFileBo.setDownloadCount(0);
        newFileBo.setViewCount(0);
        newFileBo.setEditCount(0);
        newFileBo.setShareCount(0);

        if (!insertByBo(newFileBo)) {
            // 删除已复制的物理文件
            FileUtil.del(newStoragePath);
            throw new ServiceException("文件记录创建失败");
        }

        // 更新工作空间存储使用量
        workspaceService.updateStorageUsage(sourceFile.getWorkspaceId(), sourceFile.getFileSize());

        // 更新目标文件夹的文件数量和大小
        if (targetFolderId != null && targetFolderId > 0) {
            folderService.updateFileCount(targetFolderId, 1);
            folderService.updateTotalSize(targetFolderId, sourceFile.getFileSize());
        }

        return queryById(newFileId);
    }

    /**
     * 重命名文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean renameFile(Long fileId, String newFileName) {
        CoFileVo file = queryById(fileId);
        if (file == null) {
            throw new ServiceException("文件不存在");
        }

        // 允许同名文件存在,符合在线文档系统的业务需求
        // 已移除文件名重复性校验

        // 获取新文件扩展名
        String newFileExtension = FileUtil.extName(newFileName);
        String newFileType = newFileExtension.toLowerCase();

        // 更新文件名和扩展名
        LambdaUpdateWrapper<CoFile> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoFile::getId, fileId);
        updateWrapper.set(CoFile::getFileName, newFileName);
        updateWrapper.set(CoFile::getFileExtension, newFileExtension);
        updateWrapper.set(CoFile::getFileType, newFileType);

        // 可选：重命名物理文件
        // 这里暂不重命名物理文件，保持原有的UUID命名方式

        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 批量移动文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchMoveFiles(List<Long> fileIds, Long targetFolderId) {
        for (Long fileId : fileIds) {
            if (!moveFile(fileId, targetFolderId)) {
                throw new ServiceException("文件移动失败：" + fileId);
            }
        }
        return true;
    }

    /**
     * 批量复制文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchCopyFiles(List<Long> fileIds, Long targetFolderId) {
        for (Long fileId : fileIds) {
            copyFile(fileId, targetFolderId, null);
        }
        return true;
    }

    /**
     * 创建在线文档
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoFileVo createDocument(Long workspaceId, Long folderId, String fileName, String fileType) {
        // 1. 验证参数
        if (workspaceId == null) {
            throw new ServiceException("工作空间ID不能为空");
        }
        if (StringUtils.isBlank(fileName)) {
            throw new ServiceException("文件名不能为空");
        }
        if (StringUtils.isBlank(fileType)) {
            throw new ServiceException("文件类型不能为空");
        }

        // 2. 验证文件类型是否支持创建
        if (!documentTemplateService.isSupportedType(fileType)) {
            throw new ServiceException("不支持创建该类型的文档：" + fileType);
        }

        // 3. 确保文件名包含扩展名
        String fullFileName = fileName;
        if (!fileName.toLowerCase().endsWith("." + fileType.toLowerCase())) {
            fullFileName = fileName + "." + fileType.toLowerCase();
        }

        // 4. 允许同名文件存在,符合在线文档系统的业务需求
        // 已移除文件名重复性校验

        // 5. 生成文件ID和存储路径
        Long fileId = IdUtil.getSnowflakeNextId();
        String storagePath = buildStoragePath(workspaceId, folderId, fileId, fullFileName);

        // 6. 创建文档模板
        try {
            documentTemplateService.createTemplate(storagePath, fileType, fileName);
        } catch (Exception e) {
            log.error("创建文档模板失败", e);
            throw new ServiceException("创建文档模板失败：" + e.getMessage());
        }

        // 7. 获取文件大小
        File file = new File(storagePath);
        long fileSize = file.length();

        // 8. 检查存储空间
        if (!workspaceService.checkStorageAvailable(workspaceId, fileSize)) {
            // 删除已创建的文件
            FileUtil.del(storagePath);
            throw new ServiceException("工作空间存储空间不足");
        }

        // 9. 生成documentKey
        String documentKey = generateDocumentKey(fileId, 1);

        // 10. 获取当前用户信息
        Long userId = LoginHelper.getUserId();
        String username = LoginHelper.getUserName();

        // 11. 创建文件记录
        CoFileBo fileBo = new CoFileBo();
        fileBo.setId(fileId);
        fileBo.setWorkspaceId(workspaceId);
        fileBo.setFolderId(folderId);
        fileBo.setFileName(fullFileName);
        fileBo.setFileType(fileType.toLowerCase());
        fileBo.setFileSize(fileSize);
        fileBo.setFilePath(storagePath);
        fileBo.setFileExtension(fileType.toLowerCase());
        fileBo.setMimeType(getMimeType(fileType));
        fileBo.setDocumentKey(documentKey);
        fileBo.setCurrentVersion(1);
        fileBo.setEditStatus(WorkspaceConstants.EditStatus.NOT_EDITED);
        fileBo.setOwnerId(userId);
        fileBo.setOwnerName(username);
        fileBo.setStatus(WorkspaceConstants.FileStatus.NORMAL);

        // 插入数据库
        if (!insertByBo(fileBo)) {
            // 删除已创建的文件
            FileUtil.del(storagePath);
            throw new ServiceException("文件记录创建失败");
        }

        // 12. 更新工作空间存储使用量
        workspaceService.updateStorageUsage(workspaceId, fileSize);

        // 13. 更新文件夹统计
        if (folderId != null && folderId > 0) {
            folderService.updateFileCount(folderId, 1);
            folderService.updateTotalSize(folderId, fileSize);
        }

        // 14. 记录文件访问 - 创建后查看
        try {
            // 记录到文件访问记录表
            fileAccessRecordService.recordAccess(fileId, userId, 1); // 1-查看
            // 记录到通用访问记录表
            accessRecordService.recordFileAccess(userId, fileId, workspaceId, 1);
            log.debug("记录文档创建访问: fileId={}, userId={}, accessType=1(查看)", fileId, userId);
        } catch (Exception e) {
            log.error("记录文档创建访问失败: fileId={}", fileId, e);
        }

        // 15. 返回文件信息
        log.info("在线文档创建成功: fileId={}, fileName={}, fileType={}", fileId, fullFileName, fileType);
        return queryById(fileId);
    }



    /**
     * 根据文件类型获取MIME类型
     */
    private String getMimeType(String fileType) {
        switch (fileType.toLowerCase()) {
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt":
                return "text/plain";
            default:
                return "application/octet-stream";
        }
    }


        /**
     * 清空文件表（物理删除）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearAll() {
        baseMapper.clearAll();
    }


}



