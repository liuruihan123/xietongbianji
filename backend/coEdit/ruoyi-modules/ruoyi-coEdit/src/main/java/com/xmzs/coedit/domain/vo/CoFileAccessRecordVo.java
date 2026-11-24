package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.coedit.domain.CoFileAccessRecord;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 文件访问记录视图对象 co_file_access_record
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = CoFileAccessRecord.class)
@Schema(description = "文件访问记录视图对象")
public class CoFileAccessRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @ExcelProperty(value = "记录ID")
    @Schema(description = "记录ID")
    private Long id;

    /**
     * 文件ID
     */
    @ExcelProperty(value = "文件ID")
    @Schema(description = "文件ID")
    private Long fileId;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 工作空间ID
     */
    @ExcelProperty(value = "工作空间ID")
    @Schema(description = "工作空间ID")
    private Long workspaceId;

    /**
     * 访问时间
     */
    @ExcelProperty(value = "访问时间")
    @Schema(description = "访问时间")
    private Date accessTime;

    /**
     * 访问类型:1-查看 2-编辑 3-下载
     */
    @ExcelProperty(value = "访问类型")
    @Schema(description = "访问类型:1-查看 2-编辑 3-下载")
    private Integer accessType;

    /**
     * 访问时长(秒)
     */
    @ExcelProperty(value = "访问时长")
    @Schema(description = "访问时长")
    private Integer accessDuration;

    /**
     * 访问IP
     */
    @ExcelProperty(value = "访问IP")
    @Schema(description = "访问IP")
    private String accessIp;

    /**
     * 用户代理
     */
    @ExcelProperty(value = "用户代理")
    @Schema(description = "用户代理")
    private String userAgent;

    /**
     * 最后访问时间
     */
    @ExcelProperty(value = "最后访问时间")
    @Schema(description = "最后访问时间")
    private Date lastAccessedAt;

    /**
     * 访问次数
     */
    @ExcelProperty(value = "访问次数")
    @Schema(description = "访问次数")
    private Integer viewCount;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 文件详细信息(扩展字段)
     */
    @Schema(description = "文件详细信息")
    private CoFileVo fileInfo;

}

