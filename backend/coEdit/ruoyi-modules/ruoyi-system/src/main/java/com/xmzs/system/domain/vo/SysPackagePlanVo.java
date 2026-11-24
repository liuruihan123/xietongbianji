package com.xmzs.system.domain.vo;

import java.math.BigDecimal;

import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.excel.annotation.ExcelDictFormat;
import com.xmzs.common.excel.convert.ExcelDictConvert;
import com.xmzs.system.domain.SysPackagePlan;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 套餐管理视图对象 sys_package_plan
 *
 * @author zhuzi
 * @date 2024-06-09
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysPackagePlan.class)
public class SysPackagePlanVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 套餐名称
     */
    @ExcelProperty(value = "套餐名称")
    @NotBlank(message = "套餐名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 套餐价格（元）
     */
    @ExcelProperty(value = "套餐价格", converter = ExcelDictConvert.class)
    @NotBlank(message = "套餐价格（人民币）不能为空", groups = { AddGroup.class, EditGroup.class })
    @ExcelDictFormat(readConverterExp = "元=")
    private BigDecimal price;

    @ExcelDictFormat(readConverterExp = "美元=")
    @NotNull(message = "套餐价格（美元）不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal enPrice;


    /**
     * 有效时间
     */
    @ExcelProperty(value = "有效时间")
    private Integer duration;

    /**
     * 套餐积分数量
     */
    private Integer pgPoints;

    /**
     * 计划详情
     */
    @ExcelProperty(value = "计划详情")
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
    @ExcelProperty(value = "备注")
    private String remark;


}
