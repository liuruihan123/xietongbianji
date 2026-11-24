package com.xmzs.coedit.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.xmzs.coedit.config.OnlyOfficeConfig;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.common.core.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * 文档导出服务
 * 用于将文档导出为PDF、图片等格式
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentExportService {

    private final OnlyOfficeService onlyOfficeService;
    private final ICoFileService fileService;
    private final OnlyOfficeConfig onlyOfficeConfig;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * 导出文档为PDF
     *
     * @param fileId 文件ID
     * @return PDF文件字节数组
     */
    public byte[] exportToPdf(Long fileId) {
        log.info("开始导出PDF: fileId={}", fileId);

        // 1. 获取文件信息
        CoFileVo file = fileService.queryById(fileId);
        if (file == null) {
            throw new ServiceException("文件不存在");
        }

        // 2. 检查文件类型是否支持导出
        String fileType = file.getFileExtension();
        if (!isSupportedForPdfExport(fileType)) {
            throw new ServiceException("该文件类型不支持导出为PDF");
        }

        // 3. 构建文件访问URL
        String fileUrl = buildFileAccessUrl(fileId);
        log.info("文件访问URL: {}", fileUrl);

        // 4. 生成转换Key
        String convertKey = generateConvertKey(fileId);

        // 5. 调用OnlyOffice转换服务
        Map<String, Object> result = onlyOfficeService.convertDocument(
            fileUrl,
            fileType,
            "pdf",
            convertKey
        );

        // 6. 检查转换结果
        Integer error = (Integer) result.get("error");
        if (error == null || error != 0) {
            String message = (String) result.get("message");
            throw new ServiceException("文档转换失败: " + message);
        }

        // 7. 获取转换后的文件URL
        String convertedFileUrl = (String) result.get("fileUrl");
        if (convertedFileUrl == null || convertedFileUrl.isEmpty()) {
            throw new ServiceException("转换后的文件URL为空");
        }

        log.info("转换后的文件URL: {}", convertedFileUrl);

        // 8. 下载转换后的文件
        try {
            return downloadFile(convertedFileUrl);
        } catch (IOException e) {
            log.error("下载转换后的文件失败", e);
            throw new ServiceException("下载转换后的文件失败: " + e.getMessage());
        }
    }

    /**
     * 导出文档为图片（仅支持演示文稿）
     *
     * @param fileId 文件ID
     * @param imageFormat 图片格式（png/jpg）
     * @return 图片文件字节数组
     */
    public byte[] exportToImage(Long fileId, String imageFormat) {
        log.info("开始导出图片: fileId={}, format={}", fileId, imageFormat);

        // 1. 获取文件信息
        CoFileVo file = fileService.queryById(fileId);
        if (file == null) {
            throw new ServiceException("文件不存在");
        }

        // 2. 检查文件类型（仅支持演示文稿）
        String fileType = file.getFileExtension();
        if (!isSupportedForImageExport(fileType)) {
            throw new ServiceException("仅支持演示文稿（pptx）导出为图片");
        }

        // 3. 验证图片格式
        if (!"png".equalsIgnoreCase(imageFormat) && !"jpg".equalsIgnoreCase(imageFormat)) {
            throw new ServiceException("不支持的图片格式，仅支持png和jpg");
        }

        // 4. 构建文件访问URL
        String fileUrl = buildFileAccessUrl(fileId);

        // 5. 生成转换Key
        String convertKey = generateConvertKey(fileId);

        // 6. 调用OnlyOffice转换服务
        Map<String, Object> result = onlyOfficeService.convertDocument(
            fileUrl,
            fileType,
            imageFormat.toLowerCase(),
            convertKey
        );

        // 7. 检查转换结果
        Integer error = (Integer) result.get("error");
        if (error == null || error != 0) {
            String message = (String) result.get("message");
            throw new ServiceException("文档转换失败: " + message);
        }

        // 8. 获取转换后的文件URL
        String convertedFileUrl = (String) result.get("fileUrl");
        if (convertedFileUrl == null || convertedFileUrl.isEmpty()) {
            throw new ServiceException("转换后的文件URL为空");
        }

        // 9. 下载转换后的文件
        try {
            return downloadFile(convertedFileUrl);
        } catch (IOException e) {
            log.error("下载转换后的文件失败", e);
            throw new ServiceException("下载转换后的文件失败: " + e.getMessage());
        }
    }

    /**
     * 构建文件访问URL
     *
     * @param fileId 文件ID
     * @return 文件访问URL
     */
    private String buildFileAccessUrl(Long fileId) {
        // 使用本地服务器地址构建URL
        String baseUrl = "http://localhost:" + serverPort;
        if (contextPath != null && !contextPath.isEmpty()) {
            baseUrl += contextPath;
        }
        return baseUrl + "/coedit/file/download/" + fileId;
    }

    /**
     * 生成转换Key
     *
     * @param fileId 文件ID
     * @return 转换Key
     */
    private String generateConvertKey(Long fileId) {
        return fileId + "_convert_" + System.currentTimeMillis();
    }

    /**
     * 下载文件
     *
     * @param fileUrl 文件URL
     * @return 文件字节数组
     * @throws IOException IO异常
     */
    private byte[] downloadFile(String fileUrl) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(fileUrl);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                return response.getEntity().getContent().readAllBytes();
            }
        }
    }

    /**
     * 检查文件类型是否支持导出为PDF
     *
     * @param fileType 文件类型
     * @return 是否支持
     */
    private boolean isSupportedForPdfExport(String fileType) {
        String[] supportedTypes = {
            "docx", "doc", "docm", "dot", "dotx", "dotm", "odt", "rtf", "txt",
            "xlsx", "xls", "xlsm", "xlt", "xltx", "xltm", "ods", "csv",
            "pptx", "ppt", "pptm", "pps", "ppsx", "ppsm", "pot", "potx", "potm", "odp",
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
     * 检查文件类型是否支持导出为图片
     *
     * @param fileType 文件类型
     * @return 是否支持
     */
    private boolean isSupportedForImageExport(String fileType) {
        String[] supportedTypes = {
            "pptx", "ppt", "pptm", "pps", "ppsx", "ppsm", "pot", "potx", "potm", "odp",
            "dps", "dpt"    // WPS演示格式
        };
        for (String type : supportedTypes) {
            if (type.equalsIgnoreCase(fileType)) {
                return true;
            }
        }
        return false;
    }

}

