package com.xmzs.common.core.constant;

/**
 * 特殊状态标识
 * packageName com.xmzs.common.core.constant
 *
 * @author zhuzi
 * @date 2024/11/25
 * @description 聊天的状态码，主要用于错误的前台弹框识别
 */
public interface ChatStatus {


    /**
     * 今日免费额度用完,提示升级
     */
    String WARN_NUM_UPGRADE = "[WARN][701]";

    /**
     * 会员今日额度用完
     */
    String WARN_VIP_NUM = "[WARN][702]";

    /**
     * 通用警告
     */
    String WARN = "[WARN][703]";


    /**
     * 挤下线
     */
    String TOKEN_OUT = "[WARN][4001]";



}
