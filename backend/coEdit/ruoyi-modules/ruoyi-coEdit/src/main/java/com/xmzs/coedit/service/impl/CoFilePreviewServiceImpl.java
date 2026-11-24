package com.xmzs.coedit.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.domain.CoFileThumbnail;
import com.xmzs.coedit.domain.bo.CoFilePreviewBo;
import com.xmzs.coedit.domain.vo.CoFilePreviewVo;
import com.xmzs.coedit.domain.vo.CoFileThumbnailVo;
import com.xmzs.coedit.mapper.CoFileMapper;
import com.xmzs.coedit.mapper.CoFileThumbnailMapper;
import com.xmzs.coedit.service.ICoFilePreviewService;
import com.xmzs.common.core.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件预览Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoFilePreviewServiceImpl implements ICoFilePreviewService {

    private final CoFileMapper fileMapper;
    private final CoFileThumbnailMapper thumbnailMapper;

    @Value("${onlyoffice.url:http://192.168.245.129:9000}")
    private String onlyOfficeUrl;

    @Value("${file.upload.path:/upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * 支持预览的文件类型
     */
    private static final String[] PREVIEW_TYPES = {
        "docx", "doc", "xlsx", "xls", "pptx", "ppt",
        "pdf", "txt", "csv", "odt", "ods", "odp",
        "wps",          // WPS文字格式
        "et", "ett",    // WPS表格格式
        "dps", "dpt"    // WPS演示格式
    };

    /**
     * 支持缩略图的图片类型
     */
    private static final String[] IMAGE_TYPES = {
        "jpg", "jpeg", "png", "gif", "bmp", "webp"
    };

    /**
     * 生成文件预览配置
     */
    @Override
    public CoFilePreviewVo generatePreviewConfig(CoFilePreviewBo previewBo, Long userId) {
        // 查询文件信息
        CoFile file = fileMapper.selectById(previewBo.getFileId());
        if (file == null) {
            throw new ServiceException("文件不存在");
        }

        // 检查是否支持预览
        if (!isSupportPreview(file.getFileType())) {
            throw new ServiceException("该文件类型不支持预览");
        }

        CoFilePreviewVo previewVo = new CoFilePreviewVo();
        previewVo.setFileId(file.getId());
        previewVo.setFileName(file.getFileName());
        previewVo.setFileType(file.getFileType());
        previewVo.setFileSize(file.getFileSize());
        previewVo.setSupportPreview(true);
        previewVo.setMode(previewBo.getMode());

        // 生成预览URL
        String previewUrl = generatePreviewUrl(file);
        previewVo.setPreviewUrl(previewUrl);

        // 生成OnlyOffice编辑器配置
        Map<String, Object> editorConfig = buildEditorConfig(file, previewBo, userId);
        previewVo.setEditorConfig(editorConfig);

        // 生成文档配置
        Map<String, Object> documentConfig = buildDocumentConfig(file);
        previewVo.setDocumentConfig(documentConfig);

        // 配置水印
        if (Boolean.TRUE.equals(previewBo.getShowWatermark())) {
            CoFilePreviewVo.WatermarkConfig watermark = new CoFilePreviewVo.WatermarkConfig();
            watermark.setText(StrUtil.isNotBlank(previewBo.getWatermarkText())
                ? previewBo.getWatermarkText()
                : "仅供预览");
            previewVo.setWatermark(watermark);
        }

        // 生成缩略图
        if (Boolean.TRUE.equals(previewBo.getGenerateThumbnail())) {
            try {
                CoFileThumbnailVo thumbnail = getThumbnail(
                    file.getId(),
                    previewBo.getThumbnailWidth(),
                    previewBo.getThumbnailHeight()
                );
                if (thumbnail != null) {
                    previewVo.setThumbnailUrl(thumbnail.getThumbnailUrl());
                }
            } catch (Exception e) {
                log.error("生成缩略图失败", e);
            }
        }

        return previewVo;
    }

    /**
     * 构建编辑器配置
     */
    private Map<String, Object> buildEditorConfig(CoFile file, CoFilePreviewBo previewBo, Long userId) {
        Map<String, Object> config = new HashMap<>();

        // 文档类型
        config.put("documentType", getDocumentType(file.getFileType()));

        // 文档信息
        Map<String, Object> document = new HashMap<>();
        document.put("fileType", file.getFileType());
        document.put("key", file.getId() + "_" + file.getUpdateTime().getTime());
        document.put("title", file.getFileName());
        document.put("url", generateFileUrl(file));
        config.put("document", document);

        // 编辑器类型
        config.put("type", "desktop");

        // 编辑器模式
        config.put("mode", previewBo.getMode());

        // 用户信息
        Map<String, Object> editorConfig = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("id", userId.toString());
        user.put("name", "User" + userId);
        editorConfig.put("user", user);

        // 自定义配置
        Map<String, Object> customization = new HashMap<>();
        customization.put("forcesave", false);
        customization.put("comments", "view".equals(previewBo.getMode()) ? false : true);
        customization.put("chat", false);
        customization.put("plugins", false);
        customization.put("compactHeader", true);

        // 隐藏"提出功能建议"按钮 (id="fm-btn-suggest")
        customization.put("suggestFeature", false);

        // 隐藏"反馈与支持"按钮
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("visible", false);
        customization.put("feedback", feedback);

        customization.put("help", false);
        customization.put("hideRightMenu", "view".equals(previewBo.getMode()));
        editorConfig.put("customization", customization);

        config.put("editorConfig", editorConfig);

        return config;
    }

    /**
     * 构建文档配置
     */
    private Map<String, Object> buildDocumentConfig(CoFile file) {
        Map<String, Object> config = new HashMap<>();
        config.put("fileType", file.getFileType());
        config.put("key", file.getId() + "_" + file.getUpdateTime().getTime());
        config.put("title", file.getFileName());
        config.put("url", generateFileUrl(file));

        // 文档权限
        Map<String, Object> permissions = new HashMap<>();
        permissions.put("comment", false);
        permissions.put("download", true);
        permissions.put("edit", false);
        permissions.put("fillForms", false);
        permissions.put("modifyFilter", false);
        permissions.put("modifyContentControl", false);
        permissions.put("review", false);
        permissions.put("print", true);
        config.put("permissions", permissions);

        return config;
    }

    /**
     * 获取文档类型
     */
    private String getDocumentType(String fileType) {
        String type = fileType.toLowerCase();
        // Word文档（包含WPS文字格式）
        if (Arrays.asList("docx", "doc", "odt", "txt", "wps").contains(type)) {
            return "word";
        // Excel表格（包含WPS表格格式）
        } else if (Arrays.asList("xlsx", "xls", "ods", "csv", "et", "ett").contains(type)) {
            return "cell";
        // PowerPoint演示（包含WPS演示格式）
        } else if (Arrays.asList("pptx", "ppt", "odp", "dps", "dpt").contains(type)) {
            return "slide";
        } else if ("pdf".equals(type)) {
            return "word"; // PDF使用word编辑器预览
        }
        return "word";
    }

    /**
     * 生成预览URL
     */
    private String generatePreviewUrl(CoFile file) {
        return contextPath + "/coedit/preview/" + file.getId();
    }

    /**
     * 生成文件URL
     */
    private String generateFileUrl(CoFile file) {
        return contextPath + "/coedit/file/download/" + file.getId();
    }

    /**
     * 检查文件是否支持预览
     */
    @Override
    public Boolean isSupportPreview(String fileType) {
        if (StrUtil.isBlank(fileType)) {
            return false;
        }
        return Arrays.asList(PREVIEW_TYPES).contains(fileType.toLowerCase());
    }

    /**
     * 获取文件缩略图
     */
    @Override
    public CoFileThumbnailVo getThumbnail(Long fileId, Integer width, Integer height) {
        // 查询已存在的缩略图
        CoFileThumbnail thumbnail = thumbnailMapper.selectOne(
            Wrappers.lambdaQuery(CoFileThumbnail.class)
                .eq(CoFileThumbnail::getFileId, fileId)
                .eq(CoFileThumbnail::getWidth, width)
                .eq(CoFileThumbnail::getHeight, height)
                .eq(CoFileThumbnail::getStatus, 1) // 成功状态
                .orderByDesc(CoFileThumbnail::getCreateTime)
                .last("LIMIT 1")
        );

        if (thumbnail != null) {
            // 更新访问统计
            updateThumbnailAccess(thumbnail.getId());

            CoFileThumbnailVo vo = thumbnailMapper.selectVoById(thumbnail.getId());
            vo.setStatusDesc(getStatusDesc(vo.getStatus()));
            return vo;
        }

        // 不存在则生成
        return generateThumbnail(fileId, width, height);
    }

    /**
     * 生成文件缩略图
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoFileThumbnailVo generateThumbnail(Long fileId, Integer width, Integer height) {
        // 查询文件信息
        CoFile file = fileMapper.selectById(fileId);
        if (file == null) {
            throw new ServiceException("文件不存在");
        }

        // 检查是否为图片类型
        if (!Arrays.asList(IMAGE_TYPES).contains(file.getFileType().toLowerCase())) {
            log.warn("文件类型{}不支持生成缩略图", file.getFileType());
            return null;
        }

        // 创建缩略图记录
        CoFileThumbnail thumbnail = new CoFileThumbnail();
        thumbnail.setFileId(fileId);
        thumbnail.setWidth(width);
        thumbnail.setHeight(height);
        thumbnail.setFormat("jpg");
        thumbnail.setStatus(0); // 生成中
        thumbnailMapper.insert(thumbnail);

        try {
            // 生成缩略图文件
            String thumbnailPath = generateThumbnailFile(file, width, height);

            // 更新缩略图信息
            thumbnail.setThumbnailPath(thumbnailPath);
            thumbnail.setThumbnailUrl(contextPath + "/upload/thumbnails/" + FileUtil.getName(thumbnailPath));

            File thumbFile = new File(thumbnailPath);
            thumbnail.setSize(thumbFile.length());
            thumbnail.setStatus(1); // 成功

            // 设置过期时间（30天）
            Date expireTime = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
            thumbnail.setExpireTime(expireTime);

            thumbnailMapper.updateById(thumbnail);

            CoFileThumbnailVo vo = thumbnailMapper.selectVoById(thumbnail.getId());
            vo.setStatusDesc(getStatusDesc(vo.getStatus()));
            return vo;

        } catch (Exception e) {
            log.error("生成缩略图失败", e);

            // 更新失败状态
            thumbnail.setStatus(2);
            thumbnail.setFailReason(e.getMessage());
            thumbnailMapper.updateById(thumbnail);

            throw new ServiceException("生成缩略图失败: " + e.getMessage());
        }
    }

    /**
     * 生成缩略图文件
     */
    private String generateThumbnailFile(CoFile file, Integer width, Integer height) throws IOException {
        // 读取原始图片
        File originalFile = new File(file.getFilePath());
        if (!originalFile.exists()) {
            throw new ServiceException("原始文件不存在");
        }

        BufferedImage originalImage = ImageIO.read(originalFile);
        if (originalImage == null) {
            throw new ServiceException("无法读取图片文件");
        }

        // 计算缩略图尺寸（保持宽高比）
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        double scale = Math.min(
            (double) width / originalWidth,
            (double) height / originalHeight
        );

        int thumbnailWidth = (int) (originalWidth * scale);
        int thumbnailHeight = (int) (originalHeight * scale);

        // 创建缩略图
        BufferedImage thumbnailImage = new BufferedImage(
            thumbnailWidth,
            thumbnailHeight,
            BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = thumbnailImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, thumbnailWidth, thumbnailHeight, null);
        g.dispose();

        // 保存缩略图
        String thumbnailDir = uploadPath + "/thumbnails";
        FileUtil.mkdir(thumbnailDir);

        String thumbnailFileName = file.getId() + "_" + width + "x" + height + ".jpg";
        String thumbnailPath = thumbnailDir + "/" + thumbnailFileName;

        File thumbnailFile = new File(thumbnailPath);
        ImageIO.write(thumbnailImage, "jpg", thumbnailFile);

        return thumbnailPath;
    }

    /**
     * 更新缩略图访问统计
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateThumbnailAccess(Long thumbnailId) {
        CoFileThumbnail thumbnail = thumbnailMapper.selectById(thumbnailId);
        if (thumbnail != null) {
            thumbnail.setAccessCount(thumbnail.getAccessCount() + 1);
            thumbnail.setLastAccessTime(new Date());
            thumbnailMapper.updateById(thumbnail);
        }
    }

    /**
     * 清理过期缩略图
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanExpiredThumbnails() {
        // 查询过期的缩略图
        Date now = new Date();
        var thumbnails = thumbnailMapper.selectList(
            Wrappers.lambdaQuery(CoFileThumbnail.class)
                .lt(CoFileThumbnail::getExpireTime, now)
        );

        int count = 0;
        for (CoFileThumbnail thumbnail : thumbnails) {
            try {
                // 删除文件
                if (StrUtil.isNotBlank(thumbnail.getThumbnailPath())) {
                    FileUtil.del(thumbnail.getThumbnailPath());
                }

                // 删除记录
                thumbnailMapper.deleteById(thumbnail.getId());
                count++;
            } catch (Exception e) {
                log.error("删除过期缩略图失败: {}", thumbnail.getId(), e);
            }
        }

        return count;
    }

    /**
     * 删除文件缩略图
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteThumbnail(Long fileId) {
        var thumbnails = thumbnailMapper.selectList(
            Wrappers.lambdaQuery(CoFileThumbnail.class)
                .eq(CoFileThumbnail::getFileId, fileId)
        );

        for (CoFileThumbnail thumbnail : thumbnails) {
            try {
                // 删除文件
                if (StrUtil.isNotBlank(thumbnail.getThumbnailPath())) {
                    FileUtil.del(thumbnail.getThumbnailPath());
                }

                // 删除记录
                thumbnailMapper.deleteById(thumbnail.getId());
            } catch (Exception e) {
                log.error("删除缩略图失败: {}", thumbnail.getId(), e);
            }
        }

        return true;
    }

    /**
     * 批量删除文件缩略图
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteThumbnails(Long[] fileIds) {
        for (Long fileId : fileIds) {
            deleteThumbnail(fileId);
        }
        return true;
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "生成中";
            case 1:
                return "成功";
            case 2:
                return "失败";
            default:
                return "未知";
        }
    }
}

