package com.xmzs.system.request;

import lombok.Data;

/**
 * packageName com.xmzs.system.request
 *
 * @author zhuzi
 * @date 2025/3/9
 * @description TODO
 */
@Data
public class WxLoginRequest {
    // 使用 uni.login 获取的 code
    private String code;
    // 手机号授权的 code
    private String  phoneCode;
    private Integer type;
    private String inviteCode;
}
