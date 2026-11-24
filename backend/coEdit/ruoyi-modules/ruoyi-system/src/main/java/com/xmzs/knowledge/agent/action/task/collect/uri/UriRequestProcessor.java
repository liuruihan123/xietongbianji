package com.xmzs.knowledge.agent.action.task.collect.uri;

import com.xmzs.knowledge.agent.action.task.TaskProcessor;

/**
 * Uri请求处理器
 */
public interface UriRequestProcessor extends TaskProcessor {

    void request(String url);
}
