package com.xmzs.system.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

import java.io.Serial;

/**
 * 套餐管理对象 sys_package_plan
 *
 * @author zhuzi
 * @date 2024-06-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_package_plan")
public class SysPackagePlan extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 套餐名称
     */
    private String name;

    /**
     * 套餐价格（元）
     */
    private BigDecimal price;




    private BigDecimal enPrice;



    /**
     * 有效时间
     */
    private Integer duration;

    /**
     * 套餐积分数量
     */
    private Integer pgPoints;



    /**
     * 计划详情
     */
    private String planDetail;

    /**
     * 套餐英文名称
     */
    @ExcelProperty(value = "套餐英文名称")
    private String enName;

    /**
     * 英文计划详情
     */
    @ExcelProperty(value = "英文计划详情")
    private String enPlanDetail;

    /**
     * 备注
     */
    private String remark;


}
