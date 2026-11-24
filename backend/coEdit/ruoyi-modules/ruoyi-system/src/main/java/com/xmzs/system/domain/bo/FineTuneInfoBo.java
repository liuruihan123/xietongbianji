package com.xmzs.system.domain.bo;

import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.system.domain.FineTuneInfo;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 精调管理业务对象 fine_tune_info
 *
 * @author zhuzi
 * @date 2024-07-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = FineTuneInfo.class, reverseConvertGenerate = false)
public class FineTuneInfoBo extends BaseEntity {

    /**
     * 编码
     */
    private Integer id;

    /**
     * 微调ID
     */
    private String ftId;

    /**
     * 作业id
     */
    private String ftJobId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long uid;

    /**
     * 微调名
     */
    @NotBlank(message = "微调名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String ftName;

    /**
     * 微调的模型名称
     */
    private String ftModelName;

    /**
     * 基础模型
     */
    @NotBlank(message = "基础模型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String baseModel;

    /**
     * 训练状态（0未使用 1使用中 99训练失败）
     */
    @NotBlank(message = "训练状态（0未使用 1使用中 99训练失败）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 使用状态（0未使用 1使用中）
     */
    @NotBlank(message = "使用状态（0未使用 1使用中）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String useStatus;


}
