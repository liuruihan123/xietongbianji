package com.xmzs.system.domain.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xmzs.system.domain.BaseEmptyEntity;
import com.xmzs.system.domain.PkulawLaw;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 法律条文业务对象 pkulaw_law
 *
 * @author zuleed
 * @date 2025-05-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PkulawLaw.class, reverseConvertGenerate = false)
public class PkulawLawBo extends BaseEmptyEntity {

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

    @NotBlank(message = "发布部门不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("IssueDepartment")
    private String IssueDepartment;

    @NotBlank(message = "批准部门不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("RatifyDepartment")
    private String RatifyDepartment;

    @NotBlank(message = "发文字号不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("DocumentNO")
    private String DocumentNO;

    @NotBlank(message = "批准日期不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("RatifyDate")
    private String RatifyDate;

    @NotBlank(message = "发布日期不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("IssueDate")
    private String IssueDate;

    @NotBlank(message = "实施日期不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("ImplementDate")
    private String ImplementDate;

    @NotBlank(message = "时效性不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("TimelinessDic")
    private String TimelinessDic;

    @NotBlank(message = "失效依据不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("InvalidBasis")
    private String InvalidBasis;

    @NotBlank(message = "效力级别不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("EffectivenessDic")
    private String EffectivenessDic;

    @NotBlank(message = "法规类别不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("Category")
    private String Category;

    @NotBlank(message = "关键词不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("Keywords")
    private String Keywords;

    @NotBlank(message = "专题分类不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("TopicType")
    private String TopicType;

    @NotBlank(message = "正文不能为空", groups = { AddGroup.class, EditGroup.class })
    @TableField("AllText")
    private String AllText;

}
