package com.xmzs.knowledge.domain.bo;

import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import com.xmzs.knowledge.domain.Knowledge;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库业务对象 knowledge_info
 *
 * @author Lion Li
 * @date 2024-06-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Knowledge.class, reverseConvertGenerate = false)
public class KnowledgeInfoBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 知识库ID
     */
    @NotBlank(message = "知识库ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String kid;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long uid;

    /**
     * 知识库名称
     */
    @NotBlank(message = "知识库名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String kname;

    /**
     * 描述
     */
    @NotBlank(message = "描述不能为空", groups = { AddGroup.class, EditGroup.class })
    private String description;

    /**
     * 知识类型id
     */
    @NotNull(message = "知识类型id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long typeId;


}
