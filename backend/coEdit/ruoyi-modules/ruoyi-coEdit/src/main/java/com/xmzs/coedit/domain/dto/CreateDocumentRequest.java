package com.xmzs.coedit.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * 创建在线文档请求DTO
 *
 * @author zuleed
 * @date 2025-11-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "创建在线文档请求")
public class CreateDocumentRequest {

    /**
     * 文件名
     */
    @NotBlank(message = "文件名不能为空")
    @Schema(description = "文件名", required = true, example = "新建文档.docx")
    private String fileName;

    /**
     * 文件类型
     */
    @NotBlank(message = "文件类型不能为空")
    @Schema(description = "文件类型（docx/xlsx/pptx/txt）", required = true, example = "docx")
    private String fileType;

    /**
     * 工作空间ID（可选，不提供则自动使用或创建默认工作空间）
     */
    @Schema(description = "工作空间ID（可选，不提供则自动使用或创建默认工作空间）", example = "1")
    private Long workspaceId;

    /**
     * 文件夹ID（可选）
     */
    @Schema(description = "文件夹ID（可选）", example = "1")
    private Long folderId;
}

