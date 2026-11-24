package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.xmzs.coedit.domain.bo.CoFileBo;
import com.xmzs.coedit.domain.dto.CreateDocumentRequest;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.service.DocumentExportService;
import com.xmzs.coedit.service.ICoAccessRecordService;
import com.xmzs.coedit.mapper.CoAccessRecordMapper;
import com.xmzs.coedit.mapper.CoFileAccessRecordMapper;
import com.xmzs.coedit.service.ICoFileAccessRecordService;
import com.xmzs.coedit.service.ICoFileService;
import com.xmzs.coedit.service.SpreadsheetService;
import com.xmzs.coedit.util.DefaultWorkspaceHelper;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.exception.ServiceException;
import com.xmzs.common.core.utils.file.FileUtils;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.excel.utils.ExcelUtil;
import com.xmzs.common.idempotent.annotation.RepeatSubmit;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件管理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/file")
@Tag(name = "文件管理", description = "文件管理")
public class CoFileController extends BaseController {

    private final ICoFileService fileService;
    private final DocumentExportService documentExportService;
    private final SpreadsheetService spreadsheetService;
    private final DefaultWorkspaceHelper defaultWorkspaceHelper;
    private final ICoFileAccessRecordService fileAccessRecordService;
    private final ICoAccessRecordService accessRecordService;
    private final CoAccessRecordMapper coAccessRecordMapper;
    private final CoFileAccessRecordMapper coFileAccessRecordMapper;
    private final Environment environment;

    @Value("${onlyoffice.storage-path}")
    private String storagePath;

    /**
     * 查询文件列表
     * 注意: 此接口已移除权限验证,所有登录用户均可访问
     */
    // @SaCheckPermission("coedit:file:list")  // 已注释,允许所有登录用户访问
    @GetMapping("/list")
    @Operation(summary = "查询文件列表", description = "查询文件列表（分页）")
    public TableDataInfo<CoFileVo> list(CoFileBo bo, PageQuery pageQuery) {
        return fileService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出文件列表
     */
    // @SaCheckPermission("coedit:file:export")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @Operation(summary = "导出文件列表", description = "导出文件列表")
    public void export(CoFileBo bo, HttpServletResponse response) {
        List<CoFileVo> list = fileService.queryList(bo);
        ExcelUtil.exportExcel(list, "文件", CoFileVo.class, response);
    }

    /**
     * 获取文件详细信息
     */
    // @SaCheckPermission("coedit:file:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/{id}")
    @Operation(summary = "获取文件详细信息", description = "根据文件ID获取详细信息")
    public R<CoFileVo> getInfo(@Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long id) {
        CoFileVo fileVo = fileService.queryById(id);

        // 记录文件访问 - 查看类型
        if (fileVo != null) {
            try {
                Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
                // 记录到文件访问记录表
                fileAccessRecordService.recordAccess(id, userId, 1); // 1-查看
                // 记录到通用访问记录表
                accessRecordService.recordFileAccess(userId, id, fileVo.getWorkspaceId(), 1);
                log.debug("记录文件访问: fileId={}, userId={}, accessType=1(查看)", id, userId);
            } catch (Exception e) {
                log.error("记录文件访问失败: fileId={}", id, e);
            }
        }

        return R.ok(fileVo);
    }

    /**
     * 新增文件
     */
    // @SaCheckPermission("coedit:file:add")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    @Operation(summary = "新增文件", description = "新增文件")
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CoFileBo bo) {
        return toAjax(fileService.insertByBo(bo));
    }

