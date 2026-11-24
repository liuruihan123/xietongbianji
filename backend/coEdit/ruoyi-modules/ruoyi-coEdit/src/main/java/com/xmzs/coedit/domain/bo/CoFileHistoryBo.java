package com.xmzs.coedit.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 文件历史业务对象
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@Schema(description = "文件历史业务对象")
public class CoFileHistoryBo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @NotNull(message = "文件ID不能为空")
    @Schema(description = "文件ID", required = true)
    private Long fileId;

    /**
     * 版本号
     */
    @Schema(description = "版本号")
    private Integer version;

    /**
     * 文档Key
     */
    @Schema(description = "文档Key")
    private String documentKey;

    /**
     * 文件路径
     */
    @Schema(description = "文件路径")
    private String filePath;

    /**
     * 文件URL
     */
    @Schema(description = "文件URL")
    private String fileUrl;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    private Long fileSize;

    /**
     * 变更数据URL
     */
    @Schema(description = "变更数据URL")
    private String changesUrl;

    /**
     * 变更数据路径
     */
    @Schema(description = "变更数据路径")
    private String changesPath;

    /**
     * 服务器版本号
     */
    @Schema(description = "服务器版本号")
    private Integer serverVersion;

    /**
     * 变更描述
     */
    @Schema(description = "变更描述")
    private String changeDescription;

    /**
     * 变更类型：1-创建 2-编辑 3-重命名 4-移动 5-恢复
     */
    @Schema(description = "变更类型")
    private Integer changeType;

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
}

