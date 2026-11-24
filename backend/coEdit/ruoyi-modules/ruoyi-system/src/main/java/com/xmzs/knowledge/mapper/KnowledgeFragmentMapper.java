package com.xmzs.knowledge.mapper;

import com.xmzs.common.mybatis.core.mapper.BaseMapperPlus;
import com.xmzs.knowledge.domain.KnowledgeFragment;
import com.xmzs.knowledge.domain.vo.KnowledgeFragmentVo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author huangkh
* @description 针对表【knowledge_fragment(知识片段)】的数据库操作Mapper
* @createDate 2023-12-14 16:43:31
* @Entity com.xmzs.knowledge.domain.KnowledgeFragment
*/
@Mapper
//public interface KnowledgeFragmentMapper extends BaseMapper<KnowledgeFragment> {
public interface KnowledgeFragmentMapper extends BaseMapperPlus<KnowledgeFragment, KnowledgeFragmentVo> {

}




