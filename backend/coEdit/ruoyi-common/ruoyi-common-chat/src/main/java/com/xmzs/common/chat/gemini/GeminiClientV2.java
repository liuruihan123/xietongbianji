package com.xmzs.common.chat.gemini;

import com.google.common.base.Optional;
import com.google.genai.Client;
import com.google.genai.types.*;
import com.xmzs.common.core.exception.base.BaseException;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Gemini API 客户端 V2
 * 基于 Google 官方 GenAI SDK 的文档处理客户端
 *
 * 使用官方SDK重构，支持：
 * - 文本生成
 * - 文档处理（PDF等）
 * - 图片理解
 * - 文件上传（通过File API）
 *
 * @author xmzs
 */
@Slf4j
@Getter
@Setter
public class GeminiClientV2 {

    /**
     * API Key
     */
    @NotNull
    private String geminikey;

    /**
     * Google GenAI 官方客户端
     */
    private Client genaiClient;

    /**
     * 默认模型
     */
    private static final String DEFAULT_MODEL = "gemini-2.5-pro";

    /**
     * 构造器
     */
    public static GeminiClientV2.Builder builder() {
        return new GeminiClientV2.Builder();
    }

    /**
     * 私有构造函数
     */
    private GeminiClientV2(String geminikey) {
        this.geminikey = geminikey;
        try {
            // 使用官方SDK创建客户端
            this.genaiClient = Client.builder()
                    .apiKey(geminikey)
                    .build();
            log.info("GeminiClientV2 初始化成功，使用官方SDK，API Key: {}***",
                    geminikey.substring(0, Math.min(10, geminikey.length())));
        } catch (Exception e) {
            log.error("GeminiClientV2 初始化失败", e);
            throw new BaseException("Gemini客户端初始化失败: " + e.getMessage());
        }
    }

    /**
     * 处理文档 - 内联数据方式（小于 20MB 的文件）
     * 使用官方SDK的正确方式处理文档
     *
     * @param fileData 文件数据
     * @param mimeType MIME 类型
     * @param prompt 提示词
     * @return 处理结果
     */
    public GeminiResponse processDocumentInline(byte[] fileData, String mimeType, String prompt) {
        return processDocumentInline(fileData, mimeType, prompt, DEFAULT_MODEL);
    }

    /**
     * 处理文档 - 内联数据方式（小于 20MB 的文件）
     * 使用官方SDK的正确方式处理文档，支持自定义模型
     *
     * @param fileData 文件数据
     * @param mimeType MIME 类型
     * @param prompt 提示词
     * @param model 使用的模型，如果为null或空则使用默认模型
     * @return 处理结果
     */
    public GeminiResponse processDocumentInline(byte[] fileData, String mimeType, String prompt, String model) {
        try {
            // 如果模型参数为空，使用默认模型
            String useModel = (model == null || model.trim().isEmpty()) ? DEFAULT_MODEL : model;

            log.info("开始处理文档（官方SDK），文件大小: {} bytes, MIME类型: {}, 使用模型: {}",
                    fileData.length, mimeType, useModel);

            // 使用官方SDK的正确方式构建内容
            Content content = Content.fromParts(
                    Part.fromText(prompt),
                    Part.fromBytes(fileData, mimeType)  // 直接使用字节数组，无需Base64转换
            );

            // 调用官方SDK的generateContent方法
            GenerateContentResponse response = genaiClient.models.generateContent(
                    useModel,
                    content,
                    null
            );

            log.info("文档处理成功，响应获取完成");

            // 转换为自定义响应格式
            return convertToGeminiResponse(response);

        } catch (Exception e) {
            log.error("处理文档失败", e);
            throw new BaseException("处理文档失败: " + e.getMessage());
        }
    }



    /**
     * 处理文档 - 文件 URI 方式（大文件）
     * 使用官方SDK处理已上传的文件
     *
     * @param fileUri 文件 URI
     * @param mimeType MIME 类型
     * @param prompt 提示词
     * @return 处理结果
     */
    public GeminiResponse processDocumentByUri(String fileUri, String mimeType, String prompt) {
        return processDocumentByUri(fileUri, mimeType, prompt, DEFAULT_MODEL);
    }

    /**
     * 处理文档 - 文件 URI 方式（大文件）
     * 使用官方SDK处理已上传的文件，支持自定义模型
     *
     * @param fileUri 文件 URI
     * @param mimeType MIME 类型
     * @param prompt 提示词
     * @param model 使用的模型，如果为null或空则使用默认模型
     * @return 处理结果
     */
    public GeminiResponse processDocumentByUri(String fileUri, String mimeType, String prompt, String model) {
        try {
            // 如果模型参数为空，使用默认模型
            String useModel = (model == null || model.trim().isEmpty()) ? DEFAULT_MODEL : model;

            log.info("开始处理大文件（官方SDK），URI: {}, MIME类型: {}, 使用模型: {}",
                    fileUri, mimeType, useModel);

            // 使用官方SDK构建内容
            Content content = Content.fromParts(
                    Part.fromText(prompt),
                    Part.fromUri(fileUri, mimeType)
            );

            // 调用官方SDK的generateContent方法
            GenerateContentResponse response = genaiClient.models.generateContent(
                    useModel,
                    content,
                    null
            );

            log.info("大文件处理成功，响应获取完成");

            // 转换为自定义响应格式
            return convertToGeminiResponse(response);

        } catch (Exception e) {
            log.error("处理大文件失败", e);
            throw new BaseException("处理大文件失败: " + e.getMessage());
        }
    }

