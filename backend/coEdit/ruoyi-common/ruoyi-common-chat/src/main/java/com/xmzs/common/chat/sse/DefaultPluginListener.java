package com.xmzs.common.chat.sse;

import com.xmzs.common.chat.entity.chat.ChatCompletion;
import com.xmzs.common.chat.openai.OpenAiStreamClient;
import com.xmzs.common.chat.plugin.PluginAbstract;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSourceListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 描述： 插件开发返回信息收集sse监听器
 *
 * @author https:www.unfbx.com
 * 2023-08-18
 */
@Slf4j
public class DefaultPluginListener extends PluginListener {

    public DefaultPluginListener(OpenAiStreamClient client, EventSourceListener eventSourceListener, PluginAbstract plugin, ChatCompletion chatCompletion, SseEmitter sseEmitter) {
        super(client, eventSourceListener, plugin, chatCompletion, sseEmitter);
    }
}
