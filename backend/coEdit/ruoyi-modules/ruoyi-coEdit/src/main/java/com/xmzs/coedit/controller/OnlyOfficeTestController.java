package com.xmzs.coedit.controller;

import com.xmzs.coedit.config.OnlyOfficeConfig;
import com.xmzs.coedit.domain.dto.EditorConfig;
import com.xmzs.coedit.service.EditorConfigService;
import com.xmzs.coedit.service.OnlyOfficeService;
import com.xmzs.coedit.utils.FileUtil;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * OnlyOffice测试控制器
 * 用于测试OnlyOffice集成是否正常
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/test")
@Tag(name = "OnlyOffice测试接口", description = "用于测试OnlyOffice集成")
public class OnlyOfficeTestController extends BaseController {

    private final OnlyOfficeConfig onlyOfficeConfig;
    private final OnlyOfficeService onlyOfficeService;
    private final EditorConfigService editorConfigService;

    /**
     * 获取OnlyOffice配置信息
     */
    @Operation(summary = "获取OnlyOffice配置信息")
    @GetMapping("/config")
    public R<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("documentServerUrl", onlyOfficeConfig.getDocumentServerUrl());
        config.put("documentServerApiUrl", onlyOfficeConfig.getDocumentServerApiUrl());
        config.put("callbackUrl", onlyOfficeConfig.getCallbackUrl());
        config.put("jwtEnabled", onlyOfficeConfig.getJwtEnabled());
        config.put("storagePath", onlyOfficeConfig.getStoragePath());
        config.put("maxFileSize", onlyOfficeConfig.getMaxFileSize());
        return R.ok(config);
    }

    /**
     * 检查OnlyOffice服务状态
     */
    @Operation(summary = "检查OnlyOffice服务状态")
    @GetMapping("/status")
    public R<Map<String, Object>> checkStatus() {
        boolean available = onlyOfficeService.isServiceAvailable();
        Map<String, Object> status = new HashMap<>();
        status.put("available", available);
        status.put("documentServerUrl", onlyOfficeConfig.getDocumentServerUrl());
        status.put("message", available ? "OnlyOffice服务正常" : "OnlyOffice服务不可用");
        return R.ok(status);
    }

    /**
     * 生成测试编辑器配置
     */
    @Operation(summary = "生成测试编辑器配置")
    @GetMapping("/editor-config")
    public R<EditorConfig> generateTestConfig(
        @RequestParam(defaultValue = "test.docx") String fileName,
        @RequestParam(defaultValue = "edit") String mode
    ) {
        try {
            // 生成测试数据
            Long fileId = 1L;
            String fileUrl = "http://example.com/files/test.docx";
            String documentKey = FileUtil.generateDocumentKey(fileId, 1);

            EditorConfig config = editorConfigService.createEditorConfig(
                fileId, fileName, fileUrl, documentKey, mode
            );

            return R.ok(config);
        } catch (Exception e) {
            log.error("生成测试配置失败", e);
            return R.fail("生成测试配置失败: " + e.getMessage());
        }
    }

    /**
     * 测试文档命令
     */
    @Operation(summary = "测试文档命令")
    @PostMapping("/command")
    public R<Map<String, Object>> testCommand(
        @RequestParam String command,
        @RequestParam String key
    ) {
        try {
            Map<String, Object> result = onlyOfficeService.sendCommand(command, key, null);
            return R.ok(result);
        } catch (Exception e) {
            log.error("执行命令失败", e);
            return R.fail("执行命令失败: " + e.getMessage());
        }
    }

    /**
     * 测试文档转换
     */
    @Operation(summary = "测试文档转换")
    @PostMapping("/convert")
    public R<Map<String, Object>> testConvert(
        @RequestParam String fileUrl,
        @RequestParam String fileType,
        @RequestParam String outputType,
        @RequestParam String key
    ) {
        try {
            Map<String, Object> result = onlyOfficeService.convertDocument(
                fileUrl, fileType, outputType, key
            );
            return R.ok(result);
        } catch (Exception e) {
            log.error("文档转换失败", e);
            return R.fail("文档转换失败: " + e.getMessage());
        }
    }

    /**
     * 获取文档类型
     */
    @Operation(summary = "获取文档类型")
    @GetMapping("/document-type")
    public R<Map<String, Object>> getDocumentType(@RequestParam String fileName) {
        String extension = FileUtil.getFileExtension(fileName);
        String documentType = FileUtil.getDocumentType(extension);
        boolean editable = FileUtil.isEditableFormat(extension);

        Map<String, Object> result = new HashMap<>();
        result.put("fileName", fileName);
        result.put("extension", extension);
        result.put("documentType", documentType);
        result.put("editable", editable);

        return R.ok(result);
    }

    /**
     * 测试JWT Token生成
     */
    @Operation(summary = "测试JWT Token生成")
    @GetMapping("/jwt-test")
    public R<Map<String, Object>> testJwt() {
        Map<String, Object> result = new HashMap<>();
        result.put("jwtEnabled", onlyOfficeConfig.getJwtEnabled());

        if (onlyOfficeConfig.getJwtEnabled()) {
            try {
                // 生成测试配置
                EditorConfig config = editorConfigService.createEditConfig(
                    1L, "test.docx", "http://example.com/test.docx", "test_key_123"
                );
                result.put("token", config.getToken());
                result.put("message", "JWT Token生成成功");
            } catch (Exception e) {
                result.put("error", e.getMessage());
                result.put("message", "JWT Token生成失败");
            }
        } else {
            result.put("message", "JWT未启用");
        }

        return R.ok(result);
    }

    /**
     * 健康检查
     */
    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public R<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("module", "ruoyi-coEdit");
        health.put("timestamp", System.currentTimeMillis());
        health.put("onlyofficeConfigured", onlyOfficeConfig.getDocumentServerUrl() != null);
        return R.ok(health);
    }

    /**
     * 测试完整的编辑流程
     */
    @Operation(summary = "测试完整的编辑流程")
    @GetMapping("/full-test")
    public R<Map<String, Object>> fullTest() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 检查OnlyOffice服务
            boolean available = onlyOfficeService.isServiceAvailable();
            result.put("onlyofficeAvailable", available);

            // 2. 检查配置
            result.put("documentServerUrl", onlyOfficeConfig.getDocumentServerUrl());
            result.put("callbackUrl", onlyOfficeConfig.getCallbackUrl());
            result.put("jwtEnabled", onlyOfficeConfig.getJwtEnabled());
            result.put("storagePath", onlyOfficeConfig.getStoragePath());

            // 3. 测试建议
            result.put("nextSteps", java.util.List.of(
                "1. 使用 POST /coedit/upload 上传测试文件",
                "2. 使用返回的fileId访问 GET /coedit/editor/{fileId} 打开编辑器",
                "3. 在编辑器中编辑文档并保存",
                "4. 检查回调日志确认保存成功"
            ));

            result.put("status", "success");
            result.put("message", "所有检查通过，可以开始测试");

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        return R.ok(result);
    }
}

