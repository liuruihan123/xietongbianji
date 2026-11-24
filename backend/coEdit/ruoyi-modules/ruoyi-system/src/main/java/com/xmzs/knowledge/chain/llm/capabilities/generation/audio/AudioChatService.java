package com.xmzs.knowledge.chain.llm.capabilities.generation.audio;

import com.xmzs.knowledge.domain.Conversation;
import com.xmzs.knowledge.domain.CustomChatMessage;
import com.xmzs.system.domain.SysUser;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * 语音聊天服务
 * @author huangkh
 */
public interface AudioChatService {
    String audioToText(File audio, String prompt);

    void audioChat(CustomChatMessage customChatMessage, List<String> nearestList, List<Conversation> history, SseEmitter sseEmitter, SysUser sysUser, String mediaId);

    InputStream createSpeech(String content);
}
