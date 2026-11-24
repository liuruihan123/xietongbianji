package com.xmzs.system.domain.bo;

import com.xmzs.system.domain.AiQaCase;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 对话案例业务对象 ai_qa_case
 *
 * @author zuleed
 * @date 2024-12-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiQaCase.class, reverseConvertGenerate = false)
public class AiQaCaseBo extends BaseEntity {

    /**
     * 编号
     */
    @NotNull(message = "编号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;

    /**
     * 案例内容
     */
    @NotBlank(message = "案例内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String caseContent;

    /**
     * 状态（0正常 1停用）
     */
    @NotBlank(message = "状态（0正常 1停用）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 来源文件地址
     */
    private String sourceUrl;

    /**
     * 来源文件名称
     */
    private String sourceName;


    /**
     * 来源文件ID
     */
    private String sourceId;


}
