package com.xmzs.system.domain.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xmzs.common.mybatis.annotation.DataColumn;
import com.xmzs.system.domain.BaseEmptyEntity;
import com.xmzs.system.domain.PkulawApy;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 案例库业务对象 pkulaw_apy
 *
 * @author zuleed
 * @date 2025-05-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PkulawApy.class, reverseConvertGenerate = false)
public class PkulawApyBo extends BaseEmptyEntity {

    @NotNull(message = "不能为空", groups = { EditGroup.class })
    @TableField("id")
    private Long id;

    @NotBlank(message = "法规目录不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("library")
    private String library;

    @NotBlank(message = "Gid不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("Gid")
    private String Gid;

    @NotBlank(message = "标题不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("Title")
    private String Title;

    @NotBlank(message = "发文案号不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("DocumentNO")
    private String DocumentNO;

    @TableField("PunishmentDate")
    private String PunishmentDate;

    @NotBlank(message = "处罚对象不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("PunishmentObject")
    private String PunishmentObject;

    @NotBlank(message = "执法级别不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("EnforcementLevel")
    private String EnforcementLevel;

    @NotBlank(message = "执法地域不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("LawRegional")
    private String LawRegional;

    @NotBlank(message = "执法机关不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("LawDepartment")
    private String LawDepartment;

    @NotBlank(message = "处罚对象分类不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("PunishmentTarget")
    private String PunishmentTarget;

    @NotBlank(message = "处罚种类不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("PunishmentType")
    private String PunishmentType;

    @NotBlank(message = "处罚依据不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("AccordingLaw")
    private String AccordingLaw;

    @NotBlank(message = "主题分类不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("Category")
    private String Category;

    @NotBlank(message = "正文不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("AllText")
    private String AllText;

    @NotNull(message = "是否含有罚款不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer hasFk;

    @NotNull(message = "罚款金额(元)不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("fkje")
    private Long fkje;

    @NotNull(message = "AccordingLaw字符个数不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("AccordingLawCharCount")
    private Long AccordingLawCharCount;

    @NotNull(message = "AccordingLaw法条个数不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("AccordingLawNum")
    private Long AccordingLawNum;

    @NotNull(message = "AllText字符个数不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("AllTextCharCount")
    private Long AllTextCharCount;

    @NotNull(message = "暂扣或者吊销许可证、暂扣或者吊销执照不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer licenseSuspensionOrRevocation;

    @NotNull(message = "责令停产停业不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer orderToCeaseOperations;

    @NotNull(message = "没收违法所得、没收非法财物不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer confiscationOfIllegalGains;

    @NotNull(message = "罚款不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("fine")
    private Integer fine;

    @NotNull(message = "警告不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("warning")
    private Integer warning;

    @NotNull(message = "行政拘留不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer administrativeDetention;

    @NotNull(message = "其他不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer otherPunishments;

    @NotNull(message = "公共文化与传媒不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer publicCultureAndMedia;

    @NotNull(message = "其他不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer otherCategories;

    @NotNull(message = "外汇不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer foreignExchange;

    @NotNull(message = "土地城建不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer landAndUrbanConstruction;

    @NotNull(message = "招标采购不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer biddingAndProcurement;

    @NotNull(message = "安全监管不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer safetyRegulation;

    @NotNull(message = "体育不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("sports")
    private Integer sports;

    @NotNull(message = "税务不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("taxation")
    private Integer taxation;

    @NotNull(message = "银行不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("banking")
    private Integer banking;

    @NotNull(message = "环保不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer environmentalProtection;

    @NotNull(message = "水利不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer waterResources;

    @NotNull(message = "海关不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("customs")
    private Integer customs;

    @NotNull(message = "商标不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("trademark")
    private Integer trademark;

    @NotNull(message = "保险不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("insurance")
    private Integer insurance;

    @NotNull(message = "审计不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("audit")
    private Integer audit;

    @NotNull(message = "交通运输不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("transportation")
    private Integer transportation;

    @NotNull(message = "金融不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("finance")
    private Integer finance;

    @NotNull(message = "教育不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("education")
    private Integer education;

    @NotNull(message = "民政与劳动社会保障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer civilAffairsAndSocialSecurity;

    @NotNull(message = "能源与电力不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer energyAndPower;

    @NotNull(message = "专利不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("patent")
    private Integer patent;

    @NotNull(message = "食品 药品 医疗不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer foodDrugMedical;

    @NotNull(message = "司法不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("judiciary")
    private Integer judiciary;

    @NotNull(message = "著作权不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("copyright")
    private Integer copyright;

    @NotNull(message = "工信不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer industryAndInformationTechnology;

    @NotNull(message = "证券期货不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer securitiesAndFutures;

    @NotNull(message = "财税不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer financeAndTaxation;

    @NotNull(message = "文化和旅游不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer cultureAndTourism;

    @NotNull(message = "互联网不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("internet")
    private Integer internet;

    @NotNull(message = "农林渔牧不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer agricultureForestryFishing;

    @NotNull(message = "知识产权不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer intellectualProperty;

    @NotNull(message = "市场监管不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer marketRegulation;

    @NotNull(message = "公安不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer publicSecurity;

    @NotNull(message = "会计不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("accounting")
    private Integer accounting;

    @NotNull(message = "疫情防控不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer epidemicPreventionAndControl;

    @NotBlank(message = "省份不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("province")
    private String province;

    @NotBlank(message = "城市不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("city")
    private String city;

    @NotBlank(message = "区县不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("county")
    private String county;

}
