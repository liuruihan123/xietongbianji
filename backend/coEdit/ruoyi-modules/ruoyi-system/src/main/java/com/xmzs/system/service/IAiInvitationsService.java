package com.xmzs.system.service;

import com.xmzs.system.domain.AiInvitations;
import com.xmzs.system.domain.vo.AiInvitationsVo;
import com.xmzs.system.domain.bo.AiInvitationsBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 邀请管理Service接口
 *
 * @author zuleed
 * @date 2024-12-11
 */
public interface IAiInvitationsService {

    /**
     * 查询邀请管理
     */
    AiInvitationsVo queryById(Long id);

    /**
     * 查询邀请管理列表
     */
    TableDataInfo<AiInvitationsVo> queryPageList(AiInvitationsBo bo, PageQuery pageQuery);

    /**
     * 查询邀请管理列表
     */
    List<AiInvitationsVo> queryList(AiInvitationsBo bo);

    /**
     * 新增邀请管理
     */
    Boolean insertByBo(AiInvitationsBo bo);

    /**
     * 修改邀请管理
     */
    Boolean updateByBo(AiInvitationsBo bo);

    /**
     * 校验并批量删除邀请管理信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
