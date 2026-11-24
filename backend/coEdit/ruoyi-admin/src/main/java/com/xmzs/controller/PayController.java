package com.xmzs.controller;//package com.mdd.front.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.xmzs.common.config.PayConfig;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.enums.PayMenthodEnum;
import com.xmzs.common.core.enums.PayStatusEnum;
import com.xmzs.common.core.exception.base.BaseException;
import com.xmzs.common.core.utils.MapUtils;
import com.xmzs.common.core.utils.PayUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.oss.core.OssClient;
import com.xmzs.common.oss.entity.UploadResult;
import com.xmzs.common.oss.factory.OssFactory;
import com.xmzs.common.response.PayResponse;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.pay.alipay.config.AliPayDriver;
import com.xmzs.pay.stripe.config.StripePayDriver;
import com.xmzs.pay.xcxpay.XcxPayDriver;
import com.xmzs.system.config.base.BaseConfig;
import com.xmzs.system.domain.bo.PaymentOrdersBo;
import com.xmzs.system.domain.bo.SysPackagePlanBo;
import com.xmzs.system.domain.request.GoodsOrderRequest;
import com.xmzs.system.domain.request.OrderRequest;
import com.xmzs.system.domain.vo.PaymentOrdersVo;
import com.xmzs.system.domain.vo.SysPackagePlanVo;
import com.xmzs.system.service.IPayService;
import com.xmzs.system.service.IPaymentOrdersService;
import com.xmzs.system.service.ISysPackagePlanService;
import com.xmzs.system.service.ISysUserService;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

import org.apache.commons.io.IOUtils;

@Api(tags = "支付管理")
@RequiredArgsConstructor
@RestController
@RequestMapping("/pay")
@Slf4j
public class PayController {


//    private final PayService payService;

    private final ISysUserService userService;

    private final IPaymentOrdersService paymentOrdersService;

    private final ISysPackagePlanService sysPackagePlanService;

//    private final WechatPayDriver wechatPayDriver;

    private final IPayService payService;

    private final WxPayService wxPayService;

    private final AliPayDriver aliPayDriver;

    private final StripePayDriver stripePayDriver;

    private final ISysUserService sysUserService;

    private final BaseConfig baseConfig;

    private final PayConfig payConfig;

    private final XcxPayDriver xcxPayDriver;

    /**
     * 获取支付套餐列表
     *
     * @Date 2024/11/24
     * @return void
     **/
    @PostMapping("/payPackageList")
    public R<List<SysPackagePlanVo>> payPackageList() {
        List<SysPackagePlanVo> packagePlanVoList = sysPackagePlanService.queryList(new SysPackagePlanBo());
        return R.ok(packagePlanVoList);
    }




    /**
     * 小程序支付
     *
     * @Date 2024/11/24
     * @return void
     **/
    @PostMapping("/xcxPayUrl")
    public R<Map<String,String>> xcxpayUrl(@RequestBody GoodsOrderRequest goodsOrderRequest) throws Exception {
        LoginUser loginUser = LoginHelper.getLoginUser();
        PaymentOrdersBo paymentOrdersBo = payService.beforeOrderHandler(goodsOrderRequest,loginUser);
//        Map<String,String> psMap =  payService.doXCXPay(goodsOrderRequest,paymentOrdersBo.getOrderNo(),paymentOrdersBo,payConfig.getNotify_url());
        Map<String,String> psMap =  xcxPayDriver.payment(paymentOrdersBo);
        return R.ok(psMap);
    }










