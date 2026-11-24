package com.xmzs.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.knowledge.domain.KnowledgeAttach;
import com.xmzs.knowledge.domain.bo.KnowledgeAttachBo;
import com.xmzs.knowledge.domain.vo.KnowledgeAttachVo;
import com.xmzs.knowledge.mapper.KnowledgeAttachMapper;
import com.xmzs.knowledge.request.KnowledgeAttachPageRequest;
import com.xmzs.knowledge.service.IKnowledgeAttachService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 知识库文件Service业务层处理
 *
 * @author zhuzi
 * @date 2024-06-27
 */
@RequiredArgsConstructor
@Service
public class KnowledgeAttachServiceImpl extends ServiceImpl<KnowledgeAttachMapper, KnowledgeAttach>
    implements IKnowledgeAttachService  {

    private final KnowledgeAttachMapper baseMapper;
    @Override
    public PageInfo<KnowledgeAttach> pageInfo(KnowledgeAttachPageRequest knowledgeAttachPageRequest) {
        QueryWrapper<KnowledgeAttach> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("kid",knowledgeAttachPageRequest.getKid());
        queryWrapper.and(
            (wrapper) -> {
                wrapper.like("doc_name",knowledgeAttachPageRequest.getSearchContent()).or()
                    .like("content",knowledgeAttachPageRequest.getSearchContent());
        });
        queryWrapper.orderByDesc("create_time");
        PageHelper.startPage(knowledgeAttachPageRequest.getPageNum(), knowledgeAttachPageRequest.getPageSize());
        PageInfo<KnowledgeAttach> pageInfo = new PageInfo<>(list(queryWrapper));
        return pageInfo;
    }


    /**
     * 查询知识库文件
     */
    @Override
    public KnowledgeAttachVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询知识库文件列表
     */
    @Override
    public TableDataInfo<KnowledgeAttachVo> queryPageList(KnowledgeAttachBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<KnowledgeAttach> lqw = buildQueryWrapper(bo);
        Page<KnowledgeAttachVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询知识库文件列表
     */
    @Override
    public List<KnowledgeAttachVo> queryList(KnowledgeAttachBo bo) {
        LambdaQueryWrapper<KnowledgeAttach> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<KnowledgeAttach> buildQueryWrapper(KnowledgeAttachBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<KnowledgeAttach> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getKid()), KnowledgeAttach::getKid, bo.getKid());
        lqw.like(StringUtils.isNotBlank(bo.getDocName()), KnowledgeAttach::getDocName, bo.getDocName());
        lqw.eq(StringUtils.isNotBlank(bo.getContent()), KnowledgeAttach::getContent, bo.getContent());
        return lqw;
    }

    /**
     * 新增知识库文件
     */
    @Override
    public Boolean insertByBo(KnowledgeAttachBo bo) {
        KnowledgeAttach add = MapstructUtils.convert(bo, KnowledgeAttach.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改知识库文件
     */
    @Override
    public Boolean updateByBo(KnowledgeAttachBo bo) {
        KnowledgeAttach update = MapstructUtils.convert(bo, KnowledgeAttach.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(KnowledgeAttach entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除知识库文件
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}




