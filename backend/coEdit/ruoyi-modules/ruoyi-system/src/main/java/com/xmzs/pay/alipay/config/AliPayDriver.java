package com.xmzs.pay.alipay.config;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.WebUtils;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.payment.app.models.AlipayTradeAppPayResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeRefundResponse;
import com.alipay.easysdk.payment.wap.models.AlipayTradeWapPayResponse;
import com.alipay.easysdk.util.generic.models.AlipayOpenApiGenericResponse;
import com.xmzs.common.core.enums.ClientEnum;
import com.xmzs.common.core.exception.base.BaseException;
import com.xmzs.common.core.utils.RequestUtils;
import com.xmzs.common.core.utils.YmlUtils;
import com.xmzs.common.localFile.engine.LocalFileProperties;
import com.xmzs.pay.alipay.request.PaymentRequest;
import com.xmzs.system.config.base.BaseConfig;
import com.xmzs.system.domain.bo.PaymentOrdersBo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
public class AliPayDriver {

    private final BaseConfig baseConfig;

    private final LocalFileProperties localFileProperties;





    /**
     * 统一下单
     *
     * @param request 请求参数
     * @throws Exception 异常
     */
    public Object unifiedOrder(PaymentRequest request, PaymentOrdersBo paymentOrders) throws Exception {
        // 订单参数
        String outTradeNo      = request.getOutTradeNo();
        BigDecimal orderAmount = request.getOrderAmount();
        String attach          = request.getAttach();
        Integer terminal       = request.getTerminal();

        Factory.setOptions(this.getOptions());
        Map<String, Object> result = new LinkedHashMap<>();

        if (terminal == ClientEnum.H5.getCode() || terminal == ClientEnum.OA.getCode()) {

            String successBackUrl = YmlUtils.get("payment.returnUrl");
            String cancelBackUrl = YmlUtils.get("payment.cancelUrl");

            AlipayTradeWapPayResponse response = Factory.Payment.Wap().optional("passback_params", attach).pay(
                    "订单:" + outTradeNo,
                    outTradeNo,
                    orderAmount.toPlainString(),
                    cancelBackUrl,
                    successBackUrl);
            result.put("config", response.getBody());
        } else if (terminal == ClientEnum.PC.getCode()) {
            //pc端获取二维码方扫码支付
//            String qrUrl = createQRCode(outTradeNo,orderAmount.toPlainString(),paymentOrders);
            String qrUrl = tradePagePayQr(outTradeNo,orderAmount.toPlainString(),paymentOrders);
            log.info("二维码信息:{}",qrUrl);


            result.put("config", qrUrl);
        } else if (terminal == ClientEnum.IOS.getCode() || terminal == ClientEnum.APK.getCode()) {
            AlipayTradeAppPayResponse response = Factory.Payment.App().optional("passback_params", attach).pay(
                    "订单:" + outTradeNo,
                    outTradeNo,
                    orderAmount.toPlainString());
            System.out.println("app支付---->" + response.getBody());
            result.put("config", response.getBody());
        } else {
            throw new BaseException("支付方式错误");
        }
        result.put("pay_way", 3);
        return result;
    }


    public String createQRCode(String orderId, String totalAmount, PaymentOrdersBo paymentOrders) throws AlipayApiException {
        Config config = this.getOptions();
//        String gatewayHost = config.gatewayHost;
//        String gatewayHost = "https://mianshistar.com/api/pay/aliPaynotify";
        String gatewayHost = "https://openapi.alipay.com/gateway.do";
        String appId = config.appId;
        String privateKey = config.merchantPrivateKey;
        String alipayPublicKey = config.alipayPublicKey;
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayHost, appId, privateKey, "json", "UTF-8", alipayPublicKey, "RSA2");

        // 失效时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long expireTime = System.currentTimeMillis() + (15 * 60 * 1000);
        String timeExpire = format.format(expireTime);


