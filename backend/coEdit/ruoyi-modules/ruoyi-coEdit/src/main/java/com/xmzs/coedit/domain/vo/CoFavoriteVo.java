package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.coedit.domain.CoFavorite;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 收藏视图对象 co_favorite
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = CoFavorite.class)
@Schema(description = "收藏视图对象")
public class CoFavoriteVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    @ExcelProperty(value = "收藏ID")
    @Schema(description = "收藏ID")
    private Long id;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 收藏类型:1-文件 2-文件夹 3-工作空间
     */
    @ExcelProperty(value = "收藏类型")
    @Schema(description = "收藏类型:1-文件 2-文件夹 3-工作空间")
    private Integer targetType;

    /**
     * 目标ID
     */
    @ExcelProperty(value = "目标ID")
    @Schema(description = "目标ID")
    private Long targetId;

    /**
     * 所属工作空间ID
     */
    @ExcelProperty(value = "工作空间ID")
    @Schema(description = "所属工作空间ID")
    private Long workspaceId;

    /**
     * 收藏时的名称(快照)
     */
    @ExcelProperty(value = "收藏名称")
    @Schema(description = "收藏时的名称")
    private String favoriteName;

    /**
     * 收藏时的路径(快照)
     */
    @ExcelProperty(value = "收藏路径")
    @Schema(description = "收藏时的路径")
    private String favoritePath;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    @Schema(description = "排序")
    private Integer sortOrder;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    @Schema(description = "备注")
    private String remark;

    /**
     * 是否有效:1-有效 0-已失效
     */
    @ExcelProperty(value = "是否有效")
    @Schema(description = "是否有效")
    private Integer isValid;

    /**
     * 失效原因
     */
    @ExcelProperty(value = "失效原因")
    @Schema(description = "失效原因")
    private String invalidReason;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 目标详细信息(扩展字段,根据targetType动态填充)
     */
    @Schema(description = "目标详细信息")
    private Object targetInfo;

}

