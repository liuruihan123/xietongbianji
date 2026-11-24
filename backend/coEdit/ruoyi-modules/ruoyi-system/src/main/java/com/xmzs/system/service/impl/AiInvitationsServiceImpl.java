package com.xmzs.system.service.impl;

import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.xmzs.system.domain.bo.AiInvitationsBo;
import com.xmzs.system.domain.vo.AiInvitationsVo;
import com.xmzs.system.domain.AiInvitations;
import com.xmzs.system.mapper.AiInvitationsMapper;
import com.xmzs.system.service.IAiInvitationsService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 邀请管理Service业务层处理
 *
 * @author zuleed
 * @date 2024-12-11
 */
@RequiredArgsConstructor
@Service
public class AiInvitationsServiceImpl implements IAiInvitationsService {

    private final AiInvitationsMapper baseMapper;

    /**
     * 查询邀请管理
     */
    @Override
    public AiInvitationsVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询邀请管理列表
     */
    @Override
    public TableDataInfo<AiInvitationsVo> queryPageList(AiInvitationsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AiInvitations> lqw = buildQueryWrapper(bo);
        Page<AiInvitationsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询邀请管理列表
     */
    @Override
    public List<AiInvitationsVo> queryList(AiInvitationsBo bo) {
        LambdaQueryWrapper<AiInvitations> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AiInvitations> buildQueryWrapper(AiInvitationsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<AiInvitations> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getInviterId() != null, AiInvitations::getInviterId, bo.getInviterId());
        lqw.like(StringUtils.isNotBlank(bo.getInviterUserName()), AiInvitations::getInviterUserName, bo.getInviterUserName());
        lqw.eq(bo.getInviteeId() != null, AiInvitations::getInviteeId, bo.getInviteeId());
        lqw.like(StringUtils.isNotBlank(bo.getInviteeUserName()), AiInvitations::getInviteeUserName, bo.getInviteeUserName());
        lqw.eq(StringUtils.isNotBlank(bo.getInviterCode()), AiInvitations::getInviterCode, bo.getInviterCode());
        lqw.eq(bo.getRewardDays() != null, AiInvitations::getRewardDays, bo.getRewardDays());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), AiInvitations::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增邀请管理
     */
    @Override
    public Boolean insertByBo(AiInvitationsBo bo) {
        AiInvitations add = MapstructUtils.convert(bo, AiInvitations.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改邀请管理
     */
    @Override
    public Boolean updateByBo(AiInvitationsBo bo) {
        AiInvitations update = MapstructUtils.convert(bo, AiInvitations.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AiInvitations entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除邀请管理
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
