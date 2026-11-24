package com.xmzs.common.chat.gemini;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Gemini API 响应模型
 * 
 * @author xmzs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeminiResponse {

    @JsonProperty("candidates")
    private List<Candidate> candidates;

    @JsonProperty("usageMetadata")
    private UsageMetadata usageMetadata;

    /**
     * 候选响应
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Candidate {
        @JsonProperty("content")
        private Content content;

        @JsonProperty("finishReason")
        private String finishReason;

        @JsonProperty("index")
        private Integer index;

        @JsonProperty("safetyRatings")
        private List<SafetyRating> safetyRatings;
    }

    /**
     * 内容
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Content {
        @JsonProperty("parts")
        private List<Part> parts;

        @JsonProperty("role")
        private String role;
    }

    /**
     * 内容片段
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Part {
        @JsonProperty("text")
        private String text;
    }

    /**
     * 安全评级
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SafetyRating {
        @JsonProperty("category")
        private String category;

        @JsonProperty("probability")
        private String probability;
    }

    /**
     * 使用元数据
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UsageMetadata {
        @JsonProperty("promptTokenCount")
        private Integer promptTokenCount;

        @JsonProperty("candidatesTokenCount")
        private Integer candidatesTokenCount;

        @JsonProperty("totalTokenCount")
        private Integer totalTokenCount;
    }

    /**
     * 获取第一个候选响应的文本内容
     */
    public String getText() {
        if (candidates != null && !candidates.isEmpty()) {
            Candidate firstCandidate = candidates.get(0);
            if (firstCandidate.getContent() != null && 
                firstCandidate.getContent().getParts() != null && 
                !firstCandidate.getContent().getParts().isEmpty()) {
                
                Part firstPart = firstCandidate.getContent().getParts().get(0);
                return firstPart.getText();
            }
        }
        return null;
    }

    /**
     * 获取所有候选响应的文本内容
     */
    public List<String> getAllTexts() {
        if (candidates == null) {
            return List.of();
        }

        return candidates.stream()
                .filter(candidate -> candidate.getContent() != null && 
                                   candidate.getContent().getParts() != null)
                .flatMap(candidate -> candidate.getContent().getParts().stream())
                .map(Part::getText)
                .filter(text -> text != null)
                .toList();
    }

    /**
     * 检查是否有有效响应
     */
    public boolean hasValidResponse() {
        return candidates != null && !candidates.isEmpty() && getText() != null;
    }
}
