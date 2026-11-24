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
import com.xmzs.system.domain.bo.KnowledgeTypeBo;
import com.xmzs.system.domain.vo.KnowledgeTypeVo;
import com.xmzs.system.domain.KnowledgeType;
import com.xmzs.system.mapper.KnowledgeTypeMapper;
import com.xmzs.system.service.IKnowledgeTypeService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 知识分类Service业务层处理
 *
 * @author zhuzi
 * @date 2024-06-22
 */
@RequiredArgsConstructor
@Service
public class KnowledgeTypeServiceImpl implements IKnowledgeTypeService {

    private final KnowledgeTypeMapper baseMapper;

    /**
     * 查询知识分类
     */
    @Override
    public KnowledgeTypeVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询知识分类列表
     */
    @Override
    public TableDataInfo<KnowledgeTypeVo> queryPageList(KnowledgeTypeBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<KnowledgeType> lqw = buildQueryWrapper(bo);
        Page<KnowledgeTypeVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询知识分类列表
     */
    @Override
    public List<KnowledgeTypeVo> queryList(KnowledgeTypeBo bo) {
        LambdaQueryWrapper<KnowledgeType> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<KnowledgeType> buildQueryWrapper(KnowledgeTypeBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<KnowledgeType> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), KnowledgeType::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), KnowledgeType::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增知识分类
     */
    @Override
    public Boolean insertByBo(KnowledgeTypeBo bo) {
        KnowledgeType add = MapstructUtils.convert(bo, KnowledgeType.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改知识分类
     */
    @Override
    public Boolean updateByBo(KnowledgeTypeBo bo) {
        KnowledgeType update = MapstructUtils.convert(bo, KnowledgeType.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(KnowledgeType entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除知识分类
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
