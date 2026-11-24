package com.xmzs.coedit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

/**
 * WebSocket 异常处理器
 * 用于优雅地处理 WebSocket 连接异常,减少错误日志噪音
 *
 * @author zuleed
 * @date 2025-11-20
 */
@Slf4j
@Component
public class WebSocketExceptionHandler extends StompSubProtocolErrorHandler {

    /**
     * 处理客户端消息异常
     */
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        // 判断是否为连接中断异常
        if (isConnectionAbortedException(ex)) {
            // 只记录 debug 级别日志,不记录错误
            log.debug("WebSocket 连接已断开: {}", ex.getMessage());
            return null;
        }

        // 其他异常正常处理
        log.error("WebSocket 消息处理异常", ex);
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    /**
     * 处理错误消息
     */
    @Override
    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor,
                                             byte[] errorPayload,
                                             Throwable cause,
                                             StompHeaderAccessor clientHeaderAccessor) {
        // 判断是否为连接中断异常
        if (isConnectionAbortedException(cause)) {
            log.debug("WebSocket 连接已断开: {}", cause.getMessage());
            return null;
        }

        return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
    }

    /**
     * 判断是否为连接中断异常
     */
    private boolean isConnectionAbortedException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        String message = ex.getMessage();
        if (message == null) {
            return false;
        }

        // 检查是否包含连接中断的关键字
        return message.contains("你的主机中的软件中止了一个已建立的连接")
            || message.contains("Connection reset")
            || message.contains("Broken pipe")
            || message.contains("Connection aborted")
            || message.contains("远程主机强迫关闭了一个现有的连接")
            || ex instanceof java.io.IOException;
    }
}