        AlipayTradePrecreateRequest alipayRequest = new AlipayTradePrecreateRequest();
//        // 强制关闭 ssl 证书校验
//        WebUtils.setNeedCheckServerTrusted(false);
//        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
//        model.setOutTradeNo(orderId);
//        model.setTotalAmount(totalAmount);
//        model.setSubject(paymentOrders.getOrderName());
//        model.setBody(paymentOrders.getRemark());
//        alipayRequest.setNotifyUrl(baseConfig.get("aliPay","aliPayNotifyUrl"));
//        alipayRequest.setBizModel(model);
//        AlipayTradePrecreateResponse response = alipayClient.execute(alipayRequest);

        // 强制关闭 ssl 证书校验
        WebUtils.setNeedCheckServerTrusted(false);
//        alipayRequest.setNotifyUrl(alipayProperties.getNotifyUrl());
        alipayRequest.setNotifyUrl(baseConfig.get("aliPay","aliPayNotifyUrl"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no",orderId);// 商户订单号
        jsonObject.put("total_amount",totalAmount);// 商品价格
        jsonObject.put("subject",paymentOrders.getOrderName());// 商品标题
        jsonObject.put("product_code","FAST_INSTANT_TRADE_PAY");
        jsonObject.put("qr_pay_mode","4");
        jsonObject.put("qrcode_width","128");

//        jsonObject.put("store_id","睿到科技");//组织或公司名
        jsonObject.put("time_expire",timeExpire);// 订单有效时间
        alipayRequest.setBizContent(jsonObject.toString());
//        AlipayTradePrecreateResponse response = alipayClient.execute(alipayRequest);
        AlipayTradePrecreateResponse response = alipayClient.pageExecute(alipayRequest, "POST");

        log.info("获取二维码信息:{}",response.getBody());
        if (response.isSuccess()) {
            return response.getQrCode(); // 返回二维码链接
        } else {
            throw new RuntimeException("支付宝返回错误: " + response.getSubMsg());
        }
    }








    /**
     * 网站支付二维码模式
     * @return
     * @throws Exception
     */
    public String tradePagePayQr(String orderId, String totalAmount, PaymentOrdersBo paymentOrders) throws Exception {
        Config config = this.getOptions();
        String gatewayHost = "https://openapi.alipay.com/gateway.do";
        String appId = config.appId;
        String privateKey = config.merchantPrivateKey;
        String alipayPublicKey = config.alipayPublicKey;
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayHost, appId, privateKey, "json", "UTF-8", alipayPublicKey, "RSA2");

        WebUtils.setNeedCheckServerTrusted(false);

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        //request.setReturnUrl(returnUrl);
        request.setNotifyUrl(baseConfig.get("aliPay","aliPayNotifyUrl"));
        model.setOutTradeNo(orderId);
        model.setSubject(paymentOrders.getOrderName());
        model.setTotalAmount(totalAmount);
        model.setBody(paymentOrders.getRemark());
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        model.setQrPayMode("4");
        model.setQrcodeWidth(128L);
        //model.setTimeExpire("2m");
        model.setTimeoutExpress("2m");
        request.setBizModel(model);
        request.setNeedEncrypt(false);

        return alipayClient.pageExecute(request,"GET").getBody();
    }






