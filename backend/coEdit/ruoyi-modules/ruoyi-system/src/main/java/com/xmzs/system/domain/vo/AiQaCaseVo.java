package com.xmzs.system.domain.vo;

import com.xmzs.system.domain.AiQaCase;
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
 * 对话案例视图对象 ai_qa_case
 *
 * @author zuleed
 * @date 2024-12-27
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiQaCase.class)
public class AiQaCaseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @ExcelProperty(value = "编号")
    private Long id;

    /**
     * 标题
     */
    @ExcelProperty(value = "标题")
    private String title;

    /**
     * 案例内容
     */
    @ExcelProperty(value = "案例内容")
    private String caseContent;

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 来源文件地址
     */
    @ExcelProperty(value = "来源文件地址")
    private String sourceUrl;

    /**
     * 来源文件名称
     */
    @ExcelProperty(value = "来源文件名称")
    private String sourceName;


    /**
     * 来源文件ID
     */
    private String sourceId;


}
