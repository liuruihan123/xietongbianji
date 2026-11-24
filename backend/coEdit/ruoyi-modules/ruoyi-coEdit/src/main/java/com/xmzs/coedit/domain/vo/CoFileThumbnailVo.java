package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件缩略图视图对象
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@Schema(description = "文件缩略图视图对象")
public class CoFileThumbnailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 缩略图ID
     */
    @Schema(description = "缩略图ID")
    @ExcelProperty(value = "缩略图ID")
    private Long id;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    @ExcelProperty(value = "文件ID")
    private Long fileId;

    /**
     * 缩略图URL
     */
    @Schema(description = "缩略图URL")
    @ExcelProperty(value = "缩略图URL")
    private String thumbnailUrl;

    /**
     * 缩略图宽度
     */
    @Schema(description = "缩略图宽度")
    @ExcelProperty(value = "缩略图宽度")
    private Integer width;

    /**
     * 缩略图高度
     */
    @Schema(description = "缩略图高度")
    @ExcelProperty(value = "缩略图高度")
    private Integer height;

    /**
     * 缩略图大小（字节）
     */
    @Schema(description = "缩略图大小（字节）")
    @ExcelProperty(value = "缩略图大小")
    private Long size;

    /**
     * 缩略图格式
     */
    @Schema(description = "缩略图格式")
    @ExcelProperty(value = "缩略图格式")
    private String format;

    /**
     * 生成状态：0-生成中 1-成功 2-失败
     */
    @Schema(description = "生成状态：0-生成中 1-成功 2-失败")
    @ExcelProperty(value = "生成状态")
    private Integer status;

    /**
     * 生成状态描述
     */
    @Schema(description = "生成状态描述")
    private String statusDesc;

    /**
     * 访问次数
     */
    @Schema(description = "访问次数")
    @ExcelProperty(value = "访问次数")
    private Integer accessCount;

    /**
     * 最后访问时间
     */
    @Schema(description = "最后访问时间")
    @ExcelProperty(value = "最后访问时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastAccessTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}

