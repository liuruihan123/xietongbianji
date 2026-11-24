package com.xmzs.system.mapper;

import com.xmzs.system.domain.AiQaCase;
import com.xmzs.system.domain.vo.AiQaCaseVo;
import com.xmzs.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 对话案例Mapper接口
 *
 * @author zuleed
 * @date 2024-12-27
 */
public interface AiQaCaseMapper extends BaseMapperPlus<AiQaCase, AiQaCaseVo> {

    /**
     * 根据来源分组
     * @return
     */
    @Select("SELECT source_id,source_name FROM ai_qa_case WHERE STATUS = '0' GROUP BY  source_id,source_name ")
    List<Map<String, Object>> getAiQaCaseBySourceList();

    /**
     * 根据id 获取对应的分组
     * @param ids
     * @return
     */
//    @Select("SELECT * FROM ai_qa_case WHERE status = '0' and id in (#{ids} ) ")
//    List<AiQaCaseVo> getAiQaCaseByIds(String ids);
    @Select({
        "<script>",
        "SELECT * FROM ai_qa_case",
        "WHERE status = '0' AND id IN",
        "<foreach item='id' collection='ids' open='(' separator=',' close=')'>",
        "#{id}",
        "</foreach>",
        "</script>"
    })
    List<AiQaCaseVo> getAiQaCaseByIds(@Param("ids") List<Long> ids);



}
