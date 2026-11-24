package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 编辑器性能监控视图对象
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@Schema(description = "编辑器性能监控视图对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoEditorPerformanceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 监控ID
     */
    @Schema(description = "监控ID")
    @ExcelProperty(value = "监控ID")
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
     * 会话ID
     */
    @Schema(description = "会话ID")
    @ExcelProperty(value = "会话ID")
    private String sessionId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    @ExcelProperty(value = "用户名称")
    private String userName;

    /**
     * 连接时长（毫秒）
     */
    @Schema(description = "连接时长（毫秒）")
    @ExcelProperty(value = "连接时长（毫秒）")
    private Long connectionDuration;

    /**
     * 连接时长描述
     */
    @Schema(description = "连接时长描述")
    private String durationDesc;

    /**
     * 发送消息数
     */
    @Schema(description = "发送消息数")
    @ExcelProperty(value = "发送消息数")
    private Integer messagesSent;

    /**
     * 接收消息数
     */
    @Schema(description = "接收消息数")
    @ExcelProperty(value = "接收消息数")
    private Integer messagesReceived;

    /**
     * 总消息数
     */
    @Schema(description = "总消息数")
    private Integer totalMessages;

    /**
     * 平均延迟（毫秒）
     */
    @Schema(description = "平均延迟（毫秒）")
    @ExcelProperty(value = "平均延迟（毫秒）")
    private Long averageLatency;

    /**
     * 最大延迟（毫秒）
     */
    @Schema(description = "最大延迟（毫秒）")
    @ExcelProperty(value = "最大延迟（毫秒）")
    private Long maxLatency;

    /**
     * 最小延迟（毫秒）
     */
    @Schema(description = "最小延迟（毫秒）")
    @ExcelProperty(value = "最小延迟（毫秒）")
    private Long minLatency;

    /**
     * 错误次数
     */
    @Schema(description = "错误次数")
    @ExcelProperty(value = "错误次数")
    private Integer errorCount;

    /**
     * 重连次数
     */
    @Schema(description = "重连次数")
    @ExcelProperty(value = "重连次数")
    private Integer reconnectCount;

    /**
     * 设备类型
     */
    @Schema(description = "设备类型")
    @ExcelProperty(value = "设备类型")
    private String deviceType;

    /**
     * 浏览器类型
     */
    @Schema(description = "浏览器类型")
    @ExcelProperty(value = "浏览器类型")
    private String browserType;

    /**
     * 网络类型
     */
    @Schema(description = "网络类型")
    @ExcelProperty(value = "网络类型")
    private String networkType;

    /**
     * 连接开始时间
     */
    @Schema(description = "连接开始时间")
    @ExcelProperty(value = "连接开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date connectTime;

    /**
     * 连接结束时间
     */
    @Schema(description = "连接结束时间")
    @ExcelProperty(value = "连接结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date disconnectTime;

    /**
     * 是否正常断开
     */
    @Schema(description = "是否正常断开")
    @ExcelProperty(value = "是否正常断开")
    private Integer normalDisconnect;

    /**
     * 断开状态描述
     */
    @Schema(description = "断开状态描述")
    private String disconnectDesc;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}

