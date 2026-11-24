package com.xmzs.common.core.domain.model;

import com.xmzs.common.core.xss.Xss;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * packageName com.xmzs.common.core.domain.model
 *
 * @author zhuzi
 * @date 2024/11/24
 * @description TODO
 */
@Data
public class UpdateUserInfoBody {
    /**
     * 用户ID
     */
    private Long userId;

    private String token;
    /**
     * 用户账号
     */
    @Xss(message = "用户账号不能包含脚本字符")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过{max}个字符")
    private String userName;

    /**
     * 用户昵称
     */
    @Xss(message = "用户昵称不能包含脚本字符")
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过{max}个字符")
    private String nickName;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过{max}个字符")
    private String email;

    /**
     * 手机号码
     */

    private String phonenumber;
    /**
     * 微信头像
     */
    private String avatar;


    /** 普通用户的标识,对当前开发者帐号唯一。一个openid对应一个公众号或小程序 */
//    private String openId;
    private String openid;


    private String unionid;
    /**
     * 修改类型  email sms  nickname    other
     */
    private String type;
    /**
     * 验证码
     */
    private String code;

}
