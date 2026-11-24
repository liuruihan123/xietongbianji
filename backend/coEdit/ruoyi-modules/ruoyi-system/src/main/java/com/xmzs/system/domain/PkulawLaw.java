package com.xmzs.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 法律条文对象 pkulaw_law
 *
 * @author zuleed
 * @date 2025-05-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pkulaw_law")
public class PkulawLaw extends BaseEmptyEntity {

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

    @TableField("IssueDepartment")
    private String IssueDepartment;

    @TableField("RatifyDepartment")
    private String RatifyDepartment;

    @TableField("DocumentNO")
    private String DocumentNO;

    @TableField("RatifyDate")
    private String RatifyDate;

    @TableField("IssueDate")
    private String IssueDate;

    @TableField("ImplementDate")
    private String ImplementDate;

    @TableField("TimelinessDic")
    private String TimelinessDic;

    @TableField("InvalidBasis")
    private String InvalidBasis;

    @TableField("EffectivenessDic")
    private String EffectivenessDic;

    @TableField("Category")
    private String Category;

    @TableField("Keywords")
    private String Keywords;

    @TableField("TopicType")
    private String TopicType;

    @TableField("AllText")
    private String AllText;

}
