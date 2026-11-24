package com.xmzs.knowledge.domain.vo;

import com.xmzs.system.domain.SysUser;
import com.xmzs.system.domain.vo.KnowledgeTypeVo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 知识库视图对象 sys_user
 *
 * @author Michelle.Chung
 */
@Data
@AutoMapper(target = SysUser.class)
public class KnowledgeInfoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Long id;

    /**
     * 知识库ID
     */
    private String kid;

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 知识库名称
     */
    private String kname;

    /**
     * 描述
     */
    private String description;

    /**
     * 知识类型id
     */
    private Long typeId;

    /**
     * 知识
     */
    private KnowledgeTypeVo knowledgeType;
}
