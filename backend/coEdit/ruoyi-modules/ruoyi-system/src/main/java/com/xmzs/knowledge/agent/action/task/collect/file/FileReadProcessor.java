package com.xmzs.knowledge.agent.action.task.collect.file;

import com.xmzs.knowledge.agent.action.task.TaskProcessor;

/**
 * 文件读取处理器
 */
public interface FileReadProcessor extends TaskProcessor {

    void read(String filePath);
}
