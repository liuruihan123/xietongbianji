package com.xmzs.system.domain.bo;

import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.system.domain.ModelSetting;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 功能参数业务对象 model_setting
 *
 * @author zhuzi
 * @date 2024-08-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ModelSetting.class, reverseConvertGenerate = false)
public class ModelSettingBo extends BaseEntity {

    /**
     * id
     */
    @NotNull(message = "id不能为空", groups = { EditGroup.class })
    private Integer id;

    /**
     * 知识库ID
     */
    @NotBlank(message = "知识库ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String kid;

    /**
     * 启用知识库（0启动知识库 1不启用知识库）
     */
    @NotBlank(message = "启用知识库（0启动知识库 1不启用知识库）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String useKnowledge;

    /**
     * 多少字符以上开启知识库，0为不限制
     */
    private Integer shortNoOpen;

    /**
     * 用户问题二次包装模版
     */
    private Long standardTemplateId;

    /**
     * 上下文数量结束后的操作
     */
    private String afterTalkCount;

    /**
     * 播放速度
     */
    private Double ttsSpeed;


    /**
     * 业务类型（0AI面试 1面试例子 2公司背景 3面试要点）
     */
    @NotNull(message = "业务类型（0AI面试 1面试例子 2公司背景 3面试要点）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer busType;

    /**
     * 模型id
     */
    @NotNull(message = "模型id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long modelId;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 角色设定
     */
    private String systemRole;

    /**
     * 上下文数量
     */
    private Integer talkCount;

    /**
     * 语音人物
     */
    @NotBlank(message = "语音人物不能为空", groups = { AddGroup.class, EditGroup.class })
    private String ttsVoice;

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
    @NotBlank(message = "状态（0正常 1停用）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 备注
     */
    private String remark;


}
