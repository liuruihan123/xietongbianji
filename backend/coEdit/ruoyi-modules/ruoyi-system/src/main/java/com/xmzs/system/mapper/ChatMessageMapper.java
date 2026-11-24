package com.xmzs.system.mapper;

import com.xmzs.common.mybatis.core.mapper.BaseMapperPlus;
import com.xmzs.system.domain.ChatMessage;
import com.xmzs.system.domain.vo.ChatMessageVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


/**
 * 聊天消息Mapper接口
 *
 * @author Lion Li
 * @date 2023-11-26
 */
public interface ChatMessageMapper extends BaseMapperPlus<ChatMessage, ChatMessageVo> {

    /**
     * 计算对话数
     * @param beginTime
     * @param endTime
     * @return
     */
    @Select("SELECT count(id) as count FROM chat_message WHERE create_time >= STR_TO_DATE(#{beginTime}, '%Y-%m-%d %H:%i:%s') AND create_time <= STR_TO_DATE(#{endTime}, '%Y-%m-%d %H:%i:%s') ")
    Integer getCount(@Param("beginTime") String beginTime, @Param("endTime") String endTime);


    /**
     * 获取最近15天的注册用户
     * @return
     */
    @Select("SELECT DATE(create_time) as date, count(id) as count" +
        " FROM chat_message " +
        "WHERE  create_time >= CURDATE() - INTERVAL #{days} DAY " +
        "GROUP BY DATE(create_time) " +
        "ORDER BY DATE(create_time)")
    List<Map<String, Object>> getLastDaysCount(@Param("days") int days);




    /**
     * 计算token消耗
     * @param beginTime
     * @param endTime
     * @return
     */
    @Select("SELECT SUM(total_tokens) as count FROM chat_message WHERE create_time >= STR_TO_DATE(#{beginTime}, '%Y-%m-%d %H:%i:%s') AND create_time <= STR_TO_DATE(#{endTime}, '%Y-%m-%d %H:%i:%s') ")
    Long  getTokenCount(@Param("beginTime") String beginTime, @Param("endTime") String endTime);


    /**
     * 获取最近15天的token消耗
     * @return
     */
    @Select("SELECT DATE(create_time) as date, SUM(total_tokens) as count" +
        " FROM chat_message " +
        "WHERE  create_time >= CURDATE() - INTERVAL #{days} DAY " +
        "GROUP BY DATE(create_time) " +
        "ORDER BY DATE(create_time)")
    List<Map<String, Object>> getLastDaysTokenCount(@Param("days") int days);
}
