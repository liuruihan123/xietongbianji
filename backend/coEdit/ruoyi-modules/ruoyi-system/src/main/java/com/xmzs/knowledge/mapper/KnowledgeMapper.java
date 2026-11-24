package com.xmzs.knowledge.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.common.mybatis.core.mapper.BaseMapperPlus;
import com.xmzs.knowledge.domain.Knowledge;
import com.xmzs.knowledge.domain.vo.KnowledgeInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author huangkh
* @description 针对表【knowledge(知识库)】的数据库操作Mapper
* @createDate 2023-06-20 21:01:33
* @Entity com.hkh.openai.domain.Knowledge
*/
@Mapper
public interface KnowledgeMapper extends BaseMapperPlus<Knowledge,KnowledgeInfoVo> {

    Page<KnowledgeInfoVo> selectPageKnowledgeList(@Param("page") Page<Knowledge> page, @Param(Constants.WRAPPER) Wrapper<Knowledge> queryWrapper);

}




