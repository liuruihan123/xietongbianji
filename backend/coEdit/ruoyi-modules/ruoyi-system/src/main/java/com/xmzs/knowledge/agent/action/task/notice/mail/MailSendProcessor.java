package com.xmzs.knowledge.agent.action.task.notice.mail;

import com.xmzs.knowledge.agent.action.task.TaskProcessor;

/**
 * 邮件发送处理器
 * @author huangkh
 */
public interface MailSendProcessor extends TaskProcessor {

    void send(String content);
}
