package com.xmzs.system.service;

import com.xmzs.system.domain.PkulawLaw;
import com.xmzs.system.domain.vo.LawsVo;
import com.xmzs.system.domain.vo.PkulawLawVo;
import com.xmzs.system.domain.bo.PkulawLawBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 法律条文Service接口
 *
 * @author zuleed
 * @date 2025-05-10
 */
public interface IPkulawLawService {

    /**
     * 查询法律条文
     */
    PkulawLawVo queryById(Long id);


    String queryByGid(String gid);
    List<LawsVo> queryByGids(List<String> gids);
    /**
     * 查询法律条文列表
     */
    TableDataInfo<PkulawLawVo> queryPageList(PkulawLawBo bo, PageQuery pageQuery);

    /**
     * 查询法律条文列表
     */
    List<PkulawLawVo> queryList(PkulawLawBo bo);

    /**
     * 新增法律条文
     */
    Boolean insertByBo(PkulawLawBo bo);

    /**
     * 修改法律条文
     */
    Boolean updateByBo(PkulawLawBo bo);

    /**
     * 校验并批量删除法律条文信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
