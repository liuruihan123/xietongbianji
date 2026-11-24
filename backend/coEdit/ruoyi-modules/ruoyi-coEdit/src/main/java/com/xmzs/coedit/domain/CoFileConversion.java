package com.xmzs.coedit.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 文件转换记录对象 co_file_conversion
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_file_conversion")
@Schema(description = "文件转换记录对象")
public class CoFileConversion extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 转换ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "转换ID")
    private Long id;

    /**
     * 源文件ID
     */
    @Schema(description = "源文件ID")
    private Long sourceFileId;

    /**
     * 源文件名称
     */
    @Schema(description = "源文件名称")
    private String sourceFileName;

    /**
     * 源文件类型
     */
    @Schema(description = "源文件类型")
    private String sourceFileType;

    /**
     * 源文件路径
     */
    @Schema(description = "源文件路径")
    private String sourceFilePath;

    /**
     * 源文件URL
     */
    @Schema(description = "源文件URL")
    private String sourceFileUrl;

    /**
     * 目标文件类型
     */
    @Schema(description = "目标文件类型")
    private String targetFileType;

    /**
     * 目标文件名称
     */
    @Schema(description = "目标文件名称")
    private String targetFileName;

    /**
     * 目标文件路径
     */
    @Schema(description = "目标文件路径")
    private String targetFilePath;

    /**
     * 目标文件URL
     */
    @Schema(description = "目标文件URL")
    private String targetFileUrl;

    /**
     * 目标文件大小
     */
    @Schema(description = "目标文件大小")
    private Long targetFileSize;

    /**
     * 转换状态：0-等待中 1-转换中 2-成功 3-失败
     */
    @Schema(description = "转换状态：0-等待中 1-转换中 2-成功 3-失败")
    private Integer status = 0;

    /**
     * 转换进度（0-100）
     */
    @Schema(description = "转换进度（0-100）")
    private Integer progress = 0;

    /**
     * 失败原因
     */
    @Schema(description = "失败原因")
    private String failReason;

    /**
     * 转换Key（OnlyOffice）
     */
    @Schema(description = "转换Key")
    private String conversionKey;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private Date endTime;

    /**
     * 耗时（毫秒）
     */
    @Schema(description = "耗时（毫秒）")
    private Long duration;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;
}

