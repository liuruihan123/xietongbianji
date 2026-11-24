package com.xmzs.knowledge.agent.action.task.collect.search;

import com.xmzs.knowledge.agent.action.task.TaskProcessor;

/**
 * 搜索引擎收集处理器接口
 */
public interface SearchCollectProcessor extends TaskProcessor {

    void search(String searchTxt);
}
