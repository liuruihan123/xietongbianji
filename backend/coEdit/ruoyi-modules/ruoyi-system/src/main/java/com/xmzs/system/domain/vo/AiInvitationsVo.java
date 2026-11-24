package com.xmzs.system.domain.vo;

import com.xmzs.system.domain.AiInvitations;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.common.excel.annotation.ExcelDictFormat;
import com.xmzs.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 邀请管理视图对象 ai_invitations
 *
 * @author zuleed
 * @date 2024-12-11
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiInvitations.class)
public class AiInvitationsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @ExcelProperty(value = "编号")
    private Long id;

    /**
     * 邀请者的用户 ID
     */
    @ExcelProperty(value = "邀请者的用户 ID")
    private Long inviterId;

    /**
     * 邀请者的用户账号
     */
    @ExcelProperty(value = "邀请者的用户账号")
    private String inviterUserName;

    /**
     * 被邀请者的用户 ID
     */
    @ExcelProperty(value = "被邀请者的用户 ID")
    private Long inviteeId;

    /**
     * 被邀请者的用户账号
     */
    @ExcelProperty(value = "被邀请者的用户账号")
    private String inviteeUserName;

    /**
     * 邀请码
     */
    @ExcelProperty(value = "邀请码")
    private String inviterCode;

    /**
     * 奖励天数
     */
    @ExcelProperty(value = "奖励天数")
    private Integer rewardDays;

    /**
     * 邀请状态（1邀请成功、2待处理、3邀请失败）
     */
    @ExcelProperty(value = "邀请状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "invitations")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
