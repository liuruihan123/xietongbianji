package com.xmzs.coedit.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 文件转换业务对象
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@Schema(description = "文件转换业务对象")
public class CoFileConversionBo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 源文件ID
     */
    @NotNull(message = "源文件ID不能为空")
    @Schema(description = "源文件ID", required = true)
    private Long sourceFileId;

    /**
     * 目标文件类型
     */
    @NotBlank(message = "目标文件类型不能为空")
    @Schema(description = "目标文件类型", required = true)
    private String targetFileType;

    /**
     * 目标文件名称
     */
    @Schema(description = "目标文件名称")
    private String targetFileName;

    /**
     * 是否异步转换
     */
    @Schema(description = "是否异步转换")
    private Boolean async = true;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;
}

