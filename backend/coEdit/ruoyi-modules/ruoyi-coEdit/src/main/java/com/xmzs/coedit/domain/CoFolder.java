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
 * 文件夹实体
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_folder")
@Schema(description = "文件夹实体")
public class CoFolder extends BaseEntity {

    /**
     * 文件夹ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "文件夹ID")
    private Long id;

    /**
     * 文件夹名称
     */
    @Schema(description = "文件夹名称")
    private String folderName;

    /**
     * 文件夹路径
     */
    @Schema(description = "文件夹路径")
    private String folderPath;

    /**
     * 文件夹层级
     */
    @Schema(description = "文件夹层级")
    private Integer folderLevel;

    /**
     * 文件夹图标
     */
    @Schema(description = "文件夹图标")
    private String folderIcon;

    /**
     * 文件夹颜色
     */
    @Schema(description = "文件夹颜色")
    private String folderColor;

    /**
     * 所属工作空间ID
     */
    @Schema(description = "所属工作空间ID")
    private Long workspaceId;

    /**
     * 父文件夹ID（0表示根目录）
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
    private String ownerName;

    /**
     * 文件夹类型：1-普通文件夹 2-私密文件夹 3-共享文件夹 4-系统文件夹
     */
    @Schema(description = "文件夹类型")
    private Integer folderType;

    /**
     * 是否私密：0-否 1-是（仅创建者可访问）
     */
    @Schema(description = "是否私密")
    private Boolean isPrivate;

    /**
     * 是否系统文件夹：0-否 1-是（不可删除）
     */
    @Schema(description = "是否系统文件夹")
    private Boolean isSystem;

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
     * 默认权限：view-查看 edit-编辑 admin-管理
     */
    @Schema(description = "默认权限")
    private String defaultPermission;

    /**
     * 是否继承父文件夹权限：0-否 1-是
     */
    @Schema(description = "是否继承父文件夹权限")
    private Boolean inheritPermission;

    /**
     * 状态：0-已删除 1-正常 2-已锁定
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 是否在回收站：0-否 1-是
     */
    @Schema(description = "是否在回收站")
    private Boolean isDeleted;

    /**
     * 删除时间
     */
    @Schema(description = "删除时间")
    private Date deletedTime;

    /**
     * 排序顺序
     */
    @Schema(description = "排序顺序")
    private Integer sortOrder;
}

