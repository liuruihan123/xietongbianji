package com.xmzs.system.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.core.util.ObjectUtil;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.xmzs.common.chat.config.LocalCache;
import com.xmzs.common.chat.domain.request.ChatRequest;
import com.xmzs.common.chat.entity.chat.Message;
import com.xmzs.common.core.constant.ChatStatus;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.enums.DeviceType;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.knowledge.chain.vectorizer.Vectorization;
import com.xmzs.knowledge.chain.vectorizer.local.LocalAiVectorization;
import com.xmzs.knowledge.chain.vectorstore.VectorStore;
import com.xmzs.knowledge.common.ResultData;
import com.xmzs.knowledge.service.EmbeddingService;
import com.xmzs.system.domain.bo.ChatMessageBo;
import com.xmzs.system.domain.bo.ModelSettingBo;
import com.xmzs.system.domain.vo.AiConversationStartersVo;
import com.xmzs.system.domain.vo.AiQaCaseVo;
import com.xmzs.system.domain.vo.AiQaTemplateVo;
import com.xmzs.system.domain.vo.ModelSettingVo;
import com.xmzs.system.mapper.AiQaCaseMapper;
import com.xmzs.system.service.IAiConversationStartersService;
import com.xmzs.system.service.IAiQaTemplateService;
import com.xmzs.system.service.IChatCostService;
import com.xmzs.system.service.IModelSettingService;
import com.xmzs.system.util.WebSocketUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * packageName com.xmzs.system.handler
 *
 * @author zhuzi
 * @date 2025/1/2
 * @description 通用消息处理
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MessageHandler {

    private final IChatCostService chatService;

    private final IModelSettingService modelSettingService;

    private final IAiConversationStartersService aiConversationStartersService;

    private final IAiQaTemplateService aiQaTemplateService;

    private final EmbeddingService embeddingService;

    private final AiQaCaseMapper aiQaCaseMapper;

    //向量数据库
    private final VectorStore vectorStore;
    private final Vectorization vectorization;

    /**
     * 消息处理
     * @param chatRequest
     * @param msgList
     * @param type
     *   pc  xcx
     * @return
     */
    public ResultData msgHandler(ChatRequest chatRequest, List<Message> msgList,String type,WebSocketSession session){

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
//            sendErrorEvent(sseEmitter,"管理员尚未对该业务模型进行配置，请先配置模型参数！");
//            return sseEmitter;

            String errorContent = "管理员尚未对该业务模型进行配置，请先配置模型参数！";
            return ResultData.fail(ChatStatus.WARN,errorContent);
        }
        ModelSettingVo useModelSetting = list.get(0);

        //修改前端传递配置
        chatRequest.setModel(useModelSetting.getSysModel().getModelName());
        chatRequest.setSystemMessage(useModelSetting.getSystemRole());
        chatRequest.setTop_p(useModelSetting.getTopP());
        chatRequest.setTemperature(useModelSetting.getTemperature());
        chatRequest.setContentNumber(useModelSetting.getTalkCount());
        chatRequest.setMaxTokens(useModelSetting.getMaxTokens());

        // 用户提问消息
        Message message = msgList.get(msgList.size() - 1);

        // 复用
        Message userQ = message;
        ChatMessageBo chatMessageBo = new ChatMessageBo();
        if(DeviceType.PC.getDevice().equals(type)){
            LoginUser loginUser = LoginHelper.getLoginUser();
            if(ObjectUtils.isEmpty(loginUser)){
                String errorContent = "user is logout！";
                return ResultData.fail(ChatStatus.WARN,errorContent);
            }else{

                chatMessageBo.setUserId(loginUser.getUserId());
            }
        }else{
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
            LoginUser loginUser = LoginHelper.getLoginUser(decodedToken);
            if(ObjectUtils.isEmpty(loginUser)){
                String errorContent = "user is logout！";
                return ResultData.fail(ChatStatus.WARN,errorContent);
            }else{
                LocalCache.CACHE.put("userId",loginUser.getUserId());
                chatMessageBo.setUserId(loginUser.getUserId());
            }




        }

//        chatMessageBo.setModelName(chatRequest.getModel());
        chatMessageBo.setModelName(useModelSetting.getSysModel().getModelName());
        chatMessageBo.setContent(message.getContent());
        chatMessageBo.setMsgId(chatRequest.getUuid());
        //此处检查费用相关
        //计费
        ResultData res =  chatService.deductUseCount(chatMessageBo,msgList);
        if(!"200".equals(res.getCode())){
                String errorContent = res.getCode()+res.getMessage();
                return ResultData.fail(res.getCode(),errorContent);

//            sendErrorEvent(sseEmitter,res.getCode()+res.getMessage());
//            return sseEmitter;
        }

//        try{
//            //计费
//            chatService.deductUseCount(chatMessageBo);
//        }catch (Exception e){
//            sendErrorEvent(sseEmitter, e.getMessage());
//            return sseEmitter;
//        }


        //替换第一个系统角色
        //这里增加一个如果第一条不是系统角色则需要增加
        if(msgList.size()==1){
            Message fristSysRole = new Message();
            fristSysRole.setRole(Message.Role.SYSTEM.getName());
            fristSysRole.setContent(useModelSetting.getSystemRole());
            msgList.add(0,fristSysRole);
        }else{
            Message checkMessageSystemRole = msgList.get(0);
            checkMessageSystemRole.setRole(Message.Role.SYSTEM.getName());
            checkMessageSystemRole.setContent(useModelSetting.getSystemRole());
        }


//        checkMessageSystemRole.setName("");

        //是否使用知识库
        Boolean startUserKnowledge = false;
        //查看是否为对话开场 && 为非固定 则使用以下
        if(chatRequest.getIs_starters()!=null&&chatRequest.getIs_starters()){
            String startersId =  chatRequest.getStarters_id();
            if(StringUtils.isNotBlank(startersId)){
                AiConversationStartersVo conversationStartersVo =  aiConversationStartersService.queryById(Long.valueOf(startersId));
                startUserKnowledge =  "0".equals(conversationStartersVo.getUseKnowledge());

                if(!ObjectUtils.isEmpty(conversationStartersVo)&& "0".equals(conversationStartersVo.getTemplateFixed()) ){
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
//                                msgList.add(userQ);
                            }else{
                                template = template.replace("{{question}}",message.getContent());
                                message.setContent( template);
                            }
                        }

                    }


                }

            }
        }


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
        Map<String,Object> map = new HashMap<>();
        map.put("msgList",msgList);
        map.put("chatRequest",chatRequest);
        return ResultData.success(map);

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


//    /**
//     * 获取用户Id
//     *
//     * @return
//     */
//    public Long getUserId(){
//        LoginUser loginUser = LoginHelper.getLoginUser();
//        if (loginUser == null) {
//            throw new NotLoginException("Login failure！","","");
//        }
//        return loginUser.getUserId();
//    }
//
//    /**
//     * 小程序端 获取用户Id
//     *
//     * @return
//     */
//    public Long getXcxUserId( String token){
//        LoginUser loginUser = LoginHelper.getLoginUser(token);
//        if (loginUser == null) {
////            WebSocketUtils.sendMessage(session, "{\n" +
////                " \"data\":{\n" +
////                "   \"content\":\"登录失败，请重新登录！\"\n" +
////                " }\n" +
////                "}");
////            throw new NotLoginException("用户未登录！","","");
//            WebSocketUtils.sendErrorMessages(ChatStatus.WARN,"登录失败，请重新登录");
//            return null;
//        }
//        return loginUser.getUserId();
//    }

}
