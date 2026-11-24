package com.xmzs.pay.wechat.utils;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * packageName com.xmzs.pay.wechat.utils
 *
 * @author zhuzi
 * @date 2025/2/21
 * @description TODO
 */
public class WechatUtils {


    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateNonceStr() {
        // 生成16个随机字节
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);

        // 使用Base64编码生成字符串
        String nonceStr = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        // 限制长度不超过32位
        return nonceStr.length() > 32 ? nonceStr.substring(0, 32) : nonceStr;
    }
}
