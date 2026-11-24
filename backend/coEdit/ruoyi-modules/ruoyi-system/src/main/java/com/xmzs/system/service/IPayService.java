package com.xmzs.system.service;

import com.github.binarywang.wxpay.exception.WxPayException;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.system.domain.bo.PaymentOrdersBo;
import com.xmzs.system.domain.request.GoodsOrderRequest;
import com.xmzs.system.domain.vo.PaymentOrdersVo;
import com.xmzs.system.domain.vo.SysUserVo;

import java.util.Map;

/**
 * packageName com.xmzs.system.service
 *
 * @author zhuzi
 * @date 2024/12/1
 * @description 支付服务
 */
public interface IPayService {

    /**
     * 订单前先创建待支付订单
     * @param goodsOrderRequest
     * @param loginUser
     */
    public PaymentOrdersBo beforeOrderHandler(GoodsOrderRequest goodsOrderRequest, LoginUser loginUser);

    /**
     * 处理支付成功后续的订单处理
     * @param orderId
     * @param tradeNo
     * @param totalFee
     */
    public void orderHandler(String orderId,String tradeNo, String totalFee);
    /**
     * 处理用户付费升级
     * @param sysUserVo
     * @param paymentOrdersVo
     */
    public void doUserUpgrade(SysUserVo sysUserVo, PaymentOrdersVo paymentOrdersVo);

    /**
     * 微信支付
     * @param goodsOrderRequest
     * @param orderNo
     * @param paymentOrders
     * @return
     */
    Object doWechatPay(GoodsOrderRequest goodsOrderRequest, String orderNo, PaymentOrdersBo paymentOrders ) throws WxPayException;


    /**
     * 微信小程序支付
     * @param goodsOrderRequest
     * @param orderNo
     * @param paymentOrders
     * @param notify_url
     * @return
     */
    Map<String, String> doXCXPay(GoodsOrderRequest goodsOrderRequest, String orderNo, PaymentOrdersBo paymentOrders,String notify_url ) throws Exception;


    /**
     * 支付宝支付
     * @param goodsOrderRequest
     * @param orderNo
     * @param paymentOrders
     * @return
     * @throws Exception
     */
    Object doAliPay(GoodsOrderRequest goodsOrderRequest, String orderNo, PaymentOrdersBo paymentOrders ) throws Exception;



}
