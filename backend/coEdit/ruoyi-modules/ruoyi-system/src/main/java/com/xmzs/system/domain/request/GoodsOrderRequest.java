package com.xmzs.system.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * packageName com.xmzs.system.domain.request
 *
 * @author zhuzi
 * @date 2024/12/1
 * @description 商品购买
 */
@Data
public class GoodsOrderRequest {

    //套餐id
    @NotNull(message = "package id not null")
    private Long packageId;

    //支付方式 (wxPay微信支付,aliPay 支付宝,stripePay stripe)
    @NotNull(message = "payMethod id not null")
    private String payMethod;

    //支付平台 (1微信小程序,2微信公众号,3手机H5,4电脑PC,5苹果APP,6安卓APP,7后端)
    @NotNull(message = "payMethod id not null")
    private Integer payClient;

    //购买来源:1-用户自购:2-后台调整:3-兑换码
    @NotNull(message = "channel id not null")
    private Integer channel;

    //微信用户的唯一标识
//    private String openId;
    private String openid;

    private String unionid;
}
