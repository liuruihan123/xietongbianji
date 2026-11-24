package com.xmzs.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 功能参数对象 model_setting
 *
 * @author zhuzi
 * @date 2024-08-27
 */
@Data
@TableName("model_setting")
public class ModelSetting implements Serializable {

//    @Serial
//    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 知识库ID
     */
    private String kid;

    /**
     * 启用知识库（0启动知识库 1不启用知识库）
     */
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
    private Integer busType;

    /**
     * 模型id
     */
    private Long modelId;

    /**
     * 名称
     */
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
    private String status;

    /**
     * 备注
     */
    private String remark;


}
