package com.xmzs.system.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.xmzs.common.chat.config.ChatClientConfig;
import com.xmzs.common.chat.config.LocalCache;
import com.xmzs.common.chat.constant.OpenAIConst;
import com.xmzs.common.chat.domain.request.ChatRequest;
import com.xmzs.common.chat.domain.request.Dall3Request;
import com.xmzs.common.chat.entity.Tts.TextToSpeech;
import com.xmzs.common.chat.entity.chat.*;
import com.xmzs.common.chat.entity.files.UploadFileResponse;
import com.xmzs.common.chat.entity.images.Image;
import com.xmzs.common.chat.entity.images.ImageResponse;
import com.xmzs.common.chat.entity.images.Item;
import com.xmzs.common.chat.entity.whisper.WhisperResponse;
import com.xmzs.common.chat.openai.OpenAiStreamClient;
import com.xmzs.common.chat.utils.TikTokensUtil;
import com.xmzs.common.core.constant.ChatStatus;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.enums.DeviceType;
import com.xmzs.common.core.exception.base.BaseException;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.office.utils.FileUtil;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.knowledge.chain.vectorizer.Vectorization;
import com.xmzs.knowledge.chain.vectorizer.local.LocalAiVectorization;
import com.xmzs.knowledge.chain.vectorstore.VectorStore;
import com.xmzs.knowledge.common.ResultData;
import com.xmzs.knowledge.service.ConversationService;
import com.xmzs.knowledge.service.EmbeddingService;
import com.xmzs.system.domain.bo.AiQaTemplateBo;
import com.xmzs.system.domain.bo.ChatMessageBo;
import com.xmzs.system.domain.bo.ModelSettingBo;
import com.xmzs.system.domain.bo.SysModelBo;
import com.xmzs.system.domain.vo.*;
import com.xmzs.system.handler.MessageHandler;
import com.xmzs.system.listener.SSEEventSourceListener;
import com.xmzs.system.mapper.AiQaCaseMapper;
import com.xmzs.system.service.*;
import com.xmzs.system.util.WebSocketUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 描述：
 *
 * @author https:www.unfbx.com
 * @date 2023-04-08
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SseServiceImpl implements ISseService {

    @Autowired(required = false)
    private ChatClientConfig chatConfig;
    @Autowired(required = false)
    private OpenAiStreamClient openAiStreamClient;

    private final IChatCostService chatService;

    private final IChatMessageService chatMessageService;

    private final ISysModelService sysModelService;

    private final IModelSettingService modelSettingService;

    private final IAiQaTemplateService aiQaTemplateService;

    //向量数据库
    private final VectorStore vectorStore;
    private final Vectorization vectorization;

    private final EmbeddingService embeddingService;
    private final ConversationService conversationService;

    private final IAiConversationStartersService aiConversationStartersService;

    private final AiQaCaseMapper aiQaCaseMapper;

    private final MessageHandler messageHandler;

    @Override
    public SseEmitter sseChat(ChatRequest chatRequest) {
        openAiStreamClient =  chatConfig.getOpenAiStreamClient();
        SseEmitter sseEmitter = new SseEmitter(0L);
        try {
            LocalCache.CACHE.put("userId",getUserId());
        }catch (NotLoginException notLoginException){
            sendErrorEvent(sseEmitter, ChatStatus.TOKEN_OUT);
            return sseEmitter;
        }
        SSEEventSourceListener openAIEventSourceListener = new SSEEventSourceListener(sseEmitter,chatRequest);


        // 获取对话消息列表
        List<Message> msgList = chatRequest.getMessages();
        ResultData resultData =   messageHandler.msgHandler(chatRequest,msgList, DeviceType.PC.getDevice(), null);
        if (!"200".equals(resultData.getCode())){
            sendErrorEvent(sseEmitter, resultData.getMessage());
            return sseEmitter;
        }
        Map<String,Object> map = (Map<String,Object>) resultData.getData();
        msgList = (List<Message>) map.get("msgList");
        chatRequest = (ChatRequest) map.get("chatRequest");


        log.info("sse发送消息详情：");
        // 使用 Streams 打印消息
//        msgList.forEach(msg -> {
//            log.info("Role:{}，Content:{} ，\n " , msg.getRole(), msg.getContent());
//            log.info("----------");
//        });

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
        openAiStreamClient.streamChatCompletion(completion, openAIEventSourceListener);
//        }
        return sseEmitter;

    }


    public SseEmitter sseChat2(ChatRequest chatRequest) {
        openAiStreamClient =  chatConfig.getOpenAiStreamClient();
        SseEmitter sseEmitter = new SseEmitter(0L);

        try {
            LocalCache.CACHE.put("userId",getUserId());
        }catch (NotLoginException notLoginException){
            sendErrorEvent(sseEmitter, ChatStatus.TOKEN_OUT);
            return sseEmitter;
        }


        SSEEventSourceListener openAIEventSourceListener = new SSEEventSourceListener(sseEmitter,new ChatRequest());
        // 图文识别上下文信息
//        List<Content> contentList = chatRequest.getImageContent();

        // 获取对话消息列表
        List<Message> msgList = chatRequest.getMessages();


        // 用户提问消息
        Message message = msgList.get(msgList.size() - 1);

        // 复用
        Message userQ = message;

        ChatMessageBo chatMessageBo = new ChatMessageBo();
        chatMessageBo.setUserId(getUserId());
        chatMessageBo.setModelName(chatRequest.getModel());
        chatMessageBo.setContent(message.getContent());
        chatMessageBo.setMsgId(chatRequest.getUuid());
        //此处检查费用相关
        //计费
        ResultData res =  chatService.deductUseCount(chatMessageBo,msgList);
        if(!"200".equals(res.getCode())){
            sendErrorEvent(sseEmitter,res.getCode()+res.getMessage());
            return sseEmitter;
        }

//        try{
//            //计费
//            chatService.deductUseCount(chatMessageBo);
//        }catch (Exception e){
//            sendErrorEvent(sseEmitter, e.getMessage());
//            return sseEmitter;
//        }

        //此处为三大业务类型  前端对应值分别是 intv 面试  company公司咨询  point 要点总结
        String busiType = chatRequest.getBusiType();
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
//            throw new IllegalArgumentException();
            sendErrorEvent(sseEmitter,"管理员尚未对该业务模型进行配置，请先配置模型参数！");
            return sseEmitter;
        }
        ModelSettingVo useModelSetting = list.get(0);
        //替换第一个系统角色
        Message checkMessageSystemRole = msgList.get(0);
        checkMessageSystemRole.setRole(Message.Role.SYSTEM.getName());
        checkMessageSystemRole.setContent(useModelSetting.getSystemRole());

//        checkMessageSystemRole.setName("");

        //是否使用知识库
        Boolean startUserKnowledge = false;
        //查看是否为对话开场 && 为非固定 则使用以下
        if(chatRequest.getIs_starters()){
           String startersId =  chatRequest.getStarters_id();
           if(StringUtils.isNotBlank(startersId)){
               AiConversationStartersVo conversationStartersVo =  aiConversationStartersService.queryById(Long.valueOf(startersId));
               startUserKnowledge =  "0".equals(conversationStartersVo.getUseKnowledge());

               if(!ObjectUtils.isEmpty(conversationStartersVo)&& "0".equals(conversationStartersVo.getTemplateFixed())){
                   String knowledgeAttachIds = conversationStartersVo.getKnowledgeAttachIds();
                   if(StringUtils.isNotBlank(knowledgeAttachIds)){
                       List<Long> idList = Arrays.stream(knowledgeAttachIds.split(","))
                           .map(Long::parseLong)
                           .collect(Collectors.toList());
                       List<AiQaCaseVo>  caseVoList =  aiQaCaseMapper.getAiQaCaseByIds(idList);
                       String caseContent =  formatCaseContents(caseVoList);
                       //判断是否需要二次包装
                       AiQaTemplateVo qaTemplateVo =  aiQaTemplateService.queryById(Long.valueOf(conversationStartersVo.getTemplateId()));
                       String template = "";
                       if(!ObjectUtils.isEmpty(qaTemplateVo)){
                           template = qaTemplateVo.getTemplateContent().replace("{{case}}",caseContent);

                           String roleType = qaTemplateVo.getRoleType();
                           if("system".equals(roleType)){
//                      msgList.add(prompt);
                               //重置最后一条为系统
                               message =  new Message.Builder()
                                   .role(ChatMessageRole.SYSTEM.value())
                                   .content(template)
                                   .build();
                               msgList.add(msgList.size() - 1, message);
                               //增加用户提问
                                msgList.add(userQ);
                           }else{
                               template = template.replace("{{question}}",message.getContent());
                               message.setContent( template);
                           }
                       }

                   }


               }

           }
        }

        //修改前端传递配置
        chatRequest.setModel(useModelSetting.getSysModel().getModelName());
        chatRequest.setSystemMessage(useModelSetting.getSystemRole());
        chatRequest.setTop_p(useModelSetting.getTopP());
        chatRequest.setTemperature(useModelSetting.getTemperature());
        chatRequest.setContentNumber(useModelSetting.getTalkCount());

        //处理触发字数
        Integer shortNoOpen = Optional.ofNullable( useModelSetting.getShortNoOpen()).orElse(0);
//        Boolean hasPass = shortNoOpen==0||message.getContent().length() > shortNoOpen?true:false;
        //判断是否触发
        boolean hasPass = shortNoOpen == 0 || message.getContent().length() > shortNoOpen;

        //判断最终是否启用向量数据库
        Boolean isUseKnowledge = false;
        if(chatRequest.getIs_starters()){
            isUseKnowledge = startUserKnowledge;
        }else{
            isUseKnowledge = "0".equals(useModelSetting.getUseKnowledge());
        }
        if(isUseKnowledge&&hasPass){
            chatRequest.setKid(useModelSetting.getKid());
            List<String> nearestList;
            String vectorSearchResult = "";
            if (vectorization instanceof LocalAiVectorization){
                // 使用 weaviate向量数据库内置的嵌入向量模型
                nearestList = vectorStore.nearest(message.getContent(),chatRequest.getKid());
            }else {
                // 使用外部的嵌入向量模型
                List<Double> queryVector = embeddingService.getQueryVector(message.getContent());
//                nearestList = vectorStore.nearest(queryVector,chatRequest.getKid());
                nearestList = vectorStore.nearestByMore(queryVector,chatRequest.getKid());
            }
            log.info("知识库向量检索结果为{}",nearestList);
            vectorSearchResult = convertListToString(nearestList);

            //判断是否需要二次包装
              AiQaTemplateVo qaTemplateVo =  aiQaTemplateService.queryById(useModelSetting.getStandardTemplateId());
              if(!ObjectUtil.isNull(qaTemplateVo)){
                  String template = qaTemplateVo.getTemplateContent();
                  template = template.replace("{{data}}",vectorSearchResult);


                  String roleType = qaTemplateVo.getRoleType();
                  if("system".equals(roleType)){
//                      msgList.add(prompt);
                      //重置最后一条为系统
                      message =  new Message.Builder()
                          .role(ChatMessageRole.SYSTEM.value())
                          .content(template)
                          .build();
                      msgList.add(msgList.size() - 1, message);
                      //增加用户提问
//                      msgList.add(userQ);
                  }else{
                      template = template.replace("{{question}}",message.getContent());
                      message.setContent( template);
                  }
              }
        }else{
            //非知识库 且不是第一条
            if(!chatRequest.getIs_starters()&&hasPass){
                AiQaTemplateVo qaTemplateVo =  aiQaTemplateService.queryById(useModelSetting.getStandardTemplateId());
                if(!ObjectUtil.isNull(qaTemplateVo)){
                    String template = qaTemplateVo.getTemplateContent();

                    String roleType = qaTemplateVo.getRoleType();
                    if("system".equals(roleType)){
//                      msgList.add(prompt);
                        //重置最后一条为系统
                        message =  new Message.Builder()
                            .role(ChatMessageRole.SYSTEM.value())
                            .content(template)
                            .build();
                        msgList.add(msgList.size() - 1, message);
                        //增加用户提问
//                      msgList.add(userQ);
                    }else{
                        template = template.replace("{{question}}",message.getContent());
                        message.setContent( template);
                    }
                }
            }

        }




//        try{
//            //计费
//            chatService.deductUseCount(chatMessageBo);
            //老版本计费
//            if(!chatRequest.getModel().startsWith("gpt-3.5")){
//                // 判断用户是否付费
//                chatService.checkUserGrade();
//            }
//            //根据模型名称查询模型信息
//            SysModelBo sysModelBo = new SysModelBo();
//            sysModelBo.setModelName(chatRequest.getModel());
//            List<SysModelVo> sysModelList = sysModelService.queryList(sysModelBo);
//            if (CollectionUtil.isEmpty(sysModelList)) {
//                // 如果模型不存在默认使用token扣费方式
//                processByToken(chatRequest.getModel(), msgList, chatMessageBo);
//            }else {
//                // 模型设置默认提示词
//                SysModelVo firstModel = sysModelList.get(0);
//                if(StringUtils.isNotEmpty(firstModel.getSystemPrompt())){
//                    Message sysMessage = Message.builder().content(firstModel.getSystemPrompt()).role(Message.Role.SYSTEM).build();
//                    // 假设 msgList 不为空并且至少有一个元素
//                    if (msgList.get(0).equals(sysMessage)) {
//                        // 如果第一个元素与sysMessage相等，替换第一个元素
//                        msgList.set(0, sysMessage);
//                    } else {
//                        // 如果不相等，将sysMessage插入到列表的第一个位置
//                        msgList.add(0, sysMessage);
//                    }
//                }
//                // 计费类型: 1 token扣费 2 次数扣费
//                if("2".equals(firstModel.getModelType())){
//                    processByModelPrice(firstModel,chatMessageBo);
//                }else {
//                    processByToken(chatRequest.getModel(), msgList, chatMessageBo);
//                }
//            }
//        }catch (Exception e){
//            sendErrorEvent(sseEmitter, e.getMessage());
//            return sseEmitter;
//        }
        // 图文识别模型  暂不使用图文
//        if (ChatCompletion.Model.GPT_4_VISION_PREVIEW.getName().equals(chatRequest.getModel())) {
//            MessagePicture messagePicture = MessagePicture.builder().role(Message.Role.USER.getName()).content(contentList).build();
//            ChatCompletionWithPicture chatCompletion = ChatCompletionWithPicture
//                .builder()
//                .messages(Collections.singletonList(messagePicture))
//                .model(chatRequest.getModel())
//                .temperature(chatRequest.getTemperature())
//                .topP(chatRequest.getTop_p())
//                .stream(true)
//                .build();
//            openAiStreamClient.streamChatCompletion(chatCompletion, openAIEventSourceListener);
//        } else {
            ChatCompletion completion = ChatCompletion
                .builder()
                .messages(msgList)
                .model(chatRequest.getModel())
                .temperature(chatRequest.getTemperature())
                .topP(chatRequest.getTop_p())
                .maxTokens(useModelSetting.getMaxTokens())
//                .frequencyPenalty(useModelSetting.getFrequencyPenalty())
//                .presencePenalty(useModelSetting.getPresencePenalty())
                .stream(true)
                .build();
            openAiStreamClient.streamChatCompletion(completion, openAIEventSourceListener);
//        }
        return sseEmitter;
    }


    /**
     * 获取固定消息
     * @param startersId
     * @param starterContent
     * @return
     */
    public Map<String,Object> getFixedMessage(String startersId,String starterContent){
        Map<String,Object> map = new HashMap<String,Object>();

                AiConversationStartersVo conversationStartersVo =  aiConversationStartersService.queryById(Long.valueOf(startersId));
                if(!ObjectUtils.isEmpty(conversationStartersVo)&& "1".equals(conversationStartersVo.getTemplateFixed())){
                    String knowledgeAttachIds = conversationStartersVo.getKnowledgeAttachIds();
                    if(StringUtils.isNotBlank(knowledgeAttachIds)){
                        List<Long> idList = Arrays.stream(knowledgeAttachIds.split(","))
                            .map(Long::parseLong)
                            .collect(Collectors.toList());
                        List<AiQaCaseVo>  caseVoList =  aiQaCaseMapper.getAiQaCaseByIds(idList);
                        String caseContent =  formatCaseContents(caseVoList);
                        //判断是否需要二次包装
                        AiQaTemplateVo qaTemplateVo =  aiQaTemplateService.queryById(Long.valueOf(conversationStartersVo.getTemplateId()));
                        String template = "";
                        if(!ObjectUtils.isEmpty(qaTemplateVo)){
                            template = qaTemplateVo.getTemplateContent().replace("{{case}}",caseContent);

                            String roleType = qaTemplateVo.getRoleType();
                            if("system".equals(roleType)){
                                    map.put("role","system");
                                map.put("content",template);
                                //增加用户提问
//                      msgList.add(userQ);
                            }else{
                                template = template.replace("{{question}}",starterContent);
                                map.put("role","user");
                                map.put("content",template);
                            }
                        }

                    }


                }
                return map;

    }

    /**
     * 获取业务类型对应的模型配置信息
     * @param busiTypeCode
     * @return
     */
    public Map<String,Object> getModelSettingInfo(Integer busiTypeCode){
        ModelSettingBo modelSettingBo = new ModelSettingBo();
        modelSettingBo.setBusType(busiTypeCode);
        List<ModelSettingVo> list = modelSettingService.queryList(modelSettingBo);
        ModelSettingVo settingVo = list.get(0);
//        ModelSettingVo settingVo =  modelSettingService.queryById(useModelSetting.getId());
        Map<String,Object> resMap = new HashMap<String,Object>();


        resMap.put("model",settingVo.getSysModel().getModelName());
        resMap.put("modelLabel",settingVo.getSysModel().getModelDescribe());
        resMap.put("max_tokens",settingVo.getMaxTokens());
//        resMap.put("userModel","");
        resMap.put("talkCount",settingVo.getTalkCount());
        resMap.put("systemMessage",settingVo.getSystemRole());
        resMap.put("kid",settingVo.getKid());
//        resMap.put("kName","");
//        //gpts 商店
//        resMap.put("gpts","");
//        resMap.put("uuid","");
        resMap.put("temperature",settingVo.getTemperature());
        resMap.put("top_p",settingVo.getTopP());
        resMap.put("frequency_penalty",settingVo.getFrequencyPenalty());
        resMap.put("presence_penalty",settingVo.getPresencePenalty());
        resMap.put("tts_voice",settingVo.getTtsVoice());
        resMap.put("busType",settingVo.getBusType());
        resMap.put("speed",settingVo.getTtsSpeed());



        //只开放部分给前端展示
//        ModelSettingVo modelSettingVo = new ModelSettingVo();
//        modelSettingVo.setBusType(useModelSetting.getBusType());
//        modelSettingVo.setTalkCount(useModelSetting.getTalkCount());
//        modelSettingVo.setTtsVoice(useModelSetting.getTtsVoice());
        return resMap;
    }

    /**
     * List<String> 转化为string ，并且带有特殊格式
     * @param nearestList
     * @return
     */
    public static String convertListToString(List<String> nearestList) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < nearestList.size(); i++) {
            result.append(i + 1) // 序号从1开始
                .append(". ")
                .append(nearestList.get(i))
                .append("\n;");
        }

        return result.toString();
    }


    /**
     * 模版方法
     * @param aiQaCases
     * @return
     */
    private static String formatCaseContents(List<AiQaCaseVo> aiQaCases) {
        StringBuilder formattedOutput = new StringBuilder();
        int caseNumber = 1;

        for (AiQaCaseVo caseVo : aiQaCases) {
            String caseContent = caseVo.getCaseContent();
            formattedOutput.append("------\n");
            formattedOutput.append("case").append(caseNumber).append(": ").append(caseContent).append("\n");
            caseNumber++;
        }

        return formattedOutput.toString();
    }

    public static void main(String[] args) {
        System.out.println(outputStandardTemplate("向量数据检索结果片段内容1\n 向量数据检索结果片段内容2\n 向量数据检索结果片段内容3\n 向量数据检索结果片段内容4\n","用户的提问"));
    }
    /**
     * 标准rag模板,一般用于面试
     * 结合rag结果，重排prompts ，
     * @param vectorSearch
     * @return
     */
    private static  String outputStandardTemplate(String vectorSearch,String question){

        String prompts = "使用 <Data></Data> 标记中的内容以及对话上下文作为你的知识:\n" +
            "<Data>"+vectorSearch+"</Data> \n" +
            "回答要求：\n" +
            "- 你做为友善的面试官，主要根据<Data></Data>内知识向我提问。\n" +
            "- 如果你不明白我的回复，你需要澄清。\n" +
            "- 避免提及你是从 <Data></Data> 获取的知识。\n" +
            "- 请主要从<Data></Data>内容引导并作为面试官提问（或者引导我）面试内容以及方向。\n" +
            "- 如果问题与面试无关且问题与 <Data></Data> 标记中的内容也无关，要提醒用户这个应用的用途（辅助用户面试模拟），并且主动询问用户选择什么类型的案例。\n" +
            "- 使用 Markdown 语法优化回答格式。\n" +
            "- 使用与问题相同的语言回答。\n" +
            "\n" +
            "问题:\"\"\""+question+"\"\"\"";
        return prompts;
    }

    /**
     * 标准严格rag模板
     * 非知识库内容拒绝回答 ，
     * @param vectorSearch
     * @return
     */
    private String outputStrictStandardTemplate(String vectorSearch,String question){

        String prompts = "忘记你已有的知识，仅使用 <Data></Data> 标记中的内容以及对话上下文作为你的知识:\n" +
            "<Data>"+vectorSearch+"</Data> \n" +
            "思考流程：\n" +
            "1. 判断问题是否与 <Data></Data> 标记中的内容有关。\n" +
            "2. 如果有关，你按下面的要求回答。\n" +
            "3. 如果无关，你直接拒绝回答本次问题,告知用户暂无此知识储备。\n" +
            "\n" +
            "回答要求：\n" +
            "- 避免提及你是从 <Data></Data> 获取的知识。\n" +
            "- 保持答案与 <Data></Data> 中描述的一致。\n" +
            "- 使用 Markdown 语法优化回答格式。\n" +
            "- 使用与问题相同的语言回答。" +
            "问题:\"\"\""+question+"\"\"\"";
        return prompts;
    }

    /**
     * 根据次数扣除余额
     *
     * @param model 模型信息
     * @param chatMessageBo 对话信息
     */
    private void processByModelPrice(SysModelVo model, ChatMessageBo chatMessageBo) {
        double cost = model.getModelPrice();
        chatService.deductUserBalance(getUserId(), cost);
        chatMessageBo.setDeductCost(cost);
        chatMessageService.insertByBo(chatMessageBo);
    }

    /**
     * 根据token扣除余额
     *
     * @param modelName 模型名称
     * @param msgList 消息列表
     * @param chatMessageBo 消息记录
     */
    private void processByToken(String modelName, List<Message> msgList, ChatMessageBo chatMessageBo) {
        int tokens = TikTokensUtil.tokens(modelName, msgList);
        chatMessageBo.setTotalTokens(tokens);
        chatService.deductToken(chatMessageBo);
    }
    /**
     * 文字转语音
     *
     */
    @Override
    public ResponseEntity<Resource> textToSpeed(TextToSpeech textToSpeech) {
        long beg = System.currentTimeMillis();

        try (ResponseBody body = openAiStreamClient.textToSpeech(textToSpeech)) {
            if (body != null) {
//                System.out.println(body.byteStream()+"~~~");
                // 将ResponseBody转换为InputStreamResource
//                InputStreamResource resource = new InputStreamResource(body.byteStream());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                body.byteStream().transferTo(baos); // 注意处理IOException
                ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

//                long end = System.currentTimeMillis();
//                System.out.println("文字转语音消耗时间1：" + (end-beg)); // 1000
                // 创建并返回ResponseEntity
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mpeg")) // 假设是MP3文件
                    .body(resource);

            } else {
                // 如果ResponseBody为空，返回404状态码
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


//    /**
//     * 文字转语音
//     *
//     */
//    @Override
//    public ResponseEntity<Resource> textToSpeed(TextToSpeech textToSpeech) {
//
//        try (ResponseBody body = openAiStreamClient.textToSpeech(textToSpeech)) {
//            if (body != null) {
//                // 将ResponseBody转换为InputStreamResource
//                InputStreamResource resource = new InputStreamResource(body.byteStream());
//
//                // 创建并返回ResponseEntity
//                return ResponseEntity.ok()
//                        .contentType(MediaType.parseMediaType("audio/mpeg")) // 假设是MP3文件
//                        .body(resource);
//
//            } else {
//                // 如果ResponseBody为空，返回404状态码
//                return ResponseEntity.notFound().build();
//            }
//        }catch (Exception e){
//            log.error("textToSpeed error:{}",e.getMessage());
//        }
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//    }

    /**
     * 语音转文字
     *
     */
    @Override
    public WhisperResponse speechToTextTranscriptionsV2(MultipartFile file) {
        // 确保文件不为空
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot convert an empty MultipartFile");
        }
        // 创建一个文件对象
//        String uuidFile = UUID.randomUUID().toString().replace("-", "");
//        File tempFile = new File(System.getProperty("java.io.tmpdir") + File.separator +uuidFile+ file.getOriginalFilename());


        // 创建一个文件对象
        String uuidFile = UUID.randomUUID().toString().replace("-", "");
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            originalFilename = originalFilename.substring(originalFilename.lastIndexOf(File.separator) + 1);
        }

        File tempFile = new File(System.getProperty("java.io.tmpdir") + File.separator + uuidFile + originalFilename);


        WhisperResponse whisperResponse = new WhisperResponse();
        try {
            // 将 MultipartFile 转换为临时文件
            file.transferTo(tempFile);

            // 调用你的方法
            whisperResponse = openAiStreamClient.speechToTextTranscriptions(tempFile);
        } catch (FileNotFoundException e) {
            log.error("File not found: " + e.getMessage());
            throw new RuntimeException("File not found while converting MultipartFile to File", e);
        } catch (IOException e) {
            log.error("IO error: " + e.getMessage());
            throw new RuntimeException("Failed to convert MultipartFile to File due to IO error", e);
        } finally {
            // 删除临时文件
            if (tempFile.exists()) {
                try {
                    Files.delete(tempFile.toPath());
                } catch (IOException e) {
                    log.error("Failed to delete temporary file: " + e.getMessage());
                }
            }
        }

        return whisperResponse;
//        System.out.println(fileA.getAbsoluteFile()+"~~~");
//        try {
//            file.transferTo(fileA);
//        } catch (FileNotFoundException e) {
//            log.error("File not found: " + e.getMessage());
//            throw new RuntimeException("File not found while converting MultipartFile to File", e);
//        } catch (IOException e) {
//            log.error("IO error: " + e.getMessage());
//            throw new RuntimeException("Failed to convert MultipartFile to File due to IO error", e);
//        }
//        return openAiStreamClient.speechToTextTranscriptions(fileA);
    }

    @Override
    public String chat(ChatRequest chatRequest) {
        openAiStreamClient =  chatConfig.getOpenAiStreamClient();
        Message message = Message.builder().role(Message.Role.USER).content(chatRequest.getPrompt()).build();
        ChatCompletion chatCompletion = ChatCompletion
            .builder()
            .messages(Collections.singletonList(message))
            .model(chatRequest.getModel())
            .build();
        ChatCompletionResponse chatCompletionResponse = openAiStreamClient.chatCompletion(chatCompletion);
        return chatCompletionResponse.getChoices().get(0).getMessage().getContent();
    }

    /**
     * dall-e-3绘画接口
     *
     * @param request
     * @return
     */
    public List<Item> dall3(Dall3Request request) {
        openAiStreamClient =  chatConfig.getOpenAiStreamClient();
        chatService.checkUserGrade();
        // DALL3 绘图模型
        Image image = Image.builder()
            .responseFormat(com.xmzs.common.chat.entity.images.ResponseFormat.URL.getName())
            .model(Image.Model.DALL_E_3.getName())
            .prompt(request.getPrompt())
            .n(1)
            .quality(request.getQuality())
            .size(request.getSize())
            .style(request.getStyle())
            .build();
        ImageResponse imageResponse = openAiStreamClient.genImages(image);
        SysModelBo sysModelBo = new SysModelBo();
        sysModelBo.setModelName(request.getModel());
        List<SysModelVo> sysModelList = sysModelService.queryList(sysModelBo);
        //TODO 暂时删除扣费
//        chatService.deductUserBalance(getUserId(),sysModelList.get(0).getModelPrice());
        // 保存消息记录
        ChatMessageBo chatMessageBo = new ChatMessageBo();
        chatMessageBo.setUserId(getUserId());
        chatMessageBo.setModelName(Image.Model.DALL_E_3.getName());
        chatMessageBo.setContent(request.getPrompt());
        chatMessageBo.setDeductCost(sysModelList.get(0).getModelPrice());
        chatMessageBo.setTotalTokens(0);
        chatMessageService.insertByBo(chatMessageBo);
        return imageResponse.getData();
    }

    /**
     * 获取用户Id
     *
     * @return
     */
    public Long getUserId(){
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (loginUser == null) {
            throw new NotLoginException("Login failure！","","");
        }
        return loginUser.getUserId();
    }

    @Override
    public UploadFileResponse upload(MultipartFile file) {
       openAiStreamClient =  chatConfig.getOpenAiStreamClient();
       return openAiStreamClient.uploadFile("fine-tune", convertMultiPartToFile(file));
    }

    private File convertMultiPartToFile(MultipartFile multipartFile) {
        File file = null;
        try {
            // 获取原始文件名
            String originalFileName = multipartFile.getOriginalFilename();
            // 默认扩展名
            String extension = ".tmp";
            // 尝试从原始文件名中获取扩展名
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            // 使用原始文件的扩展名创建临时文件
            Path tempFile = Files.createTempFile(null, extension);
            file = tempFile.toFile();

            // 将MultipartFile的内容写入文件
            try (InputStream inputStream = multipartFile.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(file)) {
                int read;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            } catch (IOException e) {
                // 处理文件写入异常
                e.printStackTrace();
            }
        } catch (IOException e) {
            // 处理临时文件创建异常
            e.printStackTrace();
        }
        return file;
    }

    // 发送SSE错误事件的封装方法
    public void sendErrorEvent(SseEmitter sseEmitter, String errorMessage) {
        SseEmitter.SseEventBuilder event = SseEmitter.event()
            .name("error")
            .data(errorMessage);
        try {
            sseEmitter.send(event);
        } catch (IOException e) {
            log.error("发送事件失败: {}", e.getMessage());
        }
        sseEmitter.complete();
    }
}
