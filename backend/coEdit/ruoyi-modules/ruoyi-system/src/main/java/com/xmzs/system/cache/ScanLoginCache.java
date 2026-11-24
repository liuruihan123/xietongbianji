package com.xmzs.system.cache;


import cn.hutool.core.util.ObjectUtil;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.redis.utils.RedisUtils;

/**
 * 微信扫码登录缓存
 */
public class ScanLoginCache {

    private static final String KEY = "wechat:scan:login:";

    public static String get(String sessionId) {
        Object o = RedisUtils.getCacheObject(KEY+sessionId);
        if (ObjectUtil.isEmpty(o)) {
            return "";
        }

        RedisUtils.deleteObject(KEY+sessionId);
        return o.toString();
    }

    public static void set(String sessionId, String state) {
        System.out.println(KEY+sessionId);
        RedisUtils.setKey(KEY+sessionId, state, 60000);
    }

}
