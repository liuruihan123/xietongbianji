package com.xmzs.coedit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OnlyOffice配置类
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@Component
@ConfigurationProperties(prefix = "onlyoffice")
public class OnlyOfficeConfig {

    /**
     * OnlyOffice Document Server地址
     */
    private String documentServerUrl;

    /**
     * OnlyOffice Document Server内部地址（可选）
     */
    private String documentServerInternalUrl;

    /**
     * 后端服务器地址(用于生成文档URL,必须是OnlyOffice服务器可访问的地址)
     */
    private String serverUrl;

    /**
     * 回调地址前缀
     */
    private String callbackUrl;

    /**
     * JWT密钥
     */
    private String jwtSecret;

    /**
     * JWT Header名称
     */
    private String jwtHeader = "Authorization";

    /**
     * 是否启用JWT
     */
    private Boolean jwtEnabled = true;

    /**
     * 文件存储路径
     */
    private String storagePath;

    /**
     * 允许的最大文件大小（MB）
     */
    private Integer maxFileSize = 100;

    /**
     * 支持的文档格式
     */
    private String[] supportedFormats = {
        "doc", "docx", "docm", "dot", "dotx", "dotm", "odt", "fodt", "ott", "rtf", "txt",
        "html", "htm", "mht", "pdf", "djvu", "fb2", "epub", "xps",
        "xls", "xlsx", "xlsm", "xlt", "xltx", "xltm", "ods", "fods", "ots", "csv",
        "pps", "ppsx", "ppsm", "ppt", "pptx", "pptm", "pot", "potx", "potm", "odp", "fodp", "otp",
        "wps",          // WPS文字格式
        "et", "ett",    // WPS表格格式
        "dps", "dpt"    // WPS演示格式
    };

    /**
     * 可编辑的文档格式
     */
    private String[] editableFormats = {
        "docx", "xlsx", "pptx", "txt", "csv"
    };

    /**
     * 获取Document Server API URL
     */
    public String getDocumentServerApiUrl() {
        return documentServerUrl + "/web-apps/apps/api/documents/api.js";
    }

    /**
     * 获取Document Server转换URL
     */
    public String getDocumentServerConvertUrl() {
        return documentServerUrl + "/ConvertService.ashx";
    }

    /**
     * 获取Document Server命令URL
     */
    public String getDocumentServerCommandUrl() {
        return documentServerUrl + "/coauthoring/CommandService.ashx";
    }
}

