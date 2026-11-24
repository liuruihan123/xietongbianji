package com.xmzs.coedit.service;

import com.xmzs.coedit.config.OnlyOfficeConfig;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文档管理服务（基础版）
 * 暂时使用文件系统存储，后续集成数据库
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@Service
public class DocumentService {

    private final OnlyOfficeConfig onlyOfficeConfig;

    @Autowired
    @Lazy
    private ICoFileService coFileService;

    public DocumentService(OnlyOfficeConfig onlyOfficeConfig) {
        this.onlyOfficeConfig = onlyOfficeConfig;
    }

    /**
     * 文档Key缓存（fileId -> documentKey）
     * 后续将移到数据库
     */
    private final Map<String, String> documentKeyCache = new ConcurrentHashMap<>();

    /**
     * 文档版本缓存（fileId -> version）
     * 后续将移到数据库
     */
    private final Map<String, Integer> documentVersionCache = new ConcurrentHashMap<>();

    /**
     * 文档信息缓存（fileId -> 文档信息）
     * 后续将移到数据库
     */
    private final Map<String, Map<String, Object>> documentInfoCache = new ConcurrentHashMap<>();

    /**
     * 上传文件
     *
     * @param file     文件
     * @param fileName 文件名
     * @return 文件ID
     */
    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        // 生成文件ID（暂时使用UUID）
        String fileId = java.util.UUID.randomUUID().toString();

        // 确保存储目录存在
        String storagePath = onlyOfficeConfig.getStoragePath();
        FileUtil.createDirectory(storagePath);

        // 保存文件
        String extension = FileUtil.getFileExtension(fileName);
        String savedFileName = fileId + "." + extension;
        Path filePath = Paths.get(storagePath, savedFileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 初始化文档信息
        Map<String, Object> docInfo = new HashMap<>();
        docInfo.put("fileId", fileId);
        docInfo.put("fileName", fileName);
        docInfo.put("extension", extension);
        docInfo.put("size", file.getSize());
        docInfo.put("uploadTime", System.currentTimeMillis());

        documentInfoCache.put(fileId, docInfo);
        documentVersionCache.put(fileId, 1);

        // 生成初始文档Key
        String documentKey = FileUtil.generateDocumentKey(Long.parseLong(fileId.hashCode() + ""), 1);
        documentKeyCache.put(fileId, documentKey);

        log.info("文件上传成功: fileId={}, fileName={}", fileId, fileName);
        return fileId;
    }

    /**
     * 获取文档Key
     *
     * @param fileId 文件ID
     * @return 文档Key
     */
    public String getDocumentKey(String fileId) {
        return documentKeyCache.computeIfAbsent(fileId, id -> {
            Integer version = documentVersionCache.getOrDefault(id, 1);
            return FileUtil.generateDocumentKey(Long.parseLong(id.hashCode() + ""), version);
        });
    }

    /**
     * 更新文档Key（保存新版本后）
     *
     * @param fileId 文件ID
     * @return 新的文档Key
     */
    public String updateDocumentKey(String fileId) {
        Integer version = documentVersionCache.getOrDefault(fileId, 1);
        version++;
        documentVersionCache.put(fileId, version);

        String newKey = FileUtil.generateDocumentKey(Long.parseLong(fileId.hashCode() + ""), version);
        documentKeyCache.put(fileId, newKey);

        log.info("文档Key已更新: fileId={}, version={}, newKey={}", fileId, version, newKey);
        return newKey;
    }

    /**
     * 强制设置文档Key（用于版本回滚等场景）
     * <p>
     * 【重要】此方法用于在版本回滚后同步更新内存缓存中的 documentKey
     * 确保后续的编辑器配置请求能获取到正确的 documentKey
     *
     * @param fileId 文件ID
     * @param newDocumentKey 新的文档Key
     */
    public void setDocumentKey(String fileId, String newDocumentKey) {
        String oldKey = documentKeyCache.get(fileId);
        documentKeyCache.put(fileId, newDocumentKey);
        log.info("[DocumentService] 强制更新documentKey缓存: fileId={}, oldKey={}, newKey={}",
            fileId, oldKey, newDocumentKey);
    }

    /**
     * 清除文档Key缓存（强制下次从数据库重新加载）
     *
     * @param fileId 文件ID
     */
    public void invalidateDocumentKeyCache(String fileId) {
        String removedKey = documentKeyCache.remove(fileId);
        log.info("[DocumentService] 清除documentKey缓存: fileId={}, removedKey={}", fileId, removedKey);
    }

