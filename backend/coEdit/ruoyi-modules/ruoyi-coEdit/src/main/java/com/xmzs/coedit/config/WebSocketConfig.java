package com.xmzs.coedit.config;

import com.xmzs.coedit.websocket.CollaborationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类
 * 用于协同编辑的实时通信，支持移动端和桌面端
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {

    private final CollaborationWebSocketHandler collaborationWebSocketHandler;
    private final WebSocketExceptionHandler webSocketExceptionHandler;



    /**
     * 配置消息代理
     */
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        // 启用简单消息代理，用于向客户端发送消息
//        // /topic - 用于广播消息（一对多）
//        // /queue - 用于点对点消息（一对一）
//        registry.enableSimpleBroker("/topic", "/queue")
//                // 设置心跳间隔（毫秒）
//                .setHeartbeatValue(new long[]{10000, 10000});
//
//        // 设置应用程序目的地前缀，客户端发送消息时需要加上此前缀
//        registry.setApplicationDestinationPrefixes("/app");
//
//        // 设置用户目的地前缀，用于点对点消息
//        registry.setUserDestinationPrefix("/user");
//    }

    /**
     * 配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用简单消息代理
        registry.enableSimpleBroker("/topic", "/queue")
            // 设置心跳间隔
            .setHeartbeatValue(new long[]{10000, 10000})

            // 3. (这是关键)
            // 直接调用本类中的 taskScheduler() @Bean 方法
            // Spring 会拦截这个调用并返回正确的单例 Bean
            .setTaskScheduler(taskScheduler());

        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }


    /**
     * 注册STOMP端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册WebSocket端点，客户端通过此端点连接到WebSocket服务器
        registry.addEndpoint("/ws")
                // 允许所有来源访问（开发和生产环境通用）
                // 使用 setAllowedOriginPatterns 而不是 setAllowedOrigins
                // 从 Spring 5.3.2 开始，setAllowedOriginPatterns 支持 "*" 通配符
                .setAllowedOriginPatterns("*")
                // 启用SockJS支持（用于不支持WebSocket的浏览器和移动端）
                .withSockJS()
                // 设置SockJS客户端库URL（可选，用于离线环境）
                // .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js")
                // 设置会话超时时间（毫秒）
                .setSessionCookieNeeded(false)
                // 设置心跳时间（毫秒）
                .setHeartbeatTime(25000)
                // 禁用不稳定的传输方式,只使用 WebSocket 和 XHR
                // 这可以减少 EventSource 导致的连接中断错误
                .setTransportHandlers(new org.springframework.web.socket.sockjs.transport.handler.WebSocketTransportHandler(new org.springframework.web.socket.server.support.DefaultHandshakeHandler()),
                                     new org.springframework.web.socket.sockjs.transport.handler.XhrPollingTransportHandler(),
                                     new org.springframework.web.socket.sockjs.transport.handler.XhrStreamingTransportHandler())
                // 设置断开连接的回调,用于清理资源
                .setDisconnectDelay(5000L); // 5秒后清理断开的连接
    }

    /**
     * 配置WebSocket传输
     * 优化移动端性能
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        // 设置消息大小限制（字节）
        registry.setMessageSizeLimit(128 * 1024); // 128KB

        // 设置发送缓冲区大小限制（字节）
        registry.setSendBufferSizeLimit(512 * 1024); // 512KB

        // 设置发送超时时间（毫秒）
        registry.setSendTimeLimit(20000); // 20秒

        // 设置时间限制（毫秒）
        registry.setTimeToFirstMessage(30000); // 30秒

        // 设置装饰器工厂,用于处理异常
        registry.setDecoratorFactories(webSocketHandler -> webSocketHandler);
    }

    /**
     * 配置错误处理器
     */
    @Override
    public boolean configureMessageConverters(java.util.List<org.springframework.messaging.converter.MessageConverter> messageConverters) {
        return true;
    }

    /**
     * 配置TaskScheduler Bean
     * 用于处理WebSocket心跳任务，解决SimpleBrokerMessageHandler启动失败问题
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("websocket-heartbeat-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.initialize();
        return scheduler;
    }

    /**
     * 注册WebSocket处理器（用于实时协作）
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册协作WebSocket处理器
        registry.addHandler(collaborationWebSocketHandler, "/ws/collaboration")
                .setAllowedOriginPatterns("*") // 在生产环境中应该限制为特定域名
                .withSockJS()
                // 同样禁用不稳定的传输方式
                .setTransportHandlers(new org.springframework.web.socket.sockjs.transport.handler.WebSocketTransportHandler(new org.springframework.web.socket.server.support.DefaultHandshakeHandler()),
                                     new org.springframework.web.socket.sockjs.transport.handler.XhrPollingTransportHandler(),
                                     new org.springframework.web.socket.sockjs.transport.handler.XhrStreamingTransportHandler())
                .setDisconnectDelay(5000L);
    }

}

