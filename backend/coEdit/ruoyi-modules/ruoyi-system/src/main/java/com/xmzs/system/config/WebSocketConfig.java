package com.xmzs.system.config;

/*
 * 暂时禁用WebSocketConfig以解决编译问题
 *
import cn.hutool.core.util.StrUtil;
import com.xmzs.common.chat.config.properties.WebSocketProperties;
//import com.xmzs.common.chat.handler.PlusWebSocketHandler;
//import com.xmzs.common.chat.interceptor.PlusWebSocketInterceptor;
//import com.xmzs.common.chat.interceptor.PlusWebSocketInterceptor;
//import com.xmzs.common.chat.interceptor.PlusWebSocketInterceptor;
//import com.xmzs.common.chat.listener.WebSocketTopicListener;
//import com.xmzs.common.chat.listener.WebSocketTopicListener;
import com.xmzs.interceptor.PlusWebSocketInterceptor;
import com.xmzs.knowledge.chain.vectorizer.Vectorization;
import com.xmzs.knowledge.chain.vectorstore.VectorStore;
import com.xmzs.knowledge.service.ConversationService;
import com.xmzs.knowledge.service.EmbeddingService;
import com.xmzs.system.handler.MessageHandler;
import com.xmzs.system.handler.PlusWebSocketHandler;
import com.xmzs.system.listener.WebSocketTopicListener;
import com.xmzs.system.service.IChatCostService;
import com.xmzs.system.service.IModelSettingService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * WebSocket 配置
 *
 * @author zendwang
 */
/*
@AutoConfiguration
@ConditionalOnProperty(value = "websocket.enabled", havingValue = "true")
@EnableConfigurationProperties(WebSocketProperties.class)
@EnableWebSocket
public class WebSocketConfig {
*/

/*
    @Bean
    public WebSocketConfigurer webSocketConfigurer(HandshakeInterceptor handshakeInterceptor,
                                                   WebSocketHandler webSocketHandler,
                                                   WebSocketProperties webSocketProperties) {
        if (StrUtil.isBlank(webSocketProperties.getPath())) {
            webSocketProperties.setPath("/websocket");
        }

        if (StrUtil.isBlank(webSocketProperties.getAllowedOrigins())) {
            webSocketProperties.setAllowedOrigins("*");
        }

        return registry -> registry
            .addHandler(webSocketHandler, webSocketProperties.getPath())
            .addInterceptors(handshakeInterceptor)
            .setAllowedOrigins(webSocketProperties.getAllowedOrigins());
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new PlusWebSocketInterceptor();
    }

    @Bean
    public WebSocketHandler webSocketHandler(IModelSettingService modelSettingService,
                                             VectorStore vectorStore,
                                             Vectorization vectorization,
                                             EmbeddingService embeddingService,
                                             ConversationService conversationService,
                                             IChatCostService chatService,
                                             MessageHandler messageHandler) {
        return new PlusWebSocketHandler(modelSettingService, vectorStore, vectorization, embeddingService, conversationService,chatService,messageHandler);
    }

    @Bean
    public WebSocketTopicListener topicListener() {
        return new WebSocketTopicListener();
    }
*/
