package com.xmzs.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 微调附件对象 fine_tune_attach
 *
 * @author zhuzi
 * @date 2024-07-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fine_tune_attach")
public class FineTuneAttach extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    @TableId(value = "id")
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

    private String ftJobId;


    private String fileName;

    /**
     * 上传文件id
     */
    private String fileId;

    /**
     * 文件状态（0未转化 1已转化 99转化失败)
     */
    private String status;

    /**
     * 文件内容
     */
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
