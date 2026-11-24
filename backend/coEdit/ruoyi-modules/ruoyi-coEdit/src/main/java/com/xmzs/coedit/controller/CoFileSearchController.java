package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.xmzs.coedit.domain.bo.CoFileSearchBo;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.domain.vo.CoSearchHistoryVo;
import com.xmzs.coedit.service.ICoFileSearchService;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 文件搜索Controller
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/search")
@Tag(name = "文件搜索", description = "文件搜索接口")
public class CoFileSearchController extends BaseController {

    private final ICoFileSearchService fileSearchService;

    /**
     * 搜索文件
     */
    // @SaCheckPermission("coedit:file:search")  // 协同编辑模块基础功能,允许所有登录用户访问
    @PostMapping("/files")
    @Operation(summary = "搜索文件")
    public TableDataInfo<CoFileVo> searchFiles(
        @Validated @RequestBody CoFileSearchBo searchBo,
        PageQuery pageQuery) {
//        Long userId = StpUtil.getLoginIdAsLong();
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        return fileSearchService.searchFiles(searchBo, pageQuery, userId);
    }

    /**
     * 快速搜索
     */
    @GetMapping("/quick")
    @Operation(summary = "快速搜索")
    public R<List<CoFileVo>> quickSearch(
        @Parameter(description = "关键词") @RequestParam String keyword,
        @Parameter(description = "工作空间ID") @RequestParam(required = false) Long workspaceId) {
//        Long userId = StpUtil.getLoginIdAsLong();
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        List<CoFileVo> list = fileSearchService.quickSearch(keyword, workspaceId, userId);
        return R.ok(list);
    }

    /**
     * 高级搜索
     */
    // @SaCheckPermission("coedit:file:search")  // 协同编辑模块基础功能,允许所有登录用户访问
    @PostMapping("/advanced")
    @Operation(summary = "高级搜索")
    public TableDataInfo<CoFileVo> advancedSearch(
        @Validated @RequestBody CoFileSearchBo searchBo,
        PageQuery pageQuery) {
//        Long userId = StpUtil.getLoginIdAsLong();
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        return fileSearchService.advancedSearch(searchBo, pageQuery, userId);
    }

    /**
     * 获取搜索历史
     */
    @GetMapping("/history")
    @Operation(summary = "获取搜索历史")
    public R<List<CoSearchHistoryVo>> getSearchHistory(
        @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
//        Long userId = StpUtil.getLoginIdAsLong();
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        List<CoSearchHistoryVo> list = fileSearchService.getSearchHistory(userId, limit);
        return R.ok(list);
    }

    /**
     * 清空搜索历史
     */
    @Log(title = "清空搜索历史", businessType = BusinessType.DELETE)
    @DeleteMapping("/history/clear")
    @Operation(summary = "清空搜索历史")
    public R<Void> clearSearchHistory() {
//        Long userId = StpUtil.getLoginIdAsLong();
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        return toAjax(fileSearchService.clearSearchHistory(userId));
    }

    /**
     * 删除搜索历史
     */
    @Log(title = "删除搜索历史", businessType = BusinessType.DELETE)
    @DeleteMapping("/history/{historyId}")
    @Operation(summary = "删除搜索历史")
    public R<Void> deleteSearchHistory(@PathVariable Long historyId) {
//        Long userId = StpUtil.getLoginIdAsLong();
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        return toAjax(fileSearchService.deleteSearchHistory(historyId, userId));
    }

    /**
     * 获取热门搜索关键词
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门搜索关键词")
    public R<List<Map<String, Object>>> getHotKeywords(
        @Parameter(description = "工作空间ID") @RequestParam(required = false) Long workspaceId,
        @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<Map<String, Object>> list = fileSearchService.getHotKeywords(workspaceId, limit);
        return R.ok(list);
    }

    /**
     * 获取搜索统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取搜索统计")
    public R<Map<String, Object>> getSearchStatistics() {
//        Long userId = StpUtil.getLoginIdAsLong();
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        Map<String, Object> statistics = fileSearchService.getSearchStatistics(userId);
        return R.ok(statistics);
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    @Operation(summary = "获取搜索建议")
    public R<List<String>> getSearchSuggestions(
        @Parameter(description = "关键词前缀") @RequestParam String keyword,
        @Parameter(description = "工作空间ID") @RequestParam(required = false) Long workspaceId,
        @Parameter(description = "数量限制") @RequestParam(defaultValue = "5") Integer limit) {
        List<String> suggestions = fileSearchService.getSearchSuggestions(keyword, workspaceId, limit);
        return R.ok(suggestions);
    }
}

