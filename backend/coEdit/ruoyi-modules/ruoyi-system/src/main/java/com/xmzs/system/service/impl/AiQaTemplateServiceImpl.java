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
import com.xmzs.system.domain.bo.AiQaTemplateBo;
import com.xmzs.system.domain.vo.AiQaTemplateVo;
import com.xmzs.system.domain.AiQaTemplate;
import com.xmzs.system.mapper.AiQaTemplateMapper;
import com.xmzs.system.service.IAiQaTemplateService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 问答模版Service业务层处理
 *
 * @author zuleed
 * @date 2024-12-19
 */
@RequiredArgsConstructor
@Service
public class AiQaTemplateServiceImpl implements IAiQaTemplateService {

    private final AiQaTemplateMapper baseMapper;

    /**
     * 查询问答模版
     */
    @Override
    public AiQaTemplateVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询问答模版列表
     */
    @Override
    public TableDataInfo<AiQaTemplateVo> queryPageList(AiQaTemplateBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AiQaTemplate> lqw = buildQueryWrapper(bo);
        Page<AiQaTemplateVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询问答模版列表
     */
    @Override
    public List<AiQaTemplateVo> queryList(AiQaTemplateBo bo) {
        LambdaQueryWrapper<AiQaTemplate> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AiQaTemplate> buildQueryWrapper(AiQaTemplateBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<AiQaTemplate> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), AiQaTemplate::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getTemDescription()), AiQaTemplate::getTemDescription, bo.getTemDescription());
        lqw.eq(StringUtils.isNotBlank(bo.getRoleType()), AiQaTemplate::getRoleType, bo.getRoleType());
        lqw.eq(StringUtils.isNotBlank(bo.getTemplateContent()), AiQaTemplate::getTemplateContent, bo.getTemplateContent());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), AiQaTemplate::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增问答模版
     */
    @Override
    public Boolean insertByBo(AiQaTemplateBo bo) {
        AiQaTemplate add = MapstructUtils.convert(bo, AiQaTemplate.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改问答模版
     */
    @Override
    public Boolean updateByBo(AiQaTemplateBo bo) {
        AiQaTemplate update = MapstructUtils.convert(bo, AiQaTemplate.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AiQaTemplate entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除问答模版
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
