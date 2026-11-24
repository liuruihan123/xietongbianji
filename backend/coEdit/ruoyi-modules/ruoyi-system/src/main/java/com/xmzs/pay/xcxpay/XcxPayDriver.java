package com.xmzs.pay.xcxpay;

import cn.hutool.core.bean.BeanUtil;
import com.github.wxpay.sdk.WXPayUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.xmzs.common.core.utils.AmountUtil;
import com.xmzs.pay.xcxpay.entity.*;
import com.xmzs.pay.xcxpay.util.*;
import com.xmzs.system.config.base.BaseConfig;
import com.xmzs.system.domain.bo.PaymentOrdersBo;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName com.xmzs.pay.xcxpay
 *
 * @author zhuzi
 * @date 2025/2/24
 * @description TODO
 */
@Data
@Component
@Slf4j
public class XcxPayDriver {

    private final BaseConfig baseConfig;


    /**
     * 小程序支付
     * @param paymentOrdersBo
     * @return
     */
    public Map payment(PaymentOrdersBo paymentOrdersBo) {

        Map<String,String> payMap =   baseConfig.get("wxPay");
        String appid = payMap.get("appId");; // APP对应的微信的appid
        String mch_id = payMap.get("mch_id");
        String notifyURL = payMap.get("notifyBaseUrl");
        String mchKey = payMap.get("mchKey");

        Map<String, Object> map = new HashMap<>();
        String title = paymentOrdersBo.getOrderName();

        try {
            OrderInfo order = new OrderInfo();
            order.setAppid(appid);
            order.setMch_id(mch_id);
//            order.setAppid(Configure.getAppID());
//            order.setMch_id(Configure.getMch_id());
            order.setNonce_str(RandomStringGenerator.getRandomStringByLength(32));
            order.setBody(title);
//            order.setOut_trade_no(RandomStringGenerator.getRandomStringByLength(32));
            order.setOut_trade_no(paymentOrdersBo.getOrderNo());
            order.setTotal_fee(AmountUtil.yuan2Fen(paymentOrdersBo.getAmount().toString()));     // 该金钱其实10 是 0.1元
            order.setSpbill_create_ip("127.0.0.1");
            order.setNotify_url(notifyURL);
            order.setTrade_type("JSAPI");
            //这里直接使用当前用户的openid
            order.setOpenid(paymentOrdersBo.getOpenid());
//            order.setUnionid(paymentOrdersBo.getUnionid());
            order.setSign_type("MD5");
            //生成签名
            String sign = Signature.getSign(order,mchKey);
            order.setSign(sign);

            String result = HttpRequest.sendPost("https://api.mch.weixin.qq.com/pay/unifiedorder", order);
            System.out.println(result);
            XStream xStream = new XStream();
            xStream.alias("xml", OrderReturnInfo.class);
//            Object obj = xStream.fromXML(result);

            Map<String, String> rsMap = null;
            try {
                rsMap = WXPayUtil.xmlToMap(result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println(rsMap+"--------");

            OrderReturnInfo returnInfo = BeanUtil.copyProperties(rsMap,OrderReturnInfo.class);
//            OrderReturnInfo returnInfo = (OrderReturnInfo) xStream.fromXML(result);
            // 二次签名
            if ("SUCCESS".equals(returnInfo.getReturn_code()) && returnInfo.getReturn_code().equals(returnInfo.getResult_code())) {
                SignInfo signInfo = new SignInfo();
                signInfo.setAppId(appid);
                long time = System.currentTimeMillis() / 1000;
                signInfo.setTimeStamp(String.valueOf(time));
                signInfo.setNonceStr(RandomStringGenerator.getRandomStringByLength(32));
                signInfo.setRepay_id("prepay_id=" + returnInfo.getPrepay_id());
                signInfo.setSignType("MD5");
                //生成签名
                String sign1 = Signature.getSign(signInfo,mchKey);
                Map<String, String> payInfo = new HashMap<>();
                payInfo.put("timeStamp", signInfo.getTimeStamp());
                payInfo.put("nonceStr", signInfo.getNonceStr());
                payInfo.put("package", signInfo.getRepay_id());
                payInfo.put("signType", signInfo.getSignType());
                payInfo.put("paySign", sign1);
                map.put("status", 200);
                map.put("msg", "统一下单成功!");
                map.put("data", payInfo);
                log.info("下单成功:{}",map);
                // 此处可以写唤起支付前的业务逻辑

                // 业务逻辑结束 回传给小程序端唤起支付
                return map;
            }
            log.error("解析数据:{}",rsMap);
            log.error("returnInfo:{}",returnInfo);
            map.put("status", 500);
            map.put("msg", "统一下单失败!");
            map.put("data", null);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public Map transfer( Integer money) throws Exception {

        Map<String,String> payMap =   baseConfig.get("wxPay");
//        String mchId = payMap.get("mch_id");
//        String appId = payMap.get("appId");
        // 1.0 拼凑企业支付需要的参数
//        String appid = Configure.getAppID(); // APP对应的微信的appid
        String appid = payMap.get("appId");; // APP对应的微信的appid
//        String mch_id = Configure.getMch_id(); // 商户号
        String mch_id = payMap.get("mch_id");; // 商户号
        String mchKey = payMap.get("mchKey");
        String nonce_str = RandomStringGenerator.getRandomStringByLength(32); // 生成随机数
        String partner_trade_no = new SnowflakeIdWorker(1, 2).nextId() + ""; // 生成商户订单号
        String openid = ""; // 收款用户openid
        String unionid = ""; // 收款用户unionid
        String check_name = "NO_CHECK"; // 是否验证真实姓名呢
        // String re_user_name = "KOLO"; // 收款用户姓名(非必须)
        int amount = money * 100; // 企业付款金额，最少为100，单位为分
        String desc = "押金提现到账！"; // 企业付款操作说明信息。必填。
        // String spbill_create_ip = IpKit.getIpAddr(request); // 用户的ip地址

        // 2.0 封装参数
        Transfer transfer = new Transfer();
        transfer.setMch_appid(appid);
        transfer.setMchid(mch_id);
        transfer.setNonce_str(nonce_str);
        transfer.setPartner_trade_no(partner_trade_no);
        transfer.setOpenid(openid);
        transfer.setUnionid(unionid);
        transfer.setCheck_name(check_name);
        transfer.setAmount(amount);
        transfer.setDesc(desc);

        try {

            // 3.0 利用上面的参数，先去生成自己的签名
            String sign = Signature.getSign(transfer,mchKey);

            // 4.0 将签名再放回map中，它也是一个参数
            transfer.setSign(sign);

            //解决XStream对出现双下划线的bug
            XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
            xStreamForRequestPostData.alias("xml", transfer.getClass());
            //将要提交给API的数据对象转换成XML格式数据Post给API
            String postDataXML = xStreamForRequestPostData.toXML(transfer);
            String s = doRefund("https://api.mch.weixin.qq.com/pay/unifiedorder", postDataXML);
            System.out.println(s);

            // 5.0 向微信发送请求转账请求
//            String result = HttpRequest.sendPost(Configure.getTransfers_url(), transfer);
//            System.out.println(result);
            XStream xStream = new XStream();
            xStream.alias("xml", OrderReturnInfo.class);

            OrderReturnInfo returnInfo = (OrderReturnInfo) xStream.fromXML(s);

            if ("SUCCESS".equals(returnInfo.getReturn_code()) && returnInfo.getReturn_code().equals(returnInfo.getResult_code())) {                // 付款成功
                return null;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 携带认证证书
    public String doRefund(String url, String data) throws Exception {
        Map<String,String> payMap =   baseConfig.get("wxPay");
        String appid = payMap.get("appId");; // APP对应的微信的appid
        String mch_id = payMap.get("mch_id");;

        //注意PKCS12证书 是从微信商户平台-》账户设置-》 API安全 中下载的
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        File file = new File(this.getClass().getClassLoader().getResource("apiclient_cert.p12").getFile());

        //指向你的证书的绝对路径，带着证书去访问
        FileInputStream instream = new FileInputStream(file);//P12文件目录
        try {
            //下载证书时的密码、默认密码是你的MCHID mch_id
//            keyStore.load(instream, Configure.getMch_id().toCharArray());//这里写密码
            keyStore.load(instream, mch_id.toCharArray());//这里写密码
        } finally {
            instream.close();
        }
        //下载证书时的密码、默认密码是你的MCHID mch_id
        SSLContext sslcontext = SSLContexts.custom()
//            .loadKeyMaterial(keyStore, Configure.getMch_id().toCharArray())//这里也是写密码的
            .loadKeyMaterial(keyStore, mch_id.toCharArray())//这里也是写密码的
            .build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
            sslcontext,
            new String[]{"TLSv1"},
            null,
            SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom()
            .setSSLSocketFactory(sslsf)
            .build();
        try {
            HttpPost httpost = new HttpPost(url); // 设置响应头信息
            httpost.addHeader("Connection", "keep-alive");
            httpost.addHeader("Accept", "*/*");
            httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpost.addHeader("Host", "api.mch.weixin.qq.com");
            httpost.addHeader("X-Requested-With", "XMLHttpRequest");
            httpost.addHeader("Cache-Control", "max-age=0");
            httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
            httpost.setEntity(new StringEntity(data, "UTF-8"));
            CloseableHttpResponse response = httpclient.execute(httpost);
            try {
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                EntityUtils.consume(entity);
                return jsonStr;
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }


    /**
     * 微信小程序支付成功回调函数
     */
    public void wxNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,String> payMap =   baseConfig.get("wxPay");
        String appid = payMap.get("appId");; // APP对应的微信的appid
        String mch_id = payMap.get("mch_id");
        String notifyURL = payMap.get("notifyURL");
        String mchKey = payMap.get("mchKey");

        BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        //sb为微信返回的xml
        String notityXml = sb.toString();
        String resXml = "";
        System.out.println("接收到的报文：" + notityXml);

        Map map = PayUtil.doXMLParse(notityXml);

        String returnCode = (String) map.get("return_code");
        if ("SUCCESS".equals(returnCode)) {
            //验证签名是否正确
            Map<String, String> validParams = PayUtil.paraFilter(map);  //回调验签时需要去除sign和空值参数
            String validStr = PayUtil.createLinkString(validParams);//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
            String sign = PayUtil.sign(validStr, mchKey, "utf-8").toUpperCase();//拼装生成服务器端验证的签名
            // 因为微信回调会有八次之多,所以当第一次回调成功了,那么我们就不再执行逻辑了

            //根据微信官网的介绍，此处不仅对回调的参数进行验签，还需要对返回的金额与系统订单的金额进行比对等
            if (sign.equals(map.get("sign"))) {
                /**此处添加自己的业务逻辑代码start**/
                // bla bla bla....
                /**此处添加自己的业务逻辑代码end**/
                //通知微信服务器已经支付成功
                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                    + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
            } else {
                System.out.println("微信支付回调失败!签名不一致");
            }
        } else {
            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
        }
        System.out.println(resXml);
        System.out.println("微信支付回调数据结束");

        BufferedOutputStream out = new BufferedOutputStream(
            response.getOutputStream());
        out.write(resXml.getBytes());
        out.flush();
        out.close();
    }

    /**
     * 查询订单
     */
//    public Map orderQuery() {
//        Map<String, Object> map = new HashMap<>();
//        Map<String,String> payMap =   baseConfig.get("wxPay");
//        String appid = payMap.get("appId");; // APP对应的微信的appid
//        String mch_id = payMap.get("mch_id");
//        String notifyURL = payMap.get("notifyURL");
//        String mchKey = payMap.get("mchKey");
//        try {
//            OrderInfo order = new OrderInfo();
//            order.setAppid(appid);
//            order.setMch_id(mch_id);
//            order.setNonce_str(RandomStringGenerator.getRandomStringByLength(32));
//            order.setOut_trade_no("wuyye9w8akwbq1nc60o4affwwqnxh9dn");
//            //order.setSign_type("MD5");
//            //生成签名
//            String sign = Signature.getSign(order,mchKey);
//            order.setSign(sign);
//
//            String result = HttpRequest.sendPost(Configure.getQuery_url(), order);
//            System.out.println(result);
//            XStream xStream = new XStream();
//            xStream.alias("xml", QueryReturnInfo.class);
//
//            QueryReturnInfo returnInfo = (QueryReturnInfo) xStream.fromXML(result);
//            map.put("status", 500);
//            map.put("msg", "统一下单失败!");
//            map.put("data", returnInfo);
//            return map;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


}