    /**
     * 获取配置参数
     * @return
     */
    private Config getOptions() {

        Map<String,String> payMap =   baseConfig.get("aliPay");
//        Map<String, String> params = MapUtils.jsonToMap(config.getParams().toString());
        String mode = payMap.get("mode");
        String appId = payMap.get("app_id");
        String privateKey  = payMap.get("private_key").trim();;
        String alipayPublicKey = payMap.get("ali_public_key").trim();

//        String $public_cert = payMap.get("public_cert").trim();
//        String $ali_public_cert = payMap.get("ali_public_cert").trim();
//        String $ali_root_cert = payMap.get("ali_root_cert").trim();



        Assert.notNull(payMap, "请配置好支付设置");


        Config payConfig = new Config();
        payConfig.protocol = "https";
        payConfig.appId = appId;
        payConfig.merchantPrivateKey = privateKey;
        payConfig.gatewayHost = "openapi.alipay.com";
        payConfig.signType = "RSA2";

        if ("certificate".equals(mode)) {
            String directory = YmlUtils.get("like.upload-directory");
            if (directory == null || directory.equals("")) {
                throw new BaseException("请配置上传目录like.upload-directory");
            }

//            if(!directory.endsWith("/")) {
//                directory = directory + File.separator;
//            }

//            String publicCertPath = directory + "certificate/" + Md5.md5($public_cert.getBytes()) + ".crt";
//
//            String aliPublicCertPath = directory + "certificate/" + Md5.md5($ali_public_cert.getBytes()) + ".crt";
//
//            String aliRootCertPath = directory + "certificate/" + Md5.md5($ali_root_cert.getBytes()) + ".crt";
//            File publicCertFile = new File(publicCertPath);
//            File aliPublicCertFile = new File(aliPublicCertPath);
//            File aliRootCertFile = new File(aliRootCertPath);
//            if (!FileUtil.exist(publicCertFile)) {
//                FileUtil.appendString($public_cert, publicCertFile , "utf-8");
//            }
//
//            if (!FileUtil.exist(aliPublicCertFile)) {
//                FileUtil.appendString($ali_public_cert, aliPublicCertFile , "utf-8");
//            }
//
//            if (!FileUtil.exist(aliRootCertFile)) {
//                FileUtil.appendString($ali_root_cert, aliRootCertFile , "utf-8");
//            }
//
//            payConfig.alipayCertPath = aliPublicCertPath;
//            log.info("aliPublicCertPath:" + payConfig.alipayCertPath);
//
//            payConfig.alipayRootCertPath = aliRootCertPath;
//            log.info("aliRootCertPath:" + payConfig.alipayRootCertPath);
//
//            payConfig.merchantCertPath = publicCertPath;
//            log.info("publicCertPath:" + payConfig.merchantCertPath);

        } else {
            payConfig.alipayPublicKey = alipayPublicKey;
        }

        payConfig.notifyUrl = RequestUtils.uri() + "/api/pay/notifyAli";

        return payConfig;
    }

    public boolean verifyNotify(Map<String, String> data) {
        Factory.setOptions(this.getOptions());
        boolean isSignVerified = false;
        try {
            log.info("进行验签：" + data.toString());
            isSignVerified = Factory.Payment.Common().verifyNotify(data);
        } catch (Exception e) {
            log.error("支付宝支付验签失败:" + e.getMessage());
        }
        return isSignVerified;
    }

    //退款
    public AlipayTradeRefundResponse refund(String outTradeNo, String refundAmount) {

        Factory.setOptions(this.getOptions());
        try {
            AlipayTradeRefundResponse refundResponse = Factory.Payment.Common().refund(outTradeNo, refundAmount);
            return refundResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public AlipayOpenApiGenericResponse transfer(String account, String name, BigDecimal money, String sn, String orderTitle) throws Exception {
        Factory.setOptions(this.getOptions());
        Map<String,Object> bizMap = new HashMap<>();
        bizMap.put("out_biz_no", sn);
        bizMap.put("trans_amount", money.toString());
        bizMap.put("product_code", "TRANS_ACCOUNT_NO_PWD");
        bizMap.put("biz_scene", "DIRECT_TRANSFER");
        bizMap.put("order_title", orderTitle);
        Map<String,String> payee_info = new HashMap<>();
        payee_info.put("identity", account);
        payee_info.put("name", name);
        payee_info.put("identity_type", "ALIPAY_LOGON_ID");
        bizMap.put("payee_info", payee_info);
        AlipayOpenApiGenericResponse response = Factory.Util.Generic().execute("alipay.fund.trans.uni.transfer", new HashMap<>(), bizMap);
        return response;
    }

    public AlipayOpenApiGenericResponse transferQuery(String sn) throws Exception {
        Factory.setOptions(this.getOptions());
        Map<String,Object> bizMap = new HashMap<>();
        bizMap.put("out_biz_no", sn);
        bizMap.put("product_code", "TRANS_ACCOUNT_NO_PWD");
        bizMap.put("biz_scene", "DIRECT_TRANSFER");
        AlipayOpenApiGenericResponse response = Factory.Util.Generic().execute("alipay.fund.trans.common.query", new HashMap<>(), bizMap);
        return response;
    }


}
