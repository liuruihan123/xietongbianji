package com.xmzs.system.domain.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.system.domain.PaymentOrder;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 支付订单业务对象 payment_orders
 *
 * @author Lion Li
 * @date 2024-04-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PaymentOrder.class, reverseConvertGenerate = false)
public class PaymentOrdersBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 订单编号
     */
    @NotBlank(message = "订单编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String orderNo;

    /**
     * 订单名称
     */
    @NotBlank(message = "订单名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String orderName;

    /**
     * 套餐英文名称
     */
    private String enName;
    /**
     * 金额
     */
    @NotNull(message = "金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal amount;

    /**
     * 支付状态
     */
    @NotBlank(message = "支付状态不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotBlank(message = "支付方式不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
