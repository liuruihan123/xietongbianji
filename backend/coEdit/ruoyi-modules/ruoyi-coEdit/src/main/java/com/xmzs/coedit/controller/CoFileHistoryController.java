package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xmzs.coedit.domain.bo.CoFileHistoryBo;
import com.xmzs.coedit.domain.vo.CoFileChangeVo;
import com.xmzs.coedit.domain.vo.CoFileHistoryVo;
import com.xmzs.coedit.service.ICoFileHistoryService;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.satoken.utils.LoginHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 文件历史控制器
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Validated
@RestController
@RequestMapping("/coedit/history")
@RequiredArgsConstructor
@Tag(name = "文件历史管理", description = "文件历史管理接口")
public class CoFileHistoryController {

    private final ICoFileHistoryService historyService;

    /**
     * 创建文件历史记录
     */
    @PostMapping("/create")
    @Operation(summary = "创建文件历史记录")
    // @SaCheckPermission("coedit:file:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件历史", businessType = BusinessType.INSERT)
    public R<Long> createHistory(@Validated @RequestBody CoFileHistoryBo historyBo) {
        Long historyId = historyService.createHistory(historyBo);
        return R.ok(historyId);
    }

    /**
     * 获取文件历史列表
     */
    @GetMapping("/list/{fileId}")
    @Operation(summary = "获取文件历史列表")
    @Parameter(name = "fileId", description = "文件ID", required = true)
    public R<List<CoFileHistoryVo>> getFileHistoryList(@PathVariable Long fileId) {
        List<CoFileHistoryVo> list = historyService.getFileHistoryList(fileId);
        return R.ok(list);
    }

    /**
     * 获取文件历史详情
     */
    @GetMapping("/detail/{historyId}")
    @Operation(summary = "获取文件历史详情")
    @Parameter(name = "historyId", description = "历史ID", required = true)
    public R<CoFileHistoryVo> getHistoryDetail(@PathVariable Long historyId) {
        CoFileHistoryVo vo = historyService.getHistoryDetail(historyId);
        return R.ok(vo);
    }

    /**
     * 获取指定版本的历史
     */
    @GetMapping("/version/{fileId}/{version}")
    @Operation(summary = "获取指定版本的历史")
    @Parameter(name = "fileId", description = "文件ID", required = true)
    @Parameter(name = "version", description = "版本号", required = true)
    public R<CoFileHistoryVo> getHistoryByVersion(@PathVariable Long fileId, @PathVariable Integer version) {
        CoFileHistoryVo vo = historyService.getHistoryByVersion(fileId, version);
        return R.ok(vo);
    }

    /**
     * 获取当前版本的历史
     */
    @GetMapping("/current/{fileId}")
    @Operation(summary = "获取当前版本的历史")
    @Parameter(name = "fileId", description = "文件ID", required = true)
    public R<CoFileHistoryVo> getCurrentHistory(@PathVariable Long fileId) {
        CoFileHistoryVo vo = historyService.getCurrentHistory(fileId);
        return R.ok(vo);
    }

    /**
     * 获取OnlyOffice历史配置
     */
    @GetMapping("/onlyoffice/config/{fileId}")
    @Operation(summary = "获取OnlyOffice历史配置")
    @Parameter(name = "fileId", description = "文件ID", required = true)
    public R<Map<String, Object>> getOnlyOfficeHistoryConfig(@PathVariable Long fileId) {
        Map<String, Object> config = historyService.getOnlyOfficeHistoryConfig(fileId);
        return R.ok(config);
    }

    /**
     * 获取OnlyOffice历史数据
     */
    @GetMapping("/onlyoffice/data/{fileId}/{version}")
    @Operation(summary = "获取OnlyOffice历史数据")
    @Parameter(name = "fileId", description = "文件ID", required = true)
    @Parameter(name = "version", description = "版本号", required = true)
    public R<Map<String, Object>> getOnlyOfficeHistoryData(@PathVariable Long fileId, @PathVariable Integer version) {
        Map<String, Object> data = historyService.getOnlyOfficeHistoryData(fileId, version);
        return R.ok(data);
    }

