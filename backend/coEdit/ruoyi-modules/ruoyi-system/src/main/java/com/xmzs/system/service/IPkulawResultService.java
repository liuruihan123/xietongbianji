package com.xmzs.system.service;

import com.xmzs.system.domain.PkulawResult;
import com.xmzs.system.domain.vo.PkulawResultVo;
import com.xmzs.system.domain.bo.PkulawResultBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 判案结果Service接口
 *
 * @author zuleed
 * @date 2025-05-10
 */
public interface IPkulawResultService {

    /**
     * 查询判案结果
     */
    PkulawResultVo queryById(Long id);

    /**
     * 查询判案结果列表
     */
    TableDataInfo<PkulawResultVo> queryPageList(PkulawResultBo bo, PageQuery pageQuery);

    /**
     * 查询判案结果列表
     */
    List<PkulawResultVo> queryList(PkulawResultBo bo);

    /**
     * 新增判案结果
     */
    Boolean insertByBo(PkulawResultBo bo);

    /**
     * 修改判案结果
     */
    Boolean updateByBo(PkulawResultBo bo);

    /**
     * 校验并批量删除判案结果信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    Boolean handleLawTest();
}
