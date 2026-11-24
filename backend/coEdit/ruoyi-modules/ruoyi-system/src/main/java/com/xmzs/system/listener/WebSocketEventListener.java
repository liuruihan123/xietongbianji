package com.xmzs.system.listener;

import cn.hutool.cache.impl.CacheObj;
import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmzs.common.chat.config.LocalCache;
import com.xmzs.common.chat.constant.OpenAIConst;
import com.xmzs.common.chat.domain.request.ChatRequest;
import com.xmzs.common.chat.entity.chat.ChatCompletionResponse;
import com.xmzs.common.chat.utils.TikTokensUtil;
import com.xmzs.common.core.utils.SpringUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.system.domain.bo.ChatMessageBo;
import com.xmzs.system.domain.bo.SysModelBo;
import com.xmzs.system.domain.vo.SysModelVo;
import com.xmzs.system.service.IChatCostService;
import com.xmzs.system.service.IChatMessageService;
import com.xmzs.system.service.ISysModelService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Objects;

/**
 * 描述：OpenAI流式输出Socket接收
 *
 * @author https:www.unfbx.com
 * @date 2023-03-23
 */
@Slf4j
@RequiredArgsConstructor
@Component("openAiWebSocketEventListener")
public class WebSocketEventListener extends EventSourceListener {

    private WebSocketSession session;
    private StringBuilder stringBuffer = new StringBuilder();

    private ChatRequest chatRequest;

    /**
     * 消息结束标识
     */
    private final String msgEnd = "[DONE]";
    @Autowired(required = false)
    public WebSocketEventListener(WebSocketSession session,ChatRequest chatRequest) {
        this.chatRequest=chatRequest;
        this.session = session;
    }
    private static final ISysModelService sysModelService = SpringUtils.getBean(ISysModelService.class);
    private String modelName;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("OpenAI建立新Socket连接...");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
//        log.info("websocket--OpenAI返回数据：{}", data);
//        if (data.equals(msgEnd)) {
//            log.info("OpenAI返回数据结束了");
//            session.sendMessage(new TextMessage(msgEnd));
//            return;
//        }



        try {
            if (data.equals(msgEnd)) {
                //成功响应
                if(StringUtils.isNotEmpty(modelName)){
                    IChatCostService chatCostService = SpringUtils.context().getBean(IChatCostService.class);
                    IChatMessageService chatMessageService = SpringUtils.context().getBean(IChatMessageService.class);
                    ChatMessageBo chatMessageBo = new ChatMessageBo();
                    chatMessageBo.setModelName(modelName);
                    chatMessageBo.setContent(stringBuffer.toString());
                    Long userId = (Long) LocalCache.CACHE.get("userId");

                    chatMessageBo.setUserId(userId);
                    chatMessageBo.setMsgId(chatRequest.getUuid());

                    //查询按次数扣费的模型
                    SysModelBo sysModelBo = new SysModelBo();
                    sysModelBo.setModelType("2");
                    sysModelBo.setModelName(modelName);
                    List<SysModelVo> sysModelList = sysModelService.queryList(sysModelBo);
                    if (CollectionUtil.isNotEmpty(sysModelList)){
                        chatMessageBo.setDeductCost(0d);
                        chatMessageBo.setRemark("提问时扣费");
                        // 保存消息记录
                        chatMessageService.insertByBo(chatMessageBo);
                    }else{
                        int tokens = TikTokensUtil.tokens(modelName,stringBuffer.toString());
                        chatMessageBo.setTotalTokens(tokens);
                        // 按token扣费并且保存消息记录
                        chatCostService.deductToken(chatMessageBo);
                    }
                }
                session.sendMessage(new TextMessage(msgEnd));
                return;
            }
            ObjectMapper mapper = new ObjectMapper();
            // 读取Json
            ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class);
            String delta = mapper.writeValueAsString(completionResponse.getChoices().get(0).getDelta());
            session.sendMessage(new TextMessage(delta));

            // 解析返回内容
//            ObjectMapper mapper = new ObjectMapper();
//            ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class);
//            if(completionResponse == null || CollectionUtil.isEmpty(completionResponse.getChoices())){
//                return;
//            }

//           读取Json
//        String delta = mapper.writeValueAsString(completionResponse.getChoices().get(0).getDelta());
//        session.sendMessage(new TextMessage(delta));

            String content = completionResponse.getChoices().get(0).getDelta().getContent();
            if(StringUtils.isEmpty(content)){
                return;
            }
            if(StringUtils.isEmpty(modelName)){
                modelName = completionResponse.getModel();
            }
            stringBuffer.append(content);
////            emitter.send(data);
//            session.sendMessage(new TextMessage(stringBuffer));
        } catch (Exception e) {
            log.error("websocket信息推送失败{}内容：{}",e.getMessage(),stringBuffer);
            eventSource.cancel();
        }
    }


    @Override
    public void onClosed(EventSource eventSource) {
        log.info("OpenAI关闭Socket连接...");
    }


    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (Objects.isNull(response)) {
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            // 返回非流式回复内容
            if(response.code() == OpenAIConst.SUCCEED_CODE){
                ObjectMapper mapper = new ObjectMapper();
                ChatCompletionResponse completionResponse = mapper.readValue(body.string(), ChatCompletionResponse.class);
                String delta = mapper.writeValueAsString(completionResponse.getChoices().get(0).getMessage().getContent());
                session.sendMessage(new TextMessage(delta));
            }else {
                log.error("Socket连接异常data：{}，异常：{}", body.string(), t);
            }
        } else {
            log.error("Socket连接异常data：{}，异常：{}", response, t);
        }
        eventSource.cancel();
    }
}
