package com.xmzs.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xmzs.knowledge.domain.AgentField;
import org.apache.ibatis.annotations.Mapper;

/**
* @author huangkh
* @description 针对表【agent_field(代理领域)】的数据库操作Mapper
* @createDate 2023-09-23 20:40:24
* @Entity com.xmzs.knowledge.domain.AgentField
*/
@Mapper
public interface AgentFieldMapper extends BaseMapper<AgentField> {

}




