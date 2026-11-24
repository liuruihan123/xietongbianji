package com.xmzs.coedit.domain.bo;

import com.xmzs.coedit.domain.CoRecycleBin;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 回收站业务对象 co_recycle_bin
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "回收站业务对象")
@AutoMapper(target = CoRecycleBin.class, reverseConvertGenerate = false)
public class CoRecycleBinBo {

    /**
     * 回收站ID
     */
    @Schema(description = "回收站ID")
    @NotNull(message = "回收站ID不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 原文件ID
     */
    @Schema(description = "原文件ID")
    @NotNull(message = "原文件ID不能为空", groups = {AddGroup.class})
    private Long fileId;

    /**
     * 工作空间ID
     */
    @Schema(description = "工作空间ID")
    @NotNull(message = "工作空间ID不能为空", groups = {AddGroup.class})
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
    @NotBlank(message = "文件名不能为空", groups = {AddGroup.class})
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
     * 文件扩展名
     */
    @Schema(description = "文件扩展名")
    @Size(max = 20, message = "文件扩展名长度不能超过20个字符")
    private String fileExtension;

    /**
     * 原存储路径
     */
    @Schema(description = "原存储路径")
    @Size(max = 500, message = "原存储路径长度不能超过500个字符")
    private String originalPath;

    /**
     * 备份路径
     */
    @Schema(description = "备份路径")
    @Size(max = 500, message = "备份路径长度不能超过500个字符")
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
    @Size(max = 100, message = "删除人名称长度不能超过100个字符")
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
    @Size(max = 500, message = "删除原因长度不能超过500个字符")
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
    @Min(value = 0, message = "是否可恢复值不能小于0")
    @Max(value = 1, message = "是否可恢复值不能大于1")
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
    @Size(max = 100, message = "恢复人名称长度不能超过100个字符")
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
    @Size(max = 100, message = "原所有者名称长度不能超过100个字符")
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
    @Min(value = 0, message = "状态值不能小于0")
    @Max(value = 2, message = "状态值不能大于2")
    private Integer status;

}

