package com.xmzs.system.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xmzs.system.domain.PkulawResult;
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
@AutoMapper(target = PkulawResult.class)
public class PkulawResultVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "编号")
    @TableField("id")
    private Long id;

    @ExcelProperty(value = "处罚文书编号")
    @TableField("Gid")
    private String Gid;

    @ExcelProperty(value = "时间")
    @TableField("PunishmentDate")
    private String PunishmentDate;

    @ExcelProperty(value = "地点")
    @TableField("LawRegional")
    private String LawRegional;

    @ExcelProperty(value = "处罚类别")
    @TableField("PunishmentType")
    private String PunishmentType;

    @ExcelProperty(value = "引用的法律")
    @TableField("AccordingLaw")
    private String AccordingLaw;

    @ExcelProperty(value = "具体情节")
    @TableField("reasonText")
    private String reasonText;

    @ExcelProperty(value = "预测的结果")
    @TableField("aijudgment")
    private String aijudgment;

    @ExcelProperty(value = "真实判决结果")
    @TableField("realjudgment")
    private String realjudgment;

}
