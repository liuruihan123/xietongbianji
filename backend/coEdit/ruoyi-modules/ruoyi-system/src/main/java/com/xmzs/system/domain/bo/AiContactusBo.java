package com.xmzs.system.domain.bo;

import com.xmzs.system.domain.AiContactus;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * contactus业务对象 ai_contactus
 *
 * @author zuleed
 * @date 2024-12-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiContactus.class, reverseConvertGenerate = false)
public class AiContactusBo extends BaseEntity {

    /**
     * 编号
     */
    @NotNull(message = "编号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 渠道信息
     */
    @NotBlank(message = "渠道信息不能为空", groups = { AddGroup.class, EditGroup.class })
    private String channel;

    /**
     * 用户留言
     */
    @NotBlank(message = "用户留言不能为空", groups = { AddGroup.class, EditGroup.class })
    private String userMessage;

    /**
     * 创建人账号
     */
    @NotBlank(message = "创建人账号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String createdUserName;

    /**
     * 创建人
     */
    @NotBlank(message = "创建人不能为空", groups = { AddGroup.class, EditGroup.class })
    private String createdBy;


}
