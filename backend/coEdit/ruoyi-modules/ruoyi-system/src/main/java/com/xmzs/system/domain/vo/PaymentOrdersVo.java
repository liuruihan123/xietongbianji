package com.xmzs.system.domain.vo;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.system.domain.PaymentOrder;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * 支付订单视图对象 payment_orders
 *
 * @author Lion Li
 * @date 2024-04-16
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = PaymentOrder.class)
public class PaymentOrdersVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 订单编号
     */
    @ExcelProperty(value = "订单编号")
    private String orderNo;

    /**
     * 订单名称
     */
    @ExcelProperty(value = "订单名称")
    private String orderName;

    /**
     * 套餐英文名称
     */
    @ExcelProperty(value = "套餐英文名称")
    private String enName;

    /**
     * 金额
     */
    @ExcelProperty(value = "金额")
    private BigDecimal amount;

    /**
     * 支付状态
     */
    @ExcelProperty(value = "支付状态")
    private String paymentStatus;


    /**
     * 订购包编号
     */
    @NotNull(message = "订购包编号不能为空", groups = { EditGroup.class })
    private Long planId;

    /**
     * 有效时间（月份）
     */
    @NotNull(message = "有效时间（月份）", groups = { EditGroup.class })
    private Integer Duration;

    /**
     * 套餐积分数量
     */
    private Integer pgPoints;


    /**
     * 支付方式
     */
    @ExcelProperty(value = "支付方式")
    private String paymentMethod;

    /**
     * 支付二维码地址
     */
    @ExcelProperty(value = "支付二维码地址")
    private String payQrcode;


    /**
     * 1微信小程序,2微信公众号,3手机H5,4电脑PC,5苹果APP,6安卓APP,7后端
     */
    private Integer payClient;


    /**
     * 购买来源:1-用户自购:2-后台调整:3-兑换码
     */
    private Integer  channel;


    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 微信用户ID
     */
//    private String openId;
    private String openid;
    private String unionid;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 二维码网络地址
     */
    private String url;


    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


}
