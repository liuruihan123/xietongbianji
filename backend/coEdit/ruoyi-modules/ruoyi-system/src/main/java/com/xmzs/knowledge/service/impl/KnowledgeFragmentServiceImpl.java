package com.xmzs.knowledge.service.impl;

import cn.hutool.core.util.RandomUtil;
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
import com.xmzs.knowledge.domain.KnowledgeFragment;
import com.xmzs.knowledge.domain.bo.KnowledgeFragmentBo;
import com.xmzs.knowledge.domain.vo.KnowledgeFragmentVo;
import com.xmzs.knowledge.mapper.KnowledgeFragmentMapper;
import com.xmzs.knowledge.request.KnowledgeFragmentPageRequest;
import com.xmzs.knowledge.request.KnowledgeFragmentRemoveRequest;
import com.xmzs.knowledge.request.KnowledgeFragmentSaveRequest;
import com.xmzs.knowledge.service.EmbeddingService;
import com.xmzs.knowledge.service.KnowledgeFragmentService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
* @author huangkh
* @description 针对表【knowledge_fragment(知识片段)】的数据库操作Service实现
* @createDate 2023-12-14 16:43:31
*/
@Service
@AllArgsConstructor
public class KnowledgeFragmentServiceImpl extends ServiceImpl<KnowledgeFragmentMapper, KnowledgeFragment>
    implements KnowledgeFragmentService{

    private final EmbeddingService embeddingService;

    private final KnowledgeFragmentMapper baseMapper;

    @Override
    public PageInfo<KnowledgeFragment> pageInfo(KnowledgeFragmentPageRequest knowledgeFragmentPageRequest) {
        QueryWrapper<KnowledgeFragment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("kid",knowledgeFragmentPageRequest.getKid());
        if (StringUtils.isNotBlank(knowledgeFragmentPageRequest.getDocId())){
            queryWrapper.eq("doc_id",knowledgeFragmentPageRequest.getDocId());
        }
        if (StringUtils.isNotBlank(knowledgeFragmentPageRequest.getSearchContent())){
            queryWrapper.like("content",knowledgeFragmentPageRequest.getSearchContent());
        }
        queryWrapper.orderByDesc("create_time");
        PageHelper.startPage(knowledgeFragmentPageRequest.getPageNum(), knowledgeFragmentPageRequest.getPageSize());
        PageInfo<KnowledgeFragment> pageInfo = new PageInfo<>(list(queryWrapper));
        return pageInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFragment(KnowledgeFragmentRemoveRequest request) {
        QueryWrapper<KnowledgeFragment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("kid",request.getKid());
        queryWrapper.eq("fid",request.getFid());
        this.remove(queryWrapper);
        embeddingService.removeByKidAndFid(request.getKid(),request.getFid());
    }

    @Override
    public void saveFragment(KnowledgeFragmentSaveRequest request) {
        String fid = RandomUtil.randomString(16);
        String docId = "";
        KnowledgeFragment fragment = new KnowledgeFragment();
        fragment.setKid(request.getKid());
        fragment.setFid(fid);
        fragment.setDocId(docId);
        fragment.setIdx(0);
        fragment.setContent(request.getContent());
        fragment.setCreateTime(LocalDateTime.now());
        this.save(fragment);
        embeddingService.saveFragment(request.getKid(),docId,fid,request.getContent());

    }



    /**
     * 查询知识片段
     */
    @Override
    public KnowledgeFragmentVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询知识片段列表
     */
    @Override
    public TableDataInfo<KnowledgeFragmentVo> queryPageList(KnowledgeFragmentBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<KnowledgeFragment> lqw = buildQueryWrapper(bo);
        Page<KnowledgeFragmentVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询知识片段列表
     */
    @Override
    public List<KnowledgeFragmentVo> queryList(KnowledgeFragmentBo bo) {
        LambdaQueryWrapper<KnowledgeFragment> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<KnowledgeFragment> buildQueryWrapper(KnowledgeFragmentBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<KnowledgeFragment> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getKid()), KnowledgeFragment::getKid, bo.getKid());
        lqw.eq(StringUtils.isNotBlank(bo.getDocId()), KnowledgeFragment::getDocId, bo.getDocId());
        lqw.eq(bo.getIdx() != null, KnowledgeFragment::getIdx, bo.getIdx());
        lqw.like(StringUtils.isNotBlank(bo.getContent()), KnowledgeFragment::getContent, bo.getContent());
        return lqw;
    }

    /**
     * 新增知识片段
     */
    @Override
    public Boolean insertByBo(KnowledgeFragmentBo bo) {

        KnowledgeFragment add = MapstructUtils.convert(bo, KnowledgeFragment.class);
        //根据docId查询并补充 kid的值
        String fid = RandomUtil.randomString(16);
        add.setFid(fid);

        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        embeddingService.saveFragment(add.getKid(),add.getDocId(),fid,add.getContent());
        return flag;
    }

    /**
     * 修改知识片段
     */
    @Override
    public Boolean updateByBo(KnowledgeFragmentBo bo) {
        KnowledgeFragment update = MapstructUtils.convert(bo, KnowledgeFragment.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(KnowledgeFragment entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除知识片段
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}




