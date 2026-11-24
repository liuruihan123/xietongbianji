package com.xmzs.coedit.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 共享链接对象 co_share_link
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_share_link")
@Schema(description = "共享链接对象")
public class CoShareLink extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 共享链接ID
     */
    @Schema(description = "共享链接ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private Long fileId;

    /**
     * 共享码（唯一标识）
     */
    @Schema(description = "共享码")
    private String shareCode;

    /**
     * 完整的共享URL
     */
    @Schema(description = "共享URL")
    private String shareUrl;

    /**
     * 创建者ID
     */
    @Schema(description = "创建者ID")
    private Long creatorId;

    /**
     * 创建者名称
     */
    @Schema(description = "创建者名称")
    private String creatorName;

    /**
     * 共享类型：0-公开 1-密码保护
     */
    @Schema(description = "共享类型：0-公开 1-密码保护")
    private Integer shareType;

    /**
     * 访问密码（明文存储）
     */
    @Schema(description = "访问密码")
    private String sharePassword;

    /**
     * 权限类型：0-只读 1-可编辑 2-可下载 3-完全权限
     */
    @Schema(description = "权限类型：0-只读 1-可编辑 2-可下载 3-完全权限")
    private Integer permissionType;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private Date expireTime;

    /**
     * 最大访问次数（NULL表示无限制）
     */
    @Schema(description = "最大访问次数")
    private Integer maxAccessCount;

    /**
     * 已访问次数
     */
    @Schema(description = "已访问次数")
    private Integer accessCount;

    /**
     * 状态：0-正常 1-已禁用 2-已过期
     */
    @Schema(description = "状态：0-正常 1-已禁用 2-已过期")
    private Integer status;

    /**
     * 是否允许下载（0-否 1-是）
     */
    @Schema(description = "是否允许下载")
    private Integer allowDownload;

    /**
     * 是否允许打印（0-否 1-是）
     */
    @Schema(description = "是否允许打印")
    private Integer allowPrint;

    /**
     * 是否允许复制（0-否 1-是）
     */
    @Schema(description = "是否允许复制")
    private Integer allowCopy;

    /**
     * 是否允许编辑（0-否 1-是）
     */
    @Schema(description = "是否允许编辑")
    private Integer allowEdit;

    /**
     * 是否允许评论（0-否 1-是）
     */
    @Schema(description = "是否允许评论")
    private Integer allowComment;

}

