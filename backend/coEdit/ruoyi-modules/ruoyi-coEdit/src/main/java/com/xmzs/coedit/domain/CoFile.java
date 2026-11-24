package com.xmzs.coedit.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 文件对象 co_file
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_file")
@Schema(description = "文件对象")
public class CoFile extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @TableId(value = "id")
    @Schema(description = "文件ID")
    private Long id;

    /**
     * 工作空间ID
     */
    @Schema(description = "工作空间ID")
    private Long workspaceId;

    /**
     * 文件夹ID，NULL表示根目录
     */
    @Schema(description = "文件夹ID")
    private Long folderId;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String fileName;

    /**
     * 文件类型（docx, xlsx, pptx等）
     */
    @Schema(description = "文件类型")
    private String fileType;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小")
    private Long fileSize;

    /**
     * 存储路径
     */
    @Schema(description = "存储路径")
    private String filePath;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名")
    private String fileExtension;

    /**
     * MIME类型
     */
    @Schema(description = "MIME类型")
    private String mimeType;

    /**
     * OnlyOffice文档Key
     */
    @Schema(description = "OnlyOffice文档Key")
    private String documentKey;

    /**
     * 当前版本号
     */
    @Schema(description = "当前版本号")
    private Integer currentVersion;

    /**
     * 编辑状态：0-未编辑 1-编辑中 2-保存中 3-已保存
     */
    @Schema(description = "编辑状态")
    private Integer editStatus;

    /**
     * 当前编辑者ID
     */
    @Schema(description = "当前编辑者ID")
    private Long currentEditorId;

    /**
     * 当前编辑者名称
     */
    @Schema(description = "当前编辑者名称")
    private String currentEditorName;

    /**
     * 编辑开始时间
     */
    @Schema(description = "编辑开始时间")
    private Date editStartTime;

    /**
     * 当前编辑人数
     */
    @Schema(description = "当前编辑人数")
    private Integer editorCount;

    /**
     * 是否锁定：0-否 1-是
     */
    @Schema(description = "是否锁定")
    private Integer isLocked;

    /**
     * 锁定人ID
     */
    @Schema(description = "锁定人ID")
    private Long lockedBy;

    /**
     * 锁定人名称
     */
    @Schema(description = "锁定人名称")
    private String lockedByName;

    /**
     * 锁定时间
     */
    @Schema(description = "锁定时间")
    private Date lockedTime;

    /**
     * 锁定原因
     */
    @Schema(description = "锁定原因")
    private String lockReason;

    /**
     * 是否私有：0-否 1-是
     */
    @Schema(description = "是否私有")
    private Integer isPrivate;

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
     * 下载次数
     */
    @Schema(description = "下载次数")
    private Integer downloadCount;

    /**
     * 查看次数
     */
    @Schema(description = "查看次数")
    private Integer viewCount;

    /**
     * 编辑次数
     */
    @Schema(description = "编辑次数")
    private Integer editCount;

    /**
     * 分享次数
     */
    @Schema(description = "分享次数")
    private Integer shareCount;

    /**
     * 最后编辑时间
     */
    @Schema(description = "最后编辑时间")
    private Date lastEditTime;

    /**
     * 最后编辑人ID
     */
    @Schema(description = "最后编辑人ID")
    private Long lastEditBy;

    /**
     * 最后编辑人名称
     */
    @Schema(description = "最后编辑人名称")
    private String lastEditByName;

    /**
     * 状态：0-已删除 1-正常 2-归档
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 是否在回收站：0-否 1-是
     */
    @Schema(description = "是否在回收站")
    private Boolean isDeleted;

    /**
     * 文件访问URL
     */
    @Schema(description = "文件访问URL")
    private String fileUrl;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

}