    /**
     * 获取支付二维码 （支付宝/微信支付方式） PC端方式    包月支付形式
     *
     * @Date 2024/11/24
     * @return void
     **/
    @PostMapping("/payUrl")
    public R<PaymentOrdersVo> payUrl(@RequestBody GoodsOrderRequest goodsOrderRequest) throws Exception {
        LoginUser loginUser = LoginHelper.getLoginUser();
        log.info("当前登录账号信息:{}",loginUser);
        PaymentOrdersBo paymentOrdersBo = payService.beforeOrderHandler(goodsOrderRequest,loginUser);
        //调用支付
       Object obj = "";
        String payUrl = "";

        PaymentOrdersVo paymentOrdersVo = new PaymentOrdersVo();

        //微信支付
       if(goodsOrderRequest.getPayMethod().equals(PayMenthodEnum.WXPAY.getCode())){
           try {
               obj =  payService.doWechatPay(goodsOrderRequest,paymentOrdersBo.getOrderNo(),paymentOrdersBo);
           }catch (WxPayException wxe){
               log.error("微信支付异常:{}",wxe.getReturnMsg());
           }

           Map<String,String> payInfoMap = MapUtils.objectToMap(obj);
           payUrl = payInfoMap.get("codeURL");


           //微信生成扫码支付二维码
           byte[] bytes = QrCodeUtil.generatePng(payUrl, 300, 300);
           OssClient storage = OssFactory.instance();
           UploadResult upload=storage.upload(bytes, storage.getPath("qrCode",".png"), "image/png");
           BeanUtil.copyProperties(paymentOrdersBo,paymentOrdersVo);
           paymentOrdersVo.setUrl(upload.getUrl());
           //保存二维码地址
           paymentOrdersBo.setPayQrcode(upload.getUrl());
           paymentOrdersService.updateByBo(paymentOrdersBo);


       }else if(goodsOrderRequest.getPayMethod().equals(PayMenthodEnum.ALIPAY.getCode())){  //支付宝支付
           try {
               obj = payService.doAliPay(goodsOrderRequest,paymentOrdersBo.getOrderNo(),paymentOrdersBo);
           } catch (Exception e) {
               log.error("异常:{}",e.getMessage());
               throw new RuntimeException(e);
           }
           Map<String,String> payInfoMap = MapUtils.objectToMap(obj);
           payUrl = payInfoMap.get("config");
           BeanUtil.copyProperties(paymentOrdersBo,paymentOrdersVo);
           paymentOrdersVo.setUrl(payUrl);

           paymentOrdersService.updateByBo(paymentOrdersBo);

       }else if(goodsOrderRequest.getPayMethod().equals(PayMenthodEnum.STRIPEPAY.getCode())){
           try {
               payUrl = stripePayDriver.unifiedOrder(paymentOrdersBo);
               BeanUtil.copyProperties(paymentOrdersBo,paymentOrdersVo);
               paymentOrdersVo.setUrl(payUrl);
               paymentOrdersService.updateByBo(paymentOrdersBo);
           } catch (StripeException e) {
               log.error("Stripe异常:{}",e.getMessage());
               throw new RuntimeException(e);
           }
       }

           log.info("支付链接:{}",payUrl);
//       if(ObjectUtils.isEmpty(obj)){
//           return R.fail("pay config error");
//       }
        return R.ok(paymentOrdersVo);
    }






