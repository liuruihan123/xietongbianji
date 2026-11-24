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
import com.xmzs.system.domain.bo.PromptsStoreBo;
import com.xmzs.system.domain.vo.PromptsStoreVo;
import com.xmzs.system.domain.PromptsStore;
import com.xmzs.system.mapper.PromptsStoreMapper;
import com.xmzs.system.service.IPromptsStoreService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 提示词商店Service业务层处理
 *
 * @author zuleed
 * @date 2024-10-15
 */
@RequiredArgsConstructor
@Service
public class PromptsStoreServiceImpl implements IPromptsStoreService {

    private final PromptsStoreMapper baseMapper;

    /**
     * 查询提示词商店
     */
    @Override
    public PromptsStoreVo queryById(Integer id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询提示词商店列表
     */
    @Override
    public TableDataInfo<PromptsStoreVo> queryPageList(PromptsStoreBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PromptsStore> lqw = buildQueryWrapper(bo);
        Page<PromptsStoreVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询提示词商店列表
     */
    @Override
    public List<PromptsStoreVo> queryList(PromptsStoreBo bo) {
        LambdaQueryWrapper<PromptsStore> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PromptsStore> buildQueryWrapper(PromptsStoreBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PromptsStore> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), PromptsStore::getName, bo.getName());
        lqw.eq(bo.getModelId() != null, PromptsStore::getModelId, bo.getModelId());
        lqw.eq(StringUtils.isNotBlank(bo.getSystemRole()), PromptsStore::getSystemRole, bo.getSystemRole());
        lqw.eq(StringUtils.isNotBlank(bo.getUserPrompts()), PromptsStore::getUserPrompts, bo.getUserPrompts());
        lqw.eq(bo.getTalkCount() != null, PromptsStore::getTalkCount, bo.getTalkCount());
        lqw.eq(bo.getTemperature() != null, PromptsStore::getTemperature, bo.getTemperature());
        lqw.eq(bo.getTopP() != null, PromptsStore::getTopP, bo.getTopP());
        lqw.eq(bo.getMaxTokens() != null, PromptsStore::getMaxTokens, bo.getMaxTokens());
        lqw.eq(bo.getPresencePenalty() != null, PromptsStore::getPresencePenalty, bo.getPresencePenalty());
        lqw.eq(bo.getFrequencyPenalty() != null, PromptsStore::getFrequencyPenalty, bo.getFrequencyPenalty());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), PromptsStore::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增提示词商店
     */
    @Override
    public Boolean insertByBo(PromptsStoreBo bo) {
        PromptsStore add = MapstructUtils.convert(bo, PromptsStore.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改提示词商店
     */
    @Override
    public Boolean updateByBo(PromptsStoreBo bo) {
        PromptsStore update = MapstructUtils.convert(bo, PromptsStore.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(PromptsStore entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除提示词商店
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
