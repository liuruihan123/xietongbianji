package com.xmzs.coedit.controller.example;

import cn.dev33.satoken.stp.StpUtil;
import com.xmzs.coedit.domain.bo.CoFileBo;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.service.ICoFileService;
import com.xmzs.coedit.util.DefaultWorkspaceHelper;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理Controller示例
 * 展示如何集成DefaultWorkspaceHelper实现自动创建默认工作空间
 *
 * @author zuleed
 * @date 2025-11-06
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/file/example")
@Tag(name = "文件管理示例", description = "展示如何集成默认工作空间自动创建功能")
public class CoFileControllerExample extends BaseController {

    private final ICoFileService fileService;
    private final DefaultWorkspaceHelper defaultWorkspaceHelper;

    /**
     * 上传文件 - 自动创建默认工作空间版本
     *
     * 如果用户没有提供workspaceId,系统会自动:
     * 1. 检查用户是否有默认工作空间
     * 2. 如果有,使用现有的默认工作空间
     * 3. 如果没有,自动创建一个默认工作空间
     */
    @Log(title = "上传文件", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    @Operation(summary = "上传文件(支持自动创建工作空间)")
    public R<CoFileVo> uploadFile(
            @Parameter(description = "文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "工作空间ID(可选,不提供则自动创建)") @RequestParam(value = "workspaceId", required = false) Long workspaceId,
            @Parameter(description = "文件夹ID") @RequestParam(value = "folderId", required = false) Long folderId) {

        try {
            // 获取当前登录用户ID
            Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);

            // 如果没有提供workspaceId,自动获取或创建默认工作空间
            if (workspaceId == null) {
                log.info("用户[{}]未指定工作空间,开始获取或创建默认工作空间", userId);
                workspaceId = defaultWorkspaceHelper.getOrCreateDefaultWorkspaceForCurrentUser();
                log.info("用户[{}]使用默认工作空间:{}", userId, workspaceId);
            }

            // 验证文件
            if (file == null || file.isEmpty()) {
                return R.fail("文件不能为空");
            }

            // 调用文件上传服务
            // 注意:这里假设fileService有uploadFile方法,实际需要根据你的Service实现调整
            // CoFileVo fileVo = fileService.uploadFile(file, workspaceId, folderId, userId);

            // 示例返回
            CoFileVo fileVo = new CoFileVo();
            fileVo.setFileName(file.getOriginalFilename());
            fileVo.setWorkspaceId(workspaceId);
            fileVo.setFolderId(folderId);
//            fileVo.setUploaderId(userId);
//            fileVo.setFileUrl(userId);

            return R.ok("文件上传成功", fileVo);

        } catch (Exception e) {
            log.error("文件上传失败", e);
            return R.fail("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 创建文档 - 自动创建默认工作空间版本
     */
    @Log(title = "创建文档", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    @Operation(summary = "创建文档(支持自动创建工作空间)")
    public R<CoFileVo> createDocument(@Validated @RequestBody CoFileBo bo) {

        try {
            // 获取当前登录用户ID
            Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);

            // 如果没有提供workspaceId,自动获取或创建默认工作空间
            if (bo.getWorkspaceId() == null) {
                log.info("用户[{}]未指定工作空间,开始获取或创建默认工作空间", userId);
                Long workspaceId = defaultWorkspaceHelper.getOrCreateDefaultWorkspaceForCurrentUser();
                bo.setWorkspaceId(workspaceId);
                log.info("用户[{}]使用默认工作空间:{}", userId, workspaceId);
            }

            // 设置上传者信息
//            bo.setUploaderId(userId);
            // bo.setUploaderName(userName); // 需要从用户服务获取

            // 调用文件创建服务
            // CoFileVo fileVo = fileService.createDocument(bo);

            // 示例返回
            CoFileVo fileVo = new CoFileVo();
//            fileVo.setName(bo.getName());
            fileVo.setWorkspaceId(bo.getWorkspaceId());
            fileVo.setFolderId(bo.getFolderId());
//            fileVo.setUploaderId(userId);

            return R.ok("文档创建成功", fileVo);

        } catch (Exception e) {
            log.error("文档创建失败", e);
            return R.fail("文档创建失败: " + e.getMessage());
        }
    }

    /**
     * 批量上传文件 - 自动创建默认工作空间版本
     */
    @Log(title = "批量上传文件", businessType = BusinessType.INSERT)
    @PostMapping("/batch-upload")
    @Operation(summary = "批量上传文件(支持自动创建工作空间)")
    public R<java.util.List<CoFileVo>> batchUploadFiles(
            @Parameter(description = "文件列表", required = true) @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "工作空间ID(可选,不提供则自动创建)") @RequestParam(value = "workspaceId", required = false) Long workspaceId,
            @Parameter(description = "文件夹ID") @RequestParam(value = "folderId", required = false) Long folderId) {

        try {
            // 获取当前登录用户ID
            Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);

            // 如果没有提供workspaceId,自动获取或创建默认工作空间
            if (workspaceId == null) {
                log.info("用户[{}]未指定工作空间,开始获取或创建默认工作空间", userId);
                workspaceId = defaultWorkspaceHelper.getOrCreateDefaultWorkspaceForCurrentUser();
                log.info("用户[{}]使用默认工作空间:{}", userId, workspaceId);
            }

            // 验证文件
            if (files == null || files.length == 0) {
                return R.fail("文件列表不能为空");
            }

            // 批量上传文件
            java.util.List<CoFileVo> fileVoList = new java.util.ArrayList<>();

            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    // 调用文件上传服务
                    // CoFileVo fileVo = fileService.uploadFile(file, workspaceId, folderId, userId);

                    // 示例返回
                    CoFileVo fileVo = new CoFileVo();
                    fileVo.setFileName(file.getOriginalFilename());
                    fileVo.setWorkspaceId(workspaceId);
                    fileVo.setFolderId(folderId);
//                    fileVo.setUploaderId(userId);

                    fileVoList.add(fileVo);
                }
            }

            return R.ok("批量上传成功,共上传" + fileVoList.size() + "个文件", fileVoList);

        } catch (Exception e) {
            log.error("批量上传文件失败", e);
            return R.fail("批量上传失败: " + e.getMessage());
        }
    }

    /**
     * 检查用户是否有默认工作空间
     */
    @GetMapping("/check-default-workspace")
    @Operation(summary = "检查用户是否有默认工作空间")
    public R<Boolean> checkDefaultWorkspace() {
        try {
            Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
            boolean hasDefault = defaultWorkspaceHelper.hasDefaultWorkspace(userId);

            return R.ok("检查完成", hasDefault);

        } catch (Exception e) {
            log.error("检查默认工作空间失败", e);
            return R.fail("检查失败: " + e.getMessage());
        }
    }

    /**
     * 获取或创建默认工作空间ID
     */
    @GetMapping("/get-default-workspace")
    @Operation(summary = "获取或创建默认工作空间ID")
    public R<Long> getDefaultWorkspace() {
        try {
            Long workspaceId = defaultWorkspaceHelper.getOrCreateDefaultWorkspaceForCurrentUser();

            return R.ok("获取成功", workspaceId);

        } catch (Exception e) {
            log.error("获取默认工作空间失败", e);
            return R.fail("获取失败: " + e.getMessage());
        }
    }
}

