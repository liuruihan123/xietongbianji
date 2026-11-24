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
import com.xmzs.system.domain.bo.SysPackagePlanBo;
import com.xmzs.system.domain.vo.SysPackagePlanVo;
import com.xmzs.system.domain.SysPackagePlan;
import com.xmzs.system.mapper.SysPackagePlanMapper;
import com.xmzs.system.service.ISysPackagePlanService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 套餐管理Service业务层处理
 *
 * @author zhuzi
 * @date 2024-06-09
 */
@RequiredArgsConstructor
@Service
public class SysPackagePlanServiceImpl implements ISysPackagePlanService {

    private final SysPackagePlanMapper baseMapper;

    /**
     * 查询套餐管理
     */
    @Override
    public SysPackagePlanVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询套餐管理列表
     */
    @Override
    public TableDataInfo<SysPackagePlanVo> queryPageList(SysPackagePlanBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysPackagePlan> lqw = buildQueryWrapper(bo);
        Page<SysPackagePlanVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询套餐管理列表
     */
    @Override
    public List<SysPackagePlanVo> queryList(SysPackagePlanBo bo) {
        LambdaQueryWrapper<SysPackagePlan> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysPackagePlan> buildQueryWrapper(SysPackagePlanBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysPackagePlan> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), SysPackagePlan::getName, bo.getName());
        return lqw;
    }

    /**
     * 新增套餐管理
     */
    @Override
    public Boolean insertByBo(SysPackagePlanBo bo) {
        SysPackagePlan add = MapstructUtils.convert(bo, SysPackagePlan.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改套餐管理
     */
    @Override
    public Boolean updateByBo(SysPackagePlanBo bo) {
        SysPackagePlan update = MapstructUtils.convert(bo, SysPackagePlan.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysPackagePlan entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除套餐管理
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
