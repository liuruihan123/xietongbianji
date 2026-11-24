package com.xmzs.system.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xmzs.system.domain.PkulawLaw;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.common.excel.annotation.ExcelDictFormat;
import com.xmzs.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = PkulawLaw.class)
public class PkulawLawVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "")
    @TableField("id")
    private Long id;

    @ExcelProperty(value = "法规目录")
    @TableField("library")
    private String library;

    @ExcelProperty(value = "Gid")
    @TableField("Gid")
    private String Gid;

    @ExcelProperty(value = "标题")
    @TableField("Title")
    private String Title;

    @ExcelProperty(value = "发布部门")
    @TableField("IssueDepartment")
    private String IssueDepartment;

    @ExcelProperty(value = "批准部门")
    @TableField("RatifyDepartment")
    private String RatifyDepartment;

    @ExcelProperty(value = "发文字号")
    @TableField("DocumentNO")
    private String DocumentNO;

    @ExcelProperty(value = "批准日期")
    @TableField("RatifyDate")
    private String RatifyDate;

    @ExcelProperty(value = "发布日期")
    @TableField("IssueDate")
    private String IssueDate;

    @ExcelProperty(value = "实施日期")
    @TableField("ImplementDate")
    private String ImplementDate;

    @ExcelProperty(value = "时效性")
    @TableField("TimelinessDic")
    private String TimelinessDic;

    @ExcelProperty(value = "失效依据")
    @TableField("InvalidBasis")
    private String InvalidBasis;

    @ExcelProperty(value = "效力级别")
    @TableField("EffectivenessDic")
    private String EffectivenessDic;

    @ExcelProperty(value = "法规类别")
    @TableField("Category")
    private String Category;

    @ExcelProperty(value = "关键词")
    @TableField("Keywords")
    private String Keywords;

    @ExcelProperty(value = "专题分类")
    @TableField("TopicType")
    private String TopicType;

    @ExcelProperty(value = "正文")
    @TableField("AllText")
    private String AllText;

}
