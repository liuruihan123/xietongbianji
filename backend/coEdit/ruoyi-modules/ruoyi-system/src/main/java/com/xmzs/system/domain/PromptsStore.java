package com.xmzs.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 提示词商店对象 prompts_store
 *
 * @author zuleed
 * @date 2024-10-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prompts_store")
public class PromptsStore extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 提示词名称
     */
    private String name;

    /**
     * 模型id
     */
    private String modelId;

    /**
     * 角色设定
     */
    private String systemRole;

    /**
     * 用户要求
     */
    private String userPrompts;

    /**
     * 上下文数量
     */
    private Integer talkCount;

    /**
     * 随机性
     */
    private Double temperature;

    /**
     * 核采样
     */
    private Double topP;

    /**
     * 单次回复限制
     */
    private Integer maxTokens;

    /**
     * 话题新鲜度
     */
    private Double presencePenalty;

    /**
     * 频率惩罚度
     */
    private Double frequencyPenalty;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;


}
