package com.xmzs.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xmzs.common.core.constant.UserConstants;
import com.xmzs.common.tenant.core.TenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户对象 sys_user
 *
 * @author Lion Li
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends TenantEntity {

    /**
     * 用户ID
     */
    @TableId(value = "user_id")
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 外部系统组织机构ID（SSO系统的UUID）
     */
    private String ssoOrgId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户类型（sys_user系统用户）
     */
    private String userType;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 用户性别
     */
    private String sex;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 密码
     */
    @TableField(
        insertStrategy = FieldStrategy.NOT_EMPTY,
        updateStrategy = FieldStrategy.NOT_EMPTY,
        whereStrategy = FieldStrategy.NOT_EMPTY
    )
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private Date loginDate;

    /**
     * 备注
     */
    private String remark;

    /** 普通用户的标识,对当前开发者帐号唯一。一个openid对应一个公众号或小程序 */
//    private String openId;
    private String openid;

    /** 微信唯一标识符 */
    private String unionid;


    /** 用户余额 */
    private Double userBalance;

    /** 今日剩余次数/免费用户总剩余次数 */
    private Integer subMsgCount;

    /** 剩余点数（积分） */
    private Integer points;

    /** 邀请码 */
    private String inviteCode;

    /** 用户等级 */
    private String userGrade;

    /** 会员到期时间 */
    private Long vipExpiryDate;

    /** Telegram 用户ID */
    private Long telegramUserId;

    /** Telegram 聊天ID */
    private Long telegramChatId;

    /** 最后通知时间戳 */
    private Date lastNotificationTimestamp;

    public SysUser(Long userId) {
        this.userId = userId;
    }

    public boolean isSuperAdmin() {
        return UserConstants.SUPER_ADMIN_ID.equals(this.userId);
    }

}
