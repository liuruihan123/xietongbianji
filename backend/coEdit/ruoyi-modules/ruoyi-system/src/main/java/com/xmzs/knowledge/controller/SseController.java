package com.xmzs.knowledge.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.xmzs.common.chat.config.LocalCache;
import com.xmzs.common.chat.domain.request.ChatRequest;
import com.xmzs.common.chat.entity.chat.ChatCompletion;
import com.xmzs.common.chat.entity.chat.Message;
import com.xmzs.common.chat.openai.OpenAiStreamClient;
import org.springframework.beans.factory.annotation.Autowired;
import com.xmzs.common.chat.utils.TikTokensUtil;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.exception.base.BaseException;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.knowledge.chain.llm.capabilities.generation.audio.AudioChatService;
import com.xmzs.knowledge.chain.llm.capabilities.generation.image.ImageChatService;
import com.xmzs.knowledge.chain.llm.capabilities.generation.text.TextChatService;
import com.xmzs.knowledge.chain.llm.capabilities.generation.vision.VisionChatService;
import com.xmzs.knowledge.chain.vectorizer.Vectorization;
import com.xmzs.knowledge.chain.vectorizer.local.LocalAiVectorization;
import com.xmzs.knowledge.chain.vectorstore.VectorStore;
import com.xmzs.knowledge.common.ResultData;
import com.xmzs.knowledge.common.constant.SysConstants;
import com.xmzs.knowledge.domain.*;
import com.xmzs.knowledge.request.AudioChatRequest;
import com.xmzs.knowledge.request.ImageChatRequest;
import com.xmzs.knowledge.request.MediaFileBase64UploadRequest;
import com.xmzs.knowledge.request.VisionChatRequest;
import com.xmzs.knowledge.response.AudioChatResponse;
import com.xmzs.knowledge.response.CreateImageChatResponse;
import com.xmzs.knowledge.service.*;
import com.xmzs.system.domain.SysUser;
import com.xmzs.system.domain.bo.ChatMessageBo;
import com.xmzs.system.listener.SSEEventSourceListener;
import com.xmzs.system.service.IChatCostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * sse 聊天
 */
@Controller
@RequestMapping(path = "knowledge")
@Slf4j
public class SseController {
    private static Map<String, SseEmitter> sseCache = new ConcurrentHashMap<>();

    private final TextChatService textChatService;
    private final AudioChatService audioChatService;
    private final VisionChatService visionChatService;
    private final ImageChatService imageChatService;

    private final VectorStore vectorStore;
    private final Vectorization vectorization;

    private final EmbeddingService embeddingService;
    private final ConversationService conversationService;
    private final ChatRequestLogService chatRequestLogService;
    private final MediaFileService mediaFileService;

    @Autowired(required = false)
    private OpenAiStreamClient openAiStreamClient;
    private final IChatCostService chatService;

