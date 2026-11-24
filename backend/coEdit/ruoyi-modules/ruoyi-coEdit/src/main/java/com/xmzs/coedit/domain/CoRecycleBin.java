package com.xmzs.coedit.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 回收站对象 co_recycle_bin
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_recycle_bin")
@Schema(description = "回收站对象")
public class CoRecycleBin extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 回收站ID
     */
    @TableId(value = "id")
    @Schema(description = "回收站ID")
    private Long id;

    /**
     * 原文件ID
     */
    @Schema(description = "原文件ID")
    private Long fileId;

    /**
     * 工作空间ID
     */
    @Schema(description = "工作空间ID")
    private Long workspaceId;

    /**
     * 原文件夹ID
     */
    @Schema(description = "原文件夹ID")
    private Long folderId;

    /**
     * 原文件夹路径
     */
    @Schema(description = "原文件夹路径")
    private String folderPath;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String fileName;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型")
    private String fileType;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小")
    private Long fileSize;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名")
    private String fileExtension;

    /**
     * 原存储路径
     */
    @Schema(description = "原存储路径")
    private String originalPath;

    /**
     * 备份路径
     */
    @Schema(description = "备份路径")
    private String backupPath;

    /**
     * 删除人ID
     */
    @Schema(description = "删除人ID")
    private Long deletedBy;

    /**
     * 删除人名称
     */
    @Schema(description = "删除人名称")
    private String deletedByName;

    /**
     * 删除时间
     */
    @Schema(description = "删除时间")
    private Date deletedTime;

    /**
     * 删除原因
     */
    @Schema(description = "删除原因")
    private String deleteReason;

    /**
     * 过期时间（30天后自动清理）
     */
    @Schema(description = "过期时间")
    private Date expireTime;

    /**
     * 是否可恢复：0-否 1-是
     */
    @Schema(description = "是否可恢复")
    private Integer canRestore;

    /**
     * 恢复时间
     */
    @Schema(description = "恢复时间")
    private Date restoreTime;

    /**
     * 恢复人ID
     */
    @Schema(description = "恢复人ID")
    private Long restoreBy;

    /**
     * 恢复人名称
     */
    @Schema(description = "恢复人名称")
    private String restoreByName;

    /**
     * 原所有者ID
     */
    @Schema(description = "原所有者ID")
    private Long originalOwnerId;

    /**
     * 原所有者名称
     */
    @Schema(description = "原所有者名称")
    private String originalOwnerName;

    /**
     * 原创建时间
     */
    @Schema(description = "原创建时间")
    private Date originalCreateTime;

    /**
     * 原更新时间
     */
    @Schema(description = "原更新时间")
    private Date originalUpdateTime;

    /**
     * 状态：0-已清理 1-待清理 2-已恢复
     */
    @Schema(description = "状态")
    private Integer status;

}

