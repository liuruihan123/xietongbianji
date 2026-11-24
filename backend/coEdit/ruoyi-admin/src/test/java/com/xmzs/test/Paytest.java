package com.xmzs.test;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class Paytest {

    @Autowired
    private WxPayService wxPayService;  // 使用 @Autowired 注解

    @DisplayName("测试 WxPayService 的 parseOrderNotifyResult 方法")
    @Test
    public void testParseOrderNotifyResult() {


//        try {
//            WxPayOrderNotifyResult result = wxPayService.parseOrderNotifyResult(xmlResult2);
//            System.out.println("res=========="+result);
//        } catch (WxPayException e) {
//            log.error("解析订单通知结果时出错: ", e);
//        }
    }
}
