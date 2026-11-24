package com.xmzs.system.domain.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.system.domain.SysPackagePlan;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 套餐管理业务对象 sys_package_plan
 *
 * @author zhuzi
 * @date 2024-06-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysPackagePlan.class, reverseConvertGenerate = false)
public class SysPackagePlanBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 套餐名称
     */
    @NotBlank(message = "套餐名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 套餐价格（元）
     */
    @NotNull(message = "套餐价格（人民币元）不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal price;

    @NotNull(message = "套餐价格（美元）不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal enPrice;




    /**
     * 有效时间
     */
    @NotNull(message = "有效时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer duration;

    /**
     * 套餐积分数量
     */
    private Integer pgPoints;

    /**
     * 计划详情
     */
    @NotBlank(message = "计划详情不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
