package com.xmzs.common.core.domain.model;

import com.xmzs.common.core.xss.Xss;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * packageName com.xmzs.common.core.domain.model
 *
 * @author zhuzi
 * @date 2024/11/24
 * @description 修改用户基本信息
 */
@Data
public class UpbaseUserInfoBody {
    /**
     * 用户ID
     */
    private Long userId;

    private String userName;

    private String nickName;

    /**
     * 用户邮箱
     */
    @Xss(message = "用户账号不能包含脚本字符")
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过{max}个字符")
    private String email;

    /**
     * 手机号码
     */
    @Xss(message = "用户昵称不能包含脚本字符")
    private String phonenumber;
    /**
     * 微信头像
     */
    private String avatar;


    /** 短信验证码 */
    private String code;

}
