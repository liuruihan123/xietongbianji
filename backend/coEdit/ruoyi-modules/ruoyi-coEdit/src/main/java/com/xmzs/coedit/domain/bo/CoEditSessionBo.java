package com.xmzs.coedit.domain.bo;

import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * 协同编辑会话业务对象 co_edit_session
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "协同编辑会话业务对象")
public class CoEditSessionBo extends BaseEntity {

    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    @NotNull(message = "会话ID不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    @NotNull(message = "文件ID不能为空", groups = {AddGroup.class})
    private Long fileId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空", groups = {AddGroup.class})
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 会话唯一标识
     */
    @Schema(description = "会话唯一标识")
    private String sessionKey;

    /**
     * 文档Key
     */
    @Schema(description = "文档Key")
    private String documentKey;

    /**
     * 会话开始时间
     */
    @Schema(description = "会话开始时间")
    private Date startTime;

    /**
     * 会话结束时间
     */
    @Schema(description = "会话结束时间")
    private Date endTime;

    /**
     * 最后活跃时间
     */
    @Schema(description = "最后活跃时间")
    private Date lastActiveTime;

    /**
     * 编辑时长（秒）
     */
    @Schema(description = "编辑时长")
    private Integer duration;

    /**
     * 会话状态：0-活跃 1-空闲 2-已结束
     */
    @Schema(description = "会话状态")
    private Integer status;

    /**
     * 编辑次数
     */
    @Schema(description = "编辑次数")
    private Integer editCount;

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

}

