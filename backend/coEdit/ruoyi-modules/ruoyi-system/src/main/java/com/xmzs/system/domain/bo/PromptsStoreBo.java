package com.xmzs.system.domain.bo;

import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.system.domain.PromptsStore;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 提示词商店业务对象 prompts_store
 *
 * @author zuleed
 * @date 2024-10-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PromptsStore.class, reverseConvertGenerate = false)
public class PromptsStoreBo extends BaseEntity {

    /**
     * id
     */
    @NotNull(message = "id不能为空", groups = { EditGroup.class })
    private Integer id;

    /**
     * 提示词名称
     */
    @NotBlank(message = "提示词名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 模型id
     */
    @NotNull(message = "模型id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelId;

    /**
     * 角色设定
     */
    @NotBlank(message = "角色设定不能为空", groups = { AddGroup.class, EditGroup.class })
    private String systemRole;

    /**
     * 用户要求
     */
    private String userPrompts;

    /**
     * 上下文数量
     */
    @NotNull(message = "模型类型", groups = { AddGroup.class, EditGroup.class })
    private Integer talkCount;

    /**
     * 随机性
     */
    @NotNull(message = "随机性不能为空", groups = { AddGroup.class, EditGroup.class })
    private Double temperature;

    /**
     * 核采样
     */
    @NotNull(message = "核采样不能为空", groups = { AddGroup.class, EditGroup.class })
    private Double topP;

    /**
     * 单次回复限制
     */
    @NotNull(message = "单次回复限制不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer maxTokens;

    /**
     * 话题新鲜度
     */
    @NotNull(message = "话题新鲜度不能为空", groups = { AddGroup.class, EditGroup.class })
    private Double presencePenalty;

    /**
     * 频率惩罚度
     */
    @NotNull(message = "频率惩罚度不能为空", groups = { AddGroup.class, EditGroup.class })
    private Double frequencyPenalty;

    /**
     * 状态（0正常 1停用）
     */
    @NotBlank(message = "状态（0正常 1停用）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 备注
     */
    private String remark;


}
