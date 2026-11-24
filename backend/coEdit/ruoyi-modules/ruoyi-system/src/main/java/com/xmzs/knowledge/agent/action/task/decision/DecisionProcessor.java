package com.xmzs.knowledge.agent.action.task.decision;

import com.xmzs.knowledge.agent.action.task.TaskProcessor;

/**
 * 决策处理器
 * @author huangkh
 */
public interface DecisionProcessor extends TaskProcessor {

    String decide(String content);
}
