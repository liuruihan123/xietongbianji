package com.xmzs.common.chat.gemini;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmzs.common.chat.openai.function.KeyStrategyFunction;
import com.xmzs.common.chat.openai.function.KeyRandomStrategy;
import com.xmzs.common.core.exception.base.BaseException;
import com.xmzs.common.core.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Gemini API 客户端
 * 基于 Google Gemini API 的文档处理客户端
 *
 * @author xmzs
 */
@Slf4j
@Getter
@Setter
public class GeminiClient {
//
//    /**
//     * API Keys
//     */
//    @NotNull
//    private List<String> geminikey;
//
//    /**
//     * API Host
//     */
//    private String geminiHost;
//
//    /**
//     * 自定义的 OkHttpClient
//     */
//    private OkHttpClient okHttpClient;
//
//    /**
//     * API Key 获取策略
//     */
//    private KeyStrategyFunction<List<String>, String> keyStrategy;
//
//    /**
//     * JSON 处理器
//     */
//    private ObjectMapper objectMapper;
//
//    /**
//     * 默认的 Gemini API Host
//     */
//    private static final String DEFAULT_GEMINI_HOST = "https://generativelanguage.googleapis.com/";
//
//    /**
//     * 构造器
//     */
//    public static GeminiClient.Builder builder() {
//        return new GeminiClient.Builder();
//    }
//
//    /**
//     * 私有构造函数
//     */
//    private GeminiClient(Builder builder) {
//        if (builder.geminikey == null || builder.geminikey.isEmpty()) {
//            throw new BaseException("Gemini API Keys 不能为空");
//        }
//        this.geminikey = builder.geminikey;
//
//        if (StringUtils.isBlank(builder.geminiHost)) {
//            builder.geminiHost = DEFAULT_GEMINI_HOST;
//        }
//        this.geminiHost = builder.geminiHost;
//
//        if (Objects.isNull(builder.keyStrategy)) {
//            builder.keyStrategy = new KeyRandomStrategy();
//        }
//        this.keyStrategy = builder.keyStrategy;
//
//        if (Objects.isNull(builder.okHttpClient)) {
//            this.okHttpClient = createDefaultOkHttpClient();
//        } else {
//            this.okHttpClient = builder.okHttpClient;
//        }
//
//        if (Objects.isNull(builder.objectMapper)) {
//            this.objectMapper = new ObjectMapper();
//        } else {
//            this.objectMapper = builder.objectMapper;
//        }
//    }
//
//    /**
//     * 创建默认的 OkHttpClient
//     */
//    private OkHttpClient createDefaultOkHttpClient() {
//        return new OkHttpClient.Builder()
//                .connectTimeout(600, TimeUnit.SECONDS)
//                .writeTimeout(600, TimeUnit.SECONDS)
//                .readTimeout(600, TimeUnit.SECONDS)
//                .addInterceptor(new GeminiAuthInterceptor(this.geminikey, this.keyStrategy))
//                .build();
//    }
//
//    /**
//     * 处理文档 - 内联数据方式
//     *
//     * @param documentData 文档的字节数据
//     * @param mimeType MIME 类型 (如: application/pdf)
//     * @param prompt 提示词
//     * @return 处理结果
//     */
//    public GeminiResponse processDocumentInline(byte[] documentData, String mimeType, String prompt) {
//        try {
//            GeminiRequest request = GeminiRequest.builder()
//                    .addInlineData(documentData, mimeType)
//                    .addTextPart(prompt)
//                    .build();
//
//            return sendRequest(request);
//        } catch (Exception e) {
//            log.error("处理文档失败", e);
//            throw new BaseException("处理文档失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 处理文档 - 文件 URI 方式
//     *
//     * @param fileUri 文件 URI
//     * @param mimeType MIME 类型
//     * @param prompt 提示词
//     * @return 处理结果
//     */
//    public GeminiResponse processDocumentByUri(String fileUri, String mimeType, String prompt) {
//        try {
//            GeminiRequest request = GeminiRequest.builder()
//                    .addFileData(fileUri, mimeType)
//                    .addTextPart(prompt)
//                    .build();
//
//            return sendRequest(request);
//        } catch (Exception e) {
//            log.error("处理文档失败", e);
//            throw new BaseException("处理文档失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 发送请求到 Gemini API
//     */
//    private GeminiResponse sendRequest(GeminiRequest geminiRequest) throws IOException {
//        String url = geminiHost + "v1beta/models/gemini-2.5-pro:generateContent";
//
//        String jsonBody = objectMapper.writeValueAsString(geminiRequest);
//
//        RequestBody body = RequestBody.create(
//                jsonBody,
//                MediaType.get("application/json; charset=utf-8")
//        );
//
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//
//        try (Response response = okHttpClient.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                throw new IOException("请求失败: " + response.code() + " " + response.message());
//            }
//
//            String responseBody = response.body().string();
//            return objectMapper.readValue(responseBody, GeminiResponse.class);
//        }
//    }
//
//    /**
//     * 上传文件到 Gemini File API
//     *
//     * @param fileData 文件数据
//     * @param mimeType MIME 类型
//     * @param displayName 显示名称
//     * @return 文件信息
//     */
//    public GeminiFileResponse uploadFile(byte[] fileData, String mimeType, String displayName) {
//        try {
//            String url = geminiHost + "upload/v1beta/files";
//
//            // 创建文件上传请求
//            RequestBody fileBody = RequestBody.create(fileData, MediaType.parse(mimeType));
//
//            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("file", displayName, fileBody);
//
//            if (StringUtils.isNotBlank(displayName)) {
//                multipartBuilder.addFormDataPart("displayName", displayName);
//            }
//
//            RequestBody requestBody = multipartBuilder.build();
//
//            Request request = new Request.Builder()
//                    .url(url)
//                    .post(requestBody)
//                    .build();
//
//            try (Response response = okHttpClient.newCall(request).execute()) {
//                if (!response.isSuccessful()) {
//                    throw new IOException("文件上传失败: " + response.code() + " " + response.message());
//                }
//
//                String responseBody = response.body().string();
//                return objectMapper.readValue(responseBody, GeminiFileResponse.class);
//            }
//        } catch (Exception e) {
//            log.error("上传文件失败", e);
//            throw new BaseException("上传文件失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Builder 类
//     */
//    public static final class Builder {
//        private List<String> geminikey;
//        private String geminiHost;
//        private OkHttpClient okHttpClient;
//        private KeyStrategyFunction<List<String>, String> keyStrategy;
//        private ObjectMapper objectMapper;
//
//        public Builder() {}
//
//        public Builder geminikey(@NotNull List<String> val) {
//            geminikey = val;
//            return this;
//        }
//
//        public Builder geminiHost(String val) {
//            geminiHost = val;
//            return this;
//        }
//
//        public Builder okHttpClient(OkHttpClient val) {
//            okHttpClient = val;
//            return this;
//        }
//
//        public Builder keyStrategy(KeyStrategyFunction<List<String>, String> val) {
//            keyStrategy = val;
//            return this;
//        }
//
//        public Builder objectMapper(ObjectMapper val) {
//            objectMapper = val;
//            return this;
//        }
//
//        public GeminiClient build() {
//            return new GeminiClient(this);
//        }
//    }
}
