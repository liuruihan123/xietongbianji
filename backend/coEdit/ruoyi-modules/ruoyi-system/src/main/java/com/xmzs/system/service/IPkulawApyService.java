package com.xmzs.system.service;

import com.xmzs.system.domain.PkulawApy;
import com.xmzs.system.domain.vo.PkulawApyVo;
import com.xmzs.system.domain.bo.PkulawApyBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 案例库Service接口
 *
 * @author zuleed
 * @date 2025-05-10
 */
public interface IPkulawApyService {

    /**
     * 查询案例库
     */
    PkulawApyVo queryById(Long id);

    /**
     * 查询案例库列表
     */
    TableDataInfo<PkulawApyVo> queryPageList(PkulawApyBo bo, PageQuery pageQuery);

    /**
     * 查询案例库列表
     */
    List<PkulawApyVo> queryList(PkulawApyBo bo);

    /**
     * 新增案例库
     */
    Boolean insertByBo(PkulawApyBo bo);

    /**
     * 修改案例库
     */
    Boolean updateByBo(PkulawApyBo bo);

    /**
     * 校验并批量删除案例库信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
