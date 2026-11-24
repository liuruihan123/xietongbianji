package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xmzs.coedit.domain.bo.CoEditSessionBo;
import com.xmzs.coedit.domain.vo.CoEditSessionVo;
import com.xmzs.coedit.service.ICoEditSessionService;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.excel.utils.ExcelUtil;
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
 * 协同编辑会话管理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/session")
@Tag(name = "协同编辑会话管理", description = "协同编辑会话管理API")
public class CoEditSessionController extends BaseController {

    private final ICoEditSessionService sessionService;

    /**
     * 查询协同编辑会话列表
     */
    // @SaCheckPermission("coedit:session:list")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/list")
    @Operation(summary = "查询协同编辑会话列表", description = "分页查询协同编辑会话列表")
    public TableDataInfo<CoEditSessionVo> list(CoEditSessionBo bo, PageQuery pageQuery) {
        return sessionService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出协同编辑会话列表
     */
    // @SaCheckPermission("coedit:session:export")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "协同编辑会话", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @Operation(summary = "导出协同编辑会话列表", description = "导出协同编辑会话列表到Excel")
    public void export(CoEditSessionBo bo, HttpServletResponse response) {
        List<CoEditSessionVo> list = sessionService.queryList(bo);
        ExcelUtil.exportExcel(list, "协同编辑会话", CoEditSessionVo.class, response);
    }

    /**
     * 获取协同编辑会话详细信息
     */
    // @SaCheckPermission("coedit:session:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/{id}")
    @Operation(summary = "获取协同编辑会话详细信息", description = "根据ID获取协同编辑会话详细信息")
    public R<CoEditSessionVo> getInfo(
        @Parameter(description = "会话ID", required = true) @NotNull(message = "会话ID不能为空") @PathVariable Long id) {
        return R.ok(sessionService.queryById(id));
    }

    /**
     * 查询文件的活跃会话列表
     */
    // @SaCheckPermission("coedit:session:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/active/file/{fileId}")
    @Operation(summary = "查询文件的活跃会话列表", description = "查询指定文件的所有活跃编辑会话")
    public R<List<CoEditSessionVo>> getActiveSessionsByFileId(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long fileId) {
        return R.ok(sessionService.queryActiveSessionsByFileId(fileId));
    }

    /**
     * 查询用户的活跃会话列表
     */
    // @SaCheckPermission("coedit:session:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/active/user/{userId}")
    @Operation(summary = "查询用户的活跃会话列表", description = "查询指定用户的所有活跃编辑会话")
    public R<List<CoEditSessionVo>> getActiveSessionsByUserId(
        @Parameter(description = "用户ID", required = true) @NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        return R.ok(sessionService.queryActiveSessionsByUserId(userId));
    }

    /**
     * 查询用户的编辑历史
     */
    // @SaCheckPermission("coedit:session:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/history/user/{userId}")
    @Operation(summary = "查询用户的编辑历史", description = "分页查询指定用户的编辑历史")
    public TableDataInfo<CoEditSessionVo> getUserHistory(
        @Parameter(description = "用户ID", required = true) @NotNull(message = "用户ID不能为空") @PathVariable Long userId,
        PageQuery pageQuery) {
        return sessionService.queryUserHistory(userId, pageQuery);
    }

    /**
     * 查询文件的编辑历史
     */
    // @SaCheckPermission("coedit:session:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/history/file/{fileId}")
    @Operation(summary = "查询文件的编辑历史", description = "分页查询指定文件的编辑历史")
    public TableDataInfo<CoEditSessionVo> getFileHistory(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long fileId,
        PageQuery pageQuery) {
        return sessionService.queryFileHistory(fileId, pageQuery);
    }

    /**
     * 检查文件是否有活跃会话
     */
    // @SaCheckPermission("coedit:session:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/check/{fileId}")
    @Operation(summary = "检查文件是否有活跃会话", description = "检查指定文件是否有用户正在编辑")
    public R<Boolean> hasActiveSessions(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long fileId) {
        return R.ok(sessionService.hasActiveSessions(fileId));
    }

    /**
     * 获取文件的活跃会话数量
     */
    // @SaCheckPermission("coedit:session:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/count/{fileId}")
    @Operation(summary = "获取文件的活跃会话数量", description = "获取指定文件的活跃编辑会话数量")
    public R<Integer> getActiveSessionCount(
        @Parameter(description = "文件ID", required = true) @NotNull(message = "文件ID不能为空") @PathVariable Long fileId) {
        return R.ok(sessionService.getActiveSessionCount(fileId));
    }

    /**
     * 更新会话活跃时间（心跳）
     */
    // @SaCheckPermission("coedit:session:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "协同编辑会话", businessType = BusinessType.UPDATE)
    @PutMapping("/heartbeat/{sessionKey}")
    @Operation(summary = "更新会话活跃时间", description = "更新会话的最后活跃时间（心跳）")
    public R<Void> updateActiveTime(
        @Parameter(description = "会话Key", required = true) @NotNull(message = "会话Key不能为空") @PathVariable String sessionKey) {
        return toAjax(sessionService.updateActiveTime(sessionKey));
    }

    /**
     * 结束编辑会话
     */
    // @SaCheckPermission("coedit:session:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "协同编辑会话", businessType = BusinessType.UPDATE)
    @PutMapping("/end/{sessionKey}")
    @Operation(summary = "结束编辑会话", description = "结束指定的编辑会话")
    public R<Void> endSession(
        @Parameter(description = "会话Key", required = true) @NotNull(message = "会话Key不能为空") @PathVariable String sessionKey) {
        return toAjax(sessionService.endSession(sessionKey));
    }

    /**
     * 删除协同编辑会话
     */
    // @SaCheckPermission("coedit:session:remove")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "协同编辑会话", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除协同编辑会话", description = "批量删除协同编辑会话")
    public R<Void> remove(
        @Parameter(description = "会话ID数组", required = true) @NotEmpty(message = "会话ID不能为空") @PathVariable Long[] ids) {
        return toAjax(sessionService.deleteByIds(List.of(ids)));
    }

}

