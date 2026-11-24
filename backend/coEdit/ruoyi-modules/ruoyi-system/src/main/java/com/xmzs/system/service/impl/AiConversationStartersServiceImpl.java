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
import com.xmzs.system.domain.bo.AiConversationStartersBo;
import com.xmzs.system.domain.vo.AiConversationStartersVo;
import com.xmzs.system.domain.AiConversationStarters;
import com.xmzs.system.mapper.AiConversationStartersMapper;
import com.xmzs.system.service.IAiConversationStartersService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 对话开场Service业务层处理
 *
 * @author zuleed
 * @date 2024-11-26
 */
@RequiredArgsConstructor
@Service
public class AiConversationStartersServiceImpl implements IAiConversationStartersService {

    private final AiConversationStartersMapper baseMapper;

    /**
     * 查询对话开场
     */
    @Override
    public AiConversationStartersVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询对话开场列表
     */
    @Override
    public TableDataInfo<AiConversationStartersVo> queryPageList(AiConversationStartersBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AiConversationStarters> lqw = buildQueryWrapper(bo);
        Page<AiConversationStartersVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<AiConversationStartersVo> queryByBusiTypeList(AiConversationStartersBo bo) {
        return queryList(bo);
    }

    /**
     * 查询对话开场列表
     */
    @Override
    public List<AiConversationStartersVo> queryList(AiConversationStartersBo bo) {
        LambdaQueryWrapper<AiConversationStarters> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AiConversationStarters> buildQueryWrapper(AiConversationStartersBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<AiConversationStarters> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getUseKnowledge()), AiConversationStarters::getUseKnowledge, bo.getUseKnowledge());
        lqw.eq(StringUtils.isNotBlank(bo.getKidIds()), AiConversationStarters::getKidIds, bo.getKidIds());
        lqw.eq(bo.getBusType() != null, AiConversationStarters::getBusType, bo.getBusType());
        lqw.eq(StringUtils.isNotBlank(bo.getTags()), AiConversationStarters::getTags, bo.getTags());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), AiConversationStarters::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getCreatedBy()), AiConversationStarters::getCreatedBy, bo.getCreatedBy());
        lqw.eq(StringUtils.isNotBlank(bo.getVal1()), AiConversationStarters::getVal1, bo.getVal1());
        lqw.eq(StringUtils.isNotBlank(bo.getVal2()), AiConversationStarters::getVal2, bo.getVal2());
        return lqw;
    }

    /**
     * 新增对话开场
     */
    @Override
    public Boolean insertByBo(AiConversationStartersBo bo) {
        AiConversationStarters add = MapstructUtils.convert(bo, AiConversationStarters.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改对话开场
     */
    @Override
    public Boolean updateByBo(AiConversationStartersBo bo) {
        AiConversationStarters update = MapstructUtils.convert(bo, AiConversationStarters.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AiConversationStarters entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除对话开场
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
