package com.xmzs.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmzs.knowledge.domain.Conversation;
import com.xmzs.knowledge.mapper.ConversationMapper;
import com.xmzs.knowledge.service.ConversationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
* @author huangkh
* @description 针对表【conversation(对话)】的数据库操作Service实现
* @createDate 2023-06-20 16:58:23
*/
@Service
@AllArgsConstructor
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements ConversationService{

    private final ConversationMapper conversationMapper;
    @Override
    public int saveConversation(Integer userId, String sessionId, String content, String qa) {
        Conversation conversation = new Conversation();
        conversation.setUserId(userId);
        conversation.setSid(sessionId);
        conversation.setContent(content);
        conversation.setType(qa);
        conversation.setQaTime(LocalDateTime.now());
        conversation.setCreateTime(LocalDateTime.now());
        conversationMapper.insert(conversation);
        return conversation.getId();
    }

    @Override
    public List<Conversation> listBySessionId(String sessionId) {
        QueryWrapper<Conversation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sid",sessionId);
        queryWrapper.orderByAsc("id");
        List<Conversation> conversationList = list(queryWrapper);
        return conversationList;
    }

    @Override
    public void removeBySid(String sid) {
        QueryWrapper<Conversation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sid",sid);
        remove(queryWrapper);
    }

    @Override
    public List<Conversation> history(String sid) {
        QueryWrapper<Conversation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sid",sid);
        queryWrapper.orderByDesc("id");
        queryWrapper.last(" limit 4");
        List<Conversation> list = list(queryWrapper);
        Collections.reverse(list);
        return list;
    }

}




