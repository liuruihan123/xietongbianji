package com.xmzs.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 邀请管理对象 ai_invitations
 *
 * @author zuleed
 * @date 2024-12-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_invitations")
public class AiInvitations extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 邀请者的用户 ID
     */
    private Long inviterId;

    /**
     * 邀请者的用户账号
     */
    private String inviterUserName;

    /**
     * 被邀请者的用户 ID
     */
    private Long inviteeId;

    /**
     * 被邀请者的用户账号
     */
    private String inviteeUserName;

    /**
     * 邀请码
     */
    private String inviterCode;

    /**
     * 奖励天数
     */
    private Integer rewardDays;

    /**
     * 邀请状态（1邀请成功、2待处理、3邀请失败）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;


}
