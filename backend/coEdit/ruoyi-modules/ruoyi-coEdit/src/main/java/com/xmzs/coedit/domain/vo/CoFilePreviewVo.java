package com.xmzs.coedit.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件预览视图对象
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@Schema(description = "文件预览视图对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoFilePreviewVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private Long fileId;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String fileName;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型")
    private String fileType;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    private Long fileSize;

    /**
     * 预览URL
     */
    @Schema(description = "预览URL")
    private String previewUrl;

    /**
     * 缩略图URL
     */
    @Schema(description = "缩略图URL")
    private String thumbnailUrl;

    /**
     * OnlyOffice编辑器配置
     */
    @Schema(description = "OnlyOffice编辑器配置")
    private Object editorConfig;

    /**
     * 文档配置
     */
    @Schema(description = "文档配置")
    private Object documentConfig;

    /**
     * 是否支持预览
     */
    @Schema(description = "是否支持预览")
    private Boolean supportPreview;

    /**
     * 预览模式
     */
    @Schema(description = "预览模式：view-只读，edit-可编辑")
    private String mode;

    /**
     * 水印配置
     */
    @Schema(description = "水印配置")
    private WatermarkConfig watermark;

    /**
     * 水印配置内部类
     */
    @Data
    @Schema(description = "水印配置")
    public static class WatermarkConfig implements Serializable {
        
        private static final long serialVersionUID = 1L;

        /**
         * 水印文本
         */
        @Schema(description = "水印文本")
        private String text;

        /**
         * 水印透明度（0-1）
         */
        @Schema(description = "水印透明度（0-1）")
        private Double opacity = 0.3;

        /**
         * 水印角度
         */
        @Schema(description = "水印角度")
        private Integer rotate = -45;

        /**
         * 水印颜色
         */
        @Schema(description = "水印颜色")
        private String color = "#CCCCCC";
    }
}

