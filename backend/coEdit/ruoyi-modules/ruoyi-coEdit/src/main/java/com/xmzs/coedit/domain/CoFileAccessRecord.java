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
 * 文件访问记录对象 co_file_access_record
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_file_access_record")
@Schema(description = "文件访问记录对象")
public class CoFileAccessRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "记录ID")
    private Long id;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
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

    /**
     * 最后访问时间
     */
    @Schema(description = "最后访问时间")
    private Date lastAccessedAt;

    /**
     * 访问次数
     */
    @Schema(description = "访问次数")
    private Integer viewCount;

}

