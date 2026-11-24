package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.common.excel.annotation.ExcelDictFormat;
import com.xmzs.common.excel.convert.ExcelDictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 协同编辑会话视图对象 co_edit_session
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@Schema(description = "协同编辑会话视图对象")
@ExcelIgnoreUnannotated
public class CoEditSessionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    @ExcelProperty(value = "会话ID")
    private Long id;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    @ExcelProperty(value = "文件ID")
    private Long fileId;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    @ExcelProperty(value = "文件名")
    private String fileName;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    @ExcelProperty(value = "用户名称")
    private String userName;

    /**
     * 会话唯一标识
     */
    @Schema(description = "会话唯一标识")
    @ExcelProperty(value = "会话标识")
    private String sessionKey;

    /**
     * 文档Key
     */
    @Schema(description = "文档Key")
    @ExcelProperty(value = "文档Key")
    private String documentKey;

    /**
     * 会话开始时间
     */
    @Schema(description = "会话开始时间")
    @ExcelProperty(value = "开始时间")
    private Date startTime;

    /**
     * 会话结束时间
     */
    @Schema(description = "会话结束时间")
    @ExcelProperty(value = "结束时间")
    private Date endTime;

    /**
     * 最后活跃时间
     */
    @Schema(description = "最后活跃时间")
    @ExcelProperty(value = "最后活跃时间")
    private Date lastActiveTime;

    /**
     * 编辑时长（秒）
     */
    @Schema(description = "编辑时长")
    @ExcelProperty(value = "编辑时长（秒）")
    private Integer duration;

    /**
     * 会话状态：0-活跃 1-空闲 2-已结束
     */
    @Schema(description = "会话状态")
    @ExcelProperty(value = "会话状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "edit_session_status")
    private Integer status;

    /**
     * 编辑次数
     */
    @Schema(description = "编辑次数")
    @ExcelProperty(value = "编辑次数")
    private Integer editCount;

    /**
     * IP地址
     */
    @Schema(description = "IP地址")
    @ExcelProperty(value = "IP地址")
    private String ipAddress;

    /**
     * 用户代理
     */
    @Schema(description = "用户代理")
    @ExcelProperty(value = "用户代理")
    private String userAgent;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间")
    private Date createTime;

}

