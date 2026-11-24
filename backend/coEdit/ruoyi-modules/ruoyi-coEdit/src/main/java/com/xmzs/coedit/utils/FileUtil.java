package com.xmzs.coedit.utils;

import com.xmzs.coedit.constant.OnlyOfficeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

/**
 * 文件工具类
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
public class FileUtil {

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名（小写，不含点）
     */
    public static String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 获取文件名（不含扩展名）
     *
     * @param filename 文件名
     * @return 文件名（不含扩展名）
     */
    public static String getFileNameWithoutExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(0, lastDotIndex);
        }
        return filename;
    }

    /**
     * 根据文件扩展名获取文档类型
     *
     * @param extension 文件扩展名
     * @return 文档类型（word/cell/slide）
     */
    public static String getDocumentType(String extension) {
        if (!StringUtils.hasText(extension)) {
            return OnlyOfficeConstants.DocumentType.WORD;
        }

        String ext = extension.toLowerCase();

        if (Arrays.asList(OnlyOfficeConstants.FileExtension.WORD_FORMATS).contains(ext)) {
            return OnlyOfficeConstants.DocumentType.WORD;
        } else if (Arrays.asList(OnlyOfficeConstants.FileExtension.CELL_FORMATS).contains(ext)) {
            return OnlyOfficeConstants.DocumentType.CELL;
        } else if (Arrays.asList(OnlyOfficeConstants.FileExtension.SLIDE_FORMATS).contains(ext)) {
            return OnlyOfficeConstants.DocumentType.SLIDE;
        }

        return OnlyOfficeConstants.DocumentType.WORD;
    }

    /**
     * 判断文件是否可编辑
     *
     * @param extension 文件扩展名
     * @return 是否可编辑
     */
    public static boolean isEditableFormat(String extension) {
        if (!StringUtils.hasText(extension)) {
            return false;
        }
        String ext = extension.toLowerCase();
        return "docx".equals(ext) || "xlsx".equals(ext) || "pptx".equals(ext) ||
               "txt".equals(ext) || "csv".equals(ext);
    }

    /**
     * 生成唯一的文档Key
     * 用于OnlyOffice文档标识
     *
     * @param fileId 文件ID
     * @param version 文件版本
     * @return 文档Key
     */
    public static String generateDocumentKey(Long fileId, Integer version) {
        return fileId + "_" + version + "_" + System.currentTimeMillis();
    }

    /**
     * 生成UUID文件名
     *
     * @param originalFilename 原始文件名
     * @return UUID文件名
     */
    public static String generateUuidFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return StringUtils.hasText(extension) ? uuid + "." + extension : uuid;
    }

    /**
     * 创建目录
     *
     * @param dirPath 目录路径
     * @return 是否创建成功
     */
    public static boolean createDirectory(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("创建目录成功: {}", dirPath);
            }
            return true;
        } catch (IOException e) {
            log.error("创建目录失败: {}", dirPath, e);
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("删除文件成功: {}", filePath);
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("删除文件失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 获取文件大小（字节）
     *
     * @param filePath 文件路径
     * @return 文件大小
     */
    public static long getFileSize(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                return Files.size(path);
            }
            return 0;
        } catch (IOException e) {
            log.error("获取文件大小失败: {}", filePath, e);
            return 0;
        }
    }

    /**
     * 格式化文件大小
     *
     * @param size 文件大小（字节）
     * @return 格式化后的文件大小
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 验证文件名是否合法
     *
     * @param filename 文件名
     * @return 是否合法
     */
    public static boolean isValidFilename(String filename) {
        if (!StringUtils.hasText(filename)) {
            return false;
        }
        // 检查是否包含非法字符
        String illegalChars = "[\\\\/:*?\"<>|]";
        return !filename.matches(".*" + illegalChars + ".*");
    }

    /**
     * 获取安全的文件路径
     * 防止路径遍历攻击
     *
     * @param basePath 基础路径
     * @param filename 文件名
     * @return 安全的文件路径
     */
    public static String getSafeFilePath(String basePath, String filename) {
        try {
            Path base = Paths.get(basePath).normalize();
            Path resolved = base.resolve(filename).normalize();

            if (!resolved.startsWith(base)) {
                throw new SecurityException("非法的文件路径");
            }

            return resolved.toString();
        } catch (Exception e) {
            log.error("获取安全文件路径失败", e);
            throw new SecurityException("非法的文件路径", e);
        }
    }
}

