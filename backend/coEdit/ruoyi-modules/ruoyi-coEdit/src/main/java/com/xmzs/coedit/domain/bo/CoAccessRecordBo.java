package com.xmzs.coedit.domain.bo;

import com.xmzs.common.mybatis.core.domain.BaseEntity;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.xmzs.coedit.domain.CoAccessRecord;
import com.xmzs.common.core.xss.Xss;
import jakarta.validation.constraints.*;

import java.util.Date;

/**
 * 访问记录业务对象 co_access_record
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "访问记录业务对象")
public class CoAccessRecordBo extends BaseEntity {

    /**
     * 记录ID
     */
    @Schema(description = "记录ID")
    @NotNull(message = "记录ID不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 目标类型:1-工作空间 2-文件夹 3-文件
     */
    @Schema(description = "目标类型:1-工作空间 2-文件夹 3-文件")
    @NotNull(message = "目标类型不能为空", groups = {AddGroup.class})
    private Integer targetType;

    /**
     * 目标ID
     */
    @Schema(description = "目标ID")
    @NotNull(message = "目标ID不能为空", groups = {AddGroup.class})
    private Long targetId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 所属工作空间ID
     */
    @Schema(description = "所属工作空间ID")
    private Long workspaceId;

    /**
     * 访问时间
     */
    @Schema(description = "访问时间")
    private Date accessTime;

    /**
     * 访问类型:1-查看 2-编辑 3-下载
     */
    @Schema(description = "访问类型:1-查看 2-编辑 3-下载")
    private Integer accessType;

    /**
     * 访问时长(秒)
     */
    @Schema(description = "访问时长(秒)")
    private Integer accessDuration;

    /**
     * 访问IP
     */
    @Schema(description = "访问IP")
    @Xss(message = "访问IP不能包含脚本字符")
    private String accessIp;

    /**
     * 用户代理
     */
    @Schema(description = "用户代理")
    @Xss(message = "用户代理不能包含脚本字符")
    private String userAgent;

}

