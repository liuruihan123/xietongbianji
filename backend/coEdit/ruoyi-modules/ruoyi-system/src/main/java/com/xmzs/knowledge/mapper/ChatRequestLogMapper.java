package com.xmzs.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xmzs.knowledge.domain.ChatRequestLog;
import org.apache.ibatis.annotations.Mapper;

/**
* @author huangkh
* @description 针对表【chat_request_log(对话请求日志)】的数据库操作Mapper
* @createDate 2023-12-23 13:55:32
* @Entity com.xmzs.knowledge.domain.ChatRequestLog
*/
@Mapper
public interface ChatRequestLogMapper extends BaseMapper<ChatRequestLog> {

}




