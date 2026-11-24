package com.xmzs.common.chat.gemini;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Gemini File API 响应模型
 * 
 * @author xmzs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeminiFileResponse {

    @JsonProperty("file")
    private FileInfo file;

    /**
     * 文件信息
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileInfo {
        @JsonProperty("name")
        private String name;

        @JsonProperty("displayName")
        private String displayName;

        @JsonProperty("mimeType")
        private String mimeType;

        @JsonProperty("sizeBytes")
        private String sizeBytes;

        @JsonProperty("createTime")
        private String createTime;

        @JsonProperty("updateTime")
        private String updateTime;

        @JsonProperty("expirationTime")
        private String expirationTime;

        @JsonProperty("sha256Hash")
        private String sha256Hash;

        @JsonProperty("uri")
        private String uri;

        @JsonProperty("state")
        private String state;

        @JsonProperty("error")
        private ErrorInfo error;
    }

    /**
     * 错误信息
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorInfo {
        @JsonProperty("code")
        private Integer code;

        @JsonProperty("message")
        private String message;

        @JsonProperty("status")
        private String status;
    }

    /**
     * 检查文件是否上传成功
     */
    public boolean isSuccess() {
        return file != null && file.getUri() != null && !"FAILED".equals(file.getState());
    }

    /**
     * 检查文件是否正在处理中
     */
    public boolean isProcessing() {
        return file != null && "PROCESSING".equals(file.getState());
    }

    /**
     * 检查文件是否处理完成
     */
    public boolean isActive() {
        return file != null && "ACTIVE".equals(file.getState());
    }

    /**
     * 获取文件 URI
     */
    public String getFileUri() {
        return file != null ? file.getUri() : null;
    }

    /**
     * 获取文件 MIME 类型
     */
    public String getMimeType() {
        return file != null ? file.getMimeType() : null;
    }
}
