package com.xmzs.system.service;

import com.xmzs.system.domain.PromptsStore;
import com.xmzs.system.domain.vo.PromptsStoreVo;
import com.xmzs.system.domain.bo.PromptsStoreBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 提示词商店Service接口
 *
 * @author zuleed
 * @date 2024-10-15
 */
public interface IPromptsStoreService {

    /**
     * 查询提示词商店
     */
    PromptsStoreVo queryById(Integer id);

    /**
     * 查询提示词商店列表
     */
    TableDataInfo<PromptsStoreVo> queryPageList(PromptsStoreBo bo, PageQuery pageQuery);

    /**
     * 查询提示词商店列表
     */
    List<PromptsStoreVo> queryList(PromptsStoreBo bo);

    /**
     * 新增提示词商店
     */
    Boolean insertByBo(PromptsStoreBo bo);

    /**
     * 修改提示词商店
     */
    Boolean updateByBo(PromptsStoreBo bo);

    /**
     * 校验并批量删除提示词商店信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
