package com.xmzs.common.core.enums;

/**
 * 客户端枚举类
 */
public enum ChannelEnum {

    BY_USER(1, "用户自购"),
    BY_ADMIN(2, "后台调整"),
    BY_CODE(3, "兑换码");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;
    ChannelEnum(int code, String msg) {
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
        for(ChannelEnum enumItem: ChannelEnum.values()) {
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
        for(ChannelEnum enumItem: ChannelEnum.values()) {
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
        for(ChannelEnum enumItem: ChannelEnum.values()) {
            if (enumItem.getCode() == code) {
                return enumItem.getMsg();
            }
        }
        return "-";
    }

}
