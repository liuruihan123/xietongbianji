package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.coedit.domain.CoSearchHistory;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 文件搜索历史视图对象
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = CoSearchHistory.class)
@Schema(description = "文件搜索历史视图对象")
public class CoSearchHistoryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 搜索历史ID
     */
    @ExcelProperty(value = "搜索历史ID")
    @Schema(description = "搜索历史ID")
    private Long id;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 工作空间ID
     */
    @ExcelProperty(value = "工作空间ID")
    @Schema(description = "工作空间ID")
    private Long workspaceId;

    /**
     * 搜索关键词
     */
    @ExcelProperty(value = "搜索关键词")
    @Schema(description = "搜索关键词")
    private String keyword;

    /**
     * 搜索类型
     */
    @ExcelProperty(value = "搜索类型")
    @Schema(description = "搜索类型")
    private Integer searchType;

    /**
     * 搜索类型描述
     */
    @Schema(description = "搜索类型描述")
    private String searchTypeDesc;

    /**
     * 文件类型
     */
    @ExcelProperty(value = "文件类型")
    @Schema(description = "文件类型")
    private String fileType;

    /**
     * 文件夹ID
     */
    @ExcelProperty(value = "文件夹ID")
    @Schema(description = "文件夹ID")
    private Long folderId;

    /**
     * 结果数量
     */
    @ExcelProperty(value = "结果数量")
    @Schema(description = "结果数量")
    private Integer resultCount;

    /**
     * 搜索耗时（毫秒）
     */
    @ExcelProperty(value = "搜索耗时")
    @Schema(description = "搜索耗时")
    private Integer searchTime;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * IP地址
     */
    @ExcelProperty(value = "IP地址")
    @Schema(description = "IP地址")
    private String ipAddress;
}

