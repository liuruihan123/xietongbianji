package com.xmzs.pay.wechat.config;

import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * packageName com.xmzs.pay.wechat.config
 *
 * @author zhuzi
 * @date 2024/12/1
 * @description TODO
 */
@Configuration
@ConditionalOnClass(WxPayService.class)
@AllArgsConstructor
public class WechatPayConfig {

    /**
     * 微信小程序支付配置
     *
     * @author fzr
     * @return WxPayService
     */
    @Bean("wxPayMnpService")
    public WxPayService wxPayMnpService() {
        WxPayService wxPayService = new WxPayServiceImpl();
        return wxPayService;
    }

    /**
     * 微信公众号/PC网站 支付配置
     *
     * @author fzr
     * @return WxPayService
     */
    @Bean("wxPayService")
    public WxPayService wxPayService() {
        WxPayService wxPayService = new WxPayServiceImpl();
//        wxPayService.setConfig(payConfig);
        return wxPayService;
    }

    /**
     * app微信支付配置
     *
     * @author fzr
     * @return WxPayService
     */
    @Bean("wxAppPayService")
    public WxPayService wxAppPayService() {

        WxPayService wxPayService = new WxPayServiceImpl();
        return wxPayService;
    }



}