    /**
     * 获取文档文件路径
     * <p>
     * 首先检查内存缓存，如果不存在则查询数据库
     *
     * @param fileId 文件ID
     * @return 文件路径
     */
    public String getDocumentPath(String fileId) {
        // 1. 首先检查内存缓存
        Map<String, Object> docInfo = documentInfoCache.get(fileId);
        if (docInfo != null) {
            String extension = (String) docInfo.get("extension");
            String fileName = fileId + "." + extension;
            return Paths.get(onlyOfficeConfig.getStoragePath(), fileName).toString();
        }

        // 2. 查询数据库
        try {
            Long id = Long.parseLong(fileId);
            CoFileVo fileVo = coFileService.queryById(id);

            if (fileVo != null) {
                // 对于数据库中的文件，直接返回其存储路径
                String filePath = fileVo.getFilePath();
                if (filePath != null && !filePath.isEmpty()) {
                    return filePath;
                }

                // 如果数据库中没有存储路径，则按照原有逻辑构建路径
                String extension = fileVo.getFileExtension();
                String fileName = fileId + "." + extension;
                return Paths.get(onlyOfficeConfig.getStoragePath(), fileName).toString();
            }
        } catch (NumberFormatException e) {
            log.debug("文档ID不是Long类型，跳过数据库查询: fileId={}", fileId);
        } catch (Exception e) {
            log.error("查询数据库时发生错误: fileId={}", fileId, e);
        }

        throw new RuntimeException("文档不存在: " + fileId);
    }