    /**
     * 支付宝 支付 异步回调接口  必须是 Post
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/aliPaynotify")
    public String payNotify(@RequestParam Map<String, String> params) throws Exception {
        log.info("支付宝支付回调:{}",params);
        // 验证签名
        boolean isValid = aliPayDriver.verifyNotify(params);
        if (!isValid) {
            log.error("签名验证失败.....");
            return "fail"; // 签名验证失败
        }

    // 处理业务逻辑
    String tradeStatus = params.get("trade_status");
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            // 处理成功的订单
            log.info("=====支付宝交易成功===== ");
            log.info("交易名称: " + params.get("subject"));
            log.info("交易状态: " + params.get("trade_status"));
            log.info("支付宝交易凭证号: " + params.get("trade_no"));
            log.info("商户订单号: " + params.get("out_trade_no"));
            log.info("交易金额: " + params.get("total_amount"));
            log.info("买家在支付宝唯一id: " + params.get("buyer_id"));
            log.info("买家付款时间: " + params.get("gmt_payment"));
            log.info("买家付款金额: " + params.get("buyer_pay_amount"));

            //本系统订单号
            String orderId =  params.get("out_trade_no");
            //支付宝交易凭证号
            String tradeNo  =params.get("trade_no");
            //支付金额
            String totalFee =  params.get("buyer_pay_amount");
            //处理订单
            payService.orderHandler(orderId,tradeNo,totalFee);

    }

        return "success"; // 返回给支付宝成功接收
    }


    /**
     * 微信支付完毕回调地址
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping("/notifyUrl")
    public String payNotify(HttpServletRequest request, HttpServletResponse response) {
        try {
            String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            log.info("回调结果:{}",xmlResult);

            Map<String, String> resultMap =  new PayUtils().parseXmlToMap(xmlResult);
            log.info("解析resultMap结果:{}",xmlResult);
            // 提取必要的信息
            String tradeType = resultMap.get("trade_type");
            if ("JSAPI".equals(tradeType)) {
                String orderId = resultMap.get("out_trade_no");
                String tradeNo = resultMap.get("transaction_id");
                String totalFee = BaseWxPayResult.fenToYuan(Integer.valueOf(resultMap.get("total_fee")));
                //处理订单
                payService.orderHandler(orderId,tradeNo,totalFee);
            }else{
                WxPayOrderNotifyResult result = wxPayService.parseOrderNotifyResult(xmlResult);
                log.info("回调解析结果:{}",xmlResult);
                // 加入自己处理订单的业务逻辑，需要判断订单是否已经支付过，否则可能会重复调用
                //本系统订单号
                String orderId = result.getOutTradeNo();
                //微信支付的订单号
                String tradeNo = result.getTransactionId();
                //支付金额
                String totalFee = BaseWxPayResult.fenToYuan(result.getTotalFee());
                //处理订单
                payService.orderHandler(orderId,tradeNo,totalFee);
            }
            return WxPayNotifyResponse.success("处理成功!");
        } catch (Exception e) {
            log.error("微信回调结果异常,异常原因:{}", e.getMessage());
            log.error("错误详情:{}",e);
            return WxPayNotifyResponse.fail(e.getMessage());
        }
    }


    /**
     * stripe 支付回调
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = {"/stripe_notifyUrl"}, method = RequestMethod.POST)
    public void stripe_notifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("------stripe回调------");
        try {
            String endpointSecret = baseConfig.get("stripePay","endpointSecret");//webhook秘钥签名
            log.info("使用的签名：{}",endpointSecret);
            log.info("使用的request：{}",request);

            InputStream inputStream = request.getInputStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024*4];
            int n = 0;
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            byte[] bytes = output.toByteArray();
            String payload = new String(bytes, "UTF-8");
            log.info("payload:{}",payload);
            String sigHeader = request.getHeader("Stripe-Signature");
            log.info("sigHeader:{}",sigHeader);
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);//验签，并获取事件


//            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
//
//            StripeObject stripeObject = null;
//            if (dataObjectDeserializer.getObject().isPresent()) {
//                stripeObject = dataObjectDeserializer.getObject().get();
//            }
//
//            PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
//            Map<String,String> metaData = paymentIntent.getMetadata();//自定义传入参数
//            String order_id = metaData.get("order_id");//自定义订单号
//            paymentIntent.getId();

//            PaymentIntent intent = (PaymentIntent) event
//                .getData()
////                .getDataObjectDeserializer()
//                .getObject()
//                .get();

            switch(event.getType()) {
                case "checkout.session.completed"://支付完成
                   log.info("------------stripe-pay-success---------------");
//                    String s = event.getDataObjectDeserializer().getObject().get().toJson();
                    String s = event.getData().toJson();
                    log.info("回调相关参数:{}",s);
//                    JSONObject jsonObject = JSONObject.parseObject(s);
//                    JSONObject jsonObject2 = (JSONObject)jsonObject.get("object").get("metadata");
//                    String orderId = jsonObject2.getString("orderId");
                    String orderId = "";
                    try {
                        JSONObject jsonObject = JSONObject.parseObject(s);
                        if (jsonObject != null) {
                            JSONObject object = jsonObject.getJSONObject("object");
                            if (object != null) {
                                JSONObject metadata = object.getJSONObject("metadata");
                                if (metadata != null) {
                                     orderId = metadata.getString("orderId");
                                    // 处理 orderId，例如打印或其他操作
                                    System.out.println("Order ID: " + orderId);
                                } else {
                                    System.err.println("Metadata is null.");
                                }
                            } else {
                                System.err.println("Object is null.");
                            }
                        } else {
                            System.err.println("JSON object is null.");
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing JSON: " + e.getMessage());
                        e.printStackTrace();
                    }

                    log.info("订单号为："+orderId);
                    log.info("根据订单号从数据库中找到订单，并将状态置为支付成功状态");

                    //根据本系统订单号查询
                    PaymentOrdersBo paymentOrdersBo = new PaymentOrdersBo();
                    paymentOrdersBo.setOrderNo(orderId);
                    List<PaymentOrdersVo> paymentOrdersList = paymentOrdersService.queryList(paymentOrdersBo);
                    if(paymentOrdersList.size()>0){
                        PaymentOrdersVo ordersVo = paymentOrdersList.get(0);
                        //处理订单
                        payService.orderHandler(orderId,null,String.valueOf(ordersVo.getAmount()));
                    }else{
                        log.error("stripe未找到订单..");
                    }


                    break;
                case "checkout.session.expired"://过期
                   log.info("---------------checkout.session.expired---------------");
                    break;
                case "payment_intent.created"://创建订单 这里事件就是图二选着的事件
                   log.info("---------------nnnnnnnnnnnnnnnnnnnnnn---------------");
                    break;
                case "payment_intent.canceled"://取消订单
                   log.info("---------------canceledcanceledcanceledcanceledcanceledcanceled---------------");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
           log.error("stripe异步通知（webhook事件）"+e);
        }
    }




    /**
     * 查询订单列表
     */
    @PostMapping("/getMyOrders")
    public R<List<PaymentOrdersVo>> getMyOrders(@RequestBody  PaymentOrdersBo bo) {
        PaymentOrdersBo paymentOrdersBo = new PaymentOrdersBo();
        paymentOrdersBo.setUserId(bo.getUserId());
        paymentOrdersBo.setPaymentStatus(String.valueOf(PayStatusEnum.BY_COMPLETE.getCode()));
        List<PaymentOrdersVo> paymentOrdersList = paymentOrdersService.queryList(paymentOrdersBo);
        return R.ok(paymentOrdersList);
    }

