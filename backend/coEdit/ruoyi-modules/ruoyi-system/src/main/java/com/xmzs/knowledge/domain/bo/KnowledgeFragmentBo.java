package com.xmzs.knowledge.domain.bo;

import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import com.xmzs.knowledge.domain.KnowledgeFragment;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识片段业务对象 knowledge_fragment
 *
 * @author zhuzi
 * @date 2024-06-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = KnowledgeFragment.class, reverseConvertGenerate = false)
public class KnowledgeFragmentBo extends BaseEntity {

    /**
     *
     */
    private Integer id;

    /**
     * 知识库ID
     */
    private String kid;

    /**
     * 片段索引下标
     */
    @NotNull(message = "片段索引下标不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long idx;

    /**
     * 文档内容
     */
    @NotBlank(message = "文档内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String content;


    /**
     * 文档ID
     */
    private String docId;
}
