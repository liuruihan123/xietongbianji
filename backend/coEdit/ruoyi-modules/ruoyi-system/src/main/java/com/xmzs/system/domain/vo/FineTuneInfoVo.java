package com.xmzs.system.domain.vo;

import com.xmzs.common.excel.convert.ExcelDictConvert;
import com.xmzs.system.domain.FineTuneInfo;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.common.excel.annotation.ExcelDictFormat;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 精调管理视图对象 fine_tune_info
 *
 * @author zhuzi
 * @date 2024-07-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = FineTuneInfo.class)
public class FineTuneInfoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    @ExcelProperty(value = "编码")
    private Integer id;

    /**
     * 微调ID
     */
    @ExcelProperty(value = "微调ID")
    private String ftId;

    /**
     * 作业id
     */
    @ExcelProperty(value = "作业id")
    private String ftJobId;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long uid;

    /**
     * 微调名
     */
    @ExcelProperty(value = "微调名")
    private String ftName;

    /**
     * 微调的模型名称
     */
    @ExcelProperty(value = "微调的模型名称")
    private String ftModelName;

    /**
     * 基础模型
     */
    @ExcelProperty(value = "基础模型")
    private String baseModel;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 训练状态（0未使用 1使用中 99训练失败）
     */
    @ExcelProperty(value = "训练状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "training_status")
    private String status;

    /**
     * 使用状态（0未使用 1使用中）
     */
    @ExcelProperty(value = "使用状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "use_status")
    private String useStatus;


}
