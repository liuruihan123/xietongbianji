package com.xmzs.knowledge.service;


import com.xmzs.knowledge.chain.llm.capabilities.generation.function.ChatFunctionObject;
import com.xmzs.knowledge.request.*;
import com.xmzs.system.domain.SysUser;

import java.util.List;

public interface CompletionService {
    String summary(SysUser sysUser, CompletionSummaryRequest request);

    String keyword(SysUser sysUser, CompletionKeywordRequest request);

    String translate(SysUser sysUser, CompletionTranslateRequest request);

    String classic(SysUser sysUser, CompletionClassicRequest request);

    String security(SysUser sysUser, CompletionSecurityRequest request);

    String function(SysUser sysUser, String content, List<ChatFunctionObject> functionObjectList);

    String functionWeather(SysUser sysUser, CompletionFunctionWeatherRequest request);

}
