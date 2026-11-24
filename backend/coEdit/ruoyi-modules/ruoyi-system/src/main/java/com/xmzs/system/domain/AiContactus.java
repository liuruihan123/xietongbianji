package com.xmzs.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * contactus对象 ai_contactus
 *
 * @author zuleed
 * @date 2024-12-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_contactus")
public class AiContactus extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 渠道信息
     */
    private String channel;

    /**
     * 用户留言
     */
    private String userMessage;

    /**
     * 创建人账号
     */
    private String createdUserName;

    /**
     * 创建人
     */
    private String createdBy;


}
