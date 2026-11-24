package com.xmzs.system.service;

import com.xmzs.system.domain.FineTuneAttach;
import com.xmzs.system.domain.vo.FineTuneAttachVo;
import com.xmzs.system.domain.bo.FineTuneAttachBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 微调附件Service接口
 *
 * @author zhuzi
 * @date 2024-07-04
 */
public interface IFineTuneAttachService {

    /**
     * 查询微调附件
     */
    FineTuneAttachVo queryById(Long id);

    /**
     * 查询微调附件列表
     */
    TableDataInfo<FineTuneAttachVo> queryPageList(FineTuneAttachBo bo, PageQuery pageQuery);

    /**
     * 查询微调附件列表
     */
    List<FineTuneAttachVo> queryList(FineTuneAttachBo bo);

    /**
     * 新增微调附件
     */
    Boolean insertByBo(FineTuneAttachBo bo);

    /**
     * 修改微调附件
     */
    Boolean updateByBo(FineTuneAttachBo bo);

    /**
     * 校验并批量删除微调附件信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    FineTuneAttach getFirstattachByftId(Long attachId);
}
