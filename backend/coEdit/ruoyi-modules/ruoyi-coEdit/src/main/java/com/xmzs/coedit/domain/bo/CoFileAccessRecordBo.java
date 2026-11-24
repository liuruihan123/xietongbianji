package com.xmzs.coedit.domain.bo;

import com.xmzs.coedit.domain.CoFileAccessRecord;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 文件访问记录业务对象 co_file_access_record
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文件访问记录业务对象")
@AutoMapper(target = CoFileAccessRecord.class, reverseConvertGenerate = false)
public class CoFileAccessRecordBo extends BaseEntity {

    /**
     * 记录ID
     */
    @Schema(description = "记录ID")
    private Long id;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    @NotNull(message = "文件ID不能为空", groups = {AddGroup.class})
    private Long fileId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 工作空间ID
     */
    @Schema(description = "工作空间ID")
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
    @Schema(description = "访问时长")
    private Integer accessDuration;

    /**
     * 访问IP
     */
    @Schema(description = "访问IP")
    private String accessIp;

    /**
     * 用户代理
     */
    @Schema(description = "用户代理")
    private String userAgent;

}

