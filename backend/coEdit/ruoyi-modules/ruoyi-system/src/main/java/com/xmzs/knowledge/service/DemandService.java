package com.xmzs.knowledge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xmzs.knowledge.agent.prompt.demand.functionObj.DemandRoleFuncObj;
import com.xmzs.knowledge.agent.prompt.demand.functionObj.DemandStepFunObj;
import com.xmzs.knowledge.domain.AgentField;
import com.xmzs.knowledge.domain.Demand;
import com.xmzs.knowledge.request.AgentDemandProposeRequest;
import com.xmzs.system.domain.SysUser;

import java.util.List;

/**
* @author huangkh
* @description 针对表【demand(需求)】的数据库操作Service
* @createDate 2023-09-23 20:42:03
*/
public interface DemandService extends IService<Demand> {

    void propose(SysUser sysUser, AgentDemandProposeRequest request);

    Demand saveDemand(SysUser sysUser, String did, String fid, String content);

    void stepRole(SysUser sysUser, AgentField agentField, Demand demand, List<DemandRoleFuncObj> roles, List<DemandStepFunObj> steps);

}
