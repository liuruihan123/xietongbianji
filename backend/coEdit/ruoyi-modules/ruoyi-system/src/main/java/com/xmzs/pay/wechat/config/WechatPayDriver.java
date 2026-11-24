package com.xmzs.pay.wechat.config;

import cn.hutool.core.util.RandomUtil;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.bean.transfer.*;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.xmzs.common.core.enums.ClientEnum;
import com.xmzs.common.core.utils.*;
import com.xmzs.pay.wechat.request.PaymentRequestV3;
import com.xmzs.pay.wechat.request.RefundRequestV3;
import com.xmzs.pay.wechat.request.TransferRequest;
import com.xmzs.system.config.base.BaseConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * packageName com.xmzs.pay.wechat.config
 *
 * @author zhuzi
 * @date 2024/12/1
 * @description TODO
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class WechatPayDriver {

    private final BaseConfig baseConfig;

    @Autowired
    private  WxPayService wxPayService;

    @Autowired
    private   WxPayService wxPayMnpService;

    @Autowired
    private   WxPayService wxAppPayService;


    /**
     * 微信统一下单 V2
     * @param wxPayOrderQueryRequest
     * @return
     * @throws WxPayException
     */
    public  Object unifiedOrderV2(WxPayUnifiedOrderRequest wxPayOrderQueryRequest) throws WxPayException {
        // 发起订单  +++++
        WxPayService wxPayService = handler();

        Object obj = wxPayService.createOrder(wxPayOrderQueryRequest);
        System.out.println("创建支付结果:{}"+obj);
        Object obj2 = wxPayService.unifiedOrder(wxPayOrderQueryRequest);
        System.out.println("创建支付结果2:{}"+obj2);
        return obj2;
    }



    /**
     * 微信统一下单 V3
     *
     * @param requestV3 请求参数
     * @return WxPayUnifiedOrderV3Result.JsapiResult
     * @throws Exception 异常
     */
    public  Object unifiedOrderV3(PaymentRequestV3 requestV3) throws WxPayException {
        // 订单参数
        Integer terminal       = requestV3.getTerminal();
//        String openId          = requestV3.getOpenId();
        String openid          = requestV3.getOpenid();
        String unionid          = requestV3.getUnionid();
        String attach          = requestV3.getAttach();
        String outTradeNo      = requestV3.getOutTradeNo();
        BigDecimal orderAmount = requestV3.getOrderAmount();
        String description     = requestV3.getDescription();

        // 失效时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Long expireTime = System.currentTimeMillis() + (30 * 60 * 1000);
        String timeExpire = format.format(expireTime) + "+08:00";

        // 订单信息
        WxPayUnifiedOrderV3Request wxPayUnifiedOrderV3Request = new WxPayUnifiedOrderV3Request();
        wxPayUnifiedOrderV3Request.setOutTradeNo(outTradeNo);
        wxPayUnifiedOrderV3Request.setDescription(description);
        wxPayUnifiedOrderV3Request.setTimeExpire(timeExpire);
        wxPayUnifiedOrderV3Request.setAttach(attach);
        wxPayUnifiedOrderV3Request.setNotifyUrl(RequestUtils.uri() + "/api/pay/notifyMnp");

        // 订单金额
        WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
        amount.setTotal(AmountUtil.yuan2Fen(orderAmount.toPlainString()));
        amount.setCurrency("CNY");
        wxPayUnifiedOrderV3Request.setAmount(amount);

        // 付款人员
        WxPayUnifiedOrderV3Request.Payer payer = new WxPayUnifiedOrderV3Request.Payer();
//        payer.setOpenid(openId);
        payer.setOpenid(openid);

        // 小程序平台
        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.JSAPI;

        // H5平台
        if (terminal == ClientEnum.H5.getCode()) {
            tradeTypeEnum = TradeTypeEnum.H5;
            WxPayUnifiedOrderV3Request.SceneInfo sceneInfo = new WxPayUnifiedOrderV3Request.SceneInfo();
            WxPayUnifiedOrderV3Request.H5Info h5Info = new WxPayUnifiedOrderV3Request.H5Info();
            h5Info.setType(RequestUtils.device());
            sceneInfo.setH5Info(h5Info);
            sceneInfo.setPayerClientIp(IpUtils.getHostIp());
            wxPayUnifiedOrderV3Request.setSceneInfo(sceneInfo);
        }

        // PC平台
        if (terminal == ClientEnum.PC.getCode()) {
            payer.setOpenid(null);
            tradeTypeEnum = TradeTypeEnum.NATIVE;
        }

        if (terminal == ClientEnum.APK.getCode() || terminal == ClientEnum.IOS.getCode()) {
            payer.setOpenid(null);
            tradeTypeEnum = TradeTypeEnum.APP;
        }

        // 发起订单  +++++
        WxPayService wxPayService = handler(terminal);


        Object object = null;
        wxPayUnifiedOrderV3Request.setPayer(payer);
        try {
            object = wxPayService.createOrderV3(tradeTypeEnum, wxPayUnifiedOrderV3Request);
        }catch (WxPayException e){
            log.error("支付错误:{}",e.getMessage());
        }

        return object;
    }


    /**
     * 微信支付-申请退款
     * 文档地址: https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_4
     * 接口链接: https://api.mch.weixin.qq.com/secapi/pay/refund
     *
     * @param request 请求参数
     * @return WxPayRefundV3Result 退款结果
     * @throws WxPayException 退款异常
     */
    public  WxPayRefundV3Result refund(RefundRequestV3 request) throws WxPayException {
        WxPayRefundV3Request requestObj = new WxPayRefundV3Request();
        requestObj.setTransactionId(request.getTransactionId());
        requestObj.setOutTradeNo(request.getOutTradeNo());
        requestObj.setOutRefundNo(request.getOutRefundNo());
        requestObj.setReason(request.getReason());
        requestObj.setNotifyUrl(request.getNotifyUrl());
        requestObj.setSubMchid(request.getSubMchid());
        requestObj.setGoodsDetails(request.getGoodsDetails());

        WxPayRefundV3Request.Amount amount = new WxPayRefundV3Request.Amount();
        amount.setRefund(request.getRefundAmount());
        amount.setTotal(request.getTotalAmount());
        amount.setCurrency(StringUtils.isEmpty(request.getCurrency()) ? "CNY" : request.getCurrency());
        requestObj.setAmount(amount);
        return wxPayService.refundV3(requestObj);
    }


    public  TransferBatchesResult transfer(TransferRequest request) throws WxPayException {
        Integer transferAmount = request.getTransferAmount().multiply(new BigDecimal("100")).intValue();
        List<TransferBatchesRequest.TransferDetail> transferDetails = new LinkedList<>();
        TransferBatchesRequest.TransferDetail transferDetail = new TransferBatchesRequest.TransferDetail();
        transferDetail.setUserName(request.getUserName());
        transferDetail.setOpenid(request.getOpenid());
        transferDetail.setTransferAmount(transferAmount);
        transferDetail.setTransferRemark(request.getTransferRemark());
        transferDetail.setOutDetailNo(request.getOutDetailNo());
        transferDetails.add(transferDetail);

        TransferBatchesRequest transferBatchesRequest = new TransferBatchesRequest();
        transferBatchesRequest.setAppid(request.getAppId());
        transferBatchesRequest.setBatchName(request.getTransferName());
        transferBatchesRequest.setBatchRemark(request.getTransferRemark());
        transferBatchesRequest.setTotalNum(1);
        transferBatchesRequest.setTotalAmount(transferAmount);
        transferBatchesRequest.setOutBatchNo(RandomUtil.randomString(11));
        transferBatchesRequest.setTransferDetailList(transferDetails);
        TransferBatchesResult transferBatchesResult = wxPayService.getTransferService().transferBatches(transferBatchesRequest);
        return transferBatchesResult;
    }


    public  TransferBatchDetailResult queryWithdrawTransferResult(String batchId) throws WxPayException {

        QueryTransferBatchesRequest queryTransferBatchesRequest = new QueryTransferBatchesRequest();
        queryTransferBatchesRequest.setBatchId(batchId);
        queryTransferBatchesRequest.setNeedQueryDetail(true);
        queryTransferBatchesRequest.setOffset(0);
        queryTransferBatchesRequest.setLimit(100);
        queryTransferBatchesRequest.setDetailStatus("ALL");
        QueryTransferBatchesResult result = wxPayService.getTransferService().transferBatchesBatchId(queryTransferBatchesRequest);

        if (result.getTransferDetailList() != null && result.getTransferDetailList().size() > 0) {
            String detailId = result.getTransferDetailList().get(0).getDetailId();
            TransferBatchDetailResult detailResult = wxPayService.getTransferService().transferBatchesBatchIdDetail(batchId, detailId);
            return detailResult;
        }
        return null;
    }


    /**
     * 对象句柄
     *
     * @author fzr
     * @return WxPayService
     */
    public  WxPayService handler(Integer terminal) {
        if (ClientEnum.OA.getCode() == terminal || ClientEnum.PC.getCode() == terminal || ClientEnum.H5.getCode() == terminal) {
            resetConfig("oa");
            return wxPayService;
        } else if (ClientEnum.IOS.getCode() == terminal || ClientEnum.APK.getCode() == terminal) {
            resetConfig("app");
            return wxAppPayService;
        } else {
            resetConfig("mnp");
            return wxPayMnpService;
        }

    }

    /**
     * 对象句柄
     *
     * @author fzr
     * @return WxPayService
     */
    public  WxPayService handler() {
        resetConfig("oa");
        return wxPayService;
    }

    /**
     * 重设配置
     *
     * @author fzr
     * @param type 类型
     */
    private  void resetConfig(String type) {

        Map<String,String> payMap =   baseConfig.get("wxPay");
        String payInterfaceVersion = payMap.get("interface_version");
//        Map<String, String> params = MapUtils.jsonToMap(config.getParams().toString());
        String mchId = payMap.get("mch_id");
        String appId = payMap.get("appId");
        String paySignKey  = payMap.get("apiV3Key");
        String certSerialNo = payMap.get("certSerialNo");

        String privateKeyPath = payMap.get("privateKeyPath");
        String privateCertPath = payMap.get("privateCertPath");
        String publicKeyFromPath = payMap.get("publicKeyFromPath");
        String publicKeyId = payMap.get("publicKeyId");
        String keyPath = payMap.get("keyPath");
        String mchKey = payMap.get("mchKey");

//        byte[] privateCert = payMap.getOrDefault("privateCertContent", "").getBytes();
//        byte[] privateKey  = payMap.getOrDefault("privateKeyContent", "").getBytes();

        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(appId);
        payConfig.setMchId(mchId);
        if("V3".equals(payInterfaceVersion)){
            payConfig.setApiV3Key(paySignKey);
            payConfig.setCertSerialNo(certSerialNo);
            payConfig.setPrivateKeyPath(privateKeyPath);
            payConfig.setPrivateCertPath(privateCertPath);
            // TODO 需要V3 最新包的接口
//            payConfig.setPublicKeyId(publicKeyFromPath);
        }else{
            payConfig.setMchKey(mchKey);
            payConfig.setKeyPath(keyPath);
        }

//        payConfig.setPrivateKeyContent(privateKey);
//        payConfig.setPrivateCertContent(privateCert);
        payConfig.setUseSandboxEnv(false);

        if (type.equals("oa")) {
            wxPayService.setConfig(payConfig);
        } else if (type.equals("app")) {
            wxAppPayService.setConfig(payConfig);
        } else {
            wxPayMnpService.setConfig(payConfig);
        }

    }




}
