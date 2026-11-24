package com.xmzs.coedit.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 编辑器性能监控业务对象
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@Schema(description = "编辑器性能监控业务对象")
public class CoEditorPerformanceBo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private Long fileId;

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
     * 设备类型
     */
    @Schema(description = "设备类型")
    private String deviceType;

    /**
     * 浏览器类型
     */
    @Schema(description = "浏览器类型")
    private String browserType;

    /**
     * 网络类型
     */
    @Schema(description = "网络类型")
    private String networkType;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private Date endTime;
}