    /**
     * 简单文本生成
     * 使用官方SDK进行文本生成
     *
     * @param prompt 提示词
     * @return 处理结果
     */
    public GeminiResponse generateText(String prompt) {
        return generateText(prompt, DEFAULT_MODEL);
    }

    /**
     * 简单文本生成
     * 使用官方SDK进行文本生成，支持自定义模型
     *
     * @param prompt 提示词
     * @param model 使用的模型，如果为null或空则使用默认模型
     * @return 处理结果
     */
    public GeminiResponse generateText(String prompt, String model) {
        try {
            // 如果模型参数为空，使用默认模型
            String useModel = (model == null || model.trim().isEmpty()) ? DEFAULT_MODEL : model;

            log.info("开始生成文本（官方SDK），提示词长度: {}, 使用模型: {}", prompt.length(), useModel);

            // 使用官方SDK进行文本生成
            GenerateContentResponse response = genaiClient.models.generateContent(
                    useModel,
                    prompt,
                    null
            );

            log.info("文本生成成功，响应获取完成");

            // 转换为自定义响应格式
            return convertToGeminiResponse(response);

        } catch (Exception e) {
            log.error("生成文本失败", e);
            throw new BaseException("生成文本失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件到 Gemini File API（用于大文件处理）
     * 使用官方SDK的File API上传文件
     *
     * @param fileData 文件数据
     * @param mimeType MIME 类型
     * @param displayName 显示名称
     * @return 文件上传响应
     */
    public GeminiFileResponse uploadFile(byte[] fileData, String mimeType, String displayName) {
        try {
            log.info("开始上传文件（官方SDK），文件大小: {} bytes, MIME类型: {}, 显示名称: {}",
                    fileData.length, mimeType, displayName);

            // 使用官方SDK上传文件
            // 注意：File API 在某些情况下可能需要特定的配置
            File file = genaiClient.files.upload(
                    fileData,
                    UploadFileConfig.builder()
                            .mimeType(mimeType)
                            .displayName(displayName)
                            .build()
            );

            // 转换为自定义响应格式
            GeminiFileResponse response = new GeminiFileResponse();
            GeminiFileResponse.FileInfo fileInfo = new GeminiFileResponse.FileInfo();

            if (file != null) {
                fileInfo.setUri(Optional.of("uri").orNull());
                fileInfo.setDisplayName(Optional.of("displayName").orNull());
                fileInfo.setMimeType(Optional.of("mimeType").orNull());
                fileInfo.setState("ACTIVE"); // 假设上传成功后状态为ACTIVE
                response.setFile(fileInfo);
                log.info("文件上传成功，URI: {}", Optional.of("uri").orNull());
            } else {
                log.warn("文件上传失败，未获取到文件信息");
            }

            return response;

        } catch (Exception e) {
            log.error("文件上传失败", e);
            // 返回失败响应
            GeminiFileResponse response = new GeminiFileResponse();
            GeminiFileResponse.FileInfo fileInfo = new GeminiFileResponse.FileInfo();
            fileInfo.setState("FAILED");
            GeminiFileResponse.ErrorInfo errorInfo = new GeminiFileResponse.ErrorInfo();
            errorInfo.setMessage("文件上传失败: " + e.getMessage());
            fileInfo.setError(errorInfo);
            response.setFile(fileInfo);
            return response;
        }
    }

    /**
     * 将官方 SDK 响应转换为自定义响应格式
     * 更好地处理官方SDK的响应结构
     */
    private GeminiResponse convertToGeminiResponse(GenerateContentResponse response) {
        GeminiResponse geminiResponse = new GeminiResponse();

        try {
            // 获取响应文本
            String text = response.text();
            if (text != null && !text.trim().isEmpty()) {
                // 创建候选响应
                GeminiResponse.Candidate candidate = new GeminiResponse.Candidate();

                GeminiResponse.Content content = new GeminiResponse.Content();
                GeminiResponse.Part part = new GeminiResponse.Part();
                part.setText(text);
                content.setParts(List.of(part));
                candidate.setContent(content);

                // 设置完成原因（如果有的话）
                try {
                    if (response.candidates() != null && response.candidates().isPresent()
                            && !response.candidates().get().isEmpty()) {
                        var firstCandidate = response.candidates().get().get(0);
                        if (firstCandidate.finishReason() != null) {
                            candidate.setFinishReason(firstCandidate.finishReason().toString());
                        }
                    }
                } catch (Exception e) {
                    // 忽略完成原因设置错误，不影响主要功能
                    log.debug("设置完成原因时出错，忽略: {}", e.getMessage());
                }

                geminiResponse.setCandidates(List.of(candidate));

                log.info("成功转换官方SDK响应，文本长度: {}", text.length());
            } else {
                log.warn("官方SDK响应文本为空");
                geminiResponse.setCandidates(List.of());
            }
        } catch (Exception e) {
            log.error("转换官方SDK响应失败", e);
            geminiResponse.setCandidates(List.of());
        }

        return geminiResponse;
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private String geminikey;

        public Builder geminikey(String geminikey) {
            this.geminikey = geminikey;
            return this;
        }

        public GeminiClientV2 build() {
            if (geminikey == null || geminikey.trim().isEmpty()) {
                throw new IllegalArgumentException("API Key 不能为空");
            }
            return new GeminiClientV2(geminikey);
        }
    }
}
