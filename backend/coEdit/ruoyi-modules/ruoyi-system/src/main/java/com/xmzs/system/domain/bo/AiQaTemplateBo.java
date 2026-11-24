package com.xmzs.system.domain.bo;

import com.xmzs.system.domain.AiQaTemplate;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 问答模版业务对象 ai_qa_template
 *
 * @author zuleed
 * @date 2024-12-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiQaTemplate.class, reverseConvertGenerate = false)
public class AiQaTemplateBo extends BaseEntity {

    /**
     * 编号
     */
    @NotNull(message = "编号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 模版名称
     */
    @NotBlank(message = "模版名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 模版描述
     */
    private String temDescription;

    /**
     * 角色类型（system/user）
     */
    private String roleType;
    /**
     * 模版内容
     */
    @NotBlank(message = "模版内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String templateContent;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;

    /**
     * 状态（1有效、2无效）
     */
    @NotBlank(message = "状态（1有效、2无效）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;


}
