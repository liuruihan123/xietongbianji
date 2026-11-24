package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.coedit.domain.CoShareAccessRecord;
import com.xmzs.common.excel.annotation.ExcelDictFormat;
import com.xmzs.common.excel.convert.ExcelDictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import io.github.linpeilie.annotations.AutoMapper;

import java.io.Serializable;
import java.util.Date;

/**
 * 共享访问记录视图对象 co_share_access_record
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@Schema(description = "共享访问记录视图对象")
@AutoMapper(target = CoShareAccessRecord.class)
@ExcelIgnoreUnannotated
public class CoShareAccessRecordVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问记录ID
     */
    @Schema(description = "访问记录ID")
    @ExcelProperty(value = "访问记录ID")
    private Long id;

    /**
     * 共享链接ID
     */
    @Schema(description = "共享链接ID")
    @ExcelProperty(value = "共享链接ID")
    private Long shareLinkId;

    /**
     * 共享码
     */
    @Schema(description = "共享码")
    @ExcelProperty(value = "共享码")
    private String shareCode;

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
     * 访问者ID
     */
    @Schema(description = "访问者ID")
    @ExcelProperty(value = "访问者ID")
    private Long visitorId;

    /**
     * 访问者名称
     */
    @Schema(description = "访问者名称")
    @ExcelProperty(value = "访问者名称")
    private String visitorName;

    /**
     * 访问者类型：0-匿名 1-已登录
     */
    @Schema(description = "访问者类型：0-匿名 1-已登录")
    @ExcelProperty(value = "访问者类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "co_visitor_type")
    private Integer visitorType;

    /**
     * 访问者类型描述
     */
    @Schema(description = "访问者类型描述")
    private String visitorTypeDesc;

    /**
     * 访问IP地址
     */
    @Schema(description = "访问IP地址")
    @ExcelProperty(value = "访问IP地址")
    private String accessIp;

    /**
     * 访问地理位置
     */
    @Schema(description = "访问地理位置")
    @ExcelProperty(value = "访问地理位置")
    private String accessLocation;

    /**
     * 用户代理
     */
    @Schema(description = "用户代理")
    @ExcelProperty(value = "用户代理")
    private String userAgent;

    /**
     * 访问时间
     */
    @Schema(description = "访问时间")
    @ExcelProperty(value = "访问时间")
    private Date accessTime;

    /**
     * 访问操作：0-查看 1-编辑 2-下载
     */
    @Schema(description = "访问操作：0-查看 1-编辑 2-下载")
    @ExcelProperty(value = "访问操作", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "co_access_action")
    private Integer accessAction;

    /**
     * 访问操作描述
     */
    @Schema(description = "访问操作描述")
    private String accessActionDesc;

    /**
     * 访问结果：0-成功 1-失败
     */
    @Schema(description = "访问结果：0-成功 1-失败")
    @ExcelProperty(value = "访问结果", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "co_access_result")
    private Integer accessResult;

    /**
     * 访问结果描述
     */
    @Schema(description = "访问结果描述")
    private String accessResultDesc;

    /**
     * 失败原因
     */
    @Schema(description = "失败原因")
    @ExcelProperty(value = "失败原因")
    private String failReason;

    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    @ExcelProperty(value = "会话ID")
    private String sessionId;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间")
    private Date createTime;

}

