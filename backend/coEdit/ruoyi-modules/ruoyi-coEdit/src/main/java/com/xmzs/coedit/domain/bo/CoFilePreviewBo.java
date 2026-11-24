package com.xmzs.coedit.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 文件预览业务对象
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@Schema(description = "文件预览业务对象")
public class CoFilePreviewBo {

    /**
     * 文件ID
     */
    @NotNull(message = "文件ID不能为空")
    @Schema(description = "文件ID", required = true)
    private Long fileId;

    /**
     * 预览模式：view-只读预览，edit-可编辑预览
     */
    @Schema(description = "预览模式：view-只读预览，edit-可编辑预览", example = "view")
    private String mode = "view";

    /**
     * 是否显示水印
     */
    @Schema(description = "是否显示水印", example = "false")
    private Boolean showWatermark = false;

    /**
     * 水印文本
     */
    @Schema(description = "水印文本", example = "仅供预览")
    private String watermarkText;

    /**
     * 是否生成缩略图
     */
    @Schema(description = "是否生成缩略图", example = "true")
    private Boolean generateThumbnail = true;

    /**
     * 缩略图宽度
     */
    @Schema(description = "缩略图宽度", example = "200")
    private Integer thumbnailWidth = 200;

    /**
     * 缩略图高度
     */
    @Schema(description = "缩略图高度", example = "200")
    private Integer thumbnailHeight = 200;

    /**
     * 是否使用缓存
     */
    @Schema(description = "是否使用缓存", example = "true")
    private Boolean useCache = true;

    /**
     * 缓存时间（秒）
     */
    @Schema(description = "缓存时间（秒）", example = "3600")
    private Integer cacheTime = 3600;
}

