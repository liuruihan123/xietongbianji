package com.xmzs.system.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xmzs.common.chat.constant.OpenAIConst;
import com.xmzs.common.chat.entity.chat.ChatCompletion;
import com.xmzs.common.chat.entity.chat.Message;
import com.xmzs.common.chat.utils.TikTokensUtil;
import com.xmzs.common.core.constant.ChatStatus;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.exception.ServiceException;
import com.xmzs.common.core.exception.base.BaseException;
import com.xmzs.common.core.utils.DateUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.knowledge.common.ResultData;
import com.xmzs.system.domain.ChatToken;
import com.xmzs.system.domain.SysUser;
import com.xmzs.system.domain.bo.ChatMessageBo;
import com.xmzs.system.domain.bo.SysModelBo;
import com.xmzs.system.domain.vo.SysModelVo;
import com.xmzs.system.mapper.SysUserMapper;
import com.xmzs.system.service.IChatMessageService;
import com.xmzs.system.service.IChatCostService;
import com.xmzs.system.service.IChatTokenService;
import com.xmzs.system.service.ISysModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/3/22 19:41
 * 聊天相关业务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatCostServiceImpl implements IChatCostService {

    private final SysUserMapper sysUserMapper;

    private final IChatMessageService chatMessageService;

    private final IChatTokenService chatTokenService;

    private final ISysModelService sysModelService;

    /**
     * 扣除使用次数
     * @param chatMessageBo
     */
    public ResultData deductUseCount(ChatMessageBo chatMessageBo, List<Message> msgList) {
        Long userId =  chatMessageBo.getUserId();
        String lg ="";
        if(StringUtils.isEmpty(chatMessageBo.getContent())){
            lg = "en";
        }else{
            lg =  detectLanguage(chatMessageBo.getContent());
        }

        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
//            throw new BaseException("用户不存在！");
            if("cn".equals(lg)){
                return  ResultData.fail(ChatStatus.WARN, "用户不存在");
            }else{
                return  ResultData.fail(ChatStatus.WARN,"user does not exist");
            }
        }
        Integer surplus = sysUser.getSubMsgCount();

        Integer points = sysUser.getPoints();

        if(points==0){

        }
        //判断是否是会员 (RandomUtil.randomString(6));
        if(surplus==0){
            String errorjsonTemp = "";
            if("0".equals(sysUser.getUserGrade())){
//                throw new ServiceException("您的免费次数已用完，请升级使用。<free_tip>");
                if("cn".equals(lg)){
                    return   ResultData.fail(ChatStatus.WARN_NUM_UPGRADE,"您每天的免费对话额度已用完，请明天再来或升级为高级用户。");
                }else{
                    return   ResultData.fail(ChatStatus.WARN_NUM_UPGRADE,"Your daily limit of free conversations has been used up. Please come back tomorrow or upgrade to a premium user.");
                }

            }else{
                if("cn".equals(lg)){
                    return  ResultData.fail(ChatStatus.WARN_VIP_NUM,"您今天的对话次数已用完，请明天再来继续使用!");
                }else{
                    return  ResultData.fail(ChatStatus.WARN_VIP_NUM,"You have used up your quota for today. Please return tomorrow to continue.");
                }
//                throw new ServiceException("您今日的次数已用完，请明天再来继续使用!<vip_tip>");
            }

        }
//        sysUser.setSubMsgCount(surplus-1);
        sysUserMapper.update(null,
            new LambdaUpdateWrapper<SysUser>()
                .set(SysUser::getSubMsgCount, Math.max(surplus - 1, 0))
                .eq(SysUser::getUserId, userId));

        //记录token消耗
        int tokens = TikTokensUtil.tokens(chatMessageBo.getModelName(), msgList);
        chatMessageBo.setTotalTokens(tokens);
        // 保存消息记录
        chatMessageService.insertByBo(chatMessageBo);
        return  ResultData.success("200","扣费成功");
    }




    /**
     * 判断中英文
     * @param message
     * @return
     */
    public static String detectLanguage(String message) {
        // 正则表达式匹配中文字符
         String CHINESE_REGEX = "[\\u4e00-\\u9fa5]";
        // 正则表达式匹配英文字符
        String ENGLISH_REGEX = "[a-zA-Z]";
        int chineseCount = 0;
        int englishCount = 0;

        for (char c : message.toCharArray()) {
            if (String.valueOf(c).matches(CHINESE_REGEX)) {
                chineseCount++;
            } else if (String.valueOf(c).matches(ENGLISH_REGEX)) {
                englishCount++;
            }
        }

        // 计算中文字符占总字符的比例
        int totalCount = chineseCount + englishCount;
        if (totalCount == 0) {
            return "en";
        }

        double chineseRatio = (double) chineseCount / totalCount;

        // 如果中文字符比例超过50%，则认为是中文
        if (chineseRatio > 0.5) {
            return "cn";
        } else {
            return "en";
        }
    }
    /**
     * 根据消耗的tokens扣除余额
     *
     * @param chatMessageBo
     */
    public void deductToken(ChatMessageBo chatMessageBo) {
        // 计算总token数
        ChatToken chatToken = chatTokenService.queryByUserId(chatMessageBo.getUserId(), chatMessageBo.getModelName());
        if (chatToken == null) {
            chatToken = new ChatToken();
            chatToken.setToken(0);
        }
        int totalTokens = chatToken.getToken() + chatMessageBo.getTotalTokens();
        // 如果总token数大于等于1000,进行费用扣除
        if (totalTokens >= 1000) {
            // 计算费用
            int token1 = totalTokens / 1000;
            int token2 = totalTokens % 1000;
            if (token2 > 0) {
                // 保存剩余tokens
                chatToken.setToken(token2);
                chatTokenService.editToken(chatToken);
            } else {
                chatTokenService.resetToken(chatMessageBo.getUserId(), chatMessageBo.getModelName());
            }
            // 扣除用户余额

            SysModelBo sysModelBo = new SysModelBo();
            sysModelBo.setModelName(chatMessageBo.getModelName());
            List<SysModelVo> sysModelList = sysModelService.queryList(sysModelBo);
            double modelPrice = sysModelList.get(0).getModelPrice();
            Double numberCost = token1 * modelPrice;
            //TODO 暂时删除扣费
//            deductUserBalance(chatMessageBo.getUserId(), numberCost);
            chatMessageBo.setDeductCost(numberCost);
        } else {
            // 扣除用户余额
            //TODO 暂时删除扣费
//            deductUserBalance(chatMessageBo.getUserId(), 0.0);
            chatMessageBo.setDeductCost(0d);
            chatMessageBo.setRemark("不满1kToken,计入下一次!");
            chatToken.setToken(totalTokens);
            chatToken.setModelName(chatMessageBo.getModelName());
            chatToken.setUserId(chatMessageBo.getUserId());
            chatTokenService.editToken(chatToken);
        }
        // 保存消息记录
        chatMessageService.insertByBo(chatMessageBo);
    }



    /**
     * 从用户余额中扣除费用
     *
     * @param userId     用户ID
     * @param numberCost 要扣除的费用
     */
    @Override
    public void deductUserBalance(Long userId, Double numberCost) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            return;
        }

        Double userBalance = sysUser.getUserBalance();
        if (userBalance < numberCost || userBalance == 0) {
            throw new ServiceException("余额不足，请联系管理员充值!");
        }
        sysUserMapper.update(null,
            new LambdaUpdateWrapper<SysUser>()
                .set(SysUser::getUserBalance, Math.max(userBalance - numberCost, 0))
                .eq(SysUser::getUserId, userId));
    }



    /**
     * 扣除任务费用
     *
     */
    @Override
    public void taskDeduct(String type,String prompt, double cost) {
        // 判断用户是否付费
        checkUserGrade();
        // 扣除费用
        deductUserBalance(getUserId(), cost);
        // 保存消息记录
        ChatMessageBo chatMessageBo = new ChatMessageBo();
        chatMessageBo.setUserId(getUserId());
        chatMessageBo.setModelName(type);
        chatMessageBo.setContent(prompt);
        chatMessageBo.setDeductCost(cost);
        chatMessageBo.setTotalTokens(0);
        chatMessageService.insertByBo(chatMessageBo);
    }

    /**
     * 判断用户是否付费
     */
    @Override
    public void checkUserGrade() {
        SysUser sysUser = sysUserMapper.selectById(getUserId());
        if(sysUser.getVipExpiryDate()!=null){
            Long vipExpiryDate = sysUser.getVipExpiryDate();
            boolean expired =  DateUtils.isExpired(vipExpiryDate);
            if(expired){
                log.info(sysUser.getUserName()+"会员已过期");
                sysUser.setUserGrade("0");
            }
        }else{
            sysUser.setUserGrade("0");
        }

        if("0".equals(sysUser.getUserGrade())){
            throw new BaseException("您不是付费用户暂时无法继续使用！");
        }
    }

    /**
     * 获取用户Id
     *
     * @return
     */
    public Long getUserId() {
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (loginUser == null) {
            throw new BaseException("用户未登录！");
        }
        return loginUser.getUserId();
    }

    public static void main(String[] args) {
        String vectorSearch = "{{data}}";
        String question = "{{question}}";
        String prompts = "Forget your existing knowledge and only use the content in the<Data></Data>tag and the dialogue context as your knowledge:\n" +
            "<Data>"+vectorSearch+"</Data> \n" +
            "Answer requirements：\n" +
            "- You are a senior recruiting partner at a top strategy consulting firm.  \n" +
            "- Your primary task is to provide detailed, structured, and engaging answers to my questions regarding key aspects and critical attention points for strategy consulting firm recruitment interviews. \n" +
            "- You must draw answers from <Data></Data> in your knowledge base to provide comprehensive, accurate, and structured responses. \n" +
            "- If I ask unrelated questions, you should politely explain your focus, and redirect the conversation back to the topic of recruitment interviews for consulting firms." +
            "- You must conduct conversations in my native language with flawless grammar and clarity." +
            "My Question::\"\"\""+question+"\"\"\"";
        System.out.println(prompts);
    }

    /**
     * 标准rag模板,一般用于面试
     * 结合rag结果，重排prompts ，
     * @param vectorSearch
     * @return
     */
    public String outputStandardTemplate(String vectorSearch, String question){

        String prompts = "Use the content in the<Data></Data>tag and the dialogue context as your knowledge:\n" +
            "<Data>"+vectorSearch+"</Data> \n" +
            "Answer requirements：\n" +
            "- You must draw relevant information from <Data></Data> in your knowledge base.\n" +
            "- You are a senior recruiting partner at a top strategy consulting firm. \n" +
            "- You are delivering a realistic and challenging case interview simulations that mirror those used in strategy consulting recruitment processes with me.\n" +
            "- You could extend or enrich the content logically if your interactions with me deviate from predefined scenarios, ensuring alignment with the case objectives and interview setting. \n" +
            "- You must conduct conversations in my native language with flawless grammar and clarity. \n"+
            "- If I ask unrelated questions, you need to politely explain that your sole purpose is to simulate case interviews, and redirect me to the case interview topic.\n" +
            "- Please stop the interview at the appropriate time. Generally, there are about 50 conversations. Then inform the user that the interview is over and provide some polite feedback \n" +
            "My Question:\"\"\""+question+"\"\"\"";
        return prompts;
    }




    /**
     * 标准严格rag模板 用于公司信息
     * 非知识库内容拒绝回答 ，
     * @param vectorSearch
     * @return
     */
    public String outputStrictStandardTemplate(String vectorSearch,String question){

        String prompts = "Forget your existing knowledge and only use the content in the<Data></Data>tag and the dialogue context as your knowledge:\n" +
            "<Data>"+vectorSearch+"</Data> \n" +
            "Answer requirements：\n" +
            "- You are a senior recruiting partner at a top strategy consulting firm. \n" +
            "- Your primary task is to provide detailed, structured, and engaging answers regarding all aspects of top strategy consulting firms.\n" +
            "- You must draw answers from <Data></Data> in your knowledge base to provide comprehensive, accurate, and structured responses.\n" +
            "- If I ask unrelated questions, you need to politely explain that your sole purpose is to provide information regarding top strategy consulting firms, and gently redirect the conversation back to relevant topics." +
            "- You must conduct conversations in my native language with flawless grammar and clarity." +
            "My Question::\"\"\""+question+"\"\"\"";
        return prompts;
    }


    /**
     * 标准严格rag模板 用于面试要点
     * 非知识库内容拒绝回答 ，
     * @param vectorSearch
     * @return
     */
    public String outputStrictStandardTemplate2(String vectorSearch,String question){

        String prompts = "Forget your existing knowledge and only use the content in the<Data></Data>tag and the dialogue context as your knowledge:\n" +
            "<Data>"+vectorSearch+"</Data> \n" +
            "Answer requirements：\n" +
            "- You are a senior recruiting partner at a top strategy consulting firm.  \n" +
            "- Your primary task is to provide detailed, structured, and engaging answers to my questions regarding key aspects and critical attention points for strategy consulting firm recruitment interviews. \n" +
            "- You must draw answers from <Data></Data> in your knowledge base to provide comprehensive, accurate, and structured responses. \n" +
            "- If I ask unrelated questions, you should politely explain your focus, and redirect the conversation back to the topic of recruitment interviews for consulting firms." +
            "- You must conduct conversations in my native language with flawless grammar and clarity." +
            "My Question::\"\"\""+question+"\"\"\"";
        return prompts;
    }
}
