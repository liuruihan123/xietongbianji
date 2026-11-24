package com.xmzs.knowledge.controller;

import com.xmzs.knowledge.common.ResultData;
import com.xmzs.knowledge.common.constant.SysConstants;
import com.xmzs.knowledge.request.AgentDemandProposeRequest;
import com.xmzs.knowledge.service.DemandService;
import com.xmzs.system.domain.SysUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Agent控制器
 * @author huangkh
 */
@RestController
@AllArgsConstructor
@RequestMapping("agent")
public class AgentController {

    private final DemandService demandService;

    /**
     * 用户的原始需求提出
     * @param httpServletRequest
     * @param request
     * @return
     */
    @PostMapping(value = "/demand/propose")
    public ResultData demandPropose(HttpServletRequest httpServletRequest, AgentDemandProposeRequest request) {
        SysUser sysUser = (SysUser) httpServletRequest.getSession().getAttribute(SysConstants.SESSION_LOGIN_USER_KEY);
        demandService.propose(sysUser,request);
        return ResultData.success("保存成功");
    }


}
