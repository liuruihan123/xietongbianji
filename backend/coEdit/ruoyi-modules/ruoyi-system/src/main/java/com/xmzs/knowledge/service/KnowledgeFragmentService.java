package com.xmzs.knowledge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.knowledge.domain.KnowledgeFragment;
import com.xmzs.knowledge.domain.bo.KnowledgeFragmentBo;
import com.xmzs.knowledge.domain.vo.KnowledgeFragmentVo;
import com.xmzs.knowledge.request.KnowledgeFragmentPageRequest;
import com.xmzs.knowledge.request.KnowledgeFragmentRemoveRequest;
import com.xmzs.knowledge.request.KnowledgeFragmentSaveRequest;

import java.util.Collection;
import java.util.List;

/**
* @author huangkh
* @description 针对表【knowledge_fragment(知识片段)】的数据库操作Service
* @createDate 2023-12-14 16:43:31
*/
public interface KnowledgeFragmentService extends IService<KnowledgeFragment> {

    PageInfo<KnowledgeFragment> pageInfo(KnowledgeFragmentPageRequest knowledgeFragmentPageRequest);

    void removeFragment(KnowledgeFragmentRemoveRequest request);

    void saveFragment(KnowledgeFragmentSaveRequest request);


    /**
     * 查询知识片段
     */
    KnowledgeFragmentVo queryById(Long id);

    /**
     * 查询知识片段列表
     */
    TableDataInfo<KnowledgeFragmentVo> queryPageList(KnowledgeFragmentBo bo, PageQuery pageQuery);

    /**
     * 查询知识片段列表
     */
    List<KnowledgeFragmentVo> queryList(KnowledgeFragmentBo bo);

    /**
     * 新增知识片段
     */
    Boolean insertByBo(KnowledgeFragmentBo bo);

    /**
     * 修改知识片段
     */
    Boolean updateByBo(KnowledgeFragmentBo bo);

    /**
     * 校验并批量删除知识片段信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