    /**
     * 对比两个版本
     */
    @GetMapping("/compare/{fileId}")
    @Operation(summary = "对比两个版本")
    @Parameter(name = "fileId", description = "文件ID", required = true)
    @Parameter(name = "version1", description = "版本1", required = true)
    @Parameter(name = "version2", description = "版本2", required = true)
    public R<Map<String, Object>> compareVersions(
        @PathVariable Long fileId,
        @RequestParam Integer version1,
        @RequestParam Integer version2
    ) {
        Map<String, Object> result = historyService.compareVersions(fileId, version1, version2);
        return R.ok(result);
    }

    /**
     * 恢复到指定版本
     */
    @PostMapping("/restore/{fileId}/{version}")
    @Operation(summary = "恢复到指定版本")
    @Parameter(name = "fileId", description = "文件ID", required = true)
    @Parameter(name = "version", description = "版本号", required = true)
    // @SaCheckPermission("coedit:file:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件历史", businessType = BusinessType.UPDATE)
    public R<Boolean> restoreVersion(@PathVariable Long fileId, @PathVariable Integer version) {
        Long userId = LoginHelper.getUserId();
        Boolean result = historyService.restoreVersion(fileId, version, userId);
        return R.ok(result);
    }

    /**
     * 删除文件历史
     */
    @DeleteMapping("/{historyId}")
    @Operation(summary = "删除文件历史")
    @Parameter(name = "historyId", description = "历史ID", required = true)
    // @SaCheckPermission("coedit:file:delete")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件历史", businessType = BusinessType.DELETE)
    public R<Boolean> deleteHistory(@PathVariable Long historyId) {
        Boolean result = historyService.deleteHistory(historyId);
        return R.ok(result);
    }

    /**
     * 批量删除文件历史
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除文件历史")
    // @SaCheckPermission("coedit:file:delete")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件历史", businessType = BusinessType.DELETE)
    public R<Boolean> batchDeleteHistory(@RequestBody Long[] historyIds) {
        Boolean result = historyService.batchDeleteHistory(historyIds);
        return R.ok(result);
    }

    /**
     * 删除文件的所有历史
     */
    @DeleteMapping("/file/{fileId}")
    @Operation(summary = "删除文件的所有历史")
    @Parameter(name = "fileId", description = "文件ID", required = true)
    // @SaCheckPermission("coedit:file:delete")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件历史", businessType = BusinessType.DELETE)
    public R<Integer> deleteFileHistory(@PathVariable Long fileId) {
        Integer count = historyService.deleteFileHistory(fileId);
        return R.ok(count);
    }

    /**
     * 获取文件的变更列表
     */
    @GetMapping("/changes/{fileId}")
    @Operation(summary = "获取文件的变更列表")
    @Parameter(name = "fileId", description = "文件ID", required = true)
    public R<List<CoFileChangeVo>> getFileChanges(@PathVariable Long fileId) {
        List<CoFileChangeVo> changes = historyService.getFileChanges(fileId);
        return R.ok(changes);
    }

    /**
     * 获取版本的变更列表
     */
    @GetMapping("/changes/version/{historyId}")
    @Operation(summary = "获取版本的变更列表")
    @Parameter(name = "historyId", description = "历史ID", required = true)
    public R<List<CoFileChangeVo>> getVersionChanges(@PathVariable Long historyId) {
        List<CoFileChangeVo> changes = historyService.getVersionChanges(historyId);
        return R.ok(changes);
    }

    /**
     * 保存变更数据
     */
    @PostMapping("/changes/save")
    @Operation(summary = "保存变更数据")
    // @SaCheckPermission("coedit:file:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件历史", businessType = BusinessType.UPDATE)
    public R<Boolean> saveChanges(@RequestParam Long historyId, @RequestBody String changesData) {
        Boolean result = historyService.saveChanges(historyId, changesData);
        return R.ok(result);
    }

    /**
     * 清理过期历史
     */
    @PostMapping("/clean/{fileId}")
    @Operation(summary = "清理过期历史")
    @Parameter(name = "fileId", description = "文件ID", required = true)
    @Parameter(name = "keepVersions", description = "保留版本数", required = true)
    // @SaCheckPermission("coedit:file:delete")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件历史", businessType = BusinessType.DELETE)
    public R<Integer> cleanOldHistory(@PathVariable Long fileId, @RequestParam(defaultValue = "10") Integer keepVersions) {
        Integer count = historyService.cleanOldHistory(fileId, keepVersions);
        return R.ok(count);
    }
}

