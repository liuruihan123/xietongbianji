package com.xmzs.pay.stripe.config;

import cn.hutool.core.lang.Assert;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.xmzs.system.config.base.BaseConfig;
import com.xmzs.system.domain.bo.PaymentOrdersBo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

//import static spark.Spark.staticFiles;

@RequiredArgsConstructor
@Slf4j
@Component
public class StripePayDriver {
    private final BaseConfig baseConfig;


    /**
     *
     * Stripe 支付
     * @return
     */
    public String unifiedOrder( PaymentOrdersBo paymentOrders) throws StripeException {
       String successUrl =  baseConfig.get("stripePay","successUrl");
       String cancelUrl =  baseConfig.get("stripePay","cancelUrl");
        Stripe.apiKey = getKey();
//        staticFiles.externalLocation(
//            Paths.get("public").toAbsolutePath().toString());

        Map<String, String> metadataMap=new HashMap<>();
        metadataMap.put("orderId",paymentOrders.getOrderNo());
        SessionCreateParams sessionParams =
            SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmountDecimal(paymentOrders.getAmount().multiply(new BigDecimal(100L)))
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(paymentOrders.getOrderName())
                                        .setDescription(paymentOrders.getRemark())
//                                        .addImage(paymentImageUrl)
                                        .build())
                                .build())
                        .build())
                // 使用 addPaymentMethodType 方法添加支付方式
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD) // 添加信用卡支付方式
//                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.ALIPAY) // 添加支付宝支付方式
//                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.WECHAT_PAY) // 添加微信支付方式
                .putAllMetadata(metadataMap)
                .build();

        Session session = Session.create(sessionParams);
        return session.getUrl();

    }


//
//    // 1-微信小程序；2-微信公众号；3-手机H5；4-电脑PC；5-苹果APP；6-安卓APP
//    public Object unifiedOrder(StripePayRequest request) throws StripeException, IOException {
//
//        String memberSuccessUrl="";
//        String rechargeSuccessUrl="";
//        String paymentImageUrl=YmlUtils.get("stripe.pay-image-url");
//
//        if(request.getTerminal().intValue()==3){
//            memberSuccessUrl=YmlUtils.get("stripe.member-h5-url");
//            rechargeSuccessUrl=YmlUtils.get("stripe.recharge-h5-url");
//        }else{
//            memberSuccessUrl=YmlUtils.get("stripe.member-url");
//            rechargeSuccessUrl=YmlUtils.get("stripe.recharge-url");
//        }
//
//
//
//        Map<String, String> metadataMap=new HashMap<>();
//        metadataMap.put("orderId",request.getOutTradeNo());
//        metadataMap.put("attach",request.getAttach());
//
//        String cancelBackUrl=request.getAttach().equals("member")?memberSuccessUrl:rechargeSuccessUrl;
//        String successBackUrl=cancelBackUrl+"?showPaySuccess=true";
//
//
//        Stripe.apiKey = getKey();
//
//        staticFiles.externalLocation(
//                Paths.get("public").toAbsolutePath().toString());
//
//        SessionCreateParams params =
//            SessionCreateParams.builder()
//                .setMode(SessionCreateParams.Mode.PAYMENT)
//                .setSuccessUrl(successBackUrl)
//                .setCancelUrl(cancelBackUrl)
//                .addLineItem(
//                    SessionCreateParams.LineItem.builder()
//                        .setQuantity(1L)
//                        .setPriceData(
//                            SessionCreateParams.LineItem.PriceData.builder()
//                                .setCurrency("cny")
//                                .setUnitAmountDecimal(request.getOrderAmount().multiply(new BigDecimal(100L)))
//                                .setProductData(
//                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                        .setName(request.getPackageName())
//                                        .setDescription(request.getPackageDescription())
//                                        .addImage(paymentImageUrl)
//                                        .build())
//                                .build())
//                        .build()).putAllMetadata(metadataMap)
//                .build();
//        Session session = Session.create(params);
//
//        return session.getUrl();
//    }

    /**
     * 获取私钥
     * @return
     */
    private String getKey() {
        String stripePrivteKey =   baseConfig.get("stripePay","private_key");
        Assert.notNull(stripePrivteKey, "请配置好支付设置");
        return stripePrivteKey;
    }
}
