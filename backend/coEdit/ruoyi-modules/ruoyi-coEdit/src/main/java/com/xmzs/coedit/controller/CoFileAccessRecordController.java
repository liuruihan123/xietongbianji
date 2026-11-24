package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xmzs.coedit.domain.bo.CoFileAccessRecordBo;
import com.xmzs.coedit.domain.vo.CoFileAccessRecordVo;
import com.xmzs.coedit.service.ICoFileAccessRecordService;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 文件访问记录管理
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/file-access-record")
@Tag(name = "文件访问记录管理", description = "文件访问记录管理接口")
public class CoFileAccessRecordController extends BaseController {

    private final ICoFileAccessRecordService accessRecordService;

    /**
     * 记录文件访问
     */
    @Operation(summary = "记录文件访问")
    @Log(title = "文件访问记录", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> recordAccess(@Validated(AddGroup.class) @RequestBody CoFileAccessRecordBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        return toAjax(accessRecordService.recordAccess(bo));
    }

    /**
     * 记录文件访问(简化版)
     */
    @Operation(summary = "记录文件访问(简化版)")
    @Log(title = "文件访问记录", businessType = BusinessType.INSERT)
    @PostMapping("/simple")
    public R<Void> recordAccessSimple(
        @Parameter(description = "文件ID") @RequestParam Long fileId,
        @Parameter(description = "访问类型:1-查看 2-编辑 3-下载") @RequestParam(defaultValue = "1") Integer accessType) {

        Long userId = LoginHelper.getUserId();
        return toAjax(accessRecordService.recordAccess(fileId, userId, accessType));
    }

    /**
     * 获取最近访问的文件列表(分页)
     */
    @Operation(summary = "获取最近访问的文件列表(分页)")
    @GetMapping("/recent")
    public TableDataInfo<CoFileAccessRecordVo> getRecentAccess(
        @Parameter(description = "工作空间ID(可选)") @RequestParam(required = false) Long workspaceId,
        PageQuery pageQuery) {

        Long userId = LoginHelper.getUserId();
        return accessRecordService.getRecentAccess(userId, workspaceId, pageQuery);
    }

    /**
     * 获取最近访问的文件列表(不分页)
     */
    @Operation(summary = "获取最近访问的文件列表(不分页)")
    @GetMapping("/recent/list")
    public R<List<CoFileAccessRecordVo>> getRecentAccessList(
        @Parameter(description = "工作空间ID(可选)") @RequestParam(required = false) Long workspaceId,
        @Parameter(description = "限制数量") @RequestParam(defaultValue = "20") Integer limit) {

        Long userId = LoginHelper.getUserId();
        List<CoFileAccessRecordVo> list = accessRecordService.getRecentAccessList(userId, workspaceId, limit);
        return R.ok(list);
    }

    /**
     * 获取访问统计
     */
    @Operation(summary = "获取访问统计")
    @GetMapping("/statistics")
    public R<Map<String, Object>> getAccessStatistics(
        @Parameter(description = "工作空间ID(可选)") @RequestParam(required = false) Long workspaceId) {

        Long userId = LoginHelper.getUserId();
        Map<String, Object> statistics = accessRecordService.getAccessStatistics(userId, workspaceId);
        return R.ok(statistics);
    }

    /**
     * 获取访问最多的文件
     */
    @Operation(summary = "获取访问最多的文件")
    @GetMapping("/top-accessed")
    public R<List<Map<String, Object>>> getTopAccessedFiles(
        @Parameter(description = "工作空间ID(可选)") @RequestParam(required = false) Long workspaceId,
        @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") Integer limit) {

        Long userId = LoginHelper.getUserId();
        List<Map<String, Object>> topFiles = accessRecordService.getTopAccessedFiles(userId, workspaceId, limit);
        return R.ok(topFiles);
    }

}

