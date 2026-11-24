package com.xmzs.system.service;

import com.xmzs.system.domain.KnowledgeType;
import com.xmzs.system.domain.vo.KnowledgeTypeVo;
import com.xmzs.system.domain.bo.KnowledgeTypeBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 知识分类Service接口
 *
 * @author zhuzi
 * @date 2024-06-22
 */
public interface IKnowledgeTypeService {

    /**
     * 查询知识分类
     */
    KnowledgeTypeVo queryById(Long id);

    /**
     * 查询知识分类列表
     */
    TableDataInfo<KnowledgeTypeVo> queryPageList(KnowledgeTypeBo bo, PageQuery pageQuery);

    /**
     * 查询知识分类列表
     */
    List<KnowledgeTypeVo> queryList(KnowledgeTypeBo bo);

    /**
     * 新增知识分类
     */
    Boolean insertByBo(KnowledgeTypeBo bo);

    /**
     * 修改知识分类
     */
    Boolean updateByBo(KnowledgeTypeBo bo);

    /**
     * 校验并批量删除知识分类信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
