package com.xmzs.coedit.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

/**
 * 文件搜索业务对象
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@Schema(description = "文件搜索业务对象")
public class CoFileSearchBo {

    /**
     * 搜索关键词
     */
    @NotBlank(message = "搜索关键词不能为空")
    @Size(max = 200, message = "搜索关键词长度不能超过{max}个字符")
    @Schema(description = "搜索关键词")
    private String keyword;

    /**
     * 工作空间ID
     */
    @Schema(description = "工作空间ID")
    private Long workspaceId;

    /**
     * 文件夹ID（在指定文件夹内搜索）
     */
    @Schema(description = "文件夹ID")
    private Long folderId;

    /**
     * 文件类型（扩展名，如：docx, xlsx, pdf）
     */
    @Schema(description = "文件类型")
    private String fileType;

    /**
     * 最小文件大小（字节）
     */
    @Schema(description = "最小文件大小")
    private Long minSize;

    /**
     * 最大文件大小（字节）
     */
    @Schema(description = "最大文件大小")
    private Long maxSize;

    /**
     * 开始时间（创建时间范围）
     */
    @Schema(description = "开始时间")
    private Date startTime;

    /**
     * 结束时间（创建时间范围）
     */
    @Schema(description = "结束时间")
    private Date endTime;

    /**
     * 创建者ID
     */
    @Schema(description = "创建者ID")
    private Long creatorId;

    /**
     * 是否包含子文件夹
     */
    @Schema(description = "是否包含子文件夹")
    private Boolean includeSubFolders = true;

    /**
     * 搜索类型：1-文件名 2-内容 3-高级搜索
     */
    @Schema(description = "搜索类型")
    private Integer searchType = 1;

    /**
     * 排序字段（name-文件名, size-大小, createTime-创建时间, updateTime-修改时间）
     */
    @Schema(description = "排序字段")
    private String sortField = "updateTime";

    /**
     * 排序方向（asc-升序, desc-降序）
     */
    @Schema(description = "排序方向")
    private String sortOrder = "desc";
}

