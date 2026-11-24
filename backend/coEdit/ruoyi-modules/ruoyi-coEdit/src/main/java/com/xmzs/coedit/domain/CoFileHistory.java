package com.xmzs.coedit.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件历史对象 co_file_history
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_file_history")
@Schema(description = "文件历史对象")
public class CoFileHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 历史ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "历史ID")
    private Long id;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private Long fileId;

    /**
     * 版本号
     */
    @Schema(description = "版本号")
    private Integer version;

    /**
     * 文档Key（OnlyOffice）
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
    @Schema(description = "变更类型：1-创建 2-编辑 3-重命名 4-移动 5-恢复")
    private Integer changeType;

    /**
     * 是否当前版本
     */
    @Schema(description = "是否当前版本")
    private Boolean isCurrent = false;

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

