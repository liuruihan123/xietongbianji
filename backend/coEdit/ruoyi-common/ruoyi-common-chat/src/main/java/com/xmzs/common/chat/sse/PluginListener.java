package com.xmzs.common.chat.sse;

import cn.hutool.json.JSONUtil;
import com.xmzs.common.chat.entity.chat.ChatCompletion;
import com.xmzs.common.chat.entity.chat.ChatCompletionResponse;
import com.xmzs.common.chat.entity.chat.FunctionCall;
import com.xmzs.common.chat.entity.chat.Message;
import com.xmzs.common.chat.openai.OpenAiStreamClient;
import com.xmzs.common.chat.plugin.PluginAbstract;
import com.xmzs.common.chat.plugin.PluginParam;
import com.xmzs.common.chat.plugin.news.NewsResp;
import com.xmzs.common.chat.plugin.weather.WeathResp;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 描述： 插件开发返回信息收集sse监听器
 *
 * @author https:www.unfbx.com
 * 2023-08-18
 */
@Slf4j
public abstract class PluginListener<R extends PluginParam, T> extends EventSourceListener {
    /**
     * openAi插件构建的参数
     */
    private String arguments = "";

    /**
     * 获取openAi插件构建的参数
     * @return arguments
     */
    private String getArguments() {
        return this.arguments;
    }

    private OpenAiStreamClient client;
    private EventSourceListener eventSourceListener;
    private PluginAbstract<R, T> plugin;
    private ChatCompletion chatCompletion;

    //hzb++ 实现自由控制发送打字
    private  SseEmitter sseEmitter;

    /**
     * 构造方法必备四+1个元素
     *
     * @param client              OpenAiStreamClient
     * @param eventSourceListener 处理真实第二次sse请求的自定义监听
     * @param plugin              插件信息
     * @param chatCompletion      请求参数
     * @param  sseEmitter        sse连接对象
     */
    public PluginListener(OpenAiStreamClient client, EventSourceListener eventSourceListener, PluginAbstract<R, T> plugin, ChatCompletion chatCompletion,SseEmitter sseEmitter) {
        this.client = client;
        this.eventSourceListener = eventSourceListener;
        this.plugin = plugin;
        this.chatCompletion = chatCompletion;
        this.sseEmitter =  sseEmitter;
    }

