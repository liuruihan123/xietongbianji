package com.xmzs.coedit.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 共享安全控制对象 co_share_security
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@TableName("co_share_security")
public class CoShareSecurity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 安全配置ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 共享链接ID
     */
    private Long shareLinkId;

    /**
     * IP白名单（JSON数组）
     */
    private String ipWhitelist;

    /**
     * IP黑名单（JSON数组）
     */
    private String ipBlacklist;

    /**
     * 每小时最大访问次数
     */
    private Integer maxAccessPerHour;

    /**
     * 每天最大访问次数
     */
    private Integer maxAccessPerDay;

    /**
     * 是否启用安全控制：0-否 1-是
     */
    private Integer isEnabled;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}

