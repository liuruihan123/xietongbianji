package com.xmzs.system.service;

import com.xmzs.system.domain.AiQaCase;
import com.xmzs.system.domain.request.UploadAttachRequest;
import com.xmzs.system.domain.vo.AiQaCaseVo;
import com.xmzs.system.domain.bo.AiQaCaseBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.system.domain.vo.QaCaseTreeNode;

import java.util.Collection;
import java.util.List;

/**
 * 对话案例Service接口
 *
 * @author zuleed
 * @date 2024-12-27
 */
public interface IAiQaCaseService {

    /**
     * 查询对话案例
     */
    AiQaCaseVo queryById(Long id);

    /**
     * 查询对话案例列表
     */
    TableDataInfo<AiQaCaseVo> queryPageList(AiQaCaseBo bo, PageQuery pageQuery);

    /**
     * 查询对话案例列表
     */
    List<AiQaCaseVo> queryList(AiQaCaseBo bo);

    public List<QaCaseTreeNode> getTreeData();

    public void upload(UploadAttachRequest request);
    /**
     * 新增对话案例
     */
    Boolean insertByBo(AiQaCaseBo bo);

    /**
     * 修改对话案例
     */
    Boolean updateByBo(AiQaCaseBo bo);

    /**
     * 校验并批量删除对话案例信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
