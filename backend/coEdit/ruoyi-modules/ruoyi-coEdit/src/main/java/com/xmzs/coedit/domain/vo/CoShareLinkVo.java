package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.coedit.domain.CoShareLink;
import com.xmzs.common.excel.annotation.ExcelDictFormat;
import com.xmzs.common.excel.convert.ExcelDictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import io.github.linpeilie.annotations.AutoMapper;

import java.io.Serializable;
import java.util.Date;

/**
 * 共享链接视图对象 co_share_link
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@Schema(description = "共享链接视图对象")
@AutoMapper(target = CoShareLink.class)
@ExcelIgnoreUnannotated
public class CoShareLinkVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 共享链接ID
     */
    @Schema(description = "共享链接ID")
    @ExcelProperty(value = "共享链接ID")
    private Long id;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    @ExcelProperty(value = "文件ID")
    private Long fileId;

    /**
     * 文件名称
     */
    @Schema(description = "文件名称")
    @ExcelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小")
    @ExcelProperty(value = "文件大小")
    private Long fileSize;

    /**
     * 文件大小（格式化）
     */
    @Schema(description = "文件大小（格式化）")
    private String fileSizeFormatted;

    /**
     * 共享码（唯一标识）
     */
    @Schema(description = "共享码")
    @ExcelProperty(value = "共享码")
    private String shareCode;

    /**
     * 完整的共享URL
     */
    @Schema(description = "共享URL")
    @ExcelProperty(value = "共享URL")
    private String shareUrl;

    /**
     * 创建者ID
     */
    @Schema(description = "创建者ID")
    @ExcelProperty(value = "创建者ID")
    private Long creatorId;

    /**
     * 创建者名称
     */
    @Schema(description = "创建者名称")
    @ExcelProperty(value = "创建者名称")
    private String creatorName;

    /**
     * 共享类型：0-公开 1-密码保护
     */
    @Schema(description = "共享类型：0-公开 1-密码保护")
    @ExcelProperty(value = "共享类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "co_share_type")
    private Integer shareType;

    /**
     * 共享类型描述
     */
    @Schema(description = "共享类型描述")
    private String shareTypeDesc;

    /**
     * 是否有密码
     */
    @Schema(description = "是否有密码")
    private Boolean hasPassword;

    /**
     * 访问密码（明文存储和展示）
     */
    @Schema(description = "访问密码")
    private String sharePassword;

    /**
     * 权限类型：0-只读 1-可编辑 2-可下载 3-完全权限
     */
    @Schema(description = "权限类型：0-只读 1-可编辑 2-可下载 3-完全权限")
    @ExcelProperty(value = "权限类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "co_permission_type")
    private Integer permissionType;

    /**
     * 权限类型描述
     */
    @Schema(description = "权限类型描述")
    private String permissionTypeDesc;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    @ExcelProperty(value = "过期时间")
    private Date expireTime;

    /**
     * 是否永久有效
     */
    @Schema(description = "是否永久有效")
    private Boolean isPermanent;

    /**
     * 最大访问次数（NULL表示无限制）
     */
    @Schema(description = "最大访问次数")
    @ExcelProperty(value = "最大访问次数")
    private Integer maxAccessCount;

    /**
     * 已访问次数
     */
    @Schema(description = "已访问次数")
    @ExcelProperty(value = "已访问次数")
    private Integer accessCount;

    /**
     * 状态：0-正常 1-已禁用 2-已过期
     */
    @Schema(description = "状态：0-正常 1-已禁用 2-已过期")
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "co_share_status")
    private Integer status;

    /**
     * 状态描述
     */
    @Schema(description = "状态描述")
    private String statusDesc;

    /**
     * 是否有效
     */
    @Schema(description = "是否有效")
    private Boolean isValid;

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
     * 是否允许下载
     */
    @Schema(description = "是否允许下载")
    @ExcelProperty(value = "是否允许下载")
    private Integer allowDownload;

    /**
     * 是否允许打印
     */
    @Schema(description = "是否允许打印")
    @ExcelProperty(value = "是否允许打印")
    private Integer allowPrint;

    /**
     * 是否允许复制
     */
    @Schema(description = "是否允许复制")
    @ExcelProperty(value = "是否允许复制")
    private Integer allowCopy;

    /**
     * 是否允许编辑
     */
    @Schema(description = "是否允许编辑")
    @ExcelProperty(value = "是否允许编辑")
    private Integer allowEdit;

    /**
     * 是否允许评论
     */
    @Schema(description = "是否允许评论")
    @ExcelProperty(value = "是否允许评论")
    private Integer allowComment;

}

