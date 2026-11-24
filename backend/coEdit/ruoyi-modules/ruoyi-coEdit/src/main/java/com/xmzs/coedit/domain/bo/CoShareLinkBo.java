package com.xmzs.coedit.domain.bo;

import com.xmzs.coedit.domain.CoShareLink;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.github.linpeilie.annotations.AutoMapper;

import java.util.Date;

/**
 * 共享链接业务对象 co_share_link
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "共享链接业务对象")
@AutoMapper(target = CoShareLink.class, reverseConvertGenerate = false)
public class CoShareLinkBo extends BaseEntity {

    /**
     * 共享链接ID
     */
    @Schema(description = "共享链接ID")
    private Long id;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    /**
     * 共享类型：0-公开 1-密码保护
     */
    @Schema(description = "共享类型：0-公开 1-密码保护")
    private Integer shareType;

    /**
     * 访问密码（明文，用于创建时）
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
     * 有效天数（用于计算过期时间）
     */
    @Schema(description = "有效天数")
    private Integer validDays;

    /**
     * 最大访问次数（NULL表示无限制）
     */
    @Schema(description = "最大访问次数")
    private Integer maxAccessCount;

    /**
     * 是否允许下载：0-否 1-是
     */
    @Schema(description = "是否允许下载：0-否 1-是")
    private Integer allowDownload;

    /**
     * 是否允许打印：0-否 1-是
     */
    @Schema(description = "是否允许打印：0-否 1-是")
    private Integer allowPrint;

    /**
     * 是否允许复制：0-否 1-是
     */
    @Schema(description = "是否允许复制：0-否 1-是")
    private Integer allowCopy;

    /**
     * 是否允许编辑：0-否 1-是
     */
    @Schema(description = "是否允许编辑：0-否 1-是")
    private Integer allowEdit;

    /**
     * 是否允许评论：0-否 1-是
     */
    @Schema(description = "是否允许评论：0-否 1-是")
    private Integer allowComment;

}

