package com.xmzs.system.mapper;

import com.xmzs.system.domain.PkulawLaw;
import com.xmzs.system.domain.vo.AiQaCaseVo;
import com.xmzs.system.domain.vo.LawsVo;
import com.xmzs.system.domain.vo.PkulawLawVo;
import com.xmzs.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 法律条文Mapper接口
 *
 * @author zuleed
 * @date 2025-05-10
 */
public interface PkulawLawMapper extends BaseMapperPlus<PkulawLaw, PkulawLawVo> {

    /**
     * 根据来源分组
     * @return
     */
    @Select("SELECT AllText FROM pkulaw_law WHERE Gid = #{Gid} ")
    String selectLawTextByGid(String Gid);



    @Select({
        "<script>",
        "SELECT AllText,Gid,Title  FROM  pkulaw_law ",
        "WHERE Gid IN",
        "<foreach item='Gid' collection='gids' open='(' separator=',' close=')'>",
        "#{Gid}",
        "</foreach>",
        "</script>"
    })
    List<LawsVo> selectLawTextByGids(@Param("gids") List<String> gids);

}
