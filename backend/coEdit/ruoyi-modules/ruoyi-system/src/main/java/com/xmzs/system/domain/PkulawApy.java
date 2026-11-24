package com.xmzs.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 案例库对象 pkulaw_apy
 *
 * @author zuleed
 * @date 2025-05-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pkulaw_apy")
public class PkulawApy extends BaseEmptyEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Long id;

    @TableField("library")
    private String library;

    @TableField("Gid")
    private String Gid;

    @TableField("Title")
    private String Title;

    @TableField("DocumentNO")
    private String DocumentNO;

    @TableField("PunishmentDate")
    private String PunishmentDate;

    @TableField("PunishmentObject")
    private String PunishmentObject;

    @TableField("EnforcementLevel")
    private String EnforcementLevel;

    @TableField("LawRegional")
    private String LawRegional;

    @TableField("LawDepartment")
    private String LawDepartment;

    @TableField("PunishmentTarget")
    private String PunishmentTarget;

    @TableField("PunishmentType")
    private String PunishmentType;

    @TableField("AccordingLaw")
    private String AccordingLaw;

    @TableField("Category")
    private String Category;

    @TableField("AllText")
    private String AllText;

    private Integer hasFk;

    @TableField("fkje")
    private Long fkje;

    @TableField("AccordingLawCharCount")
    private Long AccordingLawCharCount;

    @TableField("AccordingLawNum")
    private Long AccordingLawNum;

    @TableField("AllTextCharCount")
    private Long AllTextCharCount;

    private Integer licenseSuspensionOrRevocation;

    private Integer orderToCeaseOperations;

    private Integer confiscationOfIllegalGains;

    @TableField("fine")
    private Integer fine;

    @TableField("warning")
    private Integer warning;

    private Integer administrativeDetention;

    private Integer otherPunishments;

    private Integer publicCultureAndMedia;

    private Integer otherCategories;

    private Integer foreignExchange;

    private Integer landAndUrbanConstruction;

    private Integer biddingAndProcurement;

    private Integer safetyRegulation;

    @TableField("sports")
    private Integer sports;

    @TableField("taxation")
    private Integer taxation;

    @TableField("banking")
    private Integer banking;

    private Integer environmentalProtection;

    private Integer waterResources;

    @TableField("customs")
    private Integer customs;

    @TableField("trademark")
    private Integer trademark;

    @TableField("insurance")
    private Integer insurance;

    @TableField("audit")
    private Integer audit;

    @TableField("transportation")
    private Integer transportation;

    @TableField("finance")
    private Integer finance;

    @TableField("education")
    private Integer education;

    private Integer civilAffairsAndSocialSecurity;

    private Integer energyAndPower;

    @TableField("patent")
    private Integer patent;

    private Integer foodDrugMedical;

    @TableField("judiciary")
    private Integer judiciary;

    @TableField("copyright")
    private Integer copyright;

    private Integer industryAndInformationTechnology;
    private Integer securitiesAndFutures;

    private Integer financeAndTaxation;

    private Integer cultureAndTourism;

    @TableField("internet")
    private Integer internet;

    private Integer agricultureForestryFishing;

    private Integer intellectualProperty;

    private Integer marketRegulation;

    private Integer publicSecurity;

    @TableField("accounting")
    private Integer accounting;

    private Integer epidemicPreventionAndControl;

    @TableField("province")
    private String province;

    @TableField("city")
    private String city;

    @TableField("county")
    private String county;

}
