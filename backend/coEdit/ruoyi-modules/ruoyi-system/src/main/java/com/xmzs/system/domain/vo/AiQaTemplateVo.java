package com.xmzs.system.domain.vo;

import com.xmzs.system.domain.AiQaTemplate;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.common.excel.annotation.ExcelDictFormat;
import com.xmzs.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 问答模版视图对象 ai_qa_template
 *
 * @author zuleed
 * @date 2024-12-19
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiQaTemplate.class)
public class AiQaTemplateVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @ExcelProperty(value = "编号")
    private Long id;

    /**
     * 模版名称
     */
    @ExcelProperty(value = "模版名称")
    private String name;

    /**
     * 模版描述
     */
    @ExcelProperty(value = "模版描述")
    private String temDescription;

    /**
     * 角色类型（system/user）
     */
    private String roleType;

    /**
     * 模版内容
     */
    @ExcelProperty(value = "模版内容")
    private String templateContent;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 状态（1有效、2无效）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_yes_no")
    private String status;


}
