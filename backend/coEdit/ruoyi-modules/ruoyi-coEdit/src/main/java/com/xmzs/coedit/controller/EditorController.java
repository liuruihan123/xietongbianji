package com.xmzs.coedit.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xmzs.coedit.config.OnlyOfficeConfig;
import com.xmzs.coedit.constant.OnlyOfficeConstants;
import com.xmzs.coedit.domain.dto.EditorConfig;
import com.xmzs.coedit.service.DocumentService;
import com.xmzs.coedit.service.EditorConfigService;
import com.xmzs.coedit.service.OnlyOfficeCommandService;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 文档编辑器控制器
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@Controller
@RequestMapping("/coedit/editor")
@Tag(name = "文档编辑器接口", description = "提供文档编辑器页面和配置")
public class EditorController extends BaseController {

    private final DocumentService documentService;
    private final EditorConfigService editorConfigService;
    private final OnlyOfficeConfig onlyOfficeConfig;
    private final OnlyOfficeCommandService onlyOfficeCommandService;

    /**
     * 打开文档编辑器页面
     *
     * @param fileId 文件ID
     * @param mode 编辑模式（edit/view）
     * @param model Spring Model
     * @return 编辑器页面
     */
    @Operation(summary = "打开文档编辑器")
    @GetMapping("/editor/{fileId}")
    public String openEditor(
        @PathVariable String fileId,
        @RequestParam(defaultValue = "edit") String mode,
        Model model,
        HttpServletRequest request
    ) {
        try {
            log.info("打开编辑器: fileId={}, mode={}", fileId, mode);

            // 验证文档是否存在
            if (!documentService.exists(fileId)) {
                model.addAttribute("error", "文档不存在");
                return "error";
            }

            // 获取文档信息
            Map<String, Object> docInfo = documentService.getDocumentInfo(fileId);
            String fileName = (String) docInfo.get("fileName");

            // 获取服务器地址(优先使用配置的serverUrl,确保OnlyOffice服务器可以访问)
            String serverUrl = getEffectiveServerUrl(request);

            // 获取当前用户的authToken
            String authToken = StpUtil.getTokenValue();

            // 生成文档URL
            String fileUrl = documentService.getDocumentUrl(fileId, serverUrl, authToken);

            // 获取文档Key
            String documentKey = documentService.getDocumentKey(fileId);

            // 生成编辑器配置
            EditorConfig config = editorConfigService.createEditorConfig(
                Long.parseLong(fileId.hashCode() + ""),
                fileName,
                fileUrl,
                documentKey,
                mode
            );

            // 添加到模型
            model.addAttribute("config", config);
            model.addAttribute("documentServerUrl", onlyOfficeConfig.getDocumentServerUrl());
            model.addAttribute("fileId", fileId);
            model.addAttribute("fileName", fileName);

            return "editor";

        } catch (Exception e) {
            log.error("打开编辑器失败: fileId={}", fileId, e);
            model.addAttribute("error", "打开编辑器失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 获取编辑器配置（API方式）
     *
     * @param fileId 文件ID
     * @param mode 编辑模式
     * @return 编辑器配置
     */
    @Operation(summary = "获取编辑器配置")
    @GetMapping("/config/{fileId}")
    @ResponseBody
    public R<EditorConfig> getEditorConfig(
        @PathVariable String fileId,
        @RequestParam(defaultValue = "edit") String mode,
        HttpServletRequest request
    ) {
        try {
            // 验证文档是否存在
            if (!documentService.exists(fileId)) {
                return R.fail("文档不存在");
            }

            // 获取文档信息
            Map<String, Object> docInfo = documentService.getDocumentInfo(fileId);
            String fileName = (String) docInfo.get("fileName");

            // 获取服务器地址(优先使用配置的serverUrl,确保OnlyOffice服务器可以访问)
            String serverUrl = getEffectiveServerUrl(request);

            // 获取当前用户的authToken
            String authToken = StpUtil.getTokenValue();

            // 生成文档URL
            String fileUrl = documentService.getDocumentUrl(fileId, serverUrl, authToken);

            // 获取文档Key
            String documentKey = documentService.getDocumentKey(fileId);

            // 生成编辑器配置
            // 修复: 直接使用fileId字符串,避免hashCode导致的int溢出问题
            // 旧代码: Long.parseLong(fileId.hashCode() + "") 会导致大数字溢出为负数
            EditorConfig config = editorConfigService.createEditorConfig(
                Long.valueOf(fileId),  // 直接传递fileId字符串
                fileName,
                fileUrl,
                documentKey,
                mode
            );

            return R.ok(config);

        } catch (Exception e) {
            log.error("获取编辑器配置失败: fileId={}", fileId, e);
            return R.fail("获取编辑器配置失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件（供OnlyOffice访问）
     *
     * @param fileId 文件ID
     * @return 文件资源
     */
    @Operation(summary = "下载文件")
    @GetMapping("/file/{fileId}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        try {
            log.info("下载文件: fileId={}", fileId);

            // 获取文件
            File file = documentService.getDocumentFile(fileId);

            // 获取文件信息
            Map<String, Object> docInfo = documentService.getDocumentInfo(fileId);
            String fileName = (String) docInfo.get("fileName");

            // 创建资源
            Resource resource = new FileSystemResource(file);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                URLEncoder.encode(fileName, StandardCharsets.UTF_8));

            return ResponseEntity.ok()
                .headers(headers)
                .body(resource);

        } catch (Exception e) {
            log.error("下载文件失败: fileId={}", fileId, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件ID
     */
    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    @ResponseBody
    public R<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            log.info("上传文件: fileName={}, size={}", file.getOriginalFilename(), file.getSize());

            // 验证文件
            if (file.isEmpty()) {
                return R.fail("文件不能为空");
            }

            // 验证文件大小
            long maxSize = onlyOfficeConfig.getMaxFileSize() * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return R.fail("文件大小超过限制: " + onlyOfficeConfig.getMaxFileSize() + "MB");
            }

            // 上传文件
            String fileId = documentService.uploadFile(file, file.getOriginalFilename());

            // 返回结果
            Map<String, Object> result = Map.of(
                "fileId", fileId,
                "fileName", file.getOriginalFilename(),
                "size", file.getSize()
            );

            return R.ok(result);

        } catch (Exception e) {
            log.error("上传文件失败", e);
            return R.fail("上传文件失败: " + e.getMessage());
        }
    }

    /**
     * 获取有效的服务器地址
     * 优先使用配置的serverUrl(确保OnlyOffice服务器可以访问)
     * 如果配置为空,则从请求中提取
     */
    private String getEffectiveServerUrl(HttpServletRequest request) {
        // 优先使用配置的serverUrl
        String configuredUrl = onlyOfficeConfig.getServerUrl();
        if (configuredUrl != null && !configuredUrl.trim().isEmpty()) {
            log.debug("使用配置的服务器地址: {}", configuredUrl);
            return configuredUrl.trim();
        }

        // 如果配置为空,从请求中提取
        log.debug("配置的服务器地址为空,从请求中提取");
        return getServerUrl(request);
    }

    /**
     * 从HTTP请求中获取服务器地址
     */
    private String getServerUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        if ((scheme.equals("http") && serverPort != 80) ||
            (scheme.equals("https") && serverPort != 443)) {
            url.append(":").append(serverPort);
        }

        url.append(contextPath);

        return url.toString();
    }

    /**
     * 强制保存文档
     *
     * 该接口用于触发 OnlyOffice Document Server 强制保存当前正在编辑的文档。
     * 主要用于自动保存功能。
     *
     * 注意: 只有当文档处于活动编辑状态时才能成功执行强制保存
     *
     * @param fileId 文件ID
     * @return 操作结果
     */
    @PostMapping("/force-save/{fileId}")
    @ResponseBody
    @Operation(summary = "强制保存文档", description = "触发OnlyOffice强制保存当前编辑的文档")
    public R<Void> forceSave(@PathVariable String fileId) {
        try {
            log.info("收到强制保存请求: fileId={}", fileId);

            // 检查文档是否存在
            if (!documentService.exists(fileId)) {
                log.warn("文档不存在: fileId={}", fileId);
                return R.fail("文档不存在");
            }

            // 调用OnlyOffice命令服务执行强制保存
            boolean success = onlyOfficeCommandService.forceSave(fileId);

            if (success) {
                log.info("文档强制保存成功: fileId={}", fileId);
                return R.ok();
            } else {
                log.error("文档强制保存失败: fileId={}", fileId);
                return R.fail("强制保存失败,文档可能没有处于编辑状态或没有任何更改");
            }

        } catch (Exception e) {
            log.error("强制保存文档异常: fileId={}", fileId, e);
            return R.fail("强制保存异常: " + e.getMessage());
        }
    }
}