    public SseController(TextChatService textChatService, AudioChatService audioChatService,
                        VisionChatService visionChatService, ImageChatService imageChatService,
                        VectorStore vectorStore, Vectorization vectorization,
                        EmbeddingService embeddingService, ConversationService conversationService,
                        ChatRequestLogService chatRequestLogService, MediaFileService mediaFileService,
                        IChatCostService chatService) {
        this.textChatService = textChatService;
        this.audioChatService = audioChatService;
        this.visionChatService = visionChatService;
        this.imageChatService = imageChatService;
        this.vectorStore = vectorStore;
        this.vectorization = vectorization;
        this.embeddingService = embeddingService;
        this.conversationService = conversationService;
        this.chatRequestLogService = chatRequestLogService;
        this.mediaFileService = mediaFileService;
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    @ResponseBody
    public SseEmitter sseChat(@RequestBody @Valid ChatRequest chatRequest) {
        LocalCache.CACHE.put("userId",getUserId());
        SseEmitter sseEmitter = new SseEmitter(0L);
        SSEEventSourceListener openAIEventSourceListener = new SSEEventSourceListener(sseEmitter,new ChatRequest());
        // 获取对话消息列表
        List<Message> msgList = chatRequest.getMessages();
        // 消息记录
        Message message = msgList.get(msgList.size() - 1);

        List<String> nearestList;
        if (vectorization instanceof LocalAiVectorization){
            // 使用 weaviate向量数据库内置的嵌入向量模型
            nearestList = vectorStore.nearest(message.getContent(),chatRequest.getKid());
        }else {
            // 使用外部的嵌入向量模型
            List<Double> queryVector = embeddingService.getQueryVector(message.getContent());
            nearestList = vectorStore.nearest(queryVector,chatRequest.getKid());
        }

        log.info("知识库向量检索结果为{}",nearestList);
        for (String content : nearestList) {
            Message prompt = new Message.Builder()
                .role(ChatMessageRole.SYSTEM.value())
                .content(content)
                .build();
            msgList.add(prompt);
        }
        Message sysPrompt = new Message.Builder()
            .role(ChatMessageRole.SYSTEM.value())
            .content(message.getContent()+"\"\\n\\n注意：回答问题时，必须严格根据我给你的系统上下文内容原文进行回答，回答的内容尽量简单精炼，不要自己发挥,回答时保持原来文本的段落层级\" : \"\" ")
            .build();
        msgList.add(sysPrompt);

        ChatMessageBo chatMessageBo = new ChatMessageBo();
        chatMessageBo.setUserId(getUserId());
        chatMessageBo.setModelName(chatRequest.getModel());
        chatMessageBo.setContent(message.getContent());
        try{
            if(!chatRequest.getModel().startsWith("gpt-3.5")){
                // 判断用户是否付费
                chatService.checkUserGrade();
            }
            int tokens = TikTokensUtil.tokens(chatRequest.getModel(), msgList);
            chatMessageBo.setTotalTokens(tokens);
            // 按token扣费并且保存消息记录
            chatService.deductToken(chatMessageBo);
        }catch (Exception e){
            sendErrorEvent(sseEmitter, e.getMessage());
            return sseEmitter;
        }

        ChatCompletion completion = ChatCompletion
            .builder()
            .messages(msgList)
            .model(chatRequest.getModel())
            .temperature(chatRequest.getTemperature())
            .topP(chatRequest.getTop_p())
            .stream(true)
            .build();
        if (openAiStreamClient != null) {
            openAiStreamClient.streamChatCompletion(completion, openAIEventSourceListener);
        } else {
            // OpenAI客户端不可用，返回错误信息
            try {
                sseEmitter.send(SseEmitter.event().data("OpenAI服务暂时不可用"));
                sseEmitter.complete();
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }
        }

        return sseEmitter;
    }

    @SneakyThrows
    @PostMapping(path = "send")
    @ResponseBody
    public ResultData<List<String>> send(HttpServletRequest httpServletRequest, String sessionId, String content,String kid,String sid,Boolean useLk,Boolean useHistory) {
        SseEmitter sseEmitter = sseCache.get(sessionId);
        List<String> nearestList = new ArrayList<>();
        if (sseEmitter != null) {
            SysUser sysUser = (SysUser) httpServletRequest.getSession().getAttribute(SysConstants.SESSION_LOGIN_USER_KEY);
            ChatRequestLog chatRequestLog = new ChatRequestLog();
            chatRequestLog.setUserId(sysUser.getUserId().intValue());
            chatRequestLog.setKid(kid);
            chatRequestLog.setRequestTime(LocalDateTime.now());
            chatRequestLog.setContent(content);
            chatRequestLog.setCreateTime(LocalDateTime.now());
            chatRequestLogService.save(chatRequestLog);
            List<Conversation> history = new ArrayList<>();
            if (useHistory){
                history = conversationService.history(sid);
            }
            CustomChatMessage customChatMessage = new CustomChatMessage(content,sid);
            if (useLk){
                if (vectorization instanceof LocalAiVectorization){
                    // 使用 weaviate向量数据库内置的嵌入向量模型
                    nearestList = vectorStore.nearest(content,kid);
                }else {
                    // 使用外部的嵌入向量模型
                    List<Double> queryVector = embeddingService.getQueryVector(content);
                    nearestList = vectorStore.nearest(queryVector,kid);
                }
                log.info("知识库向量检索结果为{}",nearestList);
            }
            textChatService.streamChat(customChatMessage,nearestList,history,sseEmitter,sysUser);
        }
        return ResultData.success(nearestList,"发送成功");
    }

    @PostMapping(path = "audioChat")
    @ResponseBody
    public ResultData<AudioChatResponse> audioChat(HttpServletRequest httpServletRequest, @RequestBody AudioChatRequest request) {
        SseEmitter sseEmitter = sseCache.get(request.getSessionId());
        SysUser sysUser = (SysUser) httpServletRequest.getSession().getAttribute(SysConstants.SESSION_LOGIN_USER_KEY);
        List<String> nearestList = new ArrayList<>();
        List<Conversation> history = new ArrayList<>();
        if (request.getUseHistory()){
            history = conversationService.history(request.getSid());
        }
        CustomChatMessage customChatMessage = new CustomChatMessage(request.getContent(), request.getSid());
        if (request.getUseLk()){
            if (vectorization instanceof LocalAiVectorization){
                // 使用 weaviate向量数据库内置的嵌入向量模型
                nearestList = vectorStore.nearest(request.getContent(),request.getKid());
            }else {
                // 使用外部的嵌入向量模型
                List<Double> queryVector = embeddingService.getQueryVector(request.getContent());
                nearestList = vectorStore.nearest(queryVector,request.getKid());
            }
            log.info("知识库向量检索结果为{}",nearestList);
        }
        AudioChatResponse audioChatResponse = new AudioChatResponse();
        audioChatResponse.setNearestList(nearestList);
        audioChatService.audioChat(customChatMessage,nearestList,history,sseEmitter,sysUser, request.getMediaId());
        return ResultData.success(audioChatResponse,"发送成功");
    }

    @PostMapping(path = "visionChat")
    @ResponseBody
    public ResultData<String> visionChat(HttpServletRequest httpServletRequest, @RequestBody VisionChatRequest request) {
        SysUser sysUser = (SysUser) httpServletRequest.getSession().getAttribute(SysConstants.SESSION_LOGIN_USER_KEY);
        List<MediaFile> mediaFileList = mediaFileService.listByMfids(request.getMediaIds());
        int questionCid = conversationService.saveConversation(sysUser.getUserId().intValue(),request.getSid(), request.getContent(), "Q");
        mediaFileService.updateWithCid(request.getMediaIds(),questionCid);
        List<String> httpUrls = mediaFileList.stream().map((item)->item.getHttpUrl()).collect(Collectors.toList());
        String result = visionChatService.visionCompletion(request.getContent(), httpUrls);
        int answerCid = conversationService.saveConversation(sysUser.getUserId().intValue(),request.getSid(), result, "A");
        return ResultData.success(result,"发送成功");
    }

    @PostMapping(path = "createImageChat")
    @ResponseBody
    public ResultData<CreateImageChatResponse> createImageChat(HttpServletRequest httpServletRequest, @RequestBody ImageChatRequest request) {
        SysUser sysUser = (SysUser) httpServletRequest.getSession().getAttribute(SysConstants.SESSION_LOGIN_USER_KEY);
        List<MediaFile> medias = new ArrayList<>();
        List<String> mediaIds = new ArrayList<>();
        List<String> base64List = null;
        int questionCid = conversationService.saveConversation(sysUser.getUserId().intValue(),request.getSid(), request.getContent(), "Q");
        if (CollectionUtil.isEmpty(request.getMediaIds())){
            base64List = imageChatService.createImage(request.getContent());
        }else {
            List<MediaFile> mediaFileList = mediaFileService.listByMfids(request.getMediaIds());
            mediaFileService.updateWithCid(request.getMediaIds(),questionCid);
            List<String> httpUrls = mediaFileList.stream().map((item)->item.getHttpUrl()).collect(Collectors.toList());
            base64List = imageChatService.editImage(request.getContent(), httpUrls);
        }
        for (String base64Image : base64List){
            MediaFileBase64UploadRequest mediaFileBase64UploadRequest = new MediaFileBase64UploadRequest();
            mediaFileBase64UploadRequest.setBase64Image(base64Image);
            MediaFile mediaFile = mediaFileService.base64Upload(mediaFileBase64UploadRequest);
            medias.add(mediaFile);
            mediaIds.add(mediaFile.getMfid());
        }
        String textMsg = "图片消息";
        int answerCid = conversationService.saveConversation(sysUser.getUserId().intValue(),request.getSid(), textMsg, "A");
        mediaFileService.updateWithCid(mediaIds,answerCid);
        CreateImageChatResponse response = new CreateImageChatResponse();
        response.setTextMsg(textMsg);
        response.setMediaFileList(medias);
        return ResultData.success(response,"发送成功");
    }



    @GetMapping(path = "over")
    public String over(String sessionId) {
        SseEmitter sseEmitter = sseCache.get(sessionId);
        if (sseEmitter != null) {
            sseEmitter.complete();
            sseCache.remove(sessionId);
        }
        return "over";
    }

    @ResponseBody
    @GetMapping(path = "subscribe", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter subscribe(String sessionId) {
        SseEmitter sseEmitter = new SseEmitter(3600 * 1000L);
        sseCache.put(sessionId, sseEmitter);
        System.out.println("add " + sessionId);
        sseEmitter.onTimeout(() -> {
            System.out.println(sessionId + "超时");
            sseCache.remove(sessionId);
        });
        sseEmitter.onCompletion(() -> System.out.println("完成！！！"));
        return sseEmitter;
    }

    /**
     * 获取用户Id
     *
     * @return
     */
    public Long getUserId(){
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (loginUser == null) {
            throw new BaseException("用户未登录！");
        }
        return loginUser.getUserId();
    }


    private void sendErrorEvent(SseEmitter sseEmitter, String errorMessage) {
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
