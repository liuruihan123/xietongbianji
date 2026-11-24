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
 * 文件缩略图对象 co_file_thumbnail
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_file_thumbnail")
@Schema(description = "文件缩略图对象")
public class CoFileThumbnail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 缩略图ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "缩略图ID")
    private Long id;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private Long fileId;

    /**
     * 缩略图路径
     */
    @Schema(description = "缩略图路径")
    private String thumbnailPath;

    /**
     * 缩略图URL
     */
    @Schema(description = "缩略图URL")
    private String thumbnailUrl;

    /**
     * 缩略图宽度
     */
    @Schema(description = "缩略图宽度")
    private Integer width;

    /**
     * 缩略图高度
     */
    @Schema(description = "缩略图高度")
    private Integer height;

    /**
     * 缩略图大小（字节）
     */
    @Schema(description = "缩略图大小（字节）")
    private Long size;

    /**
     * 缩略图格式
     */
    @Schema(description = "缩略图格式")
    private String format;

    /**
     * 生成状态：0-生成中 1-成功 2-失败
     */
    @Schema(description = "生成状态：0-生成中 1-成功 2-失败")
    private Integer status;

    /**
     * 失败原因
     */
    @Schema(description = "失败原因")
    private String failReason;

    /**
     * 访问次数
     */
    @Schema(description = "访问次数")
    private Integer accessCount = 0;

    /**
     * 最后访问时间
     */
    @Schema(description = "最后访问时间")
    private Date lastAccessTime;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private Date expireTime;
}

