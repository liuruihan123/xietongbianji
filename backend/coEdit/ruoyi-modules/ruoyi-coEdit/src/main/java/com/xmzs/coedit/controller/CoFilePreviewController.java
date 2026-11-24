package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.xmzs.coedit.domain.bo.CoFilePreviewBo;
import com.xmzs.coedit.domain.vo.CoFilePreviewVo;
import com.xmzs.coedit.domain.vo.CoFileThumbnailVo;
import com.xmzs.coedit.service.ICoAccessRecordService;
import com.xmzs.coedit.service.ICoFileAccessRecordService;
import com.xmzs.coedit.service.ICoFilePreviewService;
import com.xmzs.coedit.service.ICoFileService;
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

/**
 * 文件预览Controller
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/preview")
@Tag(name = "文件预览", description = "文件预览接口")
public class CoFilePreviewController extends BaseController {

    private final ICoFilePreviewService filePreviewService;
    private final ICoFileService fileService;
    private final ICoFileAccessRecordService fileAccessRecordService;
    private final ICoAccessRecordService accessRecordService;

    /**
     * 生成文件预览配置
     */
//    @SaCheckPermission("coedit:file:preview")
    @PostMapping("/config")
    @Operation(summary = "生成文件预览配置")
    public R<CoFilePreviewVo> generatePreviewConfig(@Validated @RequestBody CoFilePreviewBo previewBo) {
//        Long userId = StpUtil.getLoginIdAsLong();
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        CoFilePreviewVo previewVo = filePreviewService.generatePreviewConfig(previewBo, userId);

        // 记录文件访问 - 查看类型
        if (previewVo != null && previewBo.getFileId() != null) {
            try {
                var fileVo = fileService.queryById(previewBo.getFileId());
                if (fileVo != null) {
                    // 记录到文件访问记录表
                    fileAccessRecordService.recordAccess(previewBo.getFileId(), userId, 1); // 1-查看
                    // 记录到通用访问记录表
                    accessRecordService.recordFileAccess(userId, previewBo.getFileId(), fileVo.getWorkspaceId(), 1);
                    log.debug("记录文件预览: fileId={}, userId={}, accessType=1(查看)", previewBo.getFileId(), userId);
                }
            } catch (Exception e) {
                log.error("记录文件预览失败: fileId={}", previewBo.getFileId(), e);
            }
        }

        return R.ok(previewVo);
    }

    /**
     * 获取文件缩略图
     */
    @GetMapping("/thumbnail/{fileId}")
    @Operation(summary = "获取文件缩略图")
    public R<CoFileThumbnailVo> getThumbnail(
        @Parameter(description = "文件ID") @PathVariable Long fileId,
        @Parameter(description = "宽度") @RequestParam(defaultValue = "200") Integer width,
        @Parameter(description = "高度") @RequestParam(defaultValue = "200") Integer height) {
        CoFileThumbnailVo thumbnail = filePreviewService.getThumbnail(fileId, width, height);
        return R.ok(thumbnail);
    }

    /**
     * 生成文件缩略图
     */
//    @SaCheckPermission("coedit:file:preview")
    @Log(title = "生成文件缩略图", businessType = BusinessType.INSERT)
    @PostMapping("/thumbnail/generate")
    @Operation(summary = "生成文件缩略图")
    public R<CoFileThumbnailVo> generateThumbnail(
        @Parameter(description = "文件ID") @RequestParam Long fileId,
        @Parameter(description = "宽度") @RequestParam(defaultValue = "200") Integer width,
        @Parameter(description = "高度") @RequestParam(defaultValue = "200") Integer height) {
        CoFileThumbnailVo thumbnail = filePreviewService.generateThumbnail(fileId, width, height);
        return R.ok(thumbnail);
    }

    /**
     * 检查文件是否支持预览
     */
    @GetMapping("/support/{fileType}")
    @Operation(summary = "检查文件是否支持预览")
    public R<Boolean> isSupportPreview(@Parameter(description = "文件类型") @PathVariable String fileType) {
        Boolean support = filePreviewService.isSupportPreview(fileType);
        return R.ok(support);
    }

    /**
     * 删除文件缩略图
     */
//    @SaCheckPermission("coedit:file:delete")
    @Log(title = "删除文件缩略图", businessType = BusinessType.DELETE)
    @DeleteMapping("/thumbnail/{fileId}")
    @Operation(summary = "删除文件缩略图")
    public R<Void> deleteThumbnail(@Parameter(description = "文件ID") @PathVariable Long fileId) {
        return toAjax(filePreviewService.deleteThumbnail(fileId));
    }

    /**
     * 批量删除文件缩略图
     */
//    @SaCheckPermission("coedit:file:delete")
    @Log(title = "批量删除文件缩略图", businessType = BusinessType.DELETE)
    @DeleteMapping("/thumbnail/batch")
    @Operation(summary = "批量删除文件缩略图")
    public R<Void> batchDeleteThumbnails(@Parameter(description = "文件ID数组") @RequestBody Long[] fileIds) {
        return toAjax(filePreviewService.batchDeleteThumbnails(fileIds));
    }

    /**
     * 清理过期缩略图
     */
//    @SaCheckPermission("coedit:file:delete")
    @Log(title = "清理过期缩略图", businessType = BusinessType.CLEAN)
    @PostMapping("/thumbnail/clean")
    @Operation(summary = "清理过期缩略图")
    public R<Integer> cleanExpiredThumbnails() {
        Integer count = filePreviewService.cleanExpiredThumbnails();
        return R.ok("清理了" + count + "个过期缩略图", count);
    }
}

