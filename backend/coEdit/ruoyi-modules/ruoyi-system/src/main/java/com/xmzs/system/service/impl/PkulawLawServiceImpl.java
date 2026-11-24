package com.xmzs.system.service.impl;

import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xmzs.system.domain.vo.LawsVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.xmzs.system.domain.bo.PkulawLawBo;
import com.xmzs.system.domain.vo.PkulawLawVo;
import com.xmzs.system.domain.PkulawLaw;
import com.xmzs.system.mapper.PkulawLawMapper;
import com.xmzs.system.service.IPkulawLawService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 法律条文Service业务层处理
 *
 * @author zuleed
 * @date 2025-05-10
 */
@RequiredArgsConstructor
@Service
public class PkulawLawServiceImpl implements IPkulawLawService {

    private final PkulawLawMapper baseMapper;

    /**
     * 查询法律条文
     */
    @Override
    public PkulawLawVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }


    @Override
    public String queryByGid(String gid){
        return baseMapper.selectLawTextByGid(gid);
    }
    @Override
    public List<LawsVo> queryByGids(List<String> gids){
        return baseMapper.selectLawTextByGids(gids);
    }

    /**
     * 查询法律条文列表
     */
    @Override
    public TableDataInfo<PkulawLawVo> queryPageList(PkulawLawBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PkulawLaw> lqw = buildQueryWrapper(bo);
        Page<PkulawLawVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询法律条文列表
     */
    @Override
    public List<PkulawLawVo> queryList(PkulawLawBo bo) {
        LambdaQueryWrapper<PkulawLaw> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PkulawLaw> buildQueryWrapper(PkulawLawBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PkulawLaw> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getLibrary()), PkulawLaw::getLibrary, bo.getLibrary());
        lqw.eq(StringUtils.isNotBlank(bo.getGid()), PkulawLaw::getGid, bo.getGid());
        lqw.eq(StringUtils.isNotBlank(bo.getTitle()), PkulawLaw::getTitle, bo.getTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getIssueDepartment()), PkulawLaw::getIssueDepartment, bo.getIssueDepartment());
        lqw.eq(StringUtils.isNotBlank(bo.getRatifyDepartment()), PkulawLaw::getRatifyDepartment, bo.getRatifyDepartment());
        lqw.eq(StringUtils.isNotBlank(bo.getDocumentNO()), PkulawLaw::getDocumentNO, bo.getDocumentNO());
        lqw.between(params.get("beginRatifyDate") != null && params.get("endRatifyDate") != null,
            PkulawLaw::getRatifyDate ,params.get("beginRatifyDate"), params.get("endRatifyDate"));
        lqw.between(params.get("beginIssueDate") != null && params.get("endIssueDate") != null,
            PkulawLaw::getIssueDate ,params.get("beginIssueDate"), params.get("endIssueDate"));
        lqw.between(params.get("beginImplementDate") != null && params.get("endImplementDate") != null,
            PkulawLaw::getImplementDate ,params.get("beginImplementDate"), params.get("endImplementDate"));
        lqw.eq(StringUtils.isNotBlank(bo.getTimelinessDic()), PkulawLaw::getTimelinessDic, bo.getTimelinessDic());
        lqw.eq(StringUtils.isNotBlank(bo.getInvalidBasis()), PkulawLaw::getInvalidBasis, bo.getInvalidBasis());
        lqw.eq(StringUtils.isNotBlank(bo.getEffectivenessDic()), PkulawLaw::getEffectivenessDic, bo.getEffectivenessDic());
        lqw.eq(StringUtils.isNotBlank(bo.getCategory()), PkulawLaw::getCategory, bo.getCategory());
        lqw.eq(StringUtils.isNotBlank(bo.getKeywords()), PkulawLaw::getKeywords, bo.getKeywords());
        lqw.eq(StringUtils.isNotBlank(bo.getTopicType()), PkulawLaw::getTopicType, bo.getTopicType());
        lqw.like(StringUtils.isNotBlank(bo.getAllText()), PkulawLaw::getAllText, bo.getAllText());
        return lqw;
    }

    /**
     * 新增法律条文
     */
    @Override
    public Boolean insertByBo(PkulawLawBo bo) {
        PkulawLaw add = MapstructUtils.convert(bo, PkulawLaw.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改法律条文
     */
    @Override
    public Boolean updateByBo(PkulawLawBo bo) {
        PkulawLaw update = MapstructUtils.convert(bo, PkulawLaw.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(PkulawLaw entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除法律条文
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
