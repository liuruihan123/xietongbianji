package com.xmzs.common.core.enums;

/**
 * 客户端枚举类
 */
public enum PayMenthodEnum {

    XCXPAY("xcxPay", "小程序支付"),
    WXPAY("wxPay", "微信支付"),
    ALIPAY("aliPay", "支付宝支付"),
    STRIPEPAY("stripePay", "stripe支付");


    /**
     * 构造方法
     */
    private final String code;
    private final String msg;
    PayMenthodEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 根据类型获取Code
     *
     * @author fzr
     * @param type 类型
     * @return Integer
     */
    public static String getCodeByType(String type){
        for(PayMenthodEnum enumItem: PayMenthodEnum.values()) {
            if (enumItem.toString().equals(type.toUpperCase())) {
                return enumItem.getCode();
            }
        }
        return null;
    }

    /**
     * 获取状态码
     *
     * @author fzr
     * @return Long
     */
    public String getCode() {
        return this.code;
    }

    /**
     * 获取提示
     *
     * @author fzr
     * @return String
     */
    public String getMsg() {
        return this.msg;
    }

    /**
     * 根据类型获取Msg
     *
     * @author fzr
     * @param type 类型
     * @return String
     */
    public static String getMsgByType(String type){
        for(PayMenthodEnum enumItem: PayMenthodEnum.values()) {
            if (enumItem.toString().equals(type)) {
                return enumItem.getMsg();
            }
        }
        return null;
    }

    /**
     * 根据编码获取Msg
     *
     * @author fzr
     * @param code 类型
     * @return String
     */
    public static String getMsgByCode(Integer code){
        for(PayMenthodEnum enumItem: PayMenthodEnum.values()) {
            if (enumItem.getCode().equals(code) ) {
                return enumItem.getMsg();
            }
        }
        return "-";
    }

}