    /**
     * sse关闭后处理，第二次请求方法
     */
    public void onClosedAfter() {
        log.debug("构造的方法值::{}", getArguments()+",,func==》"+plugin.getFunction());
        String arguments = getArguments();

        R realFunctionParam = null;
        if(!ObjectUtils.isEmpty(arguments)){
            try {
                realFunctionParam = (R) JSONUtil.toBean(arguments, plugin.getR());
            } catch (Exception e) {
                // 处理 JSON 解析异常，比如记录日志或者抛出一个自定义异常
                log.error("JSON解析错误", e);
                throw new RuntimeException("JSON解析错误", e);
            }
        }


        T tq = null;
        try {
            tq = plugin.func(realFunctionParam);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //处理不做二次调用openai 进行处理的情况
        if("newsapi".equals(plugin.getName())){
            NewsResp newsResp = (NewsResp) tq;
            List<NewsResp.newEn> newsList = newsResp.getNewsList();
//            StringBuffer output = new StringBuffer();
            if(newsList==null||newsList.size()==0){
                try {
                    sseEmitter.send("sorry,not found！");
                    Thread.sleep(200);
                } catch (IOException e) {
                    log.error("发送事件失败: {}", e.getMessage());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }else{
                int j=1;

                String answer = "";
                for(NewsResp.newEn newEn:newsList){

                    answer+=j+"."+newEn.getTitle()+"," +newEn.getDescription()+"\n";

                    j++;
                }

                try {
                    answer = answer.replace("\n", "\ndata: ");
                    sseEmitter.send(SseEmitter.event().data(answer));
//                sseEmitter.send(answer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                sseEmitter.complete();
            }
            sseEmitter.complete();
        }else if("weatherApi".equals(plugin.getName())){
            WeathResp weathResp = (WeathResp) tq;
            String weathstr = weathResp.getWeathDetail();

            //二次包装
            List<Message> messages = chatCompletion.getMessages();
            String qa =  String.valueOf(messages.get(messages.size()-1));
            String prompts = "使用 <Data></Data> 标记中的内容作为你的知识:" +
                "<Data>\n" +
                weathstr +
                "</Data>\n" +
                "回答要求：\n" +
                "- 用户是一位正在开车的驾驶员，回答内容偏向开车司机。\n" +
                "- 如果特殊天气要告知用户注意危险。\n" +
                "- 避免提及你是从 <Data></Data> 获取的知识。\n" +
                "- 保持答案与 <Data></Data> 中描述的一致。\n" +
                "- 注意知识中Timestamp字段作为你得日期数据判断依据 ，如果没有特别提及日期则回答今天数据，如果今天数据包含特殊天气则需要提醒司机。\n" +
                "- 使用与问题相同的语言回答。\n" +
                "问题:\"\"\" "+qa+ " \"\"\"";

            System.out.println("二次提问内容：："+prompts);
            Message message = Message.builder().role(Message.Role.USER).content(prompts).build();



            ChatCompletion chatCompletion2 = ChatCompletion
                .builder()
                .messages(Collections.singletonList(message))
                .model(chatCompletion.getModel())
//                .model("gpt-4o")
                .build();
//            client.streamChatCompletion(chatCompletion2, eventSourceListener);
            ChatCompletionResponse chatCompletionResponseV2 = client.chatCompletion(chatCompletion2);
            log.info("答案：{}", chatCompletionResponseV2.getChoices().get(0).getMessage().getContent());
            String answer = chatCompletionResponseV2.getChoices().get(0).getMessage().getContent();
            answer = answer.replace("\n", "\ndata: ");

            //调用openai 二次包装返回数据
            try {
                sseEmitter.send(SseEmitter.event().data(answer));
//                sseEmitter.send(answer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sseEmitter.complete();
        }else{
            FunctionCall functionCall = FunctionCall.builder()
                .arguments(getArguments())
                .name(plugin.getFunction())
                .build();
            chatCompletion.getMessages().add(Message.builder().role(Message.Role.ASSISTANT).content("function_call").functionCall(functionCall).build());
            chatCompletion.getMessages().add(Message.builder().role(Message.Role.FUNCTION).name(plugin.getFunction()).content(plugin.content(tq)).build());
            //设置第二次，请求的参数
            chatCompletion.setFunctionCall(null);
            chatCompletion.setFunctions(null);
            client.streamChatCompletion(chatCompletion, eventSourceListener);
        }

    }



    @Override
    public final void onEvent(EventSource eventSource, String id, String type, String data) {
        log.debug("插件开发返回信息收集sse监听器返回数据：{}", data);
        if ("[DONE]".equals(data)) {
            log.debug("插件开发返回信息收集sse监听器返回数据结束了");
            return;
        }
        ChatCompletionResponse chatCompletionResponse = JSONUtil.toBean(data, ChatCompletionResponse.class);
        if (Objects.nonNull(chatCompletionResponse.getChoices().get(0).getDelta().getFunctionCall())) {
            this.arguments += chatCompletionResponse.getChoices().get(0).getDelta().getFunctionCall().getArguments();
        }
    }

    @Override
    public final void onClosed(EventSource eventSource) {
        log.debug("插件开发返回信息收集sse监听器关闭连接...");
        this.onClosedAfter();
    }

    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.debug("插件开发返回信息收集sse监听器建立连接...");
    }

    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (Objects.isNull(response)) {
            log.error("插件开发返回信息收集sse监听器,连接异常:{}", t);
            eventSource.cancel();
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            log.error("插件开发返回信息收集sse监听器,连接异常data：{}，异常：{}", body.string(), t);
        } else {
            log.error("插件开发返回信息收集sse监听器,连接异常data：{}，异常：{}", response, t);
        }
        eventSource.cancel();
    }
}
