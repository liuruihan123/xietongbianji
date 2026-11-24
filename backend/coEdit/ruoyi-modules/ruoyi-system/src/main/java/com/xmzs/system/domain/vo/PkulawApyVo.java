package com.xmzs.system.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xmzs.system.domain.PkulawApy;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.common.excel.annotation.ExcelDictFormat;
import com.xmzs.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = PkulawApy.class)
public class PkulawApyVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "")
    @TableField("id")
    private Long id;

    @ExcelProperty(value = "法规目录")
    @TableField("library")
    private String library;

    @ExcelProperty(value = "Gid")
    @TableField("Gid")
    private String Gid;

    @ExcelProperty(value = "标题")
    @TableField("Title")
    private String Title;

    @ExcelProperty(value = "发文案号")
    @TableField("DocumentNO")
    private String DocumentNO;

    @ExcelProperty(value = "处罚日期")
    @TableField("PunishmentDate")
    private String PunishmentDate;

    @ExcelProperty(value = "处罚对象")
    @TableField("PunishmentObject")
    private String PunishmentObject;

    @ExcelProperty(value = "执法级别")
    @TableField("EnforcementLevel")
    private String EnforcementLevel;

    @ExcelProperty(value = "执法地域")
    @TableField("LawRegional")
    private String LawRegional;

    @ExcelProperty(value = "执法机关")
    @TableField("LawDepartment")
    private String LawDepartment;

    @ExcelProperty(value = "处罚对象分类")
    @TableField("PunishmentTarget")
    private String PunishmentTarget;

    @ExcelProperty(value = "处罚种类")
    @TableField("PunishmentType")
    private String PunishmentType;

    @ExcelProperty(value = "处罚依据")
    @TableField("AccordingLaw")
    private String AccordingLaw;

    @ExcelProperty(value = "主题分类")
    @TableField("Category")
    private String Category;

    @ExcelProperty(value = "正文")
    @TableField("AllText")
    private String AllText;

    @ExcelProperty(value = "是否含有罚款")
    private Integer hasFk;

    @ExcelProperty(value = "罚款金额(元)")
    @TableField("fkje")
    private Long fkje;

    @ExcelProperty(value = "AccordingLaw字符个数")
    @TableField("AccordingLawCharCount")
    private Long AccordingLawCharCount;

    @ExcelProperty(value = "AccordingLaw法条个数")
    @TableField("AccordingLawNum")
    private Long AccordingLawNum;

    @ExcelProperty(value = "AllText字符个数")
    @TableField("AllTextCharCount")
    private Long AllTextCharCount;

    @ExcelProperty(value = "暂扣或者吊销许可证、暂扣或者吊销执照")
    private Integer licenseSuspensionOrRevocation;

    @ExcelProperty(value = "责令停产停业")
    private Integer orderToCeaseOperations;

    @ExcelProperty(value = "没收违法所得、没收非法财物")
    private Integer confiscationOfIllegalGains;

    @ExcelProperty(value = "罚款")
    @TableField("fine")
    private Integer fine;

    @ExcelProperty(value = "警告")
    @TableField("warning")
    private Integer warning;

    @ExcelProperty(value = "行政拘留")
    private Integer administrativeDetention;

    @ExcelProperty(value = "其他")
    private Integer otherPunishments;

    @ExcelProperty(value = "公共文化与传媒")
    private Integer publicCultureAndMedia;

    @ExcelProperty(value = "其他")
    private Integer otherCategories;

    @ExcelProperty(value = "外汇")
    private Integer foreignExchange;

    @ExcelProperty(value = "土地城建")
    private Integer landAndUrbanConstruction;

    @ExcelProperty(value = "招标采购")
    private Integer biddingAndProcurement;

    @ExcelProperty(value = "安全监管")
    private Integer safetyRegulation;

    @ExcelProperty(value = "体育")
    @TableField("sports")
    private Integer sports;

    @ExcelProperty(value = "税务")
    @TableField("taxation")
    private Integer taxation;

    @ExcelProperty(value = "银行")
    @TableField("banking")
    private Integer banking;

    @ExcelProperty(value = "环保")
    private Integer environmentalProtection;

    @ExcelProperty(value = "水利")
    private Integer waterResources;

    @ExcelProperty(value = "海关")
    @TableField("customs")
    private Integer customs;

    @ExcelProperty(value = "商标")
    @TableField("trademark")
    private Integer trademark;

    @ExcelProperty(value = "保险")
    @TableField("insurance")
    private Integer insurance;

    @ExcelProperty(value = "审计")
    @TableField("audit")
    private Integer audit;

    @ExcelProperty(value = "交通运输")
    @TableField("transportation")
    private Integer transportation;

    @ExcelProperty(value = "金融")
    @TableField("finance")
    private Integer finance;

    @ExcelProperty(value = "教育")
    @TableField("education")
    private Integer education;

    @ExcelProperty(value = "民政与劳动社会保障")
    private Integer civilAffairsAndSocialSecurity;

    @ExcelProperty(value = "能源与电力")
    private Integer energyAndPower;

    @ExcelProperty(value = "专利")
    @TableField("patent")
    private Integer patent;

    @ExcelProperty(value = "食品 药品 医疗")
    private Integer foodDrugMedical;

    @ExcelProperty(value = "司法")
    @TableField("judiciary")
    private Integer judiciary;

    @ExcelProperty(value = "著作权")
    @TableField("copyright")
    private Integer copyright;

    @ExcelProperty(value = "工信")
    private Integer industryAndInformationTechnology;

    @ExcelProperty(value = "证券期货")
    private Integer securitiesAndFutures;

    @ExcelProperty(value = "财税")
    private Integer financeAndTaxation;

    @ExcelProperty(value = "文化和旅游")
    private Integer cultureAndTourism;

    @ExcelProperty(value = "互联网")
    @TableField("internet")
    private Integer internet;

    @ExcelProperty(value = "农林渔牧")
    private Integer agricultureForestryFishing;

    @ExcelProperty(value = "知识产权")
    private Integer intellectualProperty;

    @ExcelProperty(value = "市场监管")
    private Integer marketRegulation;

    @ExcelProperty(value = "公安")
    private Integer publicSecurity;

    @ExcelProperty(value = "会计")
    @TableField("accounting")
    private Integer accounting;

    @ExcelProperty(value = "疫情防控")
    private Integer epidemicPreventionAndControl;

    @ExcelProperty(value = "省份")
    @TableField("province")
    private String province;

    @ExcelProperty(value = "城市")
    @TableField("city")
    private String city;

    @ExcelProperty(value = "区县")
    @TableField("county")
    private String county;

}
