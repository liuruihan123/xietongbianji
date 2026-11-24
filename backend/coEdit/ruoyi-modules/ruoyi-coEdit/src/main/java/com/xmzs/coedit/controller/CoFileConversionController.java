package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xmzs.coedit.domain.bo.CoFileConversionBo;
import com.xmzs.coedit.domain.vo.CoFileConversionVo;
import com.xmzs.coedit.service.ICoFileConversionService;
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
 * 文件转换Controller
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/convert")
@Tag(name = "文件转换管理", description = "文件转换相关接口")
public class CoFileConversionController extends BaseController {

    private final ICoFileConversionService conversionService;

    /**
     * 转换文件
     */
    @Operation(summary = "转换文件")
//    @SaCheckPermission("coedit:file:convert")
    @Log(title = "文件转换", businessType = BusinessType.INSERT)
    @PostMapping("/file")
    public R<Long> convertFile(@Validated @RequestBody CoFileConversionBo bo) {
        Long conversionId = conversionService.convertFile(bo);
        return R.ok(conversionId);
    }

    /**
     * 批量转换文件
     */
    @Operation(summary = "批量转换文件")
//    @SaCheckPermission("coedit:file:convert")
    @Log(title = "批量文件转换", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public R<List<Long>> batchConvert(@Validated @RequestBody List<CoFileConversionBo> boList) {
        List<Long> conversionIds = conversionService.batchConvert(boList);
        return R.ok(conversionIds);
    }

    /**
     * 获取转换状态
     */
    @Operation(summary = "获取转换状态")
    @Parameter(name = "id", description = "转换记录ID", required = true)
    @GetMapping("/status/{id}")
    public R<CoFileConversionVo> getConversionStatus(@PathVariable Long id) {
        CoFileConversionVo vo = conversionService.getConversionStatus(id);
        return R.ok(vo);
    }

    /**
     * 获取支持的目标格式
     */
    @Operation(summary = "获取支持的目标格式")
    @Parameter(name = "sourceFileType", description = "源文件类型", required = true)
    @GetMapping("/formats/{sourceFileType}")
    public R<List<String>> getSupportedFormats(@PathVariable String sourceFileType) {
        List<String> formats = conversionService.getSupportedFormats(sourceFileType);
        return R.ok(formats);
    }

    /**
     * 获取所有支持的格式映射
     */
    @Operation(summary = "获取所有支持的格式映射")
    @GetMapping("/formats/all")
    public R<Map<String, List<String>>> getAllSupportedFormats() {
        Map<String, List<String>> formats = conversionService.getAllSupportedFormats();
        return R.ok(formats);
    }

    /**
     * 取消转换
     */
    @Operation(summary = "取消转换")
    @Parameter(name = "id", description = "转换记录ID", required = true)
//    @SaCheckPermission("coedit:file:convert")
    @Log(title = "取消转换", businessType = BusinessType.UPDATE)
    @DeleteMapping("/cancel/{id}")
    public R<Void> cancelConversion(@PathVariable Long id) {
        conversionService.cancelConversion(id);
        return R.ok();
    }

    /**
     * 获取文件的转换历史
     */
    @Operation(summary = "获取文件的转换历史")
    @Parameter(name = "fileId", description = "文件ID", required = true)
    @GetMapping("/history/file/{fileId}")
    public TableDataInfo<CoFileConversionVo> getConversionHistory(
            @PathVariable Long fileId,
            PageQuery pageQuery) {
        return conversionService.getConversionHistory(fileId, pageQuery);
    }

    /**
     * 获取用户的转换历史
     */
    @Operation(summary = "获取用户的转换历史")
    @Parameter(name = "userId", description = "用户ID", required = true)
    @GetMapping("/history/user/{userId}")
    public TableDataInfo<CoFileConversionVo> getUserConversionHistory(
            @PathVariable Long userId,
            PageQuery pageQuery) {
        return conversionService.getUserConversionHistory(userId, pageQuery);
    }

    /**
     * 删除转换记录
     */
    @Operation(summary = "删除转换记录")
    @Parameter(name = "id", description = "转换记录ID", required = true)
//    @SaCheckPermission("coedit:file:delete")
    @Log(title = "删除转换记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> deleteConversion(@PathVariable Long id) {
        conversionService.deleteConversion(id);
        return R.ok();
    }

    /**
     * 批量删除转换记录
     */
    @Operation(summary = "批量删除转换记录")
//    @SaCheckPermission("coedit:file:delete")
    @Log(title = "批量删除转换记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public R<Void> batchDeleteConversion(@RequestBody List<Long> ids) {
        conversionService.batchDeleteConversion(ids);
        return R.ok();
    }

    /**
     * 清理过期的转换记录
     */
    @Operation(summary = "清理过期的转换记录")
    @Parameter(name = "days", description = "保留天数", required = true)
    @SaCheckPermission("coedit:file:delete")
    @Log(title = "清理过期转换记录", businessType = BusinessType.CLEAN)
    @PostMapping("/clean/{days}")
    public R<Integer> cleanExpiredConversions(@PathVariable Integer days) {
        Integer count = conversionService.cleanExpiredConversions(days);
        return R.ok(count);
    }

    /**
     * 重试失败的转换
     */
    @Operation(summary = "重试失败的转换")
    @Parameter(name = "id", description = "转换记录ID", required = true)
    @SaCheckPermission("coedit:file:convert")
    @Log(title = "重试转换", businessType = BusinessType.INSERT)
    @PostMapping("/retry/{id}")
    public R<Long> retryConversion(@PathVariable Long id) {
        Long newConversionId = conversionService.retryConversion(id);
        return R.ok(newConversionId);
    }

    /**
     * 下载转换后的文件
     */
    @Operation(summary = "下载转换后的文件")
    @Parameter(name = "id", description = "转换记录ID", required = true)
    @GetMapping("/download/{id}")
    public R<String> downloadConvertedFile(@PathVariable Long id) {
        String fileUrl = conversionService.downloadConvertedFile(id);
        return R.ok(fileUrl);
    }
}

