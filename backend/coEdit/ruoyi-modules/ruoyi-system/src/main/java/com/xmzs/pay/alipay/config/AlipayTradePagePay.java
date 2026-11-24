package com.xmzs.pay.alipay.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.ExtUserInfo;
import com.alipay.api.domain.InvoiceKeyInfo;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.domain.InvoiceInfo;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.domain.ExtendParams;
import com.alipay.api.domain.GoodsDetail;
import com.alipay.api.domain.SubMerchant;

import com.alipay.api.FileItem;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;

public class AlipayTradePagePay {

    public static void main(String[] args) throws AlipayApiException {
        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        // 构造请求参数以调用接口
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();

        // 设置商户订单号
        model.setOutTradeNo("20150320010101001");

        // 设置订单总金额
        model.setTotalAmount("88.88");

        // 设置订单标题
        model.setSubject("Iphone6 16G");

        // 设置产品码
        model.setProductCode("FAST_INSTANT_TRADE_PAY");

        // 设置PC扫码支付的方式
        model.setQrPayMode("1");

        // 设置商户自定义二维码宽度
        model.setQrcodeWidth(100L);

        // 设置订单包含的商品列表信息
        List<GoodsDetail> goodsDetail = new ArrayList<GoodsDetail>();
        GoodsDetail goodsDetail0 = new GoodsDetail();
        goodsDetail0.setGoodsName("ipad");
        goodsDetail0.setAlipayGoodsId("20010001");
        goodsDetail0.setQuantity(1L);
        goodsDetail0.setPrice("2000");
        goodsDetail0.setGoodsId("apple-01");
        goodsDetail0.setGoodsCategory("34543238");
        goodsDetail0.setCategoriesTree("124868003|126232002|126252004");
        goodsDetail0.setShowUrl("http://www.alipay.com/xxx.jpg");
        goodsDetail.add(goodsDetail0);
        model.setGoodsDetail(goodsDetail);

        // 设置订单绝对超时时间
        model.setTimeExpire("2016-12-31 10:05:01");

        // 设置二级商户信息
        SubMerchant subMerchant = new SubMerchant();
        subMerchant.setMerchantId("2088000603999128");
        subMerchant.setMerchantType("alipay");
        model.setSubMerchant(subMerchant);

        // 设置业务扩展参数
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088511833207846");
        extendParams.setHbFqSellerPercent("100");
        extendParams.setHbFqNum("3");
        extendParams.setIndustryRefluxInfo("{\"scene_code\":\"metro_tradeorder\",\"channel\":\"xxxx\",\"scene_data\":{\"asset_name\":\"ALIPAY\"}}");
        extendParams.setSpecifiedSellerName("XXX的跨境小铺");
        extendParams.setRoyaltyFreeze("true");
        extendParams.setCardType("S0JP0000");
        model.setExtendParams(extendParams);

        // 设置商户传入业务信息
        model.setBusinessParams("{\"mc_create_trade_ip\":\"127.0.0.1\"}");

        // 设置优惠参数
        model.setPromoParams("{\"storeIdType\":\"1\"}");

        // 设置请求后页面的集成方式
        model.setIntegrationType("PCWEB");

        // 设置请求来源地址
        model.setRequestFromUrl("https://");

        // 设置商户门店编号
        model.setStoreId("NJ_001");

        // 设置商户的原始订单号
        model.setMerchantOrderNo("20161008001");

        // 设置外部指定买家
        ExtUserInfo extUserInfo = new ExtUserInfo();
        extUserInfo.setCertType("IDENTITY_CARD");
        extUserInfo.setCertNo("362334768769238881");
        extUserInfo.setName("李明");
        extUserInfo.setMobile("16587658765");
        extUserInfo.setMinAge("18");
        extUserInfo.setNeedCheckInfo("F");
        extUserInfo.setIdentityHash("27bfcd1dee4f22c8fe8a2374af9b660419d1361b1c207e9b41a754a113f38fcc");
        model.setExtUserInfo(extUserInfo);

        // 设置开票信息
        InvoiceInfo invoiceInfo = new InvoiceInfo();
        InvoiceKeyInfo keyInfo = new InvoiceKeyInfo();
        keyInfo.setTaxNum("1464888883494");
        keyInfo.setIsSupportInvoice(true);
        keyInfo.setInvoiceMerchantName("ABC|003");
        invoiceInfo.setKeyInfo(keyInfo);
        invoiceInfo.setDetails("[{\"code\":\"100294400\",\"name\":\"服饰\",\"num\":\"2\",\"sumPrice\":\"200.00\",\"taxRate\":\"6%\"}]");
        model.setInvoiceInfo(invoiceInfo);

        request.setBizModel(model);
        // 第三方代调用模式下请设置app_auth_token
        // request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");

        AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "POST");
        // 如果需要返回GET请求，请使用
        // AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "GET");
        String pageRedirectionData = response.getBody();
        System.out.println("==========="+pageRedirectionData);

        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
            // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
            // String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            // System.out.println(diagnosisUrl);
        }
    }

    private static AlipayConfig getAlipayConfig() {
        String privateKey  = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCUxiSKPAnWVxSHMwuEIQWKubAkMs9R4cox0wH/NAmzZ95SpZmMr/9qZoriqOc0CkVq7sRG/urdoJmwJCsDxjQ1/EUlKZNQItNN/4mlDYwR4vvBv1sA/qgU/pc4Yrdj990vXYKXeMytPb31gV8yWhQxe6Ao+EDxNdHZK+iMGfMwmgVspTXxyYIJ3Gen3CLJ5HECBbG1nYUHP+JaLya1Dc68J/7ZU1Xm2PGU/cMi8Jl2k666nQ+ZuINeOgOsCkJOC0FgJBQhMjBljzk+EgrX+MAVQB9L0FbAW8GypyxlvKy6tdGFdQEA/iiowKnLS72gIP2IeKFkcsgQw+zpLlJrUOBlAgMBAAECggEBAIVE9aiK3bkOqont9Ov4dLFvY1UZ2tObsndtcMV+hQ4cBnD8fQ8k/WT/1Hv0F6BrbwG6P0Llydicq++4hnvMCist/3apxperiqO9PD0JZKcFMMa/NrMua7zazvWooGVBvPZYCsrLYHXz7/1DVdaSELK+7pqkv/ATaRbkmV0fRdULT1IsIoOSO31/PsU7zdJIi+OJoJEGgxlQbnetZBf/XSsibeKRcvDkLF0ftj9gShCG3EgOyHl4yV8CsWFVP89St00ltBsLcLtnYj3zt8uyqQp25Uw3a2Trk9rr6MAkOgmpXKDcrrI3H4cz0+tfk0au9V8lKHSA7xbpn8VorSJWqCUCgYEA7ZDcvzUWRFCe58bfka19Jn4hOXByswG/m8Tv9qVPZyDNfJJCySL5QQ3NaqRA7VfueU4lUY44iBMzJ6Q4H300J8UDpVD2pAERIt/8Oit+B3WkCaR4WNy/Z/e7T+MxG8bMaWyJEs2FbP6sI5yR4ipPwctbzCurnae5OLaBIa11F38CgYEAoFF4aIFZPvjPjtVxBHeGsPfRWjEn7KTZfbAayCH5PflB3el8/gtYGmYS5qoUuU6c2aVMtq/6Szl2PLpDnvlHS+kvu7XC2wqtC1VpfhX3MUBTXznT2CRV+IVIOtbME6Q/9ss9ZdlEqwyGPqU3KWfPVzFeim354Jb5QiQVeQKrmhsCgYA834bA9ABtpt7g8yOsd3D/IQiwaDDrz4HijzcR01AiU9RznCLC2VRXKAs/zTDZ4FM6AB0t5ax0qb7M5JaA8xsfKauCe9mHMsLKVeySlzxm+uz6j4tK8d2V7XOwx714qZsNVINXLxSGVkeTpHX5JvBi7xHpojMKZcTfVFyIXaMITwKBgE/0cdXE9OqYEoQM5UGyDuXBopKVw3CdQq8RnjnD39dNsdDn+uOEats3nyOVHaLRlx5g92KBX5YWwWZGCyWE807jrF+0pcqyh10IqwUsFU1l5G6wm3vHm2zHpB9cMwl0uEXwVN2vvzSDmYwyv9ERqfG1eI4XjTC6evPGpULl1wmTAoGBAMZujwmTBaOwqp2+Y447vH1FE9Zbor9aAPB2PsN12ITBT4vCYfc6iprSOkSy0VPsMC9NeIT4e/ZW98BwBRrKURxPrzLVcUt0JBYqKpCBCdB4xKAkRxYoJUjA6NI0dCcyc7un3tlwRqCFNykMhEg7M0DU3t6wWoMj8W7W2wiOCXvj";
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6t/QP03d3VQpWjXjpxqPBb2zvVy93gmMIHxeU4xFpZqy9/Prt2SEecy3c2MgyjG4dRS3oKHGw9eg+nMPoX77soYwIz1npEkU/MK8tI7xUF2yEprisaUmlavATSeOCS4e3ZPa2fOhICrC068Dsa7/RyXGB1XSCIOhjMaqCGaa2p9rJCqIVsQB/NAq1ajmrhbpBFycXnvJwEGK2npEs/ccBDG/KfNuMScdwAT4Owt4Km3R8iuxF0oivh+Z21ztBEwBiH6/Tfc+vPnipFF6ft7pq2IGdb0/+s103nClsdU7yW+/fZHuRl8/5mzKQhTl23fhKBhV6oPPyC2hV3Rqwn0AnQIDAQAB";
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
        alipayConfig.setAppId("2021004170649874");
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
    }
}
