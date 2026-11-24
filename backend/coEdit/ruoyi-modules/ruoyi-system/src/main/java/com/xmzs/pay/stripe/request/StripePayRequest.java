package com.xmzs.pay.stripe.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StripePayRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 支付终端 */
    private Integer terminal;

    /** 扩展字段 */
    private String attach;

    /** 订单编号 */
    private String outTradeNo;

    /** 订单金额 */
    private BigDecimal orderAmount;

    /** 回调 URL */
    private String redirect_url;

    /**  套餐名称*/
    private String packageName;

    /**  套餐描述*/
    private String packageDescription;

}
