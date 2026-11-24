package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.coedit.domain.CoWorkspace;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 工作空间视图对象
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = CoWorkspace.class)
@Schema(description = "工作空间视图对象")
public class CoWorkspaceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 工作空间ID
     */
    @ExcelProperty(value = "工作空间ID")
    @Schema(description = "工作空间ID")
    private Long id;

    /**
     * 工作空间名称
     */
    @ExcelProperty(value = "工作空间名称")
    @Schema(description = "工作空间名称")
    private String workspaceName;

    /**
     * 工作空间类型：1-个人空间 2-部门空间 3-团队空间
     */
    @ExcelProperty(value = "工作空间类型")
    @Schema(description = "工作空间类型")
    private Integer workspaceType;

    /**
     * 工作空间描述
     */
    @ExcelProperty(value = "工作空间描述")
    @Schema(description = "工作空间描述")
    private String workspaceDesc;

    /**
     * 工作空间图标
     */
    @Schema(description = "工作空间图标")
    private String workspaceIcon;

    /**
     * 工作空间颜色标识
     */
    @Schema(description = "工作空间颜色标识")
    private String workspaceColor;

    /**
     * 所有者ID
     */
    @ExcelProperty(value = "所有者ID")
    @Schema(description = "所有者ID")
    private Long ownerId;

    /**
     * 所有者名称（用户名）
     */
    @ExcelProperty(value = "所有者名称")
    @Schema(description = "所有者名称")
    private String ownerName;

    /**
     * 所有者昵称（非数据库字段，动态查询）
     */
    @ExcelProperty(value = "所有者昵称")
    @Schema(description = "所有者昵称")
    private String ownerNickName;

    /**
     * 部门ID
     */
    @ExcelProperty(value = "部门ID")
    @Schema(description = "部门ID")
    private Long deptId;

    /**
     * 部门名称
     */
    @ExcelProperty(value = "部门名称")
    @Schema(description = "部门名称")
    private String deptName;

    /**
     * 最大存储空间（字节）
     */
    @ExcelProperty(value = "最大存储空间")
    @Schema(description = "最大存储空间（字节）")
    private Long maxStorage;

    /**
     * 已使用存储空间（字节）
     */
    @ExcelProperty(value = "已使用存储空间")
    @Schema(description = "已使用存储空间（字节）")
    private Long usedStorage;

    /**
     * 最大成员数量
     */
    @ExcelProperty(value = "最大成员数量")
    @Schema(description = "最大成员数量")
    private Integer maxMembers;

    /**
     * 当前成员数量（非数据库字段，动态统计）
     * 统计该工作空间的正常状态成员总数（包括所有者和普通成员）
     */
    @ExcelProperty(value = "当前成员数量")
    @Schema(description = "当前成员数量")
    private Integer memberCount;

    /**
     * 工作空间内容数（非数据库字段，动态统计）
     * 统计该工作空间下所有层级的文件和文件夹总数（只统计正常状态）
     */
    @ExcelProperty(value = "工作空间内容数")
    @Schema(description = "工作空间内容数")
    private Integer contentCount;

    /**
     * 默认权限
     */
    @ExcelProperty(value = "默认权限")
    @Schema(description = "默认权限")
    private String defaultPermission;

    /**
     * 是否允许成员邀请
     */
    @ExcelProperty(value = "是否允许成员邀请")
    @Schema(description = "是否允许成员邀请")
    private Boolean allowInvite;

    /**
     * 是否允许外部分享
     */
    @ExcelProperty(value = "是否允许外部分享")
    @Schema(description = "是否允许外部分享")
    private Boolean allowShare;

    /**
     * 是否允许下载
     */
    @ExcelProperty(value = "是否允许下载")
    @Schema(description = "是否允许下载")
    private Boolean allowDownload;

    /**
     * 状态：0-禁用 1-正常 2-归档
     */
    @ExcelProperty(value = "状态")
    @Schema(description = "状态")
    private Integer status;

    /**
     * 是否默认空间
     */
    @ExcelProperty(value = "是否默认空间")
    @Schema(description = "是否默认空间")
    private Boolean isDefault;

    /**
     * 排序顺序
     */
    @ExcelProperty(value = "排序顺序")
    @Schema(description = "排序顺序")
    private Integer sortOrder;

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
     * 备注
     */
    @ExcelProperty(value = "备注")
    @Schema(description = "备注")
    private String remark;

    /**
     * 所有权类型（非数据库字段，用于前端筛选）
     * 1-我创建的（当前用户是所有者）
     * 2-我加入的（当前用户是成员）
     */
    @Schema(description = "所有权类型：1-我创建的 2-我加入的")
    private Integer ownershipType;
}

