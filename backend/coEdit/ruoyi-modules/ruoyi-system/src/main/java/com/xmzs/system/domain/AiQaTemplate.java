package com.xmzs.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 问答模版对象 ai_qa_template
 *
 * @author zuleed
 * @date 2024-12-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_qa_template")
public class AiQaTemplate extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 模版名称
     */
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
    private String templateContent;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（1有效、2无效）
     */
    private String status;


}
