package com.xmzs.knowledge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.knowledge.domain.KnowledgeAttach;
import com.xmzs.knowledge.domain.bo.KnowledgeAttachBo;
import com.xmzs.knowledge.domain.vo.KnowledgeAttachVo;
import com.xmzs.knowledge.request.KnowledgeAttachPageRequest;

import java.util.Collection;
import java.util.List;

/**
 * 知识库文件Service接口
 *
 * @author zhuzi
 * @date 2024-06-27
 */
public interface IKnowledgeAttachService extends IService<KnowledgeAttach> {


    PageInfo<KnowledgeAttach> pageInfo(KnowledgeAttachPageRequest knowledgeAttachPageRequest);


    /**
     * 查询知识库文件
     */
    KnowledgeAttachVo queryById(Long id);

    /**
     * 查询知识库文件列表
     */
    TableDataInfo<KnowledgeAttachVo> queryPageList(KnowledgeAttachBo bo, PageQuery pageQuery);

    /**
     * 查询知识库文件列表
     */
    List<KnowledgeAttachVo> queryList(KnowledgeAttachBo bo);

    /**
     * 新增知识库文件
     */
    Boolean insertByBo(KnowledgeAttachBo bo);

    /**
     * 修改知识库文件
     */
    Boolean updateByBo(KnowledgeAttachBo bo);

    /**
     * 校验并批量删除知识库文件信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
