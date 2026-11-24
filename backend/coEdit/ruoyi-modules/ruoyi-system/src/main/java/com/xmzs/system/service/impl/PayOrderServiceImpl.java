package com.xmzs.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.wxpay.sdk.WXPayUtil;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.enums.ClientEnum;
import com.xmzs.common.core.enums.PayMenthodEnum;
import com.xmzs.common.core.enums.PayStatusEnum;
import com.xmzs.common.core.exception.base.BaseException;
import com.xmzs.common.core.utils.AmountUtil;
import com.xmzs.common.core.utils.IpUtils;
import com.xmzs.pay.alipay.config.AliPayDriver;
import com.xmzs.pay.alipay.request.PaymentRequest;
import com.xmzs.pay.wechat.config.WechatPayDriver;
import com.xmzs.pay.wechat.request.PaymentRequestV3;
import com.xmzs.pay.wechat.utils.WechatUtils;
import com.xmzs.system.config.base.BaseConfig;
import com.xmzs.system.domain.bo.PaymentOrdersBo;
import com.xmzs.system.domain.bo.SysUserBo;
import com.xmzs.system.domain.request.GoodsOrderRequest;
import com.xmzs.system.domain.vo.PaymentOrdersVo;
import com.xmzs.system.domain.vo.SysPackagePlanVo;
import com.xmzs.system.domain.vo.SysUserVo;
import com.xmzs.system.service.*;
import com.xmzs.system.util.OrderNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * packageName com.xmzs.system.service.impl
 *
 * @author zhuzi
 * @date 2024/12/1
 * @description TODO
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class PayOrderServiceImpl implements IPayService {

    private final IPaymentOrdersService paymentOrdersService;

    private final ISysPackagePlanService sysPackagePlanService;

    private final WechatPayDriver wechatPayDriver;

    private final AliPayDriver aliPayDriver;

    private final BaseConfig baseConfig;

    private final ISysUserService sysUserService;

    private final IAiInvitationsService invitationsService;
    private final SysLoginService sysLoginService;


    /**
     * 订单前先创建待支付订单
     * @param goodsOrderRequest
     * @param loginUser
     */
    public PaymentOrdersBo beforeOrderHandler(GoodsOrderRequest goodsOrderRequest, LoginUser loginUser){
        //查询套餐
        SysPackagePlanVo sysPackagePlanVo = sysPackagePlanService.queryById(goodsOrderRequest.getPackageId());
        // 创建订单
        PaymentOrdersBo paymentOrders = new PaymentOrdersBo();
        if(PayMenthodEnum.STRIPEPAY.getCode().equals(goodsOrderRequest.getPayMethod())){
            paymentOrders.setAmount(sysPackagePlanVo.getEnPrice());
            paymentOrders.setOrderName(sysPackagePlanVo.getName());
            paymentOrders.setEnName(sysPackagePlanVo.getEnName());
            paymentOrders.setRemark(sysPackagePlanVo.getEnPlanDetail());
        }else{
            paymentOrders.setAmount(sysPackagePlanVo.getPrice());
            paymentOrders.setOrderName(sysPackagePlanVo.getName());
            paymentOrders.setEnName(sysPackagePlanVo.getEnName());
            paymentOrders.setRemark(sysPackagePlanVo.getPlanDetail());
        }
        paymentOrders.setPaymentMethod(goodsOrderRequest.getPayMethod());
        paymentOrders.setPayClient(goodsOrderRequest.getPayClient());
        paymentOrders.setChannel(goodsOrderRequest.getChannel());
        paymentOrders.setPlanId(sysPackagePlanVo.getId());
        paymentOrders.setDuration(sysPackagePlanVo.getDuration());
        paymentOrders.setPgPoints(sysPackagePlanVo.getPgPoints());


//        paymentOrders.setAmount(new BigDecimal(orderRequest.getMoney()));
        String orderNo = OrderNumberGenerator.generate();
        paymentOrders.setOrderNo(orderNo);
        paymentOrders.setUserId(loginUser.getUserId());
        paymentOrders.setPaymentStatus(String.valueOf(PayStatusEnum.BY_PENDING.getCode()));
        paymentOrders.setOpenid(loginUser.getOpenid());
        paymentOrders.setUnionid(loginUser.getUnionid());
        paymentOrdersService.insertByBo(paymentOrders);
        return paymentOrders;
    }

    /**
     * 处理支付成功后续的订单处理
     * @param orderId
     * @param tradeNo
     * @param totalFee
     */
    public void orderHandler(String orderId,String tradeNo, String totalFee){
        log.info("支付成功！本系统订单号:{}"+orderId+"，支付的订单号："+tradeNo+",支付金额:"+totalFee);
        PaymentOrdersBo paymentOrdersBo = new PaymentOrdersBo();
        paymentOrdersBo.setOrderNo(orderId);
        List<PaymentOrdersVo> paymentOrdersList = paymentOrdersService.queryList(paymentOrdersBo);
        if (CollectionUtil.isEmpty(paymentOrdersList)){
            log.error("订单不存在，付款未到账！:{}",orderId,tradeNo,totalFee);
            throw new BaseException("订单不存在！");
        }
//            LoginUser loginUser = LoginHelper.getLoginUser();
        PaymentOrdersVo paymentOrdersVo = paymentOrdersList.get(0);
        PaymentOrdersBo pb = new PaymentOrdersBo();


        paymentOrdersVo.setPaymentStatus("2");
        BeanUtil.copyProperties(paymentOrdersVo,pb);

//            paymentOrdersVo.setOpenId(loginUser.getOpenId());
        paymentOrdersService.updateByBo(pb);
        Long userId = paymentOrdersVo.getUserId();
        //更新该用户
        SysUserVo sysUserVo = sysUserService.selectUserById(userId);
        //处理购买积分
        doUserUpgrade(sysUserVo,paymentOrdersVo);
    }


    /**
     * 处理用户付费购买积分
     * @param sysUserVo
     * @param paymentOrdersVo
     */
    public void doUserUpgrade(SysUserVo sysUserVo, PaymentOrdersVo paymentOrdersVo){
        SysUserBo userBo = new SysUserBo();
        BeanUtil.copyProperties(sysUserVo,userBo);

//           Integer freeCount =  Integer.valueOf(baseConfig.get("usageCount","free"));
//        Integer vipCount =  Integer.valueOf(baseConfig.get("usageCount","vip"));
        //非会员升级对应包  此处不做会员升级，仅做购买积分
//        if("0".equals(sysUserVo.getUserGrade())){
//            userBo.setUserGrade("1");
//            Integer duration = paymentOrdersVo.getDuration();
//            Integer pgPoint = paymentOrdersVo.getPgPoints();
//            long expiryTimestamp =   DateUtils.calculateExpiryTimestamp(duration);
//            userBo.setVipExpiryDate(expiryTimestamp);
//            userBo.setSubMsgCount(vipCount);
//        }else{
//            //已经是会员则在此基础上延长时间
//            if(sysUserVo.getVipExpiryDate()!=null){
//                Integer duration = paymentOrdersVo.getDuration();
//                long viptime = sysUserVo.getVipExpiryDate();
//                //续费所订购包的时间
//                long expiryTimestamp =  DateUtils.renewExpiryTimestamp(duration,viptime);
//                userBo.setVipExpiryDate(expiryTimestamp);
//                userBo.setSubMsgCount(vipCount);
//            }else{
//                userBo.setUserGrade("1");
//                Integer duration = paymentOrdersVo.getDuration();
//                long expiryTimestamp =   DateUtils.calculateExpiryTimestamp(duration);
//                userBo.setVipExpiryDate(expiryTimestamp);
//                userBo.setSubMsgCount(vipCount);
//            }
//        }
        userBo.setPoints(paymentOrdersVo.getPgPoints());


        //处理 邀请人 奖励会员
//        AiInvitationsBo ib = new AiInvitationsBo();
//        ib.setInviteeId(sysUserVo.getUserId());
//        ib.setInviterCode(sysUserVo.getInviteCode());
//        List<AiInvitationsVo> invitationsVoList =   invitationsService.queryList(ib);
//        if(invitationsVoList.size()>0){
//            String inviCode = invitationsVoList.get(0).getInviterCode();
//            SysUser user = new SysUser();
//            BeanUtil.copyProperties(sysUserVo,user);
//            sysLoginService.inviteHandler(inviCode,user,"rech");
//        }

        sysUserService.updateUser(userBo);
    }


    @Override
    public Map<String, String> doXCXPay(GoodsOrderRequest goodsOrderRequest, String orderNo, PaymentOrdersBo paymentOrdersBo,String notify_url ) throws Exception {

//        String orderNo = paymentOrdersBo.getOrderNo();
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


        // 3. 调用微信统一下单
        Map<String, String> params = new HashMap<>();
        params.put("appid", appId);
        params.put("mch_id", mchId);
        params.put("nonce_str", WechatUtils.generateNonceStr());
//        params.put("sign_type", "MD5");
        params.put("body", paymentOrdersBo.getOrderName());
        params.put("out_trade_no", orderNo);
//           params.put("total_fee", order.getTotalFee().toString());
//        params.put("total_fee", BaseWxPayRequest.yuanToFen(String.valueOf(paymentOrdersBo.getAmount())).toString());
        params.put("total_fee", String.valueOf(AmountUtil.yuan2Fen(paymentOrdersBo.getAmount().toPlainString())));

        params.put("spbill_create_ip", "127.0.0.1");
        params.put("notify_url", notify_url);
        params.put("trade_type", "JSAPI");
        params.put("openid", paymentOrdersBo.getOpenid());

        // 生成签名
        String sign = WXPayUtil.generateSignature(params, paySignKey);
//        String sign = generatePaySign(params, keyPath);





        params.put("sign", sign);


        System.out.println(params+"=========");

        // 调用统一下单接口
        String xmlResult = null;
        try {
            xmlResult = HttpUtil.post("https://api.mch.weixin.qq.com/pay/unifiedorder",
                WXPayUtil.mapToXml(params));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Map<String, String> result = null;
        try {
            result = WXPayUtil.xmlToMap(xmlResult);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (!"SUCCESS".equals(result.get("return_code")) ||
            !"SUCCESS".equals(result.get("result_code"))) {
            throw new RuntimeException("微信支付下单失败");
        }

        // 4. 生成支付参数
        String prepayId = result.get("prepay_id");
        Map<String, String> payParams = new HashMap<>();
        payParams.put("appId", appId);
        payParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        payParams.put("nonceStr", WechatUtils.generateNonceStr());
        payParams.put("package", "prepay_id=" + prepayId);
        payParams.put("signType", "MD5");

        String paySign = WXPayUtil.generateSignature(payParams, mchKey);
        payParams.put("paySign", paySign);

        return payParams;
    }
    @Override
    public Object doWechatPay(GoodsOrderRequest goodsOrderRequest, String orderNo, PaymentOrdersBo paymentOrders ) throws WxPayException {

        Map<String,String> payMap =   baseConfig.get("wxPay");
        String payInterfaceVersion = payMap.get("interface_version");
        Object payObj = null;
        if("V3".equals(payInterfaceVersion)){
            //发起订单
            PaymentRequestV3 requestV3 = new PaymentRequestV3();
            requestV3.setTerminal(goodsOrderRequest.getPayClient());
            requestV3.setOutTradeNo(orderNo);
            requestV3.setOrderAmount(paymentOrders.getAmount());
            requestV3.setDescription(paymentOrders.getRemark());
            requestV3.setOpenid(paymentOrders.getOpenid());


            try {
                payObj = wechatPayDriver.unifiedOrderV3(requestV3);
                log.info("支付反馈信息：{}",payObj);
            }catch (WxPayException e){
                log.error("支付异常：",e.getMessage());
                throw new WxPayException("pay error :"+e.getMessage());
            }
        }else{
            // 失效时间
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            Long expireTime = System.currentTimeMillis() + (30 * 60 * 1000);
            String timeExpire = format.format(expireTime) ;

            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            orderRequest.setProductId(String.valueOf(paymentOrders.getId()));
            orderRequest.setBody(paymentOrders.getOrderName());
            orderRequest.setOutTradeNo(orderNo);
            orderRequest.setTotalFee(BaseWxPayRequest.yuanToFen(String.valueOf(paymentOrders.getAmount())));//元转成分
              //TODO 暂时去掉传openid
//            orderRequest.setOpenid(paymentOrders.getOpenid());
            orderRequest.setSpbillCreateIp(IpUtils.getIpAddress());
            orderRequest.setTimeStart(DateUtil.format(new Date(),"yyyyMMddHHmmss"));
            orderRequest.setTimeExpire(timeExpire);


            String notifyUrl = payMap.get("notifyBaseUrl");
            orderRequest.setNotifyUrl(notifyUrl);
            if(ClientEnum.PC.getCode()==goodsOrderRequest.getPayClient()){
                orderRequest.setTradeType("NATIVE");
            }else{
                orderRequest.setTradeType("JSAPI");
            }


            payObj = wechatPayDriver.unifiedOrderV2(orderRequest);
            log.info("微信支付信息:{}",payObj);
        }


        return payObj;
    }


    /**
     * 支付宝支付信息
     * @param goodsOrderRequest
     * @param orderNo
     * @param paymentOrders
     * @return
     * @throws Exception
     */
    @Override
    public Object doAliPay(GoodsOrderRequest goodsOrderRequest, String orderNo, PaymentOrdersBo paymentOrders ) throws Exception {
        Map<String,String> payMap =   baseConfig.get("aliPay");
        // 失效时间
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Long expireTime = System.currentTimeMillis() + (30 * 60 * 1000);
        String timeExpire = format.format(expireTime) ;
        Object payObj = null;
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOutTradeNo(orderNo);
        paymentRequest.setOrderAmount(paymentOrders.getAmount());
        paymentRequest.setAttach(paymentOrders.getOrderName());
        paymentRequest.setTerminal(goodsOrderRequest.getPayClient());
        payObj = aliPayDriver.unifiedOrder(paymentRequest,paymentOrders);
        log.info("支付宝支付信息:{}",payObj);
        return payObj;

    }




    /**
     * 生成微信支付所需的 PaySign
     * @param params
     * @param key
     * @return
     */
    public static String generatePaySign(Map<String, String> params, String key) {
        TreeMap<String, String> sortedParams = new TreeMap<>(params);

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        sb.append("key=").append(key);

        String sign = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("MD5");
            crypt.reset();
            crypt.update(sb.toString().getBytes("UTF-8"));
            sign = byteToHex(crypt.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sign.toUpperCase();
    }

    private static String byteToHex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }


}
