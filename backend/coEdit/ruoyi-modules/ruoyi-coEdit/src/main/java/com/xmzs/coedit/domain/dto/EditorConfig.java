package com.xmzs.coedit.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * OnlyOffice编辑器配置DTO
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditorConfig {

    /**
     * 文档配置
     */
    private Document document;

    /**
     * 文档类型（word/cell/slide）
     */
    private String documentType;

    /**
     * 编辑器配置
     */
    private Editor editorConfig;

    /**
     * Token（JWT）
     */
    private String token;

    /**
     * 文档对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Document {
        /**
         * 文件类型
         */
        private String fileType;

        /**
         * 文档Key（唯一标识）
         */
        private String key;

        /**
         * 文档标题
         */
        private String title;

        /**
         * 文档URL
         */
        private String url;

        /**
         * 权限配置
         */
        private Permissions permissions;

        /**
         * 文档信息
         */
        private Info info;
    }

    /**
     * 权限配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Permissions {
        /**
         * 是否可编辑
         */
        private Boolean edit;

        /**
         * 是否可下载
         */
        private Boolean download;

        /**
         * 是否可打印
         */
        private Boolean print;

        /**
         * 是否可评论
         */
        private Boolean comment;

        /**
         * 是否可填写表单
         */
        private Boolean fillForms;

        /**
         * 是否可修订
         */
        private Boolean review;

        /**
         * 是否可复制
         */
        private Boolean copy;

        /**
         * 是否可修改过滤器
         */
        private Boolean modifyFilter;

        /**
         * 是否可修改内容控件
         */
        private Boolean modifyContentControl;
    }

    /**
     * 文档信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Info {
        /**
         * 文档所有者
         */
        private String owner;

        /**
         * 上传时间
         */
        private String uploaded;

        /**
         * 是否收藏
         */
        private Boolean favorite;

        /**
         * 文件编码（用于TXT/CSV文件）
         * 例如: "utf-8", "gbk"
         */
        private String encoding;

        /**
         * 代码页（用于TXT/CSV文件）
         * 例如: 65001 (UTF-8), 936 (GBK)
         */
        private Integer codePage;
    }

    /**
     * 编辑器配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Editor {
        /**
         * 回调URL
         */
        private String callbackUrl;

        /**
         * 语言
         */
        private String lang;

        /**
         * 模式（view/edit）
         */
        private String mode;

        /**
         * 用户信息
         */
        private User user;

        /**
         * 自定义配置
         */
        private Customization customization;

        /**
         * 嵌入配置
         */
        private Embedded embedded;
    }

    /**
     * 用户信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class User {
        /**
         * 用户ID
         */
        private String id;

        /**
         * 用户名
         */
        private String name;

        /**
         * 用户组
         */
        private String group;
    }

    /**
     * 自定义配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Customization {
        /**
         * 是否自动保存
         */
        private Boolean autosave;

        /**
         * 是否强制保存
         */
        private Boolean forcesave;

        /**
         * 是否显示评论
         */
        private Boolean comments;

        /**
         * 是否显示聊天
         */
        private Boolean chat;

        /**
         * 是否紧凑工具栏
         */
        private Boolean compactToolbar;

        /**
         * 是否显示帮助
         */
        private Boolean help;

        /**
         * 是否显示"提出功能建议"按钮 (id="fm-btn-suggest")
         */
        private Boolean suggestFeature;

        /**
         * 反馈配置
         */
        private Feedback feedback;

        /**
         * Logo配置
         */
        private Logo logo;

        /**
         * 自定义按钮
         */
        private Boolean plugins;
//        private List<Map<String, Object>> plugins;
    }

    /**
     * 反馈配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Feedback {
        /**
         * 是否显示反馈按钮
         */
        private Boolean visible;

        /**
         * 反馈URL
         */
        private String url;
    }

    /**
     * Logo配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Logo {
        /**
         * Logo图片URL
         */
        private String image;

        /**
         * Logo图片暗色模式URL
         */
        private String imageDark;

        /**
         * Logo点击跳转URL
         */
        private String url;
    }

    /**
     * 嵌入配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Embedded {
        /**
         * 嵌入URL
         */
        private String embedUrl;

        /**
         * 保存URL
         */
        private String saveUrl;

        /**
         * 分享URL
         */
        private String shareUrl;

        /**
         * 工具栏停靠位置
         */
        private String toolbarDocked;
    }
}

