package com.xmzs.common.chat.domain.request;

import com.xmzs.common.chat.entity.chat.Content;
import com.xmzs.common.chat.entity.chat.Message;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 描述：
 *
 * @author https:www.unfbx.com
 * @sine 2023-04-08
 */
@Data
public class ChatRequest {

    private String uuid;

    @NotEmpty(message = "传入的模型不能为空")
    private String model;

    @NotEmpty(message = "对话消息不能为空")
        //(前端决定)
    List<Message> messages;

    List<Content> imageContent;

    private String prompt;
//(前端决定)
    private String userId;

    //是否是开场对话
    private Boolean is_starters;

    //开场对话id
    private String starters_id;

    /**
     * 知识库id
     */
    private String kid;

    /**
     * gpt的默认设置
     */
    private String systemMessage = "";

    private double top_p = 1;

    private double temperature = 0.2;

    /**
     * 上下文的条数
     */
    private Integer contentNumber = 10;

    /**
     * 是否携带上下文 (前端决定)
     */
    private Boolean usingContext = Boolean.TRUE;

    /**
     * 业务类型 前端对应值分别是 intv 面试  company公司咨询  point 要点总结 (前端决定)
     */
    private String busiType;

    private Integer maxTokens;

}