    /**
     * 修改文件
     */
    // @SaCheckPermission("coedit:file:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    @Operation(summary = "修改文件", description = "修改文件")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CoFileBo bo) {
        return toAjax(fileService.updateByBo(bo));
    }

    /**
     * 删除文件
     */
    // @SaCheckPermission("coedit:file:remove")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除文件", description = "删除文件（软删除）")
    public R<Void> remove(@Parameter(description = "文件ID数组", required = true) @NotEmpty(message = "文件ID不能为空") @PathVariable Long[] ids) {
        return toAjax(fileService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 上传文件
     * <p>
     * workspaceId处理逻辑:
     * 1. 如果提供了workspaceId,直接使用该工作空间
     * 2. 如果未提供workspaceId,系统会自动处理:
     *    - 首先查询当前用户的默认工作空间(is_default=1)
     *    - 如果找到默认工作空间,使用该工作空间
     *    - 如果未找到默认工作空间,自动创建一个新的默认工作空间
     * </p>
     */
    // @SaCheckPermission("coedit:file:upload")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件上传", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传文件", description = "上传文件到指定工作空间和文件夹。workspaceId可选,不提供则自动使用或创建用户的默认工作空间(is_default=1)")
    public R<CoFileVo> upload(
        @Parameter(description = "工作空间ID(可选,不提供则自动使用或创建默认工作空间)") @RequestParam(required = false) Long workspaceId,
        @Parameter(description = "文件夹ID") @RequestParam(required = false) Long folderId,
        @Parameter(description = "文件", required = true) @RequestPart("file") MultipartFile file) {

        // 获取当前登录用户ID
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);

        // 如果没有提供workspaceId,自动获取或创建默认工作空间
        // 逻辑: 先查询用户的默认工作空间(is_default=1),如果不存在则创建
        if (workspaceId == null) {
            log.info("用户[{}]未指定工作空间,开始获取或创建默认工作空间", userId);
            workspaceId = defaultWorkspaceHelper.getOrCreateDefaultWorkspaceForCurrentUser();
            log.info("用户[{}]使用默认工作空间:{}", userId, workspaceId);
        }

        CoFileVo fileVo = fileService.uploadFile(workspaceId, folderId, file);
        return R.ok(fileVo);
    }

    /**
     * 下载文件
     */
    // @SaCheckPermission("coedit:file:download")  // 协同编辑模块基础功能,允许所有登录用户访问
    // @Log(title = "文件下载", businessType = BusinessType.OTHER)  // 注释掉@Log注解，避免LogAspect验证OnlyOffice的JWT Token
    /*@GetMapping("/download/{id}")
    @Operation(summary = "下载文件", description = "根据文件ID下载文件")
    public void download(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long id,
        @Parameter(description = "认证Token") @RequestParam(required = false) String authToken,
        HttpServletResponse response) {

        log.info("【调试】文件下载开始: fileId={}, hasAuthToken={}", id, authToken != null && !authToken.isEmpty());

        // 处理authToken，验证用户身份（不恢复登录状态）
        LoginUser authTokenUser = null;
        if (authToken != null && !authToken.isEmpty()) {
            try {
                // 移除Bearer前缀（如果有）
                String rawToken = authToken;
                if (authToken.startsWith("Bearer ")) {
                    rawToken = authToken.substring(7);
                }

                log.info("【调试】开始验证authToken: token前20字符={}", rawToken.substring(0, Math.min(20, rawToken.length())));

                // 使用LoginHelper.getLoginUser()验证Token并获取用户信息
                authTokenUser = LoginHelper.getLoginUser(rawToken);
                if (authTokenUser != null) {
                    log.info("【调试】authToken验证成功: loginId={}, userId={}", authTokenUser.getLoginId(), authTokenUser.getUserId());
                } else {
                    log.warn("【调试】authToken验证失败: 无法获取用户信息");
                }
            } catch (Exception e) {
                log.warn("【调试】验证authToken异常: {}", e.getMessage(), e);
            }
        }

        log.info("【调试】开始获取文件信息: fileId={}", id);

        // 获取文件信息
        CoFileVo file = fileService.queryById(id);
        if (file == null) {
            log.warn("【调试】文件不存在: fileId={}", id);
            return;
        }
        log.info("【调试】文件信息获取成功: fileId={}, fileName={}", id, file.getFileName());

        log.info("【调试】开始下载文件: fileId={}", id);
        // 下载文件
        byte[] fileBytes = fileService.downloadFile(id);
        log.info("【调试】文件下载成功: fileId={}, size={} bytes", id, fileBytes != null ? fileBytes.length : 0);

        // 设置响应头
        FileUtils.setAttachmentResponseHeader(response, file.getFileName());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8");

        try {
            response.getOutputStream().write(fileBytes);
            response.getOutputStream().flush();

            // 记录文件访问 - 下载类型
            try {
                Long userId = null;
                // 优先使用前面已解析的authTokenUser
                if (authTokenUser != null) {
                    userId = authTokenUser.getUserId();
                    log.debug("使用authToken中的用户信息记录下载: userId={}", userId);
                } else {
                    // 兜底: 若authToken无效, 尝试从当前会话读取(可能为空), 且不抛异常
                    try {
                        LoginUser current = LoginHelper.getLoginUser();
                        if (current != null) {
                            userId = current.getUserId();
                            log.debug("使用当前会话用户信息记录下载: userId={}", userId);
                        }
                    } catch (Exception e) {
                        log.debug("无法从当前会话获取用户信息: {}", e.getMessage());
                    }
                }

                if (userId != null) {
                    // 记录到文件访问记录表
                    fileAccessRecordService.recordAccess(id, userId, 3); // 3-下载
                    // 记录到通用访问记录表
                    accessRecordService.recordFileAccess(userId, id, file.getWorkspaceId(), 3);
                    log.debug("记录文件下载: fileId={}, userId={}, accessType=3(下载)", id, userId);
                } else {
                    log.warn("记录文件下载: 未能识别用户, 跳过用户维度记录 fileId={}", id);
                }
            } catch (Exception ex) {
                log.error("记录文件下载失败: fileId={}", id, ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }*/

    /**
     * 下载文件 带文件转换
     */
    @GetMapping("/download/{id}")
    @Operation(summary = "下载文件", description = "根据文件ID下载文件")
    public void download(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long id,
        @Parameter(description = "认证Token") @RequestParam(required = false) String authToken,
        HttpServletResponse response) {

        log.info("【调试】文件下载开始: fileId={}, hasAuthToken={}", id, authToken != null && !authToken.isEmpty());

        // 处理authToken，验证用户身份（不恢复登录状态）
        LoginUser authTokenUser = null;
        if (authToken != null && !authToken.isEmpty()) {
            try {
                // 移除Bearer前缀（如果有）
                String rawToken = authToken;
                if (authToken.startsWith("Bearer ")) {
                    rawToken = authToken.substring(7);
                }

                log.info("【调试】开始验证authToken: token前20字符={}", rawToken.substring(0, Math.min(20, rawToken.length())));

                // 使用LoginHelper.getLoginUser()验证Token并获取用户信息
                authTokenUser = LoginHelper.getLoginUser(rawToken);
                if (authTokenUser != null) {
                    log.info("【调试】authToken验证成功: loginId={}, userId={}", authTokenUser.getLoginId(), authTokenUser.getUserId());
                } else {
                    log.warn("【调试】authToken验证失败: 无法获取用户信息");
                }
            } catch (Exception e) {
                log.warn("【调试】验证authToken异常: {}", e.getMessage(), e);
            }
        }

        log.info("【调试】开始获取文件信息: fileId={}", id);

        // 获取文件信息
        CoFileVo file = fileService.queryById(id);
        if (file == null) {
            log.warn("【调试】文件不存在: fileId={}", id);
            return;
        }
        log.info("【调试】文件信息获取成功: fileId={}, fileName={}", id, file.getFileName());

        log.info("【调试】开始下载文件: fileId={}", id);
        // 下载文件
        byte[] fileBytes = fileService.downloadFile(id);
        log.info("【调试】文件下载成功: fileId={}, size={} bytes", id, fileBytes != null ? fileBytes.length : 0);

        // ==============================================
        // 【核心修改：下载时自动还原成原始格式 wps/et/dps】
        // ==============================================
        String downloadFileName = file.getFileName();
        String fileType = file.getFileType(); // 这里是 docx/xlsx/pptx

        // 把存储的格式 转回 用户上传时的原始格式
        if ("wps".equals(fileType)) {
            downloadFileName = FileUtil.mainName(downloadFileName) + ".wps";
        } else if ("et".equals(fileType)) {
            downloadFileName = FileUtil.mainName(downloadFileName) + ".et";
        } else if ("dps".equals(fileType)) {
            downloadFileName = FileUtil.mainName(downloadFileName) + ".dps";
        }

        // 使用【转换回来的文件名】给用户下载
        FileUtils.setAttachmentResponseHeader(response, downloadFileName);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8");

        try {
            response.getOutputStream().write(fileBytes);
            response.getOutputStream().flush();

            // 记录文件访问 - 下载类型
            try {
                Long userId = null;
                // 优先使用前面已解析的authTokenUser
                if (authTokenUser != null) {
                    userId = authTokenUser.getUserId();
                    log.debug("使用authToken中的用户信息记录下载: userId={}", userId);
                } else {
                    // 兜底: 若authToken无效, 尝试从当前会话读取(可能为空), 且不抛异常
                    try {
                        LoginUser current = LoginHelper.getLoginUser();
                        if (current != null) {
                            userId = current.getUserId();
                            log.debug("使用当前会话用户信息记录下载: userId={}", userId);
                        }
                    } catch (Exception e) {
                        log.debug("无法从当前会话获取用户信息: {}", e.getMessage());
                    }
                }

                if (userId != null) {
                    // 记录到文件访问记录表
                    fileAccessRecordService.recordAccess(id, userId, 3); // 3-下载
                    // 记录到通用访问记录表
                    accessRecordService.recordFileAccess(userId, id, file.getWorkspaceId(), 3);
                    log.debug("记录文件下载: fileId={}, userId={}, accessType=3(下载)", id, userId);
                } else {
                    log.warn("记录文件下载: 未能识别用户, 跳过用户维度记录 fileId={}", id);
                }
            } catch (Exception ex) {
                log.error("记录文件下载失败: fileId={}", id, ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }

    /**
     * 查询工作空间下的文件列表
     */
    // @SaCheckPermission("coedit:file:list")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/workspace/{workspaceId}")
    @Operation(summary = "查询工作空间下的文件列表", description = "根据工作空间ID查询文件列表")
    public R<List<CoFileVo>> listByWorkspace(
        @Parameter(description = "工作空间ID", required = true) @NotNull(message = "工作空间ID不能为空") @PathVariable Long workspaceId) {
        return R.ok(fileService.queryByWorkspaceId(workspaceId));
    }

    /**
     * 查询文件夹下的文件列表
     */
    // @SaCheckPermission("coedit:file:list")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/folder/{folderId}")
    @Operation(summary = "查询文件夹下的文件列表", description = "根据文件夹ID查询文件列表")
    public R<List<CoFileVo>> listByFolder(
        @Parameter(description = "文件夹ID", required = true) @NotNull(message = "文件夹ID不能为空") @PathVariable Long folderId) {
        return R.ok(fileService.queryByFolderId(folderId));
    }

    /**
     * 更新文件编辑状态
     */
    // @SaCheckPermission("coedit:file:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "更新文件编辑状态", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/editStatus")
    @Operation(summary = "更新文件编辑状态", description = "更新文件编辑状态")
    public R<Void> updateEditStatus(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long id,
        @Parameter(description = "编辑状态", required = true) @NotNull(message = "编辑状态不能为空") @RequestParam Integer editStatus,
        @Parameter(description = "编辑者ID") @RequestParam(required = false) Long editorId,
        @Parameter(description = "编辑者名称") @RequestParam(required = false) String editorName) {
        return toAjax(fileService.updateEditStatus(id, editStatus, editorId, editorName));
    }

    /**
     * 更新文件锁定状态
     */
    // @SaCheckPermission("coedit:file:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "更新文件锁定状态", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/lockStatus")
    @Operation(summary = "更新文件锁定状态", description = "更新文件锁定状态")
    public R<Void> updateLockStatus(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long id,
        @Parameter(description = "是否锁定", required = true) @NotNull(message = "锁定状态不能为空") @RequestParam Integer isLocked,
        @Parameter(description = "锁定人ID") @RequestParam(required = false) Long lockedBy,
        @Parameter(description = "锁定人名称") @RequestParam(required = false) String lockedByName,
        @Parameter(description = "锁定原因") @RequestParam(required = false) String lockReason) {
        return toAjax(fileService.updateLockStatus(id, isLocked, lockedBy, lockedByName, lockReason));
    }

    /**
     * 检查文件是否存在
     */
    // @SaCheckPermission("coedit:file:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/exists")
    @Operation(summary = "检查文件是否存在", description = "检查文件名是否已存在")
    public R<Boolean> checkExists(
        @Parameter(description = "工作空间ID", required = true) @NotNull(message = "工作空间ID不能为空") @RequestParam Long workspaceId,
        @Parameter(description = "文件夹ID") @RequestParam(required = false) Long folderId,
        @Parameter(description = "文件名", required = true) @NotNull(message = "文件名不能为空") @RequestParam String fileName) {
        return R.ok(fileService.checkFileExists(workspaceId, folderId, fileName));
    }

    /**
     * 检查文件是否被锁定
     */
    // @SaCheckPermission("coedit:file:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/{id}/locked")
    @Operation(summary = "检查文件是否被锁定", description = "检查文件是否被锁定")
    public R<Boolean> checkLocked(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long id) {
        return R.ok(fileService.checkFileLocked(id));
    }

    /**
     * 移动文件
     */
    // @SaCheckPermission("coedit:file:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "移动文件", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{id}/move")
    @Operation(summary = "移动文件", description = "移动文件到指定文件夹")
    public R<Void> move(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long id,
        @Parameter(description = "目标文件夹ID") @RequestParam(required = false) Long targetFolderId) {
        return toAjax(fileService.moveFile(id, targetFolderId));
    }

    /**
     * 复制文件
     */
    // @SaCheckPermission("coedit:file:add")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "复制文件", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/{id}/copy")
    @Operation(summary = "复制文件", description = "复制文件到指定文件夹")
    public R<CoFileVo> copy(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long id,
        @Parameter(description = "目标文件夹ID") @RequestParam(required = false) Long targetFolderId,
        @Parameter(description = "新文件名") @RequestParam(required = false) String newFileName) {

        CoFileVo file = fileService.copyFile(id, targetFolderId, newFileName);
        return R.ok(file);
    }

    /**
     * 重命名文件
     */
    // @SaCheckPermission("coedit:file:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "重命名文件", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{id}/rename")
    @Operation(summary = "重命名文件", description = "重命名文件")
    public R<Void> rename(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long id,
        @Parameter(description = "新文件名", required = true) @NotNull(message = "新文件名不能为空") @RequestParam String newFileName) {
        return toAjax(fileService.renameFile(id, newFileName));
    }

    /**
     * 批量移动文件
     */
    // @SaCheckPermission("coedit:file:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "批量移动文件", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/batch/move")
    @Operation(summary = "批量移动文件", description = "批量移动文件到指定文件夹")
    public R<Void> batchMove(
        @Parameter(description = "文件ID列表", required = true) @NotEmpty(message = "文件ID列表不能为空") @RequestBody List<Long> fileIds,
        @Parameter(description = "目标文件夹ID") @RequestParam(required = false) Long targetFolderId) {
        return toAjax(fileService.batchMoveFiles(fileIds, targetFolderId));
    }

    /**
     * 批量复制文件
     */
    // @SaCheckPermission("coedit:file:add")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "批量复制文件", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/batch/copy")
    @Operation(summary = "批量复制文件", description = "批量复制文件到指定文件夹")
    public R<Void> batchCopy(
        @Parameter(description = "文件ID列表", required = true) @NotEmpty(message = "文件ID列表不能为空") @RequestBody List<Long> fileIds,
        @Parameter(description = "目标文件夹ID") @RequestParam(required = false) Long targetFolderId) {
        return toAjax(fileService.batchCopyFiles(fileIds, targetFolderId));
    }

    /**
     * 创建在线文档
     * <p>
     * workspaceId处理逻辑:
     * 1. 如果提供了workspaceId,直接使用该工作空间
     * 2. 如果未提供workspaceId,系统会自动处理:
     *    - 首先查询当前用户的默认工作空间(is_default=1)
     *    - 如果找到默认工作空间,使用该工作空间
     *    - 如果未找到默认工作空间,自动创建一个新的默认工作空间
     * </p>
     */
    // @SaCheckPermission("coedit:file:add")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "创建在线文档", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/create")
    @Operation(summary = "创建在线文档", description = "创建Word/Excel/PowerPoint/文本文档。workspaceId可选,不提供则自动使用或创建用户的默认工作空间(is_default=1)")
    public R<CoFileVo> createDocument(@Validated @RequestBody CreateDocumentRequest request) {

        // 获取当前登录用户ID
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);

        // 如果没有提供workspaceId,自动获取或创建默认工作空间
        // 逻辑: 先查询用户的默认工作空间(is_default=1),如果不存在则创建bangw
        Long workspaceId = request.getWorkspaceId();
        if (workspaceId == null) {
            log.info("用户[{}]未指定工作空间,开始获取或创建默认工作空间", userId);
            workspaceId = defaultWorkspaceHelper.getOrCreateDefaultWorkspaceForCurrentUser();
            log.info("用户[{}]使用默认工作空间:{}", userId, workspaceId);
        }

        CoFileVo file = fileService.createDocument(workspaceId, request.getFolderId(), request.getFileName(), request.getFileType());
        return R.ok(file);
    }

    /**
     * 导出文档为PDF
     */
    // @SaCheckPermission("coedit:file:download")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "导出PDF", businessType = BusinessType.EXPORT)
    @GetMapping("/export/pdf/{id}")
    @Operation(summary = "导出文档为PDF", description = "将文档导出为PDF格式")
    public void exportToPdf(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long id,
        HttpServletResponse response) {

        // 获取文件信息
        CoFileVo file = fileService.queryById(id);
        if (file == null) {
            return;
        }

        // 导出为PDF
        byte[] pdfBytes = documentExportService.exportToPdf(id);

        // 设置响应头
        String pdfFileName = file.getFileName().replaceAll("\\.[^.]+$", "") + ".pdf";
        FileUtils.setAttachmentResponseHeader(response, pdfFileName);
        response.setContentType("application/pdf");

        try {
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();

            // 记录文件访问 - 下载类型
            try {
                Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
                // 记录到文件访问记录表
                fileAccessRecordService.recordAccess(id, userId, 3); // 3-下载
                // 记录到通用访问记录表
                accessRecordService.recordFileAccess(userId, id, file.getWorkspaceId(), 3);
                log.debug("记录文件导出PDF: fileId={}, userId={}, accessType=3(下载)", id, userId);
            } catch (Exception ex) {
                log.error("记录文件导出PDF失败: fileId={}", id, ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("PDF导出失败", e);
        }
    }

    /**
     * 导出文档为图片
     */
    // @SaCheckPermission("coedit:file:download")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "导出图片", businessType = BusinessType.EXPORT)
    @GetMapping("/export/image/{id}")
    @Operation(summary = "导出文档为图片", description = "将演示文稿导出为图片格式（仅支持pptx）")
    public void exportToImage(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long id,
        @Parameter(description = "图片格式（png/jpg）", required = false) @RequestParam(defaultValue = "png") String format,
        HttpServletResponse response) {

        // 获取文件信息
        CoFileVo file = fileService.queryById(id);
        if (file == null) {
            return;
        }

        // 导出为图片
        byte[] imageBytes = documentExportService.exportToImage(id, format);

        // 设置响应头
        String imageFileName = file.getFileName().replaceAll("\\.[^.]+$", "") + "." + format.toLowerCase();
        FileUtils.setAttachmentResponseHeader(response, imageFileName);
        response.setContentType("image/" + format.toLowerCase());

        try {
            response.getOutputStream().write(imageBytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new RuntimeException("图片导出失败", e);
        }
    }

    /**
     * 从CSV导入数据到Excel
     * <p>
     * workspaceId处理逻辑:
     * 1. 如果提供了workspaceId,直接使用该工作空间
     * 2. 如果未提供workspaceId,系统会自动处理:
     *    - 首先查询当前用户的默认工作空间(is_default=1)
     *    - 如果找到默认工作空间,使用该工作空间
     *    - 如果未找到默认工作空间,自动创建一个新的默认工作空间
     * </p>
     */
    // @SaCheckPermission("coedit:file:upload")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "CSV导入", businessType = BusinessType.IMPORT)
    @RepeatSubmit()
    @PostMapping(value = "/import/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "从CSV导入数据", description = "将CSV文件导入为Excel表格。workspaceId可选,不提供则自动使用或创建用户的默认工作空间(is_default=1)")
    public R<CoFileVo> importFromCsv(
        @Parameter(description = "工作空间ID(可选,不提供则自动使用或创建默认工作空间)") @RequestParam(required = false) Long workspaceId,
        @Parameter(description = "文件夹ID") @RequestParam(required = false) Long folderId,
        @Parameter(description = "CSV文件", required = true) @RequestPart("file") MultipartFile file,
        @Parameter(description = "目标文件名", required = true) @NotNull(message = "文件名不能为空") @RequestParam String fileName) {

        // 获取当前登录用户ID
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);

        // 如果没有提供workspaceId,自动获取或创建默认工作空间
        // 逻辑: 先查询用户的默认工作空间(is_default=1),如果不存在则创建
        if (workspaceId == null) {
            log.info("用户[{}]未指定工作空间,开始获取或创建默认工作空间", userId);
            workspaceId = defaultWorkspaceHelper.getOrCreateDefaultWorkspaceForCurrentUser();
            log.info("用户[{}]使用默认工作空间:{}", userId, workspaceId);
        }

        CoFileVo fileVo = spreadsheetService.importFromCsv(workspaceId, folderId, file, fileName);
        return R.ok(fileVo);
    }

    /**
     * 导出Excel为CSV
     */
    // @SaCheckPermission("coedit:file:download")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "CSV导出", businessType = BusinessType.EXPORT)
    @GetMapping("/export/csv/{id}")
    @Operation(summary = "导出Excel为CSV", description = "将Excel表格导出为CSV格式")
    public void exportToCsv(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long id,
        HttpServletResponse response) {

        // 获取文件信息
        CoFileVo file = fileService.queryById(id);
        if (file == null) {
            return;
        }

        // 导出为CSV
        byte[] csvBytes = spreadsheetService.exportToCsv(id);

        // 设置响应头
        String csvFileName = file.getFileName().replaceAll("\\.[^.]+$", "") + ".csv";
        FileUtils.setAttachmentResponseHeader(response, csvFileName);
        response.setContentType("text/csv; charset=UTF-8");

        try {
            response.getOutputStream().write(csvBytes);
            response.getOutputStream().flush();

            // 记录文件访问 - 下载类型
            try {
                Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
                // 记录到文件访问记录表
                fileAccessRecordService.recordAccess(id, userId, 3); // 3-下载
                // 记录到通用访问记录表
                accessRecordService.recordFileAccess(userId, id, file.getWorkspaceId(), 3);
                log.debug("记录文件导出CSV: fileId={}, userId={}, accessType=3(下载)", id, userId);
            } catch (Exception ex) {
                log.error("记录文件导出CSV失败: fileId={}", id, ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("CSV导出失败", e);
        }
    }

    /**
     * 清空所有业务数据 - 仅开发环境可用
     *
     * <p>
     * 极度危险操作：此接口将清空所有业务数据，包括：
     * 1. 清空所有文件内容（保留文件结构）
     * 2. 清理相关业务数据
     * 仅用于开发环境重置测试数据，生产环境将自动屏蔽此接口。
     * </p>
     */
    @Log(title = "清空所有业务数据", businessType = BusinessType.DELETE)
    @GetMapping("/dev/clear-all")
    @Operation(summary = "清空所有业务数据", description = "清空所有文件内容和业务数据（仅开发环境可用）")
    public R<String> clearAllBusinessData() {

        // 检查是否为开发环境
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isDev = false;
        for (String profile : activeProfiles) {
            if ("dev".equals(profile)) {
                isDev = true;
                break;
            }
        }

        if (!isDev) {
            log.warn("清空所有业务数据接口被调用，但当前不是开发环境，已拒绝执行");
            return R.fail("此接口仅在开发环境可用");
        }

        log.warn("开发环境：执行清空所有业务数据操作");

        try {
            StringBuilder resultLog = new StringBuilder();

            // 1. 获取所有文件
            List<CoFileVo> allFiles = fileService.queryList(new CoFileBo());
            int totalFiles = allFiles.size();
            resultLog.append(String.format("查询到 %d 个文件记录...\n", totalFiles));

            // 2. 删除物理文件
            int deletedFiles = 0;
            int failedFiles = 0;
            for (CoFileVo fileVo : allFiles) {
                try {
                    String filePath = fileVo.getFilePath();
                    if (filePath != null && !filePath.trim().isEmpty()) {
                        File file = new File(filePath);
                        if (file.exists() && file.delete()) {
                            deletedFiles++;
                            log.info("已删除物理文件: {}", filePath);
                        } else if (file.exists()) {
                            log.warn("删除物理文件失败: {}", filePath);
                            failedFiles++;
                        }
                    }
                } catch (Exception e) {
                    log.error("删除物理文件时发生错误: fileId={}, fileName={}", fileVo.getId(), fileVo.getFileName(), e);
                    failedFiles++;
                }
            }
            resultLog.append(String.format("物理文件删除完成：成功 %d 个，失败 %d 个\n", deletedFiles, failedFiles));

            // 3. 删除数据库记录
            try {
                // 删除文件表
                fileService.clearAll();
                resultLog.append(String.format("已清空文件表(co_file)\n"));

                // 删除文件访问记录表
                coFileAccessRecordMapper.clearAll();
                resultLog.append("已清空文件访问记录(co_file_access_record)\n");

                // 删除通用访问记录表
                coAccessRecordMapper.clearAll();
                resultLog.append("已清空通用访问记录(co_access_record)\n");

                resultLog.append("数据库记录清理完成\n");

            } catch (Exception e) {
                log.error("清理数据库记录时发生错误", e);
                resultLog.append("数据库记录清理失败: ").append(e.getMessage()).append("\n");
            }

            String finalResult = resultLog.toString();
            log.warn("开发环境清理结果:\n{}", finalResult);

            return R.ok(finalResult);

        } catch (Exception e) {
            log.error("清空所有业务数据操作失败", e);
            return R.fail("清空所有业务数据失败: " + e.getMessage());
        }
    }

}

