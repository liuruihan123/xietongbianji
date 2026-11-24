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
 * 文件夹权限实体
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_folder_permission")
@Schema(description = "文件夹权限实体")
public class CoFolderPermission extends BaseEntity {

    /**
     * 权限ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "权限ID")
    private Long id;

    /**
     * 文件夹ID
     */
    @Schema(description = "文件夹ID")
    private Long folderId;

    /**
     * 工作空间ID
     */
    @Schema(description = "工作空间ID")
    private Long workspaceId;

    /**
     * 权限类型：1-用户 2-部门 3-角色 4-团队
     */
    @Schema(description = "权限类型")
    private Integer permissionType;

    /**
     * 目标ID（用户ID/部门ID/角色ID/团队ID）
     */
    @Schema(description = "目标ID")
    private Long targetId;

    /**
     * 目标名称
     */
    @Schema(description = "目标名称")
    private String targetName;

    /**
     * 权限级别：view-查看 edit-编辑 admin-管理 owner-所有者
     */
    @Schema(description = "权限级别")
    private String permissionLevel;

    /**
     * 可读：0-否 1-是
     */
    @Schema(description = "可读")
    private Boolean canRead;

    /**
     * 可写：0-否 1-是
     */
    @Schema(description = "可写")
    private Boolean canWrite;

    /**
     * 可删除：0-否 1-是
     */
    @Schema(description = "可删除")
    private Boolean canDelete;

    /**
     * 可分享：0-否 1-是
     */
    @Schema(description = "可分享")
    private Boolean canShare;

    /**
     * 可下载：0-否 1-是
     */
    @Schema(description = "可下载")
    private Boolean canDownload;

    /**
     * 可上传：0-否 1-是
     */
    @Schema(description = "可上传")
    private Boolean canUpload;

    /**
     * 可管理权限：0-否 1-是
     */
    @Schema(description = "可管理权限")
    private Boolean canManagePermission;

    /**
     * 授权人ID
     */
    @Schema(description = "授权人ID")
    private Long grantBy;

    /**
     * 授权时间
     */
    @Schema(description = "授权时间")
    private Date grantTime;

    /**
     * 过期时间（NULL表示永久）
     */
    @Schema(description = "过期时间")
    private Date expireTime;

    /**
     * 状态：0-已撤销 1-正常 2-已过期
     */
    @Schema(description = "状态")
    private Integer status;
}

