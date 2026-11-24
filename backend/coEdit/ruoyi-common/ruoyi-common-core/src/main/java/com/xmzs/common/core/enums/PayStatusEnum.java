package com.xmzs.common.core.enums;

/**
 * 客户端枚举类
 */
public enum PayStatusEnum {

    BY_PENDING(1, "待支付"),
    BY_COMPLETE(2, "完成支付"),
    BY_CLOSE(3, "订单关闭"),
    BY_EXCEPTION(4, "支付异常");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;
    PayStatusEnum(int code, String msg) {
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
    public static Integer getCodeByType(String type){
        for(PayStatusEnum enumItem: PayStatusEnum.values()) {
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
    public int getCode() {
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
        for(PayStatusEnum enumItem: PayStatusEnum.values()) {
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
        for(PayStatusEnum enumItem: PayStatusEnum.values()) {
            if (enumItem.getCode() == code) {
                return enumItem.getMsg();
            }
        }
        return "-";
    }

}
