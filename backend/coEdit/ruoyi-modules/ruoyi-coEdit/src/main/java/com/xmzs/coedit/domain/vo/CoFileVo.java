package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.common.excel.annotation.ExcelDictFormat;
import com.xmzs.common.excel.convert.ExcelDictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import io.github.linpeilie.annotations.AutoMapper;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 文件视图对象 co_file
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@Schema(description = "文件视图对象")
@AutoMapper(target = CoFile.class)
public class CoFileVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    @ExcelProperty(value = "文件ID")
    private Long id;

    /**
     * 工作空间ID
     */
    @Schema(description = "工作空间ID")
    @ExcelProperty(value = "工作空间ID")
    private Long workspaceId;

    /**
     * 文件夹ID
     */
    @Schema(description = "文件夹ID")
    @ExcelProperty(value = "文件夹ID")
    private Long folderId;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    @ExcelProperty(value = "文件名")
    private String fileName;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型")
    @ExcelProperty(value = "文件类型")
    private String fileType;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小")
    @ExcelProperty(value = "文件大小")
    private Long fileSize;

    /**
     * 存储路径
     */
    @Schema(description = "存储路径")
    @ExcelProperty(value = "存储路径")
    private String filePath;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名")
    @ExcelProperty(value = "文件扩展名")
    private String fileExtension;

    /**
     * MIME类型
     */
    @Schema(description = "MIME类型")
    @ExcelProperty(value = "MIME类型")
    private String mimeType;

    /**
     * OnlyOffice文档Key
     */
    @Schema(description = "OnlyOffice文档Key")
    @ExcelProperty(value = "文档Key")
    private String documentKey;

    /**
     * 当前版本号
     */
    @Schema(description = "当前版本号")
    @ExcelProperty(value = "当前版本号")
    private Integer currentVersion;

    /**
     * 编辑状态
     */
    @Schema(description = "编辑状态")
    @ExcelProperty(value = "编辑状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "edit_status")
    private Integer editStatus;

    /**
     * 当前编辑者ID
     */
    @Schema(description = "当前编辑者ID")
    @ExcelProperty(value = "当前编辑者ID")
    private Long currentEditorId;

    /**
     * 当前编辑者名称
     */
    @Schema(description = "当前编辑者名称")
    @ExcelProperty(value = "当前编辑者名称")
    private String currentEditorName;

    /**
     * 编辑开始时间
     */
    @Schema(description = "编辑开始时间")
    @ExcelProperty(value = "编辑开始时间")
    private Date editStartTime;

    /**
     * 当前编辑人数
     */
    @Schema(description = "当前编辑人数")
    @ExcelProperty(value = "当前编辑人数")
    private Integer editorCount;

    /**
     * 是否锁定
     */
    @Schema(description = "是否锁定")
    @ExcelProperty(value = "是否锁定")
    private Integer isLocked;

    /**
     * 锁定人ID
     */
    @Schema(description = "锁定人ID")
    @ExcelProperty(value = "锁定人ID")
    private Long lockedBy;

    /**
     * 锁定人名称
     */
    @Schema(description = "锁定人名称")
    @ExcelProperty(value = "锁定人名称")
    private String lockedByName;

    /**
     * 锁定时间
     */
    @Schema(description = "锁定时间")
    @ExcelProperty(value = "锁定时间")
    private Date lockedTime;

    /**
     * 锁定原因
     */
    @Schema(description = "锁定原因")
    @ExcelProperty(value = "锁定原因")
    private String lockReason;

    /**
     * 是否私有
     */
    @Schema(description = "是否私有")
    @ExcelProperty(value = "是否私有")
    private Integer isPrivate;

    /**
     * 所有者ID
     */
    @Schema(description = "所有者ID")
    @ExcelProperty(value = "所有者ID")
    private Long ownerId;

    /**
     * 所有者名称
     */
    @Schema(description = "所有者名称")
    @ExcelProperty(value = "所有者名称")
    private String ownerName;

    /**
     * 所有者昵称（从sys_user表的nickName字段获取）
     * 此字段不在数据库中，仅用于前端展示
     */
    @Schema(description = "所有者昵称")
    private String ownerNickName;

    /**
     * 下载次数
     */
    @Schema(description = "下载次数")
    @ExcelProperty(value = "下载次数")
    private Integer downloadCount;

    /**
     * 查看次数
     */
    @Schema(description = "查看次数")
    @ExcelProperty(value = "查看次数")
    private Integer viewCount;

    /**
     * 编辑次数
     */
    @Schema(description = "编辑次数")
    @ExcelProperty(value = "编辑次数")
    private Integer editCount;

    /**
     * 分享次数
     */
    @Schema(description = "分享次数")
    @ExcelProperty(value = "分享次数")
    private Integer shareCount;

    /**
     * 最后编辑时间
     */
    @Schema(description = "最后编辑时间")
    @ExcelProperty(value = "最后编辑时间")
    private Date lastEditTime;

    /**
     * 最后编辑人ID
     */
    @Schema(description = "最后编辑人ID")
    @ExcelProperty(value = "最后编辑人ID")
    private Long lastEditBy;

    /**
     * 最后编辑人名称
     */
    @Schema(description = "最后编辑人名称")
    @ExcelProperty(value = "最后编辑人名称")
    private String lastEditByName;

    /**
     * 状态
     */
    @Schema(description = "状态")
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "file_status")
    private Integer status;

    /**
     * 是否在回收站
     */
    @Schema(description = "是否在回收站")
    @ExcelProperty(value = "是否在回收站")
    private Boolean isDeleted;

    /**
     * 文件访问URL
     */
    @Schema(description = "文件访问URL")
    @ExcelProperty(value = "文件访问URL")
    private String fileUrl;

    /**
     * 排序
     */
    @Schema(description = "排序")
    @ExcelProperty(value = "排序")
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @ExcelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 文件位置路径（层级路径，如："工作空间名称/文件夹1/文件夹2"）
     * 此字段不在数据库中，仅用于前端展示
     */
    @Schema(description = "文件位置路径")
    private String locationPath;

}

