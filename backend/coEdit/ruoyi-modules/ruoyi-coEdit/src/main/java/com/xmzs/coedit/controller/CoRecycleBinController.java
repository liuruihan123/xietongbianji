package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xmzs.coedit.domain.bo.CoRecycleBinBo;
import com.xmzs.coedit.domain.vo.CoRecycleBinVo;
import com.xmzs.coedit.service.ICoRecycleBinService;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.excel.utils.ExcelUtil;
import com.xmzs.common.idempotent.annotation.RepeatSubmit;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 回收站管理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/recycleBin")
@Tag(name = "回收站管理", description = "回收站管理")
public class CoRecycleBinController extends BaseController {

    private final ICoRecycleBinService recycleBinService;

    /**
     * 查询回收站列表
     */
//    @SaCheckPermission("coedit:recycleBin:list")
    @GetMapping("/list")
    @Operation(summary = "查询回收站列表", description = "查询回收站列表（分页）")
    public TableDataInfo<CoRecycleBinVo> list(CoRecycleBinBo bo, PageQuery pageQuery) {
        return recycleBinService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出回收站列表
     */
//    @SaCheckPermission("coedit:recycleBin:export")
    @Log(title = "回收站", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @Operation(summary = "导出回收站列表", description = "导出回收站列表")
    public void export(CoRecycleBinBo bo, HttpServletResponse response) {
        List<CoRecycleBinVo> list = recycleBinService.queryList(bo);
        ExcelUtil.exportExcel(list, "回收站", CoRecycleBinVo.class, response);
    }

    /**
     * 获取回收站详细信息
     */
//    @SaCheckPermission("coedit:recycleBin:query")
    @GetMapping("/{id}")
    @Operation(summary = "获取回收站详细信息", description = "根据回收站ID获取详细信息")
    public R<CoRecycleBinVo> getInfo(@Parameter(description = "回收站ID", required = true) @NotNull(message = "回收站ID不能为空") @PathVariable Long id) {
        return R.ok(recycleBinService.queryById(id));
    }

    /**
     * 新增回收站
     */
//    @SaCheckPermission("coedit:recycleBin:add")
    @Log(title = "回收站", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    @Operation(summary = "新增回收站", description = "新增回收站")
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CoRecycleBinBo bo) {
        return toAjax(recycleBinService.insertByBo(bo));
    }

    /**
     * 修改回收站
     */
//    @SaCheckPermission("coedit:recycleBin:edit")
    @Log(title = "回收站", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    @Operation(summary = "修改回收站", description = "修改回收站")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CoRecycleBinBo bo) {
        return toAjax(recycleBinService.updateByBo(bo));
    }

    /**
     * 删除回收站
     */
//    @SaCheckPermission("coedit:recycleBin:remove")
    @Log(title = "回收站", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除回收站", description = "删除回收站")
    public R<Void> remove(@Parameter(description = "回收站ID数组", required = true) @NotEmpty(message = "回收站ID不能为空") @PathVariable Long[] ids) {
        return toAjax(recycleBinService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 将文件移动到回收站
     */
//    @SaCheckPermission("coedit:file:remove")
    @Log(title = "移动到回收站", businessType = BusinessType.DELETE)
    @RepeatSubmit()
    @PostMapping("/moveFile")
    @Operation(summary = "将文件移动到回收站", description = "将文件移动到回收站")
    public R<Void> moveFileToRecycleBin(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @RequestParam Long fileId,
        @Parameter(description = "删除原因") @RequestParam(required = false) String deleteReason) {
        return toAjax(recycleBinService.moveFileToRecycleBin(fileId, deleteReason));
    }

    /**
     * 从回收站恢复文件
     */
//    @SaCheckPermission("coedit:recycleBin:restore")
    @Log(title = "恢复文件", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{id}/restore")
    @Operation(summary = "从回收站恢复文件", description = "从回收站恢复文件")
    public R<Void> restoreFile(
        @Parameter(description = "回收站ID", required = true) @NotNull(message = "回收站ID不能为空") @PathVariable Long id) {
        return toAjax(recycleBinService.restoreFile(id));
    }

    /**
     * 从回收站永久删除文件
     */
//    @SaCheckPermission("coedit:recycleBin:delete")
    @Log(title = "永久删除文件", businessType = BusinessType.DELETE)
    @RepeatSubmit()
    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "从回收站永久删除文件", description = "从回收站永久删除文件")
    public R<Void> permanentlyDeleteFile(
        @Parameter(description = "回收站ID", required = true) @NotNull(message = "回收站ID不能为空") @PathVariable Long id) {
        return toAjax(recycleBinService.permanentlyDeleteFile(id));
    }

    /**
     * 批量从回收站恢复文件
     */
//    @SaCheckPermission("coedit:recycleBin:restore")
    @Log(title = "批量恢复文件", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/batch/restore")
    @Operation(summary = "批量从回收站恢复文件", description = "批量从回收站恢复文件")
    public R<Void> batchRestoreFiles(
        @Parameter(description = "回收站ID列表", required = true) @NotEmpty(message = "回收站ID列表不能为空") @RequestBody List<Long> recycleBinIds) {
        return toAjax(recycleBinService.batchRestoreFiles(recycleBinIds));
    }

    /**
     * 批量从回收站永久删除文件
     */
//    @SaCheckPermission("coedit:recycleBin:delete")
    @Log(title = "批量永久删除文件", businessType = BusinessType.DELETE)
    @RepeatSubmit()
    @DeleteMapping("/batch/permanent")
    @Operation(summary = "批量从回收站永久删除文件", description = "批量从回收站永久删除文件")
    public R<Void> batchPermanentlyDeleteFiles(
        @Parameter(description = "回收站ID列表", required = true) @NotEmpty(message = "回收站ID列表不能为空") @RequestBody List<Long> recycleBinIds) {
        return toAjax(recycleBinService.batchPermanentlyDeleteFiles(recycleBinIds));
    }

    /**
     * 清空回收站
     */
//    @SaCheckPermission("coedit:recycleBin:delete")
    @Log(title = "清空回收站", businessType = BusinessType.DELETE)
    @RepeatSubmit()
    @DeleteMapping("/empty/{workspaceId}")
    @Operation(summary = "清空回收站", description = "清空指定工作空间的回收站")
    public R<Void> emptyRecycleBin(
        @Parameter(description = "工作空间ID", required = true) @NotNull(message = "工作空间ID不能为空") @PathVariable Long workspaceId) {
        return toAjax(recycleBinService.emptyRecycleBin(workspaceId));
    }

    /**
     * 查询工作空间的回收站列表
     */
//    @SaCheckPermission("coedit:recycleBin:list")
    @GetMapping("/workspace/{workspaceId}")
    @Operation(summary = "查询工作空间的回收站列表", description = "根据工作空间ID查询回收站列表")
    public R<List<CoRecycleBinVo>> listByWorkspace(
        @Parameter(description = "工作空间ID", required = true) @NotNull(message = "工作空间ID不能为空") @PathVariable Long workspaceId) {
        return R.ok(recycleBinService.queryByWorkspaceId(workspaceId));
    }

    /**
     * 查询用户的回收站列表
     */
//    @SaCheckPermission("coedit:recycleBin:list")
    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户的回收站列表", description = "根据用户ID查询回收站列表")
    public R<List<CoRecycleBinVo>> listByUser(
        @Parameter(description = "用户ID", required = true) @NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        return R.ok(recycleBinService.queryByUserId(userId));
    }

    /**
     * 统计回收站大小
     */
//    @SaCheckPermission("coedit:recycleBin:query")
    @GetMapping("/size/{workspaceId}")
    @Operation(summary = "统计回收站大小", description = "统计指定工作空间的回收站大小")
    public R<Long> calculateSize(
        @Parameter(description = "工作空间ID", required = true) @NotNull(message = "工作空间ID不能为空") @PathVariable Long workspaceId) {
        return R.ok(recycleBinService.calculateRecycleBinSize(workspaceId));
    }

    /**
     * 统计回收站文件数量
     */
//    @SaCheckPermission("coedit:recycleBin:query")
    @GetMapping("/count/{workspaceId}")
    @Operation(summary = "统计回收站文件数量", description = "统计指定工作空间的回收站文件数量")
    public R<Integer> countFiles(
        @Parameter(description = "工作空间ID", required = true) @NotNull(message = "工作空间ID不能为空") @PathVariable Long workspaceId) {
        return R.ok(recycleBinService.countRecycleBinFiles(workspaceId));
    }

}

