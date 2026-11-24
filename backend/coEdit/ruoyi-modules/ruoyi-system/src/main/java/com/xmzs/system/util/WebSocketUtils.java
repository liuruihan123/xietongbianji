package com.xmzs.system.util;

import cn.hutool.core.collection.CollUtil;
import com.xmzs.common.chat.entity.dto.WebSocketMessageDto;
//import com.xmzs.common.chat.holder.WebSocketSessionHolder;
import com.xmzs.common.core.constant.ChatStatus;
import com.xmzs.common.redis.utils.RedisUtils;
import com.xmzs.system.handler.WebSocketSessionHolder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.xmzs.common.chat.constant.WebSocketConstants.WEB_SOCKET_TOPIC;

/**
 * 工具类
 *
 * @author zendwang
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebSocketUtils {

    /**
     * 发送消息
     *
     * @param sessionKey session主键 一般为用户id
     * @param message    消息文本
     */
    public static void sendMessage(Long sessionKey, String message) {
        WebSocketSession session = WebSocketSessionHolder.getSessions(sessionKey);
        sendMessage(session, message);
    }

    /**
     * 订阅消息
     *
     * @param consumer 自定义处理
     */
    public static void subscribeMessage(Consumer<WebSocketMessageDto> consumer) {
        RedisUtils.subscribe(WEB_SOCKET_TOPIC, WebSocketMessageDto.class, consumer);
    }

    /**
     * 发布订阅的消息
     *
     * @param webSocketMessage 消息对象
     */
    public static void publishMessage(WebSocketMessageDto webSocketMessage) {
        List<Long> unsentSessionKeys = new ArrayList<>();
        // 当前服务内session,直接发送消息
        for (Long sessionKey : webSocketMessage.getSessionKeys()) {
            if (WebSocketSessionHolder.existSession(sessionKey)) {
                WebSocketUtils.sendMessage(sessionKey, webSocketMessage.getMessage());
                continue;
            }
            unsentSessionKeys.add(sessionKey);
        }
        // 不在当前服务内session,发布订阅消息
        if (CollUtil.isNotEmpty(unsentSessionKeys)) {
            WebSocketMessageDto broadcastMessage = new WebSocketMessageDto();
            broadcastMessage.setMessage(webSocketMessage.getMessage());
            broadcastMessage.setSessionKeys(unsentSessionKeys);
            RedisUtils.publish(WEB_SOCKET_TOPIC, broadcastMessage, consumer -> {
                log.info(" WebSocket发送主题订阅消息topic:{} session keys:{} message:{}",
                    WEB_SOCKET_TOPIC, unsentSessionKeys, webSocketMessage.getMessage());
            });
        }
    }

    public static void sendPongMessage(WebSocketSession session) {
        sendMessage(session, new PongMessage());
    }

    /**
     * 发送对话消息
     * @param session
     * @param message
     */
    public static void sendMessage(WebSocketSession session, String message) {
        sendMessage(session, new TextMessage(message));
    }
    /**
     * 发送错误消息--统一标准
     *
     * @param errorMessage session主键 一般为用户id
     * @param session    消息文本
     */
    public static void sendErrorMessages(String errorCode,String errorMessage, WebSocketSession session) {
        // 构建错误消息的 JSON 格式
//        String messageFormat =
//            " {\n" +
//            "   \"content\":\"%s\"\n" +
//            " }";
//        String codeMessage = String.format(messageFormat, errorCode);
        sendMessage(session, errorCode+errorMessage);

//        String firstMessage = String.format(messageFormat, errorMessage);
//        sendMessage(session, errorMessage);

//        String doneMessage = String.format("[DONE]");
//        sendMessage(session, "[DONE]");
    }

    private static void sendMessage(WebSocketSession session, WebSocketMessage<?> message) {
        if (session == null || !session.isOpen()) {
            log.error("[send] session会话已经关闭");
            sendErrorMessages(ChatStatus.WARN,"会话已经关闭，请重新打开",session);
        } else {
            try {
                // 获取当前会话中的用户
                session.sendMessage(message);
            } catch (IOException e) {
                log.error("[send] session({}) 发送消息({}) 异常", session, message, e);
                sendErrorMessages(ChatStatus.WARN," 发送消息 异常",session);
            }
        }
    }
}