    /**
     * 查询小程序订单列表
     */
    @PostMapping("/getMyXcxOrders")
    public R<List<PaymentOrdersVo>> getMyXcxOrders(@RequestBody  PaymentOrdersBo bo) {
        PaymentOrdersBo paymentOrdersBo = new PaymentOrdersBo();
        paymentOrdersBo.setUserId(bo.getUserId());
        List<PaymentOrdersVo> paymentOrdersList = paymentOrdersService.queryMyOrderList(paymentOrdersBo);
        return R.ok(paymentOrdersList);
    }



    /**
     * 获取某个订单信息
     *
     */
    @PostMapping("/orderInfo")
    public R<PaymentOrdersVo> orderInfo(@RequestBody  OrderRequest orderRequest) {
        if(StringUtils.isEmpty(orderRequest.getOrderNo())){
            throw new BaseException("订单号不能为空！");
        }
        PaymentOrdersBo paymentOrdersBo = new PaymentOrdersBo();
        paymentOrdersBo.setOrderNo(orderRequest.getOrderNo());
        List<PaymentOrdersVo> paymentOrdersList = paymentOrdersService.queryList(paymentOrdersBo);
        if (CollectionUtil.isEmpty(paymentOrdersList)){
            throw new BaseException("订单不存在！");
        }
        PaymentOrdersVo paymentOrdersVo = paymentOrdersList.get(0);




        return R.ok(paymentOrdersVo);
    }

    /**
     * 跳转通知地址
     *
     * @Date 2023/7/3
     * @param payResponse
     * @return void
     **/
    @GetMapping("/returnUrl")
    public String returnUrl(PayResponse payResponse) {
//        // 校验签名
//        String mdString = "money=" + payResponse.getMoney() + "&name=" + payResponse.getName() +
//            "&out_trade_no=" + payResponse.getOut_trade_no() + "&pid=" + payConfig.getPid() +
//            "&trade_no=" + payResponse.getTrade_no() + "&trade_status=" + payResponse.getTrade_status() +
//            "&type=" + payResponse.getType() +  payConfig.getKey();
//        String sign = MD5Util.GetMD5Code(mdString);
//        if(!sign.equals(payResponse.getSign())){
//            throw new BaseException("校验签名失败！");
//        }
//        double money = Double.parseDouble(payResponse.getMoney());
//        log.info("支付订单号{}",payResponse);
//        PaymentOrdersBo paymentOrdersBo = new PaymentOrdersBo();
//        paymentOrdersBo.setOrderNo(payResponse.getOut_trade_no());
//        List<PaymentOrdersVo> paymentOrdersList = paymentOrdersService.queryList(paymentOrdersBo);
//        if (CollectionUtil.isEmpty(paymentOrdersList)){
//            throw new BaseException("订单不存在！");
//        }
//        // 订单状态修改为已支付
//        PaymentOrdersVo paymentOrdersVo = paymentOrdersList.get(0);
//        paymentOrdersVo.setPaymentStatus("2");
//        paymentOrdersVo.setPaymentMethod(payResponse.getType());
//        BeanUtil.copyProperties(paymentOrdersVo,paymentOrdersBo);
//        paymentOrdersService.updateByBo(paymentOrdersBo);
//
//        SysUserVo sysUserVo = userService.selectUserById(paymentOrdersVo.getUserId());
//        sysUserVo.setUserBalance(sysUserVo.getUserBalance() + money);
//        SysUserBo sysUserBo = new SysUserBo();
//        BeanUtil.copyProperties(sysUserVo,sysUserBo);
//        // 设置为付费用户
//        sysUserBo.setUserGrade("1");
//        long  vipExpiryDate =  DateUtils.calculateExpiryTimestamp(1);
//        sysUserBo.setVipExpiryDate(vipExpiryDate);
//        userService.updateUser(sysUserBo);
        return "success";
    }




}
