package com.xmzs.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 判案结果对象 pkulaw_result
 *
 * @author zuleed
 * @date 2025-05-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pkulaw_result")
public class PkulawResult extends BaseEmptyEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Long id;

    @TableField("Gid")
    private String Gid;

    @TableField("PunishmentDate")
    private String PunishmentDate;

    @TableField("LawRegional")
    private String LawRegional;

    @TableField("PunishmentType")
    private String PunishmentType;

    @TableField("AccordingLaw")
    private String AccordingLaw;

    @TableField("reasonText")
    private String reasonText;

    @TableField("aijudgment")
    private String aijudgment;

    @TableField("realjudgment")
    private String realjudgment;

}
