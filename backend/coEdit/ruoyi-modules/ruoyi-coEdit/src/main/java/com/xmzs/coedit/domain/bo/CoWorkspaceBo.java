package com.xmzs.coedit.domain.bo;

import com.xmzs.coedit.domain.CoWorkspace;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工作空间业务对象
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = CoWorkspace.class, reverseConvertGenerate = false)
@Schema(description = "工作空间业务对象")
public class CoWorkspaceBo extends BaseEntity {

    /**
     * 工作空间ID
     */
    @NotNull(message = "工作空间ID不能为空", groups = {EditGroup.class})
    @Schema(description = "工作空间ID")
    private Long id;

    /**
     * 工作空间名称
     */
    @NotBlank(message = "工作空间名称不能为空", groups = {AddGroup.class, EditGroup.class})
    @Size(max = 100, message = "工作空间名称长度不能超过{max}个字符")
    @Schema(description = "工作空间名称")
    private String workspaceName;

    /**
     * 工作空间类型：1-个人空间 2-部门空间 3-团队空间
     */
    @NotNull(message = "工作空间类型不能为空", groups = {AddGroup.class, EditGroup.class})
    @Min(value = 1, message = "工作空间类型值必须在1-3之间")
    @Max(value = 3, message = "工作空间类型值必须在1-3之间")
    @Schema(description = "工作空间类型")
    private Integer workspaceType;

    /**
     * 工作空间描述
     */
    @Size(max = 500, message = "工作空间描述长度不能超过{max}个字符")
    @Schema(description = "工作空间描述")
    private String workspaceDesc;

    /**
     * 工作空间图标
     */
    @Size(max = 255, message = "工作空间图标长度不能超过{max}个字符")
    @Schema(description = "工作空间图标")
    private String workspaceIcon;

    /**
     * 工作空间颜色标识
     */
    @Size(max = 20, message = "工作空间颜色标识长度不能超过{max}个字符")
    @Schema(description = "工作空间颜色标识")
    private String workspaceColor;

    /**
     * 所有者ID（用户ID）
     */
    @Schema(description = "所有者ID")
    private Long ownerId;

    /**
     * 所有者名称
     */
    @Size(max = 100, message = "所有者名称长度不能超过{max}个字符")
    @Schema(description = "所有者名称")
    private String ownerName;

    /**
     * 部门ID（部门空间时使用）
     */
    @Schema(description = "部门ID")
    private Long deptId;

    /**
     * 部门名称
     */
    @Size(max = 100, message = "部门名称长度不能超过{max}个字符")
    @Schema(description = "部门名称")
    private String deptName;

    /**
     * 最大存储空间（字节）
     */
    @Min(value = 0, message = "最大存储空间不能为负数")
    @Schema(description = "最大存储空间（字节）")
    private Long maxStorage;

    /**
     * 已使用存储空间（字节）
     */
    @Schema(description = "已使用存储空间（字节）")
    private Long usedStorage;

    /**
     * 最大成员数量
     */
    @Min(value = 1, message = "最大成员数量不能小于1")
    @Schema(description = "最大成员数量")
    private Integer maxMembers;

    /**
     * 当前成员数量
     */
    @Schema(description = "当前成员数量")
    private Integer memberCount;

    /**
     * 默认权限：view-查看 edit-编辑 admin-管理
     */
    @Size(max = 20, message = "默认权限长度不能超过{max}个字符")
    @Schema(description = "默认权限")
    private String defaultPermission;

    /**
     * 是否允许成员邀请：0-否 1-是
     */
    @Schema(description = "是否允许成员邀请")
    private Boolean allowInvite;

    /**
     * 是否允许外部分享：0-否 1-是
     */
    @Schema(description = "是否允许外部分享")
    private Boolean allowShare;

    /**
     * 是否允许下载：0-否 1-是
     */
    @Schema(description = "是否允许下载")
    private Boolean allowDownload;

    /**
     * 状态：0-禁用 1-正常 2-归档
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 是否默认空间：0-否 1-是
     */
    @Schema(description = "是否默认空间")
    private Boolean isDefault;

    /**
     * 排序顺序
     */
    @Schema(description = "排序顺序")
    private Integer sortOrder;
}

