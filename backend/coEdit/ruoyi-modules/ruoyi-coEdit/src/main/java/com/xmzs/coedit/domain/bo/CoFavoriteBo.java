package com.xmzs.coedit.domain.bo;

import com.xmzs.coedit.domain.CoFavorite;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收藏业务对象 co_favorite
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "收藏业务对象")
@AutoMapper(target = CoFavorite.class, reverseConvertGenerate = false)
public class CoFavoriteBo extends BaseEntity {

    /**
     * 收藏ID
     */
    @Schema(description = "收藏ID")
    @NotNull(message = "收藏ID不能为空", groups = {EditGroup.class})
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
    @NotNull(message = "收藏类型不能为空", groups = {AddGroup.class})
    private Integer targetType;

    /**
     * 目标ID
     */
    @Schema(description = "目标ID")
    @NotNull(message = "目标ID不能为空", groups = {AddGroup.class})
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

