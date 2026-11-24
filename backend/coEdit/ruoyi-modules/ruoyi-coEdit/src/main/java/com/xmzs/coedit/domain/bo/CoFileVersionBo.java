package com.xmzs.coedit.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件版本业务对象 co_file_version
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@Schema(description = "文件版本业务对象")
public class CoFileVersionBo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID", required = true)
    private Long fileId;

    /**
     * 版本描述
     */
    @Schema(description = "版本描述")
    private String versionDesc;

    /**
     * 变更类型：0-创建 1-修改 2-重命名 3-回滚
     */
    @Schema(description = "变更类型")
    private Integer changeType;

    /**
     * 变更描述
     */
    @Schema(description = "变更描述")
    private String changeDescription;

    /**
     * 是否主要版本：0-否 1-是
     */
    @Schema(description = "是否主要版本")
    private Integer isMajor;

}

