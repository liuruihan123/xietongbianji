package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件转换记录视图对象
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@Schema(description = "文件转换记录视图对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoFileConversionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 转换ID
     */
    @Schema(description = "转换ID")
    @ExcelProperty(value = "转换ID")
    private Long id;

    /**
     * 源文件ID
     */
    @Schema(description = "源文件ID")
    @ExcelProperty(value = "源文件ID")
    private Long sourceFileId;

    /**
     * 源文件名称
     */
    @Schema(description = "源文件名称")
    @ExcelProperty(value = "源文件名称")
    private String sourceFileName;

    /**
     * 源文件类型
     */
    @Schema(description = "源文件类型")
    @ExcelProperty(value = "源文件类型")
    private String sourceFileType;

    /**
     * 源文件URL
     */
    @Schema(description = "源文件URL")
    private String sourceFileUrl;

    /**
     * 目标文件类型
     */
    @Schema(description = "目标文件类型")
    @ExcelProperty(value = "目标文件类型")
    private String targetFileType;

    /**
     * 目标文件名称
     */
    @Schema(description = "目标文件名称")
    @ExcelProperty(value = "目标文件名称")
    private String targetFileName;

    /**
     * 目标文件URL
     */
    @Schema(description = "目标文件URL")
    private String targetFileUrl;

    /**
     * 目标文件大小
     */
    @Schema(description = "目标文件大小")
    @ExcelProperty(value = "目标文件大小")
    private Long targetFileSize;

    /**
     * 转换状态
     */
    @Schema(description = "转换状态")
    @ExcelProperty(value = "转换状态")
    private Integer status;

    /**
     * 转换状态描述
     */
    @Schema(description = "转换状态描述")
    private String statusDesc;

    /**
     * 转换进度
     */
    @Schema(description = "转换进度")
    @ExcelProperty(value = "转换进度")
    private Integer progress;

    /**
     * 失败原因
     */
    @Schema(description = "失败原因")
    @ExcelProperty(value = "失败原因")
    private String failReason;

    /**
     * 转换Key
     */
    @Schema(description = "转换Key")
    private String conversionKey;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    @ExcelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    @ExcelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 耗时（毫秒）
     */
    @Schema(description = "耗时（毫秒）")
    @ExcelProperty(value = "耗时（毫秒）")
    private Long duration;

    /**
     * 耗时描述
     */
    @Schema(description = "耗时描述")
    private String durationDesc;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    @ExcelProperty(value = "用户名称")
    private String userName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}

