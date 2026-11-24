package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件变更记录视图对象
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@Schema(description = "文件变更记录视图对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoFileChangeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 变更ID
     */
    @Schema(description = "变更ID")
    @ExcelProperty(value = "变更ID")
    private Long id;

    /**
     * 历史ID
     */
    @Schema(description = "历史ID")
    @ExcelProperty(value = "历史ID")
    private Long historyId;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    @ExcelProperty(value = "文件ID")
    private Long fileId;

    /**
     * 版本号
     */
    @Schema(description = "版本号")
    @ExcelProperty(value = "版本号")
    private Integer version;

    /**
     * 变更类型
     */
    @Schema(description = "变更类型")
    @ExcelProperty(value = "变更类型")
    private String changeType;

    /**
     * 变更位置
     */
    @Schema(description = "变更位置")
    @ExcelProperty(value = "变更位置")
    private String position;

    /**
     * 变更内容
     */
    @Schema(description = "变更内容")
    @ExcelProperty(value = "变更内容")
    private String content;

    /**
     * 变更前内容
     */
    @Schema(description = "变更前内容")
    private String oldContent;

    /**
     * 变更后内容
     */
    @Schema(description = "变更后内容")
    private String newContent;

    /**
     * 变更用户ID
     */
    @Schema(description = "变更用户ID")
    private Long userId;

    /**
     * 变更用户名称
     */
    @Schema(description = "变更用户名称")
    @ExcelProperty(value = "变更用户名称")
    private String userName;

    /**
     * 变更数据（JSON）
     */
    @Schema(description = "变更数据（JSON）")
    private String changeData;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}

