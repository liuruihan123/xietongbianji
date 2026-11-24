package com.xmzs.system.domain.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xmzs.system.domain.BaseEmptyEntity;
import com.xmzs.system.domain.PkulawResult;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 判案结果业务对象 pkulaw_result
 *
 * @author zuleed
 * @date 2025-05-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PkulawResult.class, reverseConvertGenerate = false)
public class PkulawResultBo extends BaseEmptyEntity {

    @NotNull(message = "编号不能为空", groups = { EditGroup.class })
    @TableField("id")
    private Long id;

    @NotBlank(message = "处罚文书编号不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("Gid")
    private String Gid;

    @NotBlank(message = "时间不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("PunishmentDate")
    private String PunishmentDate;

    @NotBlank(message = "地点不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("LawRegional")
    private String LawRegional;

    @NotBlank(message = "处罚类别不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("PunishmentType")
    private String PunishmentType;

    @NotBlank(message = "引用的法律不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("AccordingLaw")
    private String AccordingLaw;

    @NotBlank(message = "具体情节不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("reasonText")
    private String reasonText;

    @NotBlank(message = "预测的结果不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("aijudgment")
    private String aijudgment;

    @NotBlank(message = "真实判决结果不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("realjudgment")
    private String realjudgment;

}
