package com.xmzs.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmzs.knowledge.domain.AgentField;
import com.xmzs.knowledge.mapper.AgentFieldMapper;
import com.xmzs.knowledge.service.AgentFieldService;
import org.springframework.stereotype.Service;

/**
* @author huangkh
* @description 针对表【agent_field(代理领域)】的数据库操作Service实现
* @createDate 2023-09-23 20:40:24
*/
@Service
public class AgentFieldServiceImpl extends ServiceImpl<AgentFieldMapper, AgentField>
    implements AgentFieldService{

    @Override
    public AgentField getByFid(String fid) {
        QueryWrapper<AgentField> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fid",fid);
        AgentField agentField = this.getOne(queryWrapper,false);
        return agentField;
    }
}




