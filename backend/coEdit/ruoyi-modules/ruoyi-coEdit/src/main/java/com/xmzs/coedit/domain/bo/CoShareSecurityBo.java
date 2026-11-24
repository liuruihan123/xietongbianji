package com.xmzs.coedit.domain.bo;

import com.xmzs.coedit.domain.CoShareSecurity;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.github.linpeilie.annotations.AutoMapper;

import java.util.List;

/**
 * 共享安全控制业务对象 co_share_security
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "共享安全控制业务对象")
@AutoMapper(target = CoShareSecurity.class, reverseConvertGenerate = false)
public class CoShareSecurityBo extends BaseEntity {

    /**
     * 安全配置ID
     */
    @Schema(description = "安全配置ID")
    private Long id;

    /**
     * 共享链接ID
     */
    @Schema(description = "共享链接ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "共享链接ID不能为空")
    private Long shareLinkId;

    /**
     * IP白名单
     */
    @Schema(description = "IP白名单")
    private List<String> ipWhitelistArray;

    /**
     * IP黑名单
     */
    @Schema(description = "IP黑名单")
    private List<String> ipBlacklistArray;

    /**
     * 每小时最大访问次数
     */
    @Schema(description = "每小时最大访问次数")
    private Integer maxAccessPerHour;

    /**
     * 每天最大访问次数
     */
    @Schema(description = "每天最大访问次数")
    private Integer maxAccessPerDay;

    /**
     * 是否启用安全控制：0-否 1-是
     */
    @Schema(description = "是否启用安全控制")
    private Integer isEnabled;

}

