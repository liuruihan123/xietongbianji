package com.xmzs.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 知识分类对象 knowledge_type
 *
 * @author zhuzi
 * @date 2024-06-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_type")
public class KnowledgeType extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;


}
