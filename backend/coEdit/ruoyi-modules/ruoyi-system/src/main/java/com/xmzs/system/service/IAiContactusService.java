package com.xmzs.system.service;

import com.xmzs.system.domain.AiContactus;
import com.xmzs.system.domain.vo.AiContactusVo;
import com.xmzs.system.domain.bo.AiContactusBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * contactusService接口
 *
 * @author zuleed
 * @date 2024-12-04
 */
public interface IAiContactusService {

    /**
     * 查询contactus
     */
    AiContactusVo queryById(Long id);

    /**
     * 查询contactus列表
     */
    TableDataInfo<AiContactusVo> queryPageList(AiContactusBo bo, PageQuery pageQuery);

    /**
     * 查询contactus列表
     */
    List<AiContactusVo> queryList(AiContactusBo bo);

    /**
     * 新增contactus
     */
    Boolean insertByBo(AiContactusBo bo);

    /**
     * 修改contactus
     */
    Boolean updateByBo(AiContactusBo bo);

    /**
     * 校验并批量删除contactus信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
