package com.xmzs.coedit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmzs.coedit.config.OnlyOfficeConfig;
import com.xmzs.coedit.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * OnlyOffice服务类
 * 提供与OnlyOffice Document Server交互的核心功能
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnlyOfficeService {

    private final OnlyOfficeConfig onlyOfficeConfig;
    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;

    /**
     * 发送命令到OnlyOffice Document Server
     *
     * @param command 命令类型
     * @param key 文档Key
     * @param additionalParams 额外参数
     * @return 响应结果
     */
    public Map<String, Object> sendCommand(String command, String key, Map<String, Object> additionalParams) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("c", command);
            requestBody.put("key", key);
            
            if (additionalParams != null) {
                requestBody.putAll(additionalParams);
            }

            // 如果启用JWT，添加token
            if (onlyOfficeConfig.getJwtEnabled()) {
                String token = jwtTokenUtil.generateToken(requestBody);
                requestBody.put("token", token);
            }

            String jsonRequest = objectMapper.writeValueAsString(requestBody);
            log.info("发送命令到OnlyOffice: {}", jsonRequest);

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(onlyOfficeConfig.getDocumentServerCommandUrl());
                httpPost.setEntity(new StringEntity(jsonRequest, ContentType.APPLICATION_JSON));

                // 如果启用JWT，添加Authorization头
                if (onlyOfficeConfig.getJwtEnabled()) {
                    String token = jwtTokenUtil.generateToken(requestBody);
                    httpPost.setHeader(onlyOfficeConfig.getJwtHeader(), "Bearer " + token);
                }

                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    String responseBody = new String(
                        response.getEntity().getContent().readAllBytes(),
                        StandardCharsets.UTF_8
                    );
                    log.info("OnlyOffice响应: {}", responseBody);

                    @SuppressWarnings("unchecked")
                    Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
                    return result;
                }
            }
        } catch (Exception e) {
            log.error("发送命令到OnlyOffice失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", -1);
            errorResult.put("message", e.getMessage());
            return errorResult;
        }
    }

    /**
     * 强制保存文档
     *
     * @param key 文档Key
     * @param userId 用户ID
     * @return 是否成功
     */
    public boolean forceSave(String key, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userdata", String.valueOf(userId));

        Map<String, Object> result = sendCommand("forcesave", key, params);
        Integer error = (Integer) result.get("error");
        return error != null && error == 0;
    }

    /**
     * 获取文档信息
     *
     * @param key 文档Key
     * @return 文档信息
     */
    public Map<String, Object> getDocumentInfo(String key) {
        return sendCommand("info", key, null);
    }

    /**
     * 删除文档
     *
     * @param key 文档Key
     * @return 是否成功
     */
    public boolean deleteDocument(String key) {
        Map<String, Object> result = sendCommand("drop", key, null);
        Integer error = (Integer) result.get("error");
        return error != null && error == 0;
    }

    /**
     * 获取文档版本
     *
     * @param key 文档Key
     * @return 版本信息
     */
    public Map<String, Object> getVersion(String key) {
        return sendCommand("version", key, null);
    }

    /**
     * 转换文档格式
     *
     * @param fileUrl 文件URL
     * @param fileType 源文件类型
     * @param outputType 目标文件类型
     * @param key 文档Key
     * @return 转换结果
     */
    public Map<String, Object> convertDocument(String fileUrl, String fileType, 
                                               String outputType, String key) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("url", fileUrl);
            requestBody.put("filetype", fileType);
            requestBody.put("outputtype", outputType);
            requestBody.put("key", key);
            requestBody.put("async", false);

            // 如果启用JWT，添加token
            if (onlyOfficeConfig.getJwtEnabled()) {
                String token = jwtTokenUtil.generateToken(requestBody);
                requestBody.put("token", token);
            }

            String jsonRequest = objectMapper.writeValueAsString(requestBody);
            log.info("发送转换请求到OnlyOffice: {}", jsonRequest);

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(onlyOfficeConfig.getDocumentServerConvertUrl());
                httpPost.setEntity(new StringEntity(jsonRequest, ContentType.APPLICATION_JSON));

                // 如果启用JWT，添加Authorization头
                if (onlyOfficeConfig.getJwtEnabled()) {
                    String token = jwtTokenUtil.generateToken(requestBody);
                    httpPost.setHeader(onlyOfficeConfig.getJwtHeader(), "Bearer " + token);
                }

                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    String responseBody = new String(
                        response.getEntity().getContent().readAllBytes(),
                        StandardCharsets.UTF_8
                    );
                    log.info("OnlyOffice转换响应: {}", responseBody);

                    @SuppressWarnings("unchecked")
                    Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
                    return result;
                }
            }
        } catch (Exception e) {
            log.error("文档转换失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", -6);
            errorResult.put("message", e.getMessage());
            return errorResult;
        }
    }

    /**
     * 检查OnlyOffice服务是否可用
     *
     * @return 是否可用
     */
    public boolean isServiceAvailable() {
        try {
            // 尝试访问OnlyOffice API
            String apiUrl = onlyOfficeConfig.getDocumentServerApiUrl();
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(apiUrl);
                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    return response.getCode() == 200;
                }
            }
        } catch (IOException e) {
            log.error("检查OnlyOffice服务可用性失败", e);
            return false;
        }
    }
}

