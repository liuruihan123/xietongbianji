package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.coedit.domain.CoAccessRecord;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 访问记录视图对象 co_access_record
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Data
@Schema(description = "访问记录视图对象")
@AutoMapper(target = CoAccessRecord.class, reverseConvertGenerate = false)
@ExcelIgnoreUnannotated
public class CoAccessRecordVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @Schema(description = "记录ID")
    @ExcelProperty(value = "记录ID")
    private Long id;

    /**
     * 目标类型:1-工作空间 2-文件夹 3-文件
     */
    @Schema(description = "目标类型:1-工作空间 2-文件夹 3-文件")
    @ExcelProperty(value = "目标类型")
    private Integer targetType;

    /**
     * 目标ID
     */
    @Schema(description = "目标ID")
    @ExcelProperty(value = "目标ID")
    private Long targetId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 所属工作空间ID
     */
    @Schema(description = "所属工作空间ID")
    @ExcelProperty(value = "所属工作空间ID")
    private Long workspaceId;

    /**
     * 访问时间
     */
    @Schema(description = "访问时间")
    @ExcelProperty(value = "访问时间")
    private Date accessTime;

    /**
     * 访问类型:1-查看 2-编辑 3-下载
     */
    @Schema(description = "访问类型:1-查看 2-编辑 3-下载")
    @ExcelProperty(value = "访问类型")
    private Integer accessType;

    /**
     * 访问时长(秒)
     */
    @Schema(description = "访问时长(秒)")
    @ExcelProperty(value = "访问时长")
    private Integer accessDuration;

    /**
     * 访问IP
     */
    @Schema(description = "访问IP")
    @ExcelProperty(value = "访问IP")
    private String accessIp;

    /**
     * 用户代理
     */
    @Schema(description = "用户代理")
    @ExcelProperty(value = "用户代理")
    private String userAgent;

    /**
     * 最后访问时间
     */
    @Schema(description = "最后访问时间")
    @ExcelProperty(value = "最后访问时间")
    private Date lastAccessedAt;

    /**
     * 访问次数
     */
    @Schema(description = "访问次数")
    @ExcelProperty(value = "访问次数")
    private Integer viewCount;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 目标详细信息(根据targetType动态填充)
     * targetType=1时为CoWorkspaceVo
     * targetType=2时为CoFolderVo
     * targetType=3时为CoFileVo
     */
    @Schema(description = "目标详细信息")
    private Object targetInfo;

}

