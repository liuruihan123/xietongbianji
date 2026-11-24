package com.xmzs.knowledge.domain.bo;

import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import com.xmzs.knowledge.domain.KnowledgeAttach;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库文件业务对象 knowledge_attach
 *
 * @author zhuzi
 * @date 2024-06-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = KnowledgeAttach.class, reverseConvertGenerate = false)
public class KnowledgeAttachBo extends BaseEntity {

    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Integer id;

    /**
     * 知识库ID
     */
    private String kid;

    /**
     * 文档名称
     */
    @NotBlank(message = "文档名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String docName;


    /**
     * 文档地址
     */
    private String docUrl;
    /**
     * 文档内容
     */
    @NotBlank(message = "文档内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String content;


}
