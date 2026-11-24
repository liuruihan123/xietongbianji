package com.xmzs.system.domain.bo;

import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.system.domain.FineTuneAttach;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 微调附件业务对象 fine_tune_attach
 *
 * @author zhuzi
 * @date 2024-07-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = FineTuneAttach.class, reverseConvertGenerate = false)
public class FineTuneAttachBo extends BaseEntity {

    /**
     * 编码
     */
    @NotNull(message = "编码不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 微调id
     */
    private String ftId;
    /**
     * 生成提示词id
     */
    private String promptsStoreId;



    /**
     * 微调文件id
     */
    private String ftFileId;
    private String fileName;

    /**
     * 上传文件id
     */
    private String fileId;
    /**
     * 微调作业id
     */
    private String ftJobId;

    /**
     * 文件状态（0未转化 1已转化 2已训练 3验证中 4转化中 97文件案例过少  98训练失败 99转化失败)
     */
    @NotBlank(message = "文件状态（0未转化 1已转化 99转化失败)不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 文件内容
     */
    @NotBlank(message = "文件内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String fileContent;

    /**
     * 微调内容
     */
    private String ftContent;

    /**
     * 错误消息
     */
    private String messages;


}
