package com.xmzs.coedit.domain.bo;

import com.xmzs.coedit.domain.CoFile;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件业务对象 co_file
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文件业务对象")
@AutoMapper(target = CoFile.class, reverseConvertGenerate = false)
public class CoFileBo extends BaseEntity {

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    @NotNull(message = "文件ID不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 工作空间ID
     */
    @Schema(description = "工作空间ID")
    @NotNull(message = "工作空间ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long workspaceId;

    /**
     * 文件夹ID
     */
    @Schema(description = "文件夹ID")
    private Long folderId;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    @NotBlank(message = "文件名不能为空", groups = {AddGroup.class, EditGroup.class})
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    private String fileName;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型")
    @Size(max = 50, message = "文件类型长度不能超过50个字符")
    private String fileType;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小")
    @Min(value = 0, message = "文件大小不能小于0")
    private Long fileSize;

    /**
     * 存储路径
     */
    @Schema(description = "存储路径")
    @Size(max = 500, message = "存储路径长度不能超过500个字符")
    private String filePath;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名")
    @Size(max = 20, message = "文件扩展名长度不能超过20个字符")
    private String fileExtension;

    /**
     * MIME类型
     */
    @Schema(description = "MIME类型")
    @Size(max = 100, message = "MIME类型长度不能超过100个字符")
    private String mimeType;

    /**
     * OnlyOffice文档Key
     */
    @Schema(description = "OnlyOffice文档Key")
    @Size(max = 100, message = "文档Key长度不能超过100个字符")
    private String documentKey;

    /**
     * 当前版本号
     */
    @Schema(description = "当前版本号")
    @Min(value = 1, message = "版本号不能小于1")
    private Integer currentVersion;

    /**
     * 编辑状态
     */
    @Schema(description = "编辑状态")
    @Min(value = 0, message = "编辑状态值不正确")
    @Max(value = 3, message = "编辑状态值不正确")
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
    @Size(max = 100, message = "当前编辑者名称长度不能超过100个字符")
    private String currentEditorName;

    /**
     * 是否锁定
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
    @Size(max = 100, message = "锁定人名称长度不能超过100个字符")
    private String lockedByName;

    /**
     * 锁定原因
     */
    @Schema(description = "锁定原因")
    @Size(max = 500, message = "锁定原因长度不能超过500个字符")
    private String lockReason;

    /**
     * 是否私有
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
    @Size(max = 100, message = "所有者名称长度不能超过100个字符")
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
     * 状态
     */
    @Schema(description = "状态")
    @Min(value = 0, message = "状态值不正确")
    @Max(value = 2, message = "状态值不正确")
    private Integer status;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

    /**
     * 是否在回收站
     */
    @Schema(description = "是否在回收站")
    private Boolean isDeleted;

}

