package com.xmzs.coedit.domain.bo;

import com.xmzs.coedit.domain.CoFolder;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件夹业务对象 co_folder
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文件夹业务对象")
@AutoMapper(target = CoFolder.class, reverseConvertGenerate = false)
public class CoFolderBo extends BaseEntity {

    /**
     * 文件夹ID
     */
    @Schema(description = "文件夹ID")
    @NotNull(message = "文件夹ID不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 文件夹名称
     */
    @Schema(description = "文件夹名称")
    @NotBlank(message = "文件夹名称不能为空", groups = {AddGroup.class, EditGroup.class})
    @Size(max = 100, message = "文件夹名称长度不能超过100个字符")
    private String folderName;

    /**
     * 文件夹路径
     */
    @Schema(description = "文件夹路径")
    @Size(max = 500, message = "文件夹路径长度不能超过500个字符")
    private String folderPath;

    /**
     * 文件夹层级
     */
    @Schema(description = "文件夹层级")
    @Min(value = 0, message = "文件夹层级不能小于0")
    private Integer folderLevel;

    /**
     * 文件夹图标
     */
    @Schema(description = "文件夹图标")
    @Size(max = 100, message = "文件夹图标长度不能超过100个字符")
    private String folderIcon;

    /**
     * 文件夹颜色
     */
    @Schema(description = "文件夹颜色")
    @Size(max = 20, message = "文件夹颜色长度不能超过20个字符")
    private String folderColor;

    /**
     * 所属工作空间ID
     */
    @Schema(description = "所属工作空间ID")
    @NotNull(message = "所属工作空间ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long workspaceId;

    /**
     * 父文件夹ID
     */
    @Schema(description = "父文件夹ID")
    private Long parentId;

    /**
     * 所有者ID
     */
    @Schema(description = "所有者ID")
    private Long ownerId;

    /**
     * 所有者名称
     */
    @Schema(description = "所有者名称")
    @Size(max = 100, message = "所有者名称长度不能超过100个字符")
    private String ownerName;

    /**
     * 文件夹类型
     */
    @Schema(description = "文件夹类型")
    @Min(value = 1, message = "文件夹类型值不正确")
    @Max(value = 4, message = "文件夹类型值不正确")
    private Integer folderType;

    /**
     * 是否私密
     */
    @Schema(description = "是否私密")
    private Boolean isPrivate;

    /**
     * 是否系统文件夹
     */
    @Schema(description = "是否系统文件夹")
    private Boolean isSystem;

    /**
     * 默认权限
     */
    @Schema(description = "默认权限")
    @Size(max = 20, message = "默认权限长度不能超过20个字符")
    private String defaultPermission;

    /**
     * 是否继承父文件夹权限
     */
    @Schema(description = "是否继承父文件夹权限")
    private Boolean inheritPermission;

    /**
     * 文件数量
     */
    @Schema(description = "文件数量")
    private Integer fileCount;

    /**
     * 子文件夹数量
     */
    @Schema(description = "子文件夹数量")
    private Integer subfolderCount;

    /**
     * 总大小（字节）
     */
    @Schema(description = "总大小（字节）")
    private Long totalSize;

    /**
     * 状态
     */
    @Schema(description = "状态")
    @Min(value = 0, message = "状态值不正确")
    @Max(value = 2, message = "状态值不正确")
    private Integer status;

    /**
     * 是否在回收站
     */
    @Schema(description = "是否在回收站")
    private Boolean isDeleted;

    /**
     * 排序顺序
     */
    @Schema(description = "排序顺序")
    private Integer sortOrder;

}

