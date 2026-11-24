package com.xmzs.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 对话案例对象 ai_qa_case
 *
 * @author zuleed
 * @date 2024-12-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_qa_case")
public class AiQaCase extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 案例内容
     */
    private String caseContent;

    /**
     * 状态（0正常 1停用）
     */
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
