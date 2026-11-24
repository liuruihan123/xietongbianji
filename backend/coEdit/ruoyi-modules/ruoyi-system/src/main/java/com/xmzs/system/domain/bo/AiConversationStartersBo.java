package com.xmzs.system.domain.bo;

import com.xmzs.system.domain.AiConversationStarters;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 对话开场业务对象 ai_conversation_starters
 *
 * @author zuleed
 * @date 2024-11-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiConversationStarters.class, reverseConvertGenerate = false)
public class AiConversationStartersBo extends BaseEntity {

    /**
     * id
     */
    @NotNull(message = "id不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 中文标题
     */
    @NotBlank(message = "中文标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String titleCn;

    /**
     * 英文标题
     */
    @NotBlank(message = "英文标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String titleEn;

    /**
     * 启用知识库（0启动知识库 1不启用知识库）
     */
    @NotBlank(message = "启用知识库（0启动知识库 1不启用知识库）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String useKnowledge;

    /**
     * 知识库ID(多选)
     */
//    @NotBlank(message = "知识库ID(多选)不能为空", groups = { AddGroup.class, EditGroup.class })
    private String kidIds;


    /**
     * 启动案例库（0启动知识库 1不启用知识库
     */
    private String useKnowledgeAttach;


    /**
     * 案例库Id(多选)
     */
    private String knowledgeAttachIds;


    /**
     * 是否固定在开头(0不固定 1固定)
     */
    private String templateFixed;


    /**
     * 模版id
     */
    private String templateId;

    /**
     * 业务类型（0AI面试 1面试例子 2公司背景 3面试要点）
     */
    @NotNull(message = "业务类型（0AI面试  2公司背景 3面试要点）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer busType;

    /**
     * 关联标签
     */
//    @NotBlank(message = "关联标签不能为空", groups = { AddGroup.class, EditGroup.class })
    private String tags;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（0正常 1停用）
     */
    @NotBlank(message = "状态（0正常 1停用）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 缺省字段1
     */
    private String val1;

    /**
     * 缺省字段1
     */
    private String val2;


}
