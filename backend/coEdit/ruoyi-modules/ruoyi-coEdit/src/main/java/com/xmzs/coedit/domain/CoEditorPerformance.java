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
 * 编辑器性能监控对象 co_editor_performance
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_editor_performance")
@Schema(description = "编辑器性能监控对象")
public class CoEditorPerformance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 监控ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "监控ID")
    private Long id;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private Long fileId;

    /**
     * 文件名称
     */
    @Schema(description = "文件名称")
    private String fileName;

    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
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
    private String userName;

    /**
     * 连接时长（毫秒）
     */
    @Schema(description = "连接时长（毫秒）")
    private Long connectionDuration;

    /**
     * 发送消息数
     */
    @Schema(description = "发送消息数")
    private Integer messagesSent = 0;

    /**
     * 接收消息数
     */
    @Schema(description = "接收消息数")
    private Integer messagesReceived = 0;

    /**
     * 平均延迟（毫秒）
     */
    @Schema(description = "平均延迟（毫秒）")
    private Long averageLatency;

    /**
     * 最大延迟（毫秒）
     */
    @Schema(description = "最大延迟（毫秒）")
    private Long maxLatency;

    /**
     * 最小延迟（毫秒）
     */
    @Schema(description = "最小延迟（毫秒）")
    private Long minLatency;

    /**
     * 错误次数
     */
    @Schema(description = "错误次数")
    private Integer errorCount = 0;

    /**
     * 重连次数
     */
    @Schema(description = "重连次数")
    private Integer reconnectCount = 0;

    /**
     * 设备类型：desktop/mobile/tablet
     */
    @Schema(description = "设备类型")
    private String deviceType;

    /**
     * 浏览器类型
     */
    @Schema(description = "浏览器类型")
    private String browserType;

    /**
     * 网络类型：wifi/4g/5g/ethernet
     */
    @Schema(description = "网络类型")
    private String networkType;

    /**
     * 连接开始时间
     */
    @Schema(description = "连接开始时间")
    private Date connectTime;

    /**
     * 连接结束时间
     */
    @Schema(description = "连接结束时间")
    private Date disconnectTime;

    /**
     * 是否正常断开：0-异常 1-正常
     */
    @Schema(description = "是否正常断开")
    private Integer normalDisconnect = 1;
}

