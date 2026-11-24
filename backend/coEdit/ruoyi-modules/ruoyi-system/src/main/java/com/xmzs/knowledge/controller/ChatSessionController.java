package com.xmzs.knowledge.controller;

import com.xmzs.knowledge.common.ResultData;
import com.xmzs.knowledge.common.constant.SysConstants;
import com.xmzs.knowledge.domain.ChatSession;
import com.xmzs.knowledge.request.ChatSessionRemoveRequest;
import com.xmzs.knowledge.request.ChatSessionSaveRequest;
import com.xmzs.knowledge.service.ChatSessionService;
import com.xmzs.system.domain.SysUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会话
 */
@RestController
@AllArgsConstructor
@RequestMapping("chatSession")
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    @PostMapping("/save")
    public ResultData save(HttpServletRequest httpServletRequest, @RequestBody @Valid ChatSessionSaveRequest request) {
        SysUser sysUser = (SysUser) httpServletRequest.getSession().getAttribute(SysConstants.SESSION_LOGIN_USER_KEY);
        chatSessionService.saveChatSession(request,sysUser);
        return ResultData.success("保存成功");
    }

    @GetMapping("/list")
    public ResultData<List<ChatSession>> list(HttpServletRequest httpServletRequest) {
        SysUser sysUser = (SysUser) httpServletRequest.getSession().getAttribute(SysConstants.SESSION_LOGIN_USER_KEY);
        List<ChatSession> chatSessionList = chatSessionService.listByUserId(sysUser.getUserId().intValue());
        return ResultData.success(chatSessionList,"查询成功");
    }

    @PostMapping("/remove")
    public ResultData remove(HttpServletRequest httpServletRequest, @RequestBody @Valid ChatSessionRemoveRequest request) {
        SysUser sysUser = (SysUser) httpServletRequest.getSession().getAttribute(SysConstants.SESSION_LOGIN_USER_KEY);
        chatSessionService.removeBySid(request.getSid(),sysUser);
        return ResultData.success("删除成功");
    }

}

