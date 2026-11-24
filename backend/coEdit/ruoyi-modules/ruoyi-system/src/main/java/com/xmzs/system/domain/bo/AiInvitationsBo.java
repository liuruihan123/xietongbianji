package com.xmzs.system.domain.bo;

import com.xmzs.system.domain.AiInvitations;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 邀请管理业务对象 ai_invitations
 *
 * @author zuleed
 * @date 2024-12-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiInvitations.class, reverseConvertGenerate = false)
public class AiInvitationsBo extends BaseEntity {

    /**
     * 编号
     */
    @NotNull(message = "编号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 邀请者的用户 ID
     */
    @NotNull(message = "邀请者的用户 ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long inviterId;

    /**
     * 邀请者的用户账号
     */
    @NotBlank(message = "邀请者的用户账号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String inviterUserName;

    /**
     * 被邀请者的用户 ID
     */
    @NotNull(message = "被邀请者的用户 ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long inviteeId;

    /**
     * 被邀请者的用户账号
     */
    @NotBlank(message = "被邀请者的用户账号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String inviteeUserName;

    /**
     * 邀请码
     */
    @NotBlank(message = "邀请码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String inviterCode;

    /**
     * 奖励天数
     */
    @NotNull(message = "奖励天数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer rewardDays;

    /**
     * 邀请状态（1邀请成功、2待处理、3邀请失败）
     */
    @NotBlank(message = "邀请状态（1邀请成功、2待处理、3邀请失败）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 备注
     */
    private String remark;


}
