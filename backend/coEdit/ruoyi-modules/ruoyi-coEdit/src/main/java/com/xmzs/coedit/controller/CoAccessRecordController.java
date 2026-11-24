package com.xmzs.coedit.controller;

import com.xmzs.coedit.domain.vo.CoAccessRecordVo;
import com.xmzs.coedit.service.ICoAccessRecordService;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 访问记录管理
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/access-record")
@Tag(name = "访问记录管理", description = "访问记录管理接口")
public class CoAccessRecordController extends BaseController {

    private final ICoAccessRecordService accessRecordService;

    /**
     * 记录工作空间访问
     */
    @Operation(summary = "记录工作空间访问")
    @PostMapping("/workspace")
    public R<Void> recordWorkspaceAccess(@RequestBody WorkspaceAccessRequest request) {
        Long userId = LoginHelper.getUserId();
        accessRecordService.recordWorkspaceAccess(userId, request.getWorkspaceId());
        return R.ok();
    }

    /**
     * 记录文件夹访问
     */
    @Operation(summary = "记录文件夹访问")
    @PostMapping("/folder")
    public R<Void> recordFolderAccess(@RequestBody FolderAccessRequest request) {
        Long userId = LoginHelper.getUserId();
        accessRecordService.recordFolderAccess(userId, request.getFolderId(), request.getWorkspaceId());
        return R.ok();
    }

    /**
     * 记录文件访问
     */
    @Operation(summary = "记录文件访问")
    @PostMapping("/file")
    public R<Void> recordFileAccess(@RequestBody FileAccessRequest request) {
        Long userId = LoginHelper.getUserId();
        Integer accessType = request.getAccessType() != null ? request.getAccessType() : 1;
        accessRecordService.recordFileAccess(userId, request.getFileId(), request.getWorkspaceId(), accessType);
        return R.ok();
    }

    /**
     * 工作空间访问请求对象
     */
    @Data
    @Schema(description = "工作空间访问请求")
    public static class WorkspaceAccessRequest {
        @Schema(description = "工作空间ID")
        @NotNull(message = "工作空间ID不能为空")
        private Long workspaceId;
    }

    /**
     * 文件夹访问请求对象
     */
    @Data
    @Schema(description = "文件夹访问请求")
    public static class FolderAccessRequest {
        @Schema(description = "文件夹ID")
        @NotNull(message = "文件夹ID不能为空")
        private Long folderId;

        @Schema(description = "工作空间ID")
        @NotNull(message = "工作空间ID不能为空")
        private Long workspaceId;
    }

    /**
     * 文件访问请求对象
     */
    @Data
    @Schema(description = "文件访问请求")
    public static class FileAccessRequest {
        @Schema(description = "文件ID")
        @NotNull(message = "文件ID不能为空")
        private Long fileId;

        @Schema(description = "工作空间ID")
        @NotNull(message = "工作空间ID不能为空")
        private Long workspaceId;

        @Schema(description = "访问类型:1-查看 2-编辑 3-下载")
        private Integer accessType;
    }


    /**
     * 获取最近访问记录(分页)
     */
    @Operation(summary = "获取最近访问记录(分页)")
    @GetMapping("/recent")
    public TableDataInfo<CoAccessRecordVo> getRecentAccess(
        @Parameter(description = "工作空间ID(可选)") @RequestParam(required = false) Long workspaceId,
        PageQuery pageQuery) {

        Long userId = LoginHelper.getUserId();
        return accessRecordService.getRecentAccess(userId, workspaceId, pageQuery);
    }

    /**
     * 获取最近访问记录列表(不分页,最多50条)
     */
    @Operation(summary = "获取最近访问记录列表(不分页,最多50条)")
    @GetMapping("/recent/list")
    public R<List<CoAccessRecordVo>> getRecentAccessList(
        @Parameter(description = "工作空间ID(可选)") @RequestParam(required = false) Long workspaceId,
        @Parameter(description = "限制数量(最多50)") @RequestParam(defaultValue = "50") Integer limit) {

        Long userId = LoginHelper.getUserId();
        List<CoAccessRecordVo> list = accessRecordService.getRecentAccessList(userId, workspaceId, limit);
        return R.ok(list);
    }

    /**
     * 获取访问统计信息
     */
    @Operation(summary = "获取访问统计信息")
    @GetMapping("/statistics")
    public R<Map<String, Object>> getAccessStatistics(
        @Parameter(description = "工作空间ID(可选)") @RequestParam(required = false) Long workspaceId) {

        Long userId = LoginHelper.getUserId();
        Map<String, Object> statistics = accessRecordService.getAccessStatistics(userId, workspaceId);
        return R.ok(statistics);
    }

    /**
     * 获取访问最多的目标
     */
    @Operation(summary = "获取访问最多的目标")
    @GetMapping("/top-accessed")
    public R<List<Map<String, Object>>> getTopAccessedTargets(
        @Parameter(description = "工作空间ID(可选)") @RequestParam(required = false) Long workspaceId,
        @Parameter(description = "目标类型(可选):1-工作空间 2-文件夹 3-文件") @RequestParam(required = false) Integer targetType,
        @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") Integer limit) {

        Long userId = LoginHelper.getUserId();
        List<Map<String, Object>> list = accessRecordService.getTopAccessedTargets(userId, workspaceId, targetType, limit);
        return R.ok(list);
    }

}

