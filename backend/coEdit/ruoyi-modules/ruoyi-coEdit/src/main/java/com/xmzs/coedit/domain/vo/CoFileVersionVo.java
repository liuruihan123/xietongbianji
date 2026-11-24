package com.xmzs.coedit.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件版本视图对象 co_file_version
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@Schema(description = "文件版本视图对象")
public class CoFileVersionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 版本ID
     */
    @Schema(description = "版本ID")
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
    private Integer versionNumber;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String fileName;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小")
    private Long fileSize;

    /**
     * 文件大小（格式化）
     */
    @Schema(description = "文件大小（格式化）")
    private String fileSizeFormatted;

    /**
     * 存储路径
     */
    @Schema(description = "存储路径")
    private String filePath;

    /**
     * 文件哈希值（MD5）
     */
    @Schema(description = "文件哈希值")
    private String fileHash;

    /**
     * 版本描述
     */
    @Schema(description = "版本描述")
    private String versionDesc;

    /**
     * OnlyOffice文档Key
     */
    @Schema(description = "OnlyOffice文档Key")
    private String documentKey;

    /**
     * 变更类型：0-创建 1-修改 2-重命名 3-回滚
     */
    @Schema(description = "变更类型")
    private Integer changeType;

    /**
     * 变更类型描述
     */
    @Schema(description = "变更类型描述")
    private String changeTypeDesc;

    /**
     * 变更描述
     */
    @Schema(description = "变更描述")
    private String changeDescription;

    /**
     * 变更摘要（详细内容）
     */
    @Schema(description = "变更摘要")
    private String changeSummary;

    /**
     * 是否当前版本：0-否 1-是
     */
    @Schema(description = "是否当前版本")
    private Integer isCurrent;

    /**
     * 是否主要版本：0-否 1-是
     */
    @Schema(description = "是否主要版本")
    private Integer isMajor;

    /**
     * 创建者ID
     */
    @Schema(description = "创建者ID")
    private Long createdBy;

    /**
     * 创建者名称
     */
    @Schema(description = "创建者名称")
    private String createdName;

    /**
     * 编辑者ID
     */
    @Schema(description = "编辑者ID")
    private Long editorId;

    /**
     * 编辑者名称
     */
    @Schema(description = "编辑者名称")
    private String editorName;

    /**
     * 编辑时长（秒）
     */
    @Schema(description = "编辑时长")
    private Integer editDuration;

    /**
     * 编辑时长（格式化）
     */
    @Schema(description = "编辑时长（格式化）")
    private String editDurationFormatted;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}

