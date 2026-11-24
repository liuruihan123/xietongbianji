package com.xmzs.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 对话开场对象 ai_conversation_starters
 *
 * @author zuleed
 * @date 2024-11-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_conversation_starters")
public class AiConversationStarters extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 中文标题
     */
    private String titleCn;

    /**
     * 英文标题
     */
    private String titleEn;

    /**
     * 启用知识库（0启动知识库 1不启用知识库）
     */
    private String useKnowledge;

    /**
     * 知识库ID(多选)
     */
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
    private Integer busType;

    /**
     * 关联标签
     */
    private String tags;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（0正常 1停用）
     */
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
