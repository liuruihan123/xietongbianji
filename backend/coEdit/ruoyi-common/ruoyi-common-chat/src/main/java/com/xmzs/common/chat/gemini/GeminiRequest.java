package com.xmzs.common.chat.gemini;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Gemini API 请求模型
 * 
 * @author xmzs
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeminiRequest {

    @JsonProperty("contents")
    private List<Content> contents;

    /**
     * 内容部分
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Content {
        @JsonProperty("parts")
        private List<Part> parts;
    }

    /**
     * 内容片段
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Part {
        @JsonProperty("text")
        private String text;

        @JsonProperty("inline_data")
        private InlineData inlineData;

        @JsonProperty("file_data")
        private FileData fileData;
    }

    /**
     * 内联数据 (用于小文件)
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InlineData {
        @JsonProperty("mime_type")
        private String mimeType;

        @JsonProperty("data")
        private String data; // Base64 编码的数据
    }

    /**
     * 文件数据 (用于大文件，通过 File API 上传)
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileData {
        @JsonProperty("mime_type")
        private String mimeType;

        @JsonProperty("file_uri")
        private String fileUri;
    }

    /**
     * 自定义 Builder 方法
     */
    public static class GeminiRequestBuilder {
        private List<Content> contents = new ArrayList<>();

        /**
         * 添加文本部分
         */
        public GeminiRequestBuilder addTextPart(String text) {
            if (contents.isEmpty()) {
                contents.add(Content.builder().parts(new ArrayList<>()).build());
            }
            
            Part textPart = Part.builder()
                    .text(text)
                    .build();
            
            contents.get(0).getParts().add(textPart);
            return this;
        }

        /**
         * 添加内联数据 (小于 20MB 的文件)
         */
        public GeminiRequestBuilder addInlineData(byte[] data, String mimeType) {
            if (contents.isEmpty()) {
                contents.add(Content.builder().parts(new ArrayList<>()).build());
            }

            String base64Data = Base64.getEncoder().encodeToString(data);
            
            InlineData inlineData = InlineData.builder()
                    .mimeType(mimeType)
                    .data(base64Data)
                    .build();

            Part dataPart = Part.builder()
                    .inlineData(inlineData)
                    .build();

            contents.get(0).getParts().add(dataPart);
            return this;
        }

        /**
         * 添加文件数据 (通过 File API 上传的大文件)
         */
        public GeminiRequestBuilder addFileData(String fileUri, String mimeType) {
            if (contents.isEmpty()) {
                contents.add(Content.builder().parts(new ArrayList<>()).build());
            }

            FileData fileData = FileData.builder()
                    .mimeType(mimeType)
                    .fileUri(fileUri)
                    .build();

            Part filePart = Part.builder()
                    .fileData(fileData)
                    .build();

            contents.get(0).getParts().add(filePart);
            return this;
        }

        /**
         * 构建请求
         */
        public GeminiRequest build() {
            return new GeminiRequest(contents);
        }
    }
}
