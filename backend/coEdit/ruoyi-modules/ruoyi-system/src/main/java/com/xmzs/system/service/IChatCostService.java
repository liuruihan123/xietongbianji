package com.xmzs.system.service;

import com.xmzs.common.chat.entity.chat.Message;
import com.xmzs.knowledge.common.ResultData;
import com.xmzs.system.domain.bo.ChatMessageBo;

import java.util.List;

public interface IChatCostService {

    /**
     * 扣除使用次数得费用
     * @param chatMessageBo
     */
    ResultData deductUseCount(ChatMessageBo chatMessageBo, List<Message> msgList);

    /**
     * 根据消耗的tokens扣除余额
     *
     * @param chatMessageBo
     * @return 结果
     */

    void deductToken(ChatMessageBo chatMessageBo);

    /**
     * 扣除用户的余额
     *
     */
    void deductUserBalance(Long userId, Double numberCost);


    /**
     * 扣除任务费用并且保存记录
     *
     * @param type 任务类型
     * @param prompt 任务描述
     * @param cost 扣除费用
     */
    void taskDeduct(String type,String prompt, double cost);


    /**
     * 判断用户是否付费
     */
    void checkUserGrade();

    /**
     * 标准rag模板,一般用于面试
     * 结合rag结果，重排prompts ，
     * @param vectorSearch
     * @return
     */
    String outputStandardTemplate(String vectorSearch,String question);

    /**
     * 标准严格rag模板 用于公司信息
     * 非知识库内容拒绝回答 ，
     * @param vectorSearch
     * @return
     */
    String outputStrictStandardTemplate(String vectorSearch,String question);

    /**
     * 标准严格rag模板 用于面试要点
     * 非知识库内容拒绝回答 ，
     * @param vectorSearch
     * @return
     */
    String outputStrictStandardTemplate2(String vectorSearch,String question);
}
