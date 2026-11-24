package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xmzs.coedit.domain.bo.CoEditorPerformanceBo;
import com.xmzs.coedit.domain.vo.CoEditorPerformanceVo;
import com.xmzs.coedit.service.ICoEditorPerformanceService;
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

import java.util.Map;

/**
 * 编辑器性能监控Controller
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/performance")
@Tag(name = "编辑器性能监控", description = "编辑器性能监控相关接口")
public class CoEditorPerformanceController extends BaseController {

    private final ICoEditorPerformanceService performanceService;

    /**
     * 查询性能监控列表
     */
    @Operation(summary = "查询性能监控列表")
    // @SaCheckPermission("coedit:performance:list")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/list")
    public TableDataInfo<CoEditorPerformanceVo> list(CoEditorPerformanceBo bo, PageQuery pageQuery) {
        return performanceService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取性能监控详情
     */
    @Operation(summary = "获取性能监控详情")
    @Parameter(name = "id", description = "监控ID", required = true)
    // @SaCheckPermission("coedit:performance:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/{id}")
    public R<CoEditorPerformanceVo> getInfo(@PathVariable Long id) {
        CoEditorPerformanceVo vo = performanceService.queryById(id);
        return R.ok(vo);
    }

    /**
     * 获取文件的性能统计
     */
    @Operation(summary = "获取文件的性能统计")
    @Parameter(name = "fileId", description = "文件ID", required = true)
    @GetMapping("/stats/file/{fileId}")
    public R<Map<String, Object>> getFileStats(@PathVariable Long fileId) {
        Map<String, Object> stats = performanceService.getFilePerformanceStats(fileId);
        return R.ok(stats);
    }

    /**
     * 获取用户的性能统计
     */
    @Operation(summary = "获取用户的性能统计")
    @Parameter(name = "userId", description = "用户ID", required = true)
    @GetMapping("/stats/user/{userId}")
    public R<Map<String, Object>> getUserStats(@PathVariable Long userId) {
        Map<String, Object> stats = performanceService.getUserPerformanceStats(userId);
        return R.ok(stats);
    }

    /**
     * 获取实时连接统计
     */
    @Operation(summary = "获取实时连接统计")
    @GetMapping("/stats/realtime")
    public R<Map<String, Object>> getRealtimeStats() {
        Map<String, Object> stats = performanceService.getRealtimeStats();
        return R.ok(stats);
    }

    /**
     * 获取设备类型统计
     */
    @Operation(summary = "获取设备类型统计")
    @GetMapping("/stats/device")
    public R<Map<String, Object>> getDeviceStats() {
        Map<String, Object> stats = performanceService.getDeviceTypeStats();
        return R.ok(stats);
    }

    /**
     * 获取网络类型统计
     */
    @Operation(summary = "获取网络类型统计")
    @GetMapping("/stats/network")
    public R<Map<String, Object>> getNetworkStats() {
        Map<String, Object> stats = performanceService.getNetworkTypeStats();
        return R.ok(stats);
    }

    /**
     * 获取延迟分布统计
     */
    @Operation(summary = "获取延迟分布统计")
    @GetMapping("/stats/latency")
    public R<Map<String, Object>> getLatencyStats() {
        Map<String, Object> stats = performanceService.getLatencyDistribution();
        return R.ok(stats);
    }

    /**
     * 删除性能监控记录
     */
    @Operation(summary = "删除性能监控记录")
    @Parameter(name = "id", description = "监控ID", required = true)
    // @SaCheckPermission("coedit:performance:remove")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "删除性能监控记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        performanceService.deleteById(id);
        return R.ok();
    }

    /**
     * 批量删除性能监控记录
     */
    @Operation(summary = "批量删除性能监控记录")
    // @SaCheckPermission("coedit:performance:remove")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "批量删除性能监控记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public R<Void> removeBatch(@RequestBody Long[] ids) {
        performanceService.deleteBatchByIds(ids);
        return R.ok();
    }

    /**
     * 清理过期的性能数据
     */
    @Operation(summary = "清理过期的性能数据")
    @Parameter(name = "days", description = "保留天数", required = true)
    // @SaCheckPermission("coedit:performance:remove")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "清理过期性能数据", businessType = BusinessType.CLEAN)
    @PostMapping("/clean/{days}")
    public R<Integer> cleanExpiredData(@PathVariable Integer days) {
        Integer count = performanceService.cleanExpiredData(days);
        return R.ok(count);
    }
}

