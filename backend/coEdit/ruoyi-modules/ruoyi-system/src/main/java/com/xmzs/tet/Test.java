package com.xmzs.tet;

import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.xmzs.common.core.utils.AmountUtil;
import com.xmzs.common.core.utils.RequestUtils;
import com.xmzs.system.util.OrderNumberGenerator;

import java.math.BigDecimal;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.Base64;

/**
 * packageName com.xmzs.tet
 *
 * @author zhuzi
 * @date 2024/12/2
 * @description TODO
 */
public class Test {


    public static void main(String[] args) {

    }
    public static void main2(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, WxPayException {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId("wxc8a27523811f9cbb");
        payConfig.setMchId("1681141853");
        payConfig.setApiV3Key("FXfG25ICKjrBY8nop1R47TvYjJmSWe5y");
        payConfig.setCertSerialNo("37537EA4B2EBFEC902EA09925514F50274DB6FA9");
        payConfig.setPrivateKeyPath("D:\\APPSoft\\wxpaytools\\WXCertUtil\\cert\\apiclient_key.pem");
        payConfig.setPrivateCertPath("D:\\APPSoft\\wxpaytools\\WXCertUtil\\cert\\apiclient_cert.pem");
        payConfig.setNotifyUrl("localhost");
        payConfig.setSignType("RSA");
        payConfig.setTradeType("JSAPI");

        //验签
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA","BC");
//        keyPairGenerator.initialize(2048,new SecureRandom());
//        KeyPair keyPair = keyPairGenerator.genKeyPair();
//        byte[] encoded = keyPair.getPrivate().getEncoded();
//        // 模拟用户配置
//        String privateKeyString = Base64.getEncoder().encodeToString(encoded);
//        payConfig.setPrivateKeyString(privateKeyString);
//        payConfig.setApiV3Key(payConfig.getApiV3Key());
//        payConfig.initApiV3HttpClient();



        //v3支付
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        String orderNo = OrderNumberGenerator.generate();
        // 失效时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Long expireTime = System.currentTimeMillis() + (30 * 60 * 1000);
        String timeExpire = format.format(expireTime) + "+08:00";
        try {

            WxPayUnifiedOrderV3Request wxPayUnifiedOrderV3Request = new WxPayUnifiedOrderV3Request();
            wxPayUnifiedOrderV3Request.setOutTradeNo(orderNo);
            wxPayUnifiedOrderV3Request.setDescription("测试支付");
            wxPayUnifiedOrderV3Request.setTimeExpire(timeExpire);
            wxPayUnifiedOrderV3Request.setAttach("深圳分店");
            wxPayUnifiedOrderV3Request.setNotifyUrl(RequestUtils.uri() + "/api/pay/notifyMnp");
            // 订单金额
            WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
            amount.setTotal(AmountUtil.yuan2Fen(new BigDecimal("1").toPlainString()));
            amount.setCurrency("CNY");
            wxPayUnifiedOrderV3Request.setAmount(amount);
            // 付款人员
            WxPayUnifiedOrderV3Request.Payer payer = new WxPayUnifiedOrderV3Request.Payer();
            payer.setOpenid("123213");
            wxPayUnifiedOrderV3Request.setPayer(payer);
//
//            WxPayUnifiedOrderV3Request orderRequest = new WxPayUnifiedOrderV3Request();
//
//            orderRequest.setBody("主题");
//            orderRequest.setOutTradeNo(orderNo);
//            orderRequest.setTotalFee(BaseWxPayRequest.yuanToFen("5.00"));//元转成分
//            orderRequest.setOpenid("openid");
//            orderRequest.setSpbillCreateIp("userIp");
//            orderRequest.setTimeStart("yyyyMMddHHmmss");
//            orderRequest.setTimeExpire("yyyyMMddHHmmss");

            Object obj = wxPayService.createOrderV3(TradeTypeEnum.JSAPI,wxPayUnifiedOrderV3Request);
            System.out.println(obj);
        } catch (Exception e) {
            System.out.println("微信支付失败！订单号：{},原因:{}"+ orderNo+ e.getMessage());
            e.printStackTrace();
        }


    }


}