    /**
     * 获取文档文件
     *
     * @param fileId 文件ID
     * @return 文件对象
     */
    public File getDocumentFile(String fileId) {
        String filePath = getDocumentPath(fileId);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("文件不存在: " + filePath);
        }
        return file;
    }

    /**
     * 保存文档（覆盖原文件）
     *
     * @param fileId     文件ID
     * @param sourceFile 源文件
     */
    public void saveDocument(String fileId, File sourceFile) throws IOException {
        String targetPath = getDocumentPath(fileId);
        Path target = Paths.get(targetPath);

        Files.copy(sourceFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);

        // 更新文档信息
        Map<String, Object> docInfo = documentInfoCache.get(fileId);
        if (docInfo != null) {
            docInfo.put("lastModified", System.currentTimeMillis());
            docInfo.put("size", sourceFile.length());
        }

        // 【修复】更新数据库中的文件路径和大小
        // 这是修复文档内容丢失问题的关键：确保数据库中的filePath指向最新保存的文件
        try {
            Long id = Long.parseLong(fileId);
            coFileService.updateFilePathAndSize(id, targetPath, sourceFile.length());
            log.info("已更新数据库中的文件路径: fileId={}, path={}, size={}", fileId, targetPath, sourceFile.length());
        } catch (NumberFormatException e) {
            log.debug("文档ID不是Long类型，跳过数据库更新: fileId={}", fileId);
        } catch (Exception e) {
            log.error("更新数据库文件路径失败: fileId={}, path={}", fileId, targetPath, e);
            // 不抛出异常，避免影响文件保存流程
        }

        log.info("文档已保存: fileId={}, path={}", fileId, targetPath);
    }

    /**
     * 获取文档信息
     * <p>
     * 首先检查内存缓存，如果不存在则查询数据库
     *
     * @param fileId 文件ID
     * @return 文档信息
     */
    public Map<String, Object> getDocumentInfo(String fileId) {
        // 1. 首先检查内存缓存
        Map<String, Object> docInfo = documentInfoCache.get(fileId);
        if (docInfo != null) {
            return new HashMap<>(docInfo);
        }

        // 2. 查询数据库
        try {
            Long id = Long.parseLong(fileId);
            CoFileVo fileVo = coFileService.queryById(id);

            if (fileVo != null) {
                // 同步到缓存
                syncToCache(fileId, fileVo);

                // 返回文档信息
                Map<String, Object> result = new HashMap<>();
                result.put("fileId", fileId);
                result.put("fileName", fileVo.getFileName());
                result.put("extension", fileVo.getFileExtension());
                result.put("size", fileVo.getFileSize());
                result.put("uploadTime", fileVo.getCreateTime() != null ? fileVo.getCreateTime().getTime() : System.currentTimeMillis());

                return result;
            }
        } catch (NumberFormatException e) {
            log.debug("文档ID不是Long类型，跳过数据库查询: fileId={}", fileId);
        } catch (Exception e) {
            log.error("查询数据库时发生错误: fileId={}", fileId, e);
        }

        throw new RuntimeException("文档不存在: " + fileId);
    }

    /**
     * 获取文档URL
     * <p>
     * 重要: 此URL必须指向文件下载接口,返回实际的文件字节流
     * 不能指向返回JSON的文件信息接口,否则ONLYOFFICE会把JSON当作文件内容
     *
     * @param fileId    文件ID
     * @param serverUrl 服务器地址
     * @return 文档下载URL
     */
    public String getDocumentUrl(String fileId, String serverUrl, String authToken) {
        // 修复: 使用下载接口而不是信息接口，并携带authToken
        // 处理authToken为null的情况（访客模式），避免URL中出现"?authToken=null"
        if (authToken != null && !authToken.isEmpty()) {
            return serverUrl + "/coedit/file/download/" + fileId + "?authToken=" + authToken;
        } else {
            // 访客模式或无Token情况，不添加authToken参数
            return serverUrl + "/coedit/file/download/" + fileId;
        }
    }

    /**
     * 获取回调URL
     *
     * @param fileId 文件ID
     * @return 回调URL
     */
    public String getCallbackUrl(String fileId) {
        String baseUrl = onlyOfficeConfig.getCallbackUrl();
        if (baseUrl.endsWith("/")) {
            return baseUrl + fileId;
        }
        return baseUrl + "/" + fileId;
    }

    /**
     * 删除文档
     *
     * @param fileId 文件ID
     */
    public void deleteDocument(String fileId) {
        try {
            String filePath = getDocumentPath(fileId);
            FileUtil.deleteFile(filePath);

            documentKeyCache.remove(fileId);
            documentVersionCache.remove(fileId);
            documentInfoCache.remove(fileId);

            log.info("文档已删除: fileId={}", fileId);
        } catch (Exception e) {
            log.error("删除文档失败: fileId={}", fileId, e);
            throw new RuntimeException("删除文档失败", e);
        }
    }

    /**
     * 检查文档是否存在
     * <p>
     * 首先检查内存缓存（兼容通过DocumentService.uploadFile上传的文件）
     * 如果缓存中不存在，则尝试查询数据库（支持通过CoFileController.upload上传的文件）
     *
     * @param fileId 文件ID
     * @return 是否存在
     */
    public boolean exists(String fileId) {
        // 1. 首先检查内存缓存（兼容现有的DocumentService上传的文件）
        if (documentInfoCache.containsKey(fileId)) {
            log.debug("文档在内存缓存中找到: fileId={}", fileId);
            return true;
        }

        // 2. 尝试查询数据库（支持CoFileController上传的文件）
        try {
            // 尝试将fileId转换为Long类型
            Long id = Long.parseLong(fileId);
            CoFileVo fileVo = coFileService.queryById(id);

            if (fileVo != null) {
                log.debug("文档在数据库中找到: fileId={}, fileName={}", fileId, fileVo.getFileName());

                // 可选：将数据库中的文件信息同步到内存缓存，提高后续查询性能
                syncToCache(fileId, fileVo);

                return true;
            }
        } catch (NumberFormatException e) {
            // fileId不是Long类型，可能是UUID格式，只依赖内存缓存的结果
            log.debug("文档ID不是Long类型，跳过数据库查询: fileId={}", fileId);
        } catch (Exception e) {
            log.error("查询数据库时发生错误: fileId={}", fileId, e);
        }

        log.debug("文档不存在: fileId={}", fileId);
        return false;
    }

    /**
     * 将数据库中的文件信息同步到内存缓存
     *
     * @param fileId 文件ID
     * @param fileVo 文件信息
     */
    private void syncToCache(String fileId, CoFileVo fileVo) {
        try {
            // 创建文档信息并添加到缓存
            Map<String, Object> docInfo = new HashMap<>();
            docInfo.put("fileId", fileId);
            docInfo.put("fileName", fileVo.getFileName());
            docInfo.put("extension", fileVo.getFileExtension());
            docInfo.put("size", fileVo.getFileSize());
            docInfo.put("uploadTime", fileVo.getCreateTime() != null ? fileVo.getCreateTime().getTime() : System.currentTimeMillis());

            documentInfoCache.put(fileId, docInfo);

            // 同步版本信息
            documentVersionCache.put(fileId, fileVo.getCurrentVersion() != null ? fileVo.getCurrentVersion() : 1);

            // 生成并缓存documentKey
            if (!documentKeyCache.containsKey(fileId)) {
                String documentKey = FileUtil.generateDocumentKey(Long.parseLong(fileId),
                    fileVo.getCurrentVersion() != null ? fileVo.getCurrentVersion() : 1);
                documentKeyCache.put(fileId, documentKey);
            }

            log.debug("文档信息已同步到缓存: fileId={}", fileId);
        } catch (Exception e) {
            log.warn("同步文档信息到缓存失败: fileId={}", fileId, e);
        }
    }
}

