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
import com.xmzs.system.domain.bo.PkulawApyBo;
import com.xmzs.system.domain.vo.PkulawApyVo;
import com.xmzs.system.domain.PkulawApy;
import com.xmzs.system.mapper.PkulawApyMapper;
import com.xmzs.system.service.IPkulawApyService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 案例库Service业务层处理
 *
 * @author zuleed
 * @date 2025-05-10
 */
@RequiredArgsConstructor
@Service
public class PkulawApyServiceImpl implements IPkulawApyService {

    private final PkulawApyMapper baseMapper;

    /**
     * 查询案例库
     */
    @Override
    public PkulawApyVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询案例库列表
     */
    @Override
    public TableDataInfo<PkulawApyVo> queryPageList(PkulawApyBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PkulawApy> lqw = buildQueryWrapper(bo);
        Page<PkulawApyVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询案例库列表
     */
    @Override
    public List<PkulawApyVo> queryList(PkulawApyBo bo) {
        LambdaQueryWrapper<PkulawApy> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PkulawApy> buildQueryWrapper(PkulawApyBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PkulawApy> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getLibrary()), PkulawApy::getLibrary, bo.getLibrary());
        lqw.eq(StringUtils.isNotBlank(bo.getGid()), PkulawApy::getGid, bo.getGid());
        lqw.eq(StringUtils.isNotBlank(bo.getTitle()), PkulawApy::getTitle, bo.getTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getDocumentNO()), PkulawApy::getDocumentNO, bo.getDocumentNO());
        lqw.eq(StringUtils.isNotBlank(bo.getPunishmentDate()), PkulawApy::getPunishmentDate, bo.getPunishmentDate());
        lqw.eq(StringUtils.isNotBlank(bo.getPunishmentObject()), PkulawApy::getPunishmentObject, bo.getPunishmentObject());
        lqw.eq(StringUtils.isNotBlank(bo.getEnforcementLevel()), PkulawApy::getEnforcementLevel, bo.getEnforcementLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getLawRegional()), PkulawApy::getLawRegional, bo.getLawRegional());
        lqw.eq(StringUtils.isNotBlank(bo.getLawDepartment()), PkulawApy::getLawDepartment, bo.getLawDepartment());
        lqw.eq(StringUtils.isNotBlank(bo.getPunishmentTarget()), PkulawApy::getPunishmentTarget, bo.getPunishmentTarget());
        lqw.eq(StringUtils.isNotBlank(bo.getPunishmentType()), PkulawApy::getPunishmentType, bo.getPunishmentType());
        lqw.eq(StringUtils.isNotBlank(bo.getAccordingLaw()), PkulawApy::getAccordingLaw, bo.getAccordingLaw());
        lqw.eq(StringUtils.isNotBlank(bo.getCategory()), PkulawApy::getCategory, bo.getCategory());
        lqw.eq(StringUtils.isNotBlank(bo.getAllText()), PkulawApy::getAllText, bo.getAllText());
        lqw.eq(bo.getHasFk() != null, PkulawApy::getHasFk, bo.getHasFk());
        lqw.eq(bo.getAccordingLawNum() != null, PkulawApy::getAccordingLawNum, bo.getAccordingLawNum());
        lqw.eq(bo.getAllTextCharCount() != null, PkulawApy::getAllTextCharCount, bo.getAllTextCharCount());
        lqw.eq(bo.getLicenseSuspensionOrRevocation() != null, PkulawApy::getLicenseSuspensionOrRevocation, bo.getLicenseSuspensionOrRevocation());
        lqw.eq(bo.getOrderToCeaseOperations() != null, PkulawApy::getOrderToCeaseOperations, bo.getOrderToCeaseOperations());
        lqw.eq(bo.getConfiscationOfIllegalGains() != null, PkulawApy::getConfiscationOfIllegalGains, bo.getConfiscationOfIllegalGains());
        lqw.eq(bo.getFine() != null, PkulawApy::getFine, bo.getFine());
        lqw.eq(bo.getWarning() != null, PkulawApy::getWarning, bo.getWarning());
        lqw.eq(bo.getAdministrativeDetention() != null, PkulawApy::getAdministrativeDetention, bo.getAdministrativeDetention());
        lqw.eq(bo.getOtherPunishments() != null, PkulawApy::getOtherPunishments, bo.getOtherPunishments());
        lqw.eq(bo.getPublicCultureAndMedia() != null, PkulawApy::getPublicCultureAndMedia, bo.getPublicCultureAndMedia());
        lqw.eq(bo.getOtherCategories() != null, PkulawApy::getOtherCategories, bo.getOtherCategories());
        lqw.eq(bo.getForeignExchange() != null, PkulawApy::getForeignExchange, bo.getForeignExchange());
        lqw.eq(bo.getLandAndUrbanConstruction() != null, PkulawApy::getLandAndUrbanConstruction, bo.getLandAndUrbanConstruction());
        lqw.eq(bo.getBiddingAndProcurement() != null, PkulawApy::getBiddingAndProcurement, bo.getBiddingAndProcurement());
        lqw.eq(bo.getSafetyRegulation() != null, PkulawApy::getSafetyRegulation, bo.getSafetyRegulation());
        lqw.eq(bo.getSports() != null, PkulawApy::getSports, bo.getSports());
        lqw.eq(bo.getTaxation() != null, PkulawApy::getTaxation, bo.getTaxation());
        lqw.eq(bo.getBanking() != null, PkulawApy::getBanking, bo.getBanking());
        lqw.eq(bo.getEnvironmentalProtection() != null, PkulawApy::getEnvironmentalProtection, bo.getEnvironmentalProtection());
        lqw.eq(bo.getWaterResources() != null, PkulawApy::getWaterResources, bo.getWaterResources());
        lqw.eq(bo.getCustoms() != null, PkulawApy::getCustoms, bo.getCustoms());
        lqw.eq(bo.getTrademark() != null, PkulawApy::getTrademark, bo.getTrademark());
        lqw.eq(bo.getInsurance() != null, PkulawApy::getInsurance, bo.getInsurance());
        lqw.eq(bo.getAudit() != null, PkulawApy::getAudit, bo.getAudit());
        lqw.eq(bo.getTransportation() != null, PkulawApy::getTransportation, bo.getTransportation());
        lqw.eq(bo.getFinance() != null, PkulawApy::getFinance, bo.getFinance());
        lqw.eq(bo.getEducation() != null, PkulawApy::getEducation, bo.getEducation());
        lqw.eq(bo.getCivilAffairsAndSocialSecurity() != null, PkulawApy::getCivilAffairsAndSocialSecurity, bo.getCivilAffairsAndSocialSecurity());
        lqw.eq(bo.getEnergyAndPower() != null, PkulawApy::getEnergyAndPower, bo.getEnergyAndPower());
        lqw.eq(bo.getPatent() != null, PkulawApy::getPatent, bo.getPatent());
        lqw.eq(bo.getFoodDrugMedical() != null, PkulawApy::getFoodDrugMedical, bo.getFoodDrugMedical());
        lqw.eq(bo.getJudiciary() != null, PkulawApy::getJudiciary, bo.getJudiciary());
        lqw.eq(bo.getCopyright() != null, PkulawApy::getCopyright, bo.getCopyright());
        lqw.eq(bo.getIndustryAndInformationTechnology() != null, PkulawApy::getIndustryAndInformationTechnology, bo.getIndustryAndInformationTechnology());
        lqw.eq(bo.getSecuritiesAndFutures() != null, PkulawApy::getSecuritiesAndFutures, bo.getSecuritiesAndFutures());
        lqw.eq(bo.getFinanceAndTaxation() != null, PkulawApy::getFinanceAndTaxation, bo.getFinanceAndTaxation());
        lqw.eq(bo.getCultureAndTourism() != null, PkulawApy::getCultureAndTourism, bo.getCultureAndTourism());
        lqw.eq(bo.getInternet() != null, PkulawApy::getInternet, bo.getInternet());
        lqw.eq(bo.getAgricultureForestryFishing() != null, PkulawApy::getAgricultureForestryFishing, bo.getAgricultureForestryFishing());
        lqw.eq(bo.getIntellectualProperty() != null, PkulawApy::getIntellectualProperty, bo.getIntellectualProperty());
        lqw.eq(bo.getMarketRegulation() != null, PkulawApy::getMarketRegulation, bo.getMarketRegulation());
        lqw.eq(bo.getPublicSecurity() != null, PkulawApy::getPublicSecurity, bo.getPublicSecurity());
        lqw.eq(bo.getAccounting() != null, PkulawApy::getAccounting, bo.getAccounting());
        lqw.eq(bo.getEpidemicPreventionAndControl() != null, PkulawApy::getEpidemicPreventionAndControl, bo.getEpidemicPreventionAndControl());
        lqw.eq(StringUtils.isNotBlank(bo.getProvince()), PkulawApy::getProvince, bo.getProvince());
        lqw.eq(StringUtils.isNotBlank(bo.getCity()), PkulawApy::getCity, bo.getCity());
        lqw.eq(StringUtils.isNotBlank(bo.getCounty()), PkulawApy::getCounty, bo.getCounty());
        return lqw;
    }

    /**
     * 新增案例库
     */
    @Override
    public Boolean insertByBo(PkulawApyBo bo) {
        PkulawApy add = MapstructUtils.convert(bo, PkulawApy.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改案例库
     */
    @Override
    public Boolean updateByBo(PkulawApyBo bo) {
        PkulawApy update = MapstructUtils.convert(bo, PkulawApy.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(PkulawApy entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除案例库
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
