package com.xmzs.coedit.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 共享访问记录对象 co_share_access_record
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@TableName("co_share_access_record")
public class CoShareAccessRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 共享链接ID
     */
    private Long shareLinkId;

    /**
     * 共享码
     */
    private String shareCode;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 访问者ID（如果已登录）
     */
    private Long visitorId;

    /**
     * 访问者名称
     */
    private String visitorName;

    /**
     * 访问者类型：0-匿名 1-已登录
     */
    private Integer visitorType;

    /**
     * 访问IP地址
     */
    private String accessIp;

    /**
     * 访问地理位置
     */
    private String accessLocation;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 访问时间
     */
    private Date accessTime;

    /**
     * 访问操作：0-查看 1-编辑 2-下载
     */
    private Integer accessAction;

    /**
     * 访问结果：0-成功 1-失败
     */
    private Integer accessResult;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 创建时间
     */
    private Date createTime;

}

