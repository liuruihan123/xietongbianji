package com.xmzs.system.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmzs.common.chat.config.LocalCache;
import com.xmzs.common.chat.domain.request.ChatRequest;
import com.xmzs.common.chat.entity.chat.ChatCompletion;
import com.xmzs.common.chat.entity.chat.Message;
//import com.xmzs.common.chat.holder.WebSocketSessionHolder;
//import com.xmzs.common.chat.listener.WebSocketEventListener;
import com.xmzs.common.chat.openai.OpenAiStreamClient;
//import com.xmzs.common.chat.utils.WebSocketUtils;
import com.xmzs.common.core.constant.ChatStatus;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.enums.DeviceType;
import com.xmzs.common.core.exception.base.BaseException;
import com.xmzs.common.core.utils.SpringUtils;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.knowledge.chain.vectorizer.Vectorization;
import com.xmzs.knowledge.chain.vectorizer.local.LocalAiVectorization;
import com.xmzs.knowledge.chain.vectorstore.VectorStore;
import com.xmzs.knowledge.common.ResultData;
import com.xmzs.knowledge.service.ConversationService;
import com.xmzs.knowledge.service.EmbeddingService;
import com.xmzs.system.domain.bo.ChatMessageBo;
import com.xmzs.system.domain.bo.ModelSettingBo;
import com.xmzs.system.domain.vo.ModelSettingVo;
import com.xmzs.system.listener.WebSocketEventListener;
import com.xmzs.system.service.IChatCostService;
import com.xmzs.system.service.IModelSettingService;
import com.xmzs.system.service.impl.SseServiceImpl;
import com.xmzs.system.util.WebSocketUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.security.auth.login.LoginException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fasterxml.jackson.core.type.TypeReference;
/**
 * WebSocketHandler 实现类
 *
 * @author zendwang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PlusWebSocketHandler extends AbstractWebSocketHandler {

    /**
     * 是否开启文本审核
     */
    @Value("${baidu.enabled}")
    private Boolean enabled;


    private final IModelSettingService modelSettingService;
    //向量数据库
    private final VectorStore vectorStore;
    private final Vectorization vectorization;

    private final EmbeddingService embeddingService;
    private final ConversationService conversationService;
    private final IChatCostService chatService;

    private final MessageHandler messageHandler;
    /**
     * 连接成功后
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        WebSocketSessionHolder.addSession(session.getId(), session);
    }

    /**
     * 处理发送来的文本消息
     *
     * @param session
     * @param message
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message){
        log.info("小程序消息session:{}",session);
        log.info("小程序消息message:{}",message);
        //解析文本和业务类型
        String msgContent = String.valueOf(JSONObject.parseObject(message.getPayload()).get("message"));
        String busiType = String.valueOf(JSONObject.parseObject(message.getPayload()).get("busiType"));
        Boolean is_starters = Boolean.valueOf(String.valueOf(JSONObject.parseObject(message.getPayload()).get("is_starters")));
        String starters_id = String.valueOf(JSONObject.parseObject(message.getPayload()).get("starters_id"));
        String uuid = String.valueOf(JSONObject.parseObject(message.getPayload()).get("uuid"));
        String currentChat = String.valueOf(JSONObject.parseObject(message.getPayload()).get("currentChat"));
        ObjectMapper objectMapper = new ObjectMapper();
        List<Message> msgList = new ArrayList<Message>();
        try {
            // 将 JSON 字符串转换为 List<Message>
            msgList =  objectMapper.readValue(currentChat, new TypeReference<List<Message>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to convert currentChat to List<Message>", e);
        }
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setBusiType(busiType);
        chatRequest.setIs_starters(is_starters);
        chatRequest.setStarters_id(starters_id);
        chatRequest.setUuid(uuid);

        ResultData resultData =   messageHandler.msgHandler(chatRequest,msgList, DeviceType.XCX.getDevice(),session);

        if(!"200".equals(resultData.getCode())){
            WebSocketUtils.sendErrorMessages(resultData.getCode(),resultData.getMessage(),session);
            return;
        }

        Map<String,Object> map = (Map<String,Object>) resultData.getData();
        msgList = (List<Message>) map.get("msgList");
        chatRequest = (ChatRequest) map.get("chatRequest");


        log.info("websocket发送消息详情：");
        // 使用 Streams 打印消息
        msgList.forEach(msg -> {
            log.info("Role:{}，Content:{} ，\n " , msg.getRole(), msg.getContent());
            log.info("----------");
        });

        ChatCompletion completion = ChatCompletion
            .builder()
            .messages(msgList)
            .model(chatRequest.getModel())
            .temperature(chatRequest.getTemperature())
            .topP(chatRequest.getTop_p())
            .maxTokens(chatRequest.getMaxTokens())
//                .frequencyPenalty(useModelSetting.getFrequencyPenalty())
//                .presencePenalty(useModelSetting.getPresencePenalty())
            .stream(true)
            .build();

        String rurl = session.getUri().getQuery();
        // 正则表达式提取 Authorization token
        String regex = "Authorization=Bearer([^&]*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(rurl);

        String decodedToken = "";
        if (matcher.find()) {
            String token = matcher.group(1);
            // URL 解码
            decodedToken = java.net.URLDecoder.decode(token, java.nio.charset.StandardCharsets.UTF_8);
            decodedToken = decodedToken.replace(" ","");
        }
        LocalCache.CACHE.put("userId",getUserId(session,decodedToken));
        WebSocketEventListener eventSourceListener = new WebSocketEventListener(session,chatRequest);

        OpenAiStreamClient openAiStreamClient=(OpenAiStreamClient) SpringUtils.context().getBean("openAiStreamClient");
        openAiStreamClient.streamChatCompletion(completion, eventSourceListener);
    }

    /**
     * 处理发送来的文本消息
     *
     * @param session
     * @param message
     */
    protected void handleTextMessage2(WebSocketSession session, TextMessage message) {
        //检测会员是否过期
//        chatService.checkUserGrade();


        //解析文本和业务类型
        String msgContent = String.valueOf(JSONObject.parseObject(message.getPayload()).get("message"));
        String busiType = String.valueOf(JSONObject.parseObject(message.getPayload()).get("busiType"));
        String currentChat = String.valueOf(JSONObject.parseObject(message.getPayload()).get("currentChat"));
        ObjectMapper objectMapper = new ObjectMapper();
        List<Message> messageList = new ArrayList<Message>();
        try {
            // 将 JSON 字符串转换为 List<Message>
            messageList =  objectMapper.readValue(currentChat, new TypeReference<List<Message>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to convert currentChat to List<Message>", e);
        }

//        ArrayList<Message>
        //-----------业务待合并----------------
        //此处为三大业务类型  前端对应值分别是 intv 面试  company公司咨询  point 要点总结
        int busiTypeCode = 0;
        switch (busiType){
            case "intv":
                busiTypeCode = 0;
                break;
            case "company":
                busiTypeCode = 2;
                break;
            case "point":
                busiTypeCode = 3;
                break;
        }
        ModelSettingBo modelSettingBo = new ModelSettingBo();
        modelSettingBo.setBusType(busiTypeCode);
        List<ModelSettingVo> list = modelSettingService.queryList(modelSettingBo);
        //获取第一
        if(list.size()==0){
            WebSocketUtils.sendErrorMessages(ChatStatus.WARN,"管理员尚未对该业务模型进行配置，请先配置模型参数！",session);
            return ;
        }
        ModelSettingVo useModelSetting = list.get(0);

//        if(enabled){
//            // 判断文本是否合规
//            TextReviewService textReviewService=(TextReviewService) SpringUtils.context().getBean("textReviewService");
//            String type = textReviewService.textReview(message.getPayload());
//            // 审核状态 1 代表合法
//            String conclusionType = "1";
//            if (!conclusionType.equals(type) && StringUtils.isNotEmpty(type)) {
//                HashMap<Object, Object> msgMap = new HashMap<>(10);
//                msgMap.put("content", "文本不合规,请修改!");
//                String jsonStr = JSONUtil.toJsonStr(msgMap);
//                WebSocketUtils.sendMessage(session, jsonStr);
//                WebSocketUtils.sendMessage(session, "[DONE]");
//                return;
//            }
//        }
        String rurl = session.getUri().getQuery();
        // 正则表达式提取 Authorization token
        String regex = "Authorization=Bearer([^&]*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(rurl);

        String decodedToken = "";
        if (matcher.find()) {
            String token = matcher.group(1);
            // URL 解码
             decodedToken = java.net.URLDecoder.decode(token, java.nio.charset.StandardCharsets.UTF_8);
            decodedToken = decodedToken.replace(" ","");
        }
        LocalCache.CACHE.put("userId",getUserId(session,decodedToken));
        WebSocketEventListener eventSourceListener = new WebSocketEventListener(session,new ChatRequest());

        Message checkMessageSystemRole = messageList.get(0);
        checkMessageSystemRole.setRole(Message.Role.SYSTEM.getName());
        checkMessageSystemRole.setContent(useModelSetting.getSystemRole());

//        String messageContext = (String) LocalCache.CACHE.get(session.getId());
//        List<Message> messages = new ArrayList<>();
//        if (StrUtil.isNotBlank(messageContext)) {
//            messages = JSONUtil.toList(messageContext, Message.class);
//            // 上下文长度
//            int contextSize=10;
//            if (messages.size() >= contextSize) {
//                messages = messages.subList(1, contextSize);
//            }
//            Message currentMessage = Message.builder().content(msgContent).role(Message.Role.USER).build();
//            messages.add(currentMessage);
//        } else {
//            //第一条
//            //替换第一个系统角色
////            List<Message> sysMessages = new ArrayList<>();
//            Message checkMessageSystemRole = new Message();
//            checkMessageSystemRole.setRole(Message.Role.SYSTEM.getName());
//            checkMessageSystemRole.setContent(useModelSetting.getSystemRole());
//            messages.add(checkMessageSystemRole);
////            LocalCache.CACHE.put(session.getId(), JSONUtil.toJsonStr(messages), LocalCache.TIMEOUT);
//
//            Message currentMessage = Message.builder().content(msgContent).role(Message.Role.USER).build();
//            messages.add(currentMessage);
//        }




        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setBusiType(busiType);
        chatRequest.setModel(useModelSetting.getSysModel().getModelName());
        chatRequest.setSystemMessage(useModelSetting.getSystemRole());
        chatRequest.setTop_p(useModelSetting.getTopP());
        chatRequest.setTemperature(useModelSetting.getTemperature());
        chatRequest.setContentNumber(useModelSetting.getTalkCount());


        Message msg = messageList.get(messageList.size() - 1);

        // 复用
        Message userQ = msg;

        //此处检查费用相关
        ChatMessageBo chatMessageBo = new ChatMessageBo();
        chatMessageBo.setUserId(getUserId(session,decodedToken));
        chatMessageBo.setModelName(chatRequest.getModel());
        chatMessageBo.setContent(msg.getContent());
        chatMessageBo.setMsgId(chatRequest.getUuid());
        //计费
        ResultData res =  chatService.deductUseCount(chatMessageBo,messageList);
        if(!"200".equals(res.getCode())){
            WebSocketUtils.sendErrorMessages(res.getCode(),res.getMessage(),session);
            return;
        }



        //判断是否启用向量数据库
        if("0".equals(useModelSetting.getUseKnowledge())){
            chatRequest.setKid(useModelSetting.getKid());
            List<String> nearestList;
            String vectorSearchResult = "";
            if (vectorization instanceof LocalAiVectorization){
                // 使用 weaviate向量数据库内置的嵌入向量模型
                nearestList = vectorStore.nearest(msg.getContent(),chatRequest.getKid());
            }else {
                // 使用外部的嵌入向量模型
                List<Double> queryVector = embeddingService.getQueryVector(msg.getContent());
//                nearestList = vectorStore.nearest(queryVector,chatRequest.getKid());
                nearestList = vectorStore.nearestByMore(queryVector,chatRequest.getKid());
            }
            log.info("知识库向量检索结果为{}",nearestList);
            vectorSearchResult = SseServiceImpl.convertListToString(nearestList);
            if("intv".equals(busiType)){
                msg.setContent( chatService.outputStandardTemplate(vectorSearchResult,msg.getContent()));
            }else if("company".equals(busiType)){
                msg.setContent(  chatService.outputStrictStandardTemplate(vectorSearchResult,msg.getContent()));
            }else if("point".equals(busiType)){
                msg.setContent(  chatService.outputStrictStandardTemplate2(vectorSearchResult,msg.getContent()));

            }

        }



        ChatCompletion chatCompletion = ChatCompletion
            .builder()
            .messages(messageList)
            .model(chatRequest.getModel())
            .temperature(chatRequest.getTemperature())
            .topP(chatRequest.getTop_p())
            .stream(true)
            .build();


//        ChatCompletion chatCompletion = ChatCompletion
//            .builder()
//            .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
//            .messages(messages)
//            .temperature(0.2)
//            .stream(true)
//            .build();
        OpenAiStreamClient openAiStreamClient=(OpenAiStreamClient) SpringUtils.context().getBean("openAiStreamClient");
        openAiStreamClient.streamChatCompletion(chatCompletion, eventSourceListener);
//        LocalCache.CACHE.put(session.getId(), JSONUtil.toJsonStr(messages), LocalCache.TIMEOUT);
    }

    /**
     * 根据key获取Value值
     *
     * @Date 2023/7/27
     * @param jsonObject
     * @param key
     * @param defaultValue
     * @return String
     **/
    public String getValue(JSONObject jsonObject,String key,String defaultValue){
        String value = (String)jsonObject.get(key);
        if(StrUtil.isEmpty(value)){
            return defaultValue;
        }
        return value;
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        super.handleBinaryMessage(session, message);
    }
    /**
     * 获取用户Id
     *
     * @return
     */
    public Long getUserId(WebSocketSession session,String token){
        LoginUser loginUser = LoginHelper.getLoginUser(token);
        if (loginUser == null) {
//            WebSocketUtils.sendMessage(session, "{\n" +
//                " \"data\":{\n" +
//                "   \"content\":\"登录失败，请重新登录！\"\n" +
//                " }\n" +
//                "}");
//            throw new NotLoginException("用户未登录！","","");
            WebSocketUtils.sendErrorMessages(ChatStatus.WARN,"登录失败，请重新登录",session);
            return null;
        }
        return loginUser.getUserId();
    }
    /**
     * 心跳监测的回复
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        WebSocketUtils.sendPongMessage(session);
    }

    /**
     * 连接出错时
     *
     * @param session
     * @param exception
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[transport error] sessionId: {} , exception:{}", session.getId(), exception.getMessage());
    }

    /**
     * 连接关闭后
     *
     * @param session
     * @param status
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        WebSocketSessionHolder.removeSession(session.getId());
    }

    /**
     * 是否支持分片消息
     *
     * @return
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

//    /**
//     * 标准rag模板,一般用于面试
//     * 结合rag结果，重排prompts ，
//     * @param vectorSearch
//     * @return
//     */
//    private String outputStandardTemplate(String vectorSearch,String question){
//
//        String prompts = "Use the content in the<Data></Data>tag and the dialogue context as your knowledge:\n" +
//            "<Data>"+vectorSearch+"</Data> \n" +
//            "Answer requirements：\n" +
//            "- You must draw relevant information from <Data></Data> in your knowledge base.\n" +
//            "- You are a senior recruiting partner at a top strategy consulting firm. \n" +
//            "- You are delivering a realistic and challenging case interview simulations that mirror those used in strategy consulting recruitment processes with me.\n" +
//            "- You could extend or enrich the content logically if your interactions with me deviate from predefined scenarios, ensuring alignment with the case objectives and interview setting. \n" +
//            "- You must conduct conversations in my native language with flawless grammar and clarity. \n"+
//            "- If I ask unrelated questions, you need to politely explain that your sole purpose is to simulate case interviews, and redirect me to the case interview topic.\n" +
//            "- Please stop the interview at the appropriate time. Generally, there are about 50 conversations. Then inform the user that the interview is over and provide some polite feedback \n" +
//            "My Question:\"\"\""+question+"\"\"\"";
//        return prompts;
//    }
//
//
//
//    /**
//     * 标准严格rag模板 用于公司信息
//     * 非知识库内容拒绝回答 ，
//     * @param vectorSearch
//     * @return
//     */
//    private String outputStrictStandardTemplate(String vectorSearch,String question){
//
//        String prompts = "Forget your existing knowledge and only use the content in the<Data></Data>tag and the dialogue context as your knowledge:\n" +
//            "<Data>"+vectorSearch+"</Data> \n" +
//            "Answer requirements：\n" +
//            "- You are a senior recruiting partner at a top strategy consulting firm. \n" +
//            "- Your primary task is to provide detailed, structured, and engaging answers regarding all aspects of top strategy consulting firms.\n" +
//            "- You must draw answers from <Data></Data> in your knowledge base to provide comprehensive, accurate, and structured responses.\n" +
//            "- If I ask unrelated questions, you need to politely explain that your sole purpose is to provide information regarding top strategy consulting firms, and gently redirect the conversation back to relevant topics." +
//            "- You must conduct conversations in my native language with flawless grammar and clarity." +
//            "My Question::\"\"\""+question+"\"\"\"";
//        return prompts;
//    }
//
//
//    /**
//     * 标准严格rag模板 用于面试要点
//     * 非知识库内容拒绝回答 ，
//     * @param vectorSearch
//     * @return
//     */
//    private String outputStrictStandardTemplate2(String vectorSearch,String question){
//
//        String prompts = "Forget your existing knowledge and only use the content in the<Data></Data>tag and the dialogue context as your knowledge:\n" +
//            "<Data>"+vectorSearch+"</Data> \n" +
//            "Answer requirements：\n" +
//            "- You are a senior recruiting partner at a top strategy consulting firm.  \n" +
//            "- Your primary task is to provide detailed, structured, and engaging answers to my questions regarding key aspects and critical attention points for strategy consulting firm recruitment interviews. \n" +
//            "- You must draw answers from <Data></Data> in your knowledge base to provide comprehensive, accurate, and structured responses. \n" +
//            "- If I ask unrelated questions, you should politely explain your focus, and redirect the conversation back to the topic of recruitment interviews for consulting firms." +
//            "- You must conduct conversations in my native language with flawless grammar and clarity." +
//            "My Question::\"\"\""+question+"\"\"\"";
//        return prompts;
//    }

//    /**
//     * 标准rag模板,一般用于面试
//     * 结合rag结果，重排prompts ，
//     * @param vectorSearch
//     * @return
//     */
//    private String outputStandardTemplate(String vectorSearch,String question){
//
//        String prompts = "使用 <Data></Data> 标记中的内容以及对话上下文作为你的知识:\n" +
//            "<Data>"+vectorSearch+"</Data> \n" +
//            "回答要求：\n" +
//            "- 你做为友善的面试官，主要根据<Data></Data>内知识向我提问。\n" +
//            "- 如果你不明白我的回复，你需要澄清。\n" +
//            "- 避免提及你是从 <Data></Data> 获取的知识。\n" +
//            "- 请主要从<Data></Data>内容引导并作为面试官提问（或者引导我）面试内容以及方向。\n" +
//            "- 如果问题与面试无关且问题与 <Data></Data> 标记中的内容也无关，要提醒用户这个应用的用途（辅助用户面试模拟），并且主动询问用户选择什么类型的案例。\n" +
//            "- 使用 Markdown 语法优化回答格式。\n" +
//            "- 使用与问题相同的语言回答。\n" +
//            "\n" +
//            "问题:\"\"\""+question+"\"\"\"";
//        return prompts;
//    }

//    /**
//     * 标准严格rag模板
//     * 非知识库内容拒绝回答 ，
//     * @param vectorSearch
//     * @return
//     */
//    private String outputStrictStandardTemplate(String vectorSearch,String question){
//
//        String prompts = "忘记你已有的知识，仅使用 <Data></Data> 标记中的内容以及对话上下文作为你的知识:\n" +
//            "<Data>"+vectorSearch+"</Data> \n" +
//            "思考流程：\n" +
//            "1. 判断问题是否与 <Data></Data> 标记中的内容有关。\n" +
//            "2. 如果有关，你按下面的要求回答。\n" +
//            "3. 如果无关，你直接拒绝回答本次问题,告知用户暂无此知识储备。\n" +
//            "\n" +
//            "回答要求：\n" +
//            "- 避免提及你是从 <Data></Data> 获取的知识。\n" +
//            "- 保持答案与 <Data></Data> 中描述的一致。\n" +
//            "- 使用 Markdown 语法优化回答格式。\n" +
//            "- 使用与问题相同的语言回答。" +
//            "问题:\"\"\""+question+"\"\"\"";
//        return prompts;
//    }
}
