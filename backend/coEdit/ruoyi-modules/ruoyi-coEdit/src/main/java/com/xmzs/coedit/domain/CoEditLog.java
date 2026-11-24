package com.xmzs.coedit.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 协同编辑日志对象 co_edit_log
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_edit_log")
@Schema(description = "协同编辑日志对象")
public class CoEditLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @Schema(description = "日志ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private Long fileId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 会话Key
     */
    @Schema(description = "会话Key")
    private String sessionKey;

    /**
     * 日志类型：JOIN-加入 LEAVE-离开 EDIT-编辑 LOCK-加锁 UNLOCK-解锁 CONFLICT-冲突
     */
    @Schema(description = "日志类型")
    private String logType;

    /**
     * 日志内容
     */
    @Schema(description = "日志内容")
    private String logContent;

    /**
     * 日志数据（JSON格式）
     */
    @Schema(description = "日志数据（JSON格式）")
    private String logData;

    /**
     * IP地址
     */
    @Schema(description = "IP地址")
    private String ipAddress;

    /**
     * 用户代理
     */
    @Schema(description = "用户代理")
    private String userAgent;

    /**
     * 日志时间
     */
    @Schema(description = "日志时间")
    private Date logTime;

}

