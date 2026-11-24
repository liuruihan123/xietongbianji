package com.xmzs.coedit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档权限配置
 * 用于控制 OnlyOffice 编辑器的权限设置
 *
 * @author zuleed
 * @date 2025-11-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentPermissionConfig {
    
    /**
     * 是否允许下载
     * 设为 false 时，"下载为..."菜单项将被隐藏
     * 注意：这是前端限制，后端仍需验证权限
     */
    private Boolean allowDownload;
    
    /**
     * 是否允许打印
     * 设为 false 时，"打印"菜单项将被隐藏
     */
    private Boolean allowPrint;
    
    /**
     * 是否允许复制
     * 设为 false 时，仅允许在编辑器内部复制粘贴
     * 无法复制到外部应用程序
     */
    private Boolean allowCopy;
    
    /**
     * 是否允许编辑
     * 设为 false 时，文档进入只读模式
     */
    private Boolean allowEdit;
    
    /**
     * 是否允许评论
     * 设为 false 时，无法添加或编辑评论
     */
    private Boolean allowComment;
    
    /**
     * 默认配置（完全开放）
     * 适用于：文件所有者、内部协作
     */
    public static DocumentPermissionConfig defaultConfig() {
        return DocumentPermissionConfig.builder()
            .allowDownload(true)
            .allowPrint(true)
            .allowCopy(true)
            .allowEdit(true)
            .allowComment(true)
            .build();
    }
    
    /**
     * 只读配置（禁止所有操作）
     * 适用于：高度保密文档、仅供查看
     */
    public static DocumentPermissionConfig readOnlyConfig() {
        return DocumentPermissionConfig.builder()
            .allowDownload(false)
            .allowPrint(false)
            .allowCopy(false)
            .allowEdit(false)
            .allowComment(false)
            .build();
    }
    
    /**
     * 预览配置（允许查看和下载，但不能编辑）
     * 适用于：外部分享、客户预览
     */
    public static DocumentPermissionConfig previewConfig() {
        return DocumentPermissionConfig.builder()
            .allowDownload(true)
            .allowPrint(true)
            .allowCopy(true)
            .allowEdit(false)
            .allowComment(false)
            .build();
    }
    
    /**
     * 评论配置（允许查看和评论，但不能编辑和下载）
     * 适用于：审阅流程、反馈收集
     */
    public static DocumentPermissionConfig commentConfig() {
        return DocumentPermissionConfig.builder()
            .allowDownload(false)
            .allowPrint(false)
            .allowCopy(true)
            .allowEdit(false)
            .allowComment(true)
            .build();
    }
    
    /**
     * 从共享链接配置创建权限配置
     * 
     * @param allowDownload 是否允许下载
     * @param allowPrint 是否允许打印
     * @param allowCopy 是否允许复制
     * @param allowEdit 是否允许编辑
     * @param allowComment 是否允许评论
     * @return 权限配置对象
     */
    public static DocumentPermissionConfig fromShareLink(
        Boolean allowDownload,
        Boolean allowPrint,
        Boolean allowCopy,
        Boolean allowEdit,
        Boolean allowComment
    ) {
        return DocumentPermissionConfig.builder()
            .allowDownload(allowDownload != null ? allowDownload : true)
            .allowPrint(allowPrint != null ? allowPrint : true)
            .allowCopy(allowCopy != null ? allowCopy : true)
            .allowEdit(allowEdit != null ? allowEdit : true)
            .allowComment(allowComment != null ? allowComment : true)
            .build();
    }
    
    /**
     * 验证权限配置的合法性
     * 
     * @return 是否合法
     */
    public boolean isValid() {
        // 至少要允许查看（所有权限都为 false 没有意义）
        return allowDownload != null && allowPrint != null && 
               allowCopy != null && allowEdit != null && allowComment != null;
    }
    
    /**
     * 获取权限级别描述
     * 
     * @return 权限级别描述
     */
    public String getPermissionLevel() {
        if (Boolean.FALSE.equals(allowDownload) && Boolean.FALSE.equals(allowPrint) && 
            Boolean.FALSE.equals(allowCopy) && Boolean.FALSE.equals(allowEdit) && 
            Boolean.FALSE.equals(allowComment)) {
            return "只读（严格）";
        } else if (Boolean.FALSE.equals(allowEdit) && Boolean.FALSE.equals(allowComment)) {
            return "预览";
        } else if (Boolean.FALSE.equals(allowEdit) && Boolean.TRUE.equals(allowComment)) {
            return "评论";
        } else if (Boolean.TRUE.equals(allowEdit)) {
            return "编辑";
        } else {
            return "自定义";
        }
    }
}

