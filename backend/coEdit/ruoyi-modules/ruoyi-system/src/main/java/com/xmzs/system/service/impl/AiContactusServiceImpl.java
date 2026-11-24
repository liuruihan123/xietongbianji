package com.xmzs.system.service.impl;

import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xmzs.system.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.xmzs.system.domain.bo.AiContactusBo;
import com.xmzs.system.domain.vo.AiContactusVo;
import com.xmzs.system.domain.AiContactus;
import com.xmzs.system.mapper.AiContactusMapper;
import com.xmzs.system.service.IAiContactusService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * contactusService业务层处理
 *
 * @author zuleed
 * @date 2024-12-04
 */
@RequiredArgsConstructor
@Service
public class AiContactusServiceImpl implements IAiContactusService {

    private final AiContactusMapper baseMapper;

    /**
     * 查询contactus
     */
    @Override
    public AiContactusVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询contactus列表
     */
    @Override
    public TableDataInfo<AiContactusVo> queryPageList(AiContactusBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AiContactus> lqw = buildQueryWrapper(bo);
        Page<AiContactusVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询contactus列表
     */
    @Override
    public List<AiContactusVo> queryList(AiContactusBo bo) {
        LambdaQueryWrapper<AiContactus> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AiContactus> buildQueryWrapper(AiContactusBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<AiContactus> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getUserMessage()), AiContactus::getUserMessage, bo.getUserMessage());
        lqw.eq(bo.getCreateTime() != null, AiContactus::getCreateTime, bo.getCreateTime());
        lqw.like(StringUtils.isNotBlank(bo.getCreatedUserName()), AiContactus::getCreatedUserName, bo.getCreatedUserName());
        lqw.eq(StringUtils.isNotBlank(bo.getCreatedBy()), AiContactus::getCreatedBy, bo.getCreatedBy());
        lqw.orderByDesc(AiContactus::getCreateTime);
        return lqw;
    }

    /**
     * 新增contactus
     */
    @Override
    public Boolean insertByBo(AiContactusBo bo) {
        AiContactus add = MapstructUtils.convert(bo, AiContactus.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改contactus
     */
    @Override
    public Boolean updateByBo(AiContactusBo bo) {
        AiContactus update = MapstructUtils.convert(bo, AiContactus.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AiContactus entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除contactus
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
