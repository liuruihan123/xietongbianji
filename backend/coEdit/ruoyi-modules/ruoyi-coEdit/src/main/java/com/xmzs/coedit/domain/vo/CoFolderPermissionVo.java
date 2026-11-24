package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.coedit.domain.CoFolderPermission;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 文件夹权限视图对象
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = CoFolderPermission.class)
@Schema(description = "文件夹权限视图对象")
public class CoFolderPermissionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    @ExcelProperty(value = "权限ID")
    @Schema(description = "权限ID")
    private Long id;

    /**
     * 文件夹ID
     */
    @ExcelProperty(value = "文件夹ID")
    @Schema(description = "文件夹ID")
    private Long folderId;

    /**
     * 工作空间ID
     */
    @ExcelProperty(value = "工作空间ID")
    @Schema(description = "工作空间ID")
    private Long workspaceId;

    /**
     * 权限类型
     */
    @ExcelProperty(value = "权限类型")
    @Schema(description = "权限类型")
    private Integer permissionType;

    /**
     * 目标ID
     */
    @ExcelProperty(value = "目标ID")
    @Schema(description = "目标ID")
    private Long targetId;

    /**
     * 目标名称
     */
    @ExcelProperty(value = "目标名称")
    @Schema(description = "目标名称")
    private String targetName;

    /**
     * 权限级别
     */
    @ExcelProperty(value = "权限级别")
    @Schema(description = "权限级别")
    private String permissionLevel;

    /**
     * 可读
     */
    @ExcelProperty(value = "可读")
    @Schema(description = "可读")
    private Boolean canRead;

    /**
     * 可写
     */
    @ExcelProperty(value = "可写")
    @Schema(description = "可写")
    private Boolean canWrite;

    /**
     * 可删除
     */
    @ExcelProperty(value = "可删除")
    @Schema(description = "可删除")
    private Boolean canDelete;

    /**
     * 可分享
     */
    @ExcelProperty(value = "可分享")
    @Schema(description = "可分享")
    private Boolean canShare;

    /**
     * 可下载
     */
    @ExcelProperty(value = "可下载")
    @Schema(description = "可下载")
    private Boolean canDownload;

    /**
     * 可上传
     */
    @ExcelProperty(value = "可上传")
    @Schema(description = "可上传")
    private Boolean canUpload;

    /**
     * 可管理权限
     */
    @ExcelProperty(value = "可管理权限")
    @Schema(description = "可管理权限")
    private Boolean canManagePermission;

    /**
     * 授权人ID
     */
    @ExcelProperty(value = "授权人ID")
    @Schema(description = "授权人ID")
    private Long grantBy;

    /**
     * 授权时间
     */
    @ExcelProperty(value = "授权时间")
    @Schema(description = "授权时间")
    private Date grantTime;

    /**
     * 过期时间
     */
    @ExcelProperty(value = "过期时间")
    @Schema(description = "过期时间")
    private Date expireTime;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    @Schema(description = "状态")
    private Integer status;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    @Schema(description = "备注")
    private String remark;
}

