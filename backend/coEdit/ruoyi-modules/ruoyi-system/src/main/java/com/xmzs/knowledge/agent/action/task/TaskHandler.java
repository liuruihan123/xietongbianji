package com.xmzs.knowledge.agent.action.task;

public interface TaskHandler<String,TaskProcessor> {

    void doHandle(String taskId, com.xmzs.knowledge.agent.action.task.TaskProcessor taskProcessor);
}
