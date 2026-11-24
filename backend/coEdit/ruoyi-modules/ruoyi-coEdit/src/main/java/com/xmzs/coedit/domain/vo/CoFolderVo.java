package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.coedit.domain.CoFolder;
import com.xmzs.common.excel.annotation.ExcelDictFormat;
import com.xmzs.common.excel.convert.ExcelDictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import io.github.linpeilie.annotations.AutoMapper;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文件夹视图对象 co_folder
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@Schema(description = "文件夹视图对象")
@AutoMapper(target = CoFolder.class)
public class CoFolderVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件夹ID
     */
    @Schema(description = "文件夹ID")
    @ExcelProperty(value = "文件夹ID")
    private Long id;

    /**
     * 文件夹名称
     */
    @Schema(description = "文件夹名称")
    @ExcelProperty(value = "文件夹名称")
    private String folderName;

    /**
     * 文件夹路径
     */
    @Schema(description = "文件夹路径")
    @ExcelProperty(value = "文件夹路径")
    private String folderPath;

    /**
     * 文件夹层级
     */
    @Schema(description = "文件夹层级")
    @ExcelProperty(value = "文件夹层级")
    private Integer folderLevel;

    /**
     * 文件夹图标
     */
    @Schema(description = "文件夹图标")
    @ExcelProperty(value = "文件夹图标")
    private String folderIcon;

    /**
     * 文件夹颜色
     */
    @Schema(description = "文件夹颜色")
    @ExcelProperty(value = "文件夹颜色")
    private String folderColor;

    /**
     * 所属工作空间ID
     */
    @Schema(description = "所属工作空间ID")
    @ExcelProperty(value = "所属工作空间ID")
    private Long workspaceId;

    /**
     * 父文件夹ID
     */
    @Schema(description = "父文件夹ID")
    @ExcelProperty(value = "父文件夹ID")
    private Long parentId;

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
     * 文件夹类型
     */
    @Schema(description = "文件夹类型")
    @ExcelProperty(value = "文件夹类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "folder_type")
    private Integer folderType;

    /**
     * 是否私密
     */
    @Schema(description = "是否私密")
    @ExcelProperty(value = "是否私密")
    private Boolean isPrivate;

    /**
     * 是否系统文件夹
     */
    @Schema(description = "是否系统文件夹")
    @ExcelProperty(value = "是否系统文件夹")
    private Boolean isSystem;

    /**
     * 文件数量
     */
    @Schema(description = "文件数量")
    @ExcelProperty(value = "文件数量")
    private Integer fileCount;

    /**
     * 子文件夹数量
     */
    @Schema(description = "子文件夹数量")
    @ExcelProperty(value = "子文件夹数量")
    private Integer subfolderCount;

    /**
     * 总大小（字节）
     */
    @Schema(description = "总大小（字节）")
    @ExcelProperty(value = "总大小")
    private Long totalSize;

    /**
     * 默认权限
     */
    @Schema(description = "默认权限")
    @ExcelProperty(value = "默认权限")
    private String defaultPermission;

    /**
     * 是否继承父文件夹权限
     */
    @Schema(description = "是否继承父文件夹权限")
    @ExcelProperty(value = "是否继承父文件夹权限")
    private Boolean inheritPermission;

    /**
     * 状态
     */
    @Schema(description = "状态")
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "folder_status")
    private Integer status;

    /**
     * 是否在回收站
     */
    @Schema(description = "是否在回收站")
    @ExcelProperty(value = "是否在回收站")
    private Boolean isDeleted;

    /**
     * 删除时间
     */
    @Schema(description = "删除时间")
    @ExcelProperty(value = "删除时间")
    private Date deletedTime;

    /**
     * 排序顺序
     */
    @Schema(description = "排序顺序")
    @ExcelProperty(value = "排序顺序")
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间")
    private Date createTime;


    /**
     * 创建时间
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
     * 子文件夹列表（用于树形结构）
     */
    @Schema(description = "子文件夹列表")
    private List<CoFolderVo> children;

}

