package com.xmzs.web.controller.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.annotation.Resource;

/**
 * WebSocket测试控制器
 * 
 * @author xmzs
 */
@Slf4j
@Controller
public class WebSocketTestController {

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 处理来自客户端的消息
     */
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) {
        log.info("收到WebSocket消息: {}", message);
        return "Hello, " + message + "!";
    }

    /**
     * 测试WebSocket连接状态
     */
    @GetMapping("/websocket/test")
    @ResponseBody
    public String testWebSocket() {
        try {
            // 发送测试消息
            messagingTemplate.convertAndSend("/topic/test", "WebSocket连接正常");
            return "WebSocket测试成功";
        } catch (Exception e) {
            log.error("WebSocket测试失败", e);
            return "WebSocket测试失败: " + e.getMessage();
        }
    }
}
