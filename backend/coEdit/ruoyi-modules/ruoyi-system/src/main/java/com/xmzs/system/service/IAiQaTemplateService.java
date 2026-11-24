package com.xmzs.system.service;

import com.xmzs.system.domain.AiQaTemplate;
import com.xmzs.system.domain.vo.AiQaTemplateVo;
import com.xmzs.system.domain.bo.AiQaTemplateBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 问答模版Service接口
 *
 * @author zuleed
 * @date 2024-12-19
 */
public interface IAiQaTemplateService {

    /**
     * 查询问答模版
     */
    AiQaTemplateVo queryById(Long id);

    /**
     * 查询问答模版列表
     */
    TableDataInfo<AiQaTemplateVo> queryPageList(AiQaTemplateBo bo, PageQuery pageQuery);

    /**
     * 查询问答模版列表
     */
    List<AiQaTemplateVo> queryList(AiQaTemplateBo bo);

    /**
     * 新增问答模版
     */
    Boolean insertByBo(AiQaTemplateBo bo);

    /**
     * 修改问答模版
     */
    Boolean updateByBo(AiQaTemplateBo bo);

    /**
     * 校验并批量删除问答模版信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
