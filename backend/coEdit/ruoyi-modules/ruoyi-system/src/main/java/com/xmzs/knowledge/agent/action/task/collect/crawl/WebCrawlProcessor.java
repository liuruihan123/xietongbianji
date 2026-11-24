package com.xmzs.knowledge.agent.action.task.collect.crawl;

import com.xmzs.knowledge.agent.action.task.TaskProcessor;

/**
 * 爬虫处理器
 * @author huangkh
 */
public interface WebCrawlProcessor extends TaskProcessor {

    void crawl(String target);

}
