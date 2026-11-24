package com.xmzs.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 精调管理对象 fine_tune_info
 *
 * @author zhuzi
 * @date 2024-07-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fine_tune_info")
public class FineTuneInfo extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    @TableId(value = "id")
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
    private Long uid;

    /**
     * 微调名
     */
    private String ftName;

    /**
     * 微调的模型名称
     */
    private String ftModelName;

    /**
     * 基础模型
     */
    private String baseModel;

    /**
     * 训练状态（0未使用 1已训练 99训练失败）
     */
    private String status;

    /**
     * 使用状态（0未使用 1使用中）
     */
    private String useStatus;


}
