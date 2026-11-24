package com.xmzs.coedit.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收藏对象 co_favorite
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_favorite")
@Schema(description = "收藏对象")
public class CoFavorite extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "收藏ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 收藏类型:1-文件 2-文件夹 3-工作空间
     */
    @Schema(description = "收藏类型:1-文件 2-文件夹 3-工作空间")
    private Integer targetType;

    /**
     * 目标ID
     */
    @Schema(description = "目标ID")
    private Long targetId;

    /**
     * 所属工作空间ID
     */
    @Schema(description = "所属工作空间ID")
    private Long workspaceId;

    /**
     * 收藏时的名称(快照)
     */
    @Schema(description = "收藏时的名称")
    private String favoriteName;

    /**
     * 收藏时的路径(快照)
     */
    @Schema(description = "收藏时的路径")
    private String favoritePath;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 是否有效:1-有效 0-已失效
     */
    @Schema(description = "是否有效")
    private Integer isValid;

    /**
     * 失效原因
     */
    @Schema(description = "失效原因")
    private String invalidReason;

}

