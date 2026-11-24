package com.xmzs.pay.wechat.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    private String userName;

//    private String openId;
    private String openid;
    private String unionid;

    private BigDecimal transferAmount;

    private String transferRemark;

    private String outDetailNo;

    private String appId;

    private String transferName;


}
