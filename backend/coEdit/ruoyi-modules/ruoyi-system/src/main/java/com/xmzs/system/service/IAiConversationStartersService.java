package com.xmzs.system.service;

import com.xmzs.system.domain.AiConversationStarters;
import com.xmzs.system.domain.vo.AiConversationStartersVo;
import com.xmzs.system.domain.bo.AiConversationStartersBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 对话开场Service接口
 *
 * @author zuleed
 * @date 2024-11-26
 */
public interface IAiConversationStartersService {

    /**
     * 查询对话开场
     */
    AiConversationStartersVo queryById(Long id);

    /**
     * 查询对话开场列表
     */
    TableDataInfo<AiConversationStartersVo> queryPageList(AiConversationStartersBo bo, PageQuery pageQuery);

    /**
     * 根据业务类型查询
     */
    List<AiConversationStartersVo> queryByBusiTypeList(AiConversationStartersBo bo);
    /**
     * 查询对话开场列表
     */
    List<AiConversationStartersVo> queryList(AiConversationStartersBo bo);

    /**
     * 新增对话开场
     */
    Boolean insertByBo(AiConversationStartersBo bo);

    /**
     * 修改对话开场
     */
    Boolean updateByBo(AiConversationStartersBo bo);

    /**
     * 校验并批量删除对话开场信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
