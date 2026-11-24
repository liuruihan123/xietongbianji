package com.xmzs.coedit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmzs.coedit.config.OnlyOfficeConfig;
import com.xmzs.coedit.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * OnlyOffice 命令服务
 * 用于调用 OnlyOffice Document Server 的命令服务 API
 *
 * @author zuleed
 * @date 2025-11-07
 */
@Slf4j
@Service
public class OnlyOfficeCommandService {

    @Autowired
    private OnlyOfficeConfig onlyOfficeConfig;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 强制保存文档
     *
     * @param fileId 文件ID
     * @return 是否成功
     */
    public boolean forceSave(String fileId) {
        try {
            // 获取文档Key
            String documentKey = documentService.getDocumentKey(fileId);

            log.info("开始强制保存文档: fileId={}, documentKey={}", fileId, documentKey);

            // 先检查文档是否有活动的编辑会话
            Map<String, Object> docInfo = getDocumentInfo(fileId);
            if (docInfo != null) {
                Integer infoError = (Integer) docInfo.get("error");
                if (infoError != null && infoError == 0) {
                    // 文档有活动会话,可以继续强制保存
                    log.debug("文档有活动编辑会话,继续执行强制保存");
                } else if (infoError != null && infoError == 1) {
                    // error=1 表示文档Key未找到或没有活动会话
                    log.warn("文档没有活动的编辑会话,无法执行强制保存: fileId={}, documentKey={}", fileId, documentKey);
                    return false;
                }
            }

            // 构建命令请求
            Map<String, Object> command = new HashMap<>();
            command.put("c", "forcesave");
            command.put("key", documentKey);

            // 如果启用了JWT,需要添加token
            if (onlyOfficeConfig.getJwtEnabled()) {
                String token = jwtTokenUtil.generateToken(command);
                command.put("token", token);
            }

            // 发送命令到OnlyOffice Document Server
            String commandUrl = onlyOfficeConfig.getDocumentServerCommandUrl();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 如果启用了JWT,添加到header
            if (onlyOfficeConfig.getJwtEnabled()) {
                String token = jwtTokenUtil.generateToken(command);
                headers.set(onlyOfficeConfig.getJwtHeader(), "Bearer " + token);
            }

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(command, headers);

            log.debug("发送强制保存命令到: {}", commandUrl);
            log.debug("命令内容: {}", objectMapper.writeValueAsString(command));

            ResponseEntity<Map> response = restTemplate.exchange(
                commandUrl,
                HttpMethod.POST,
                request,
                Map.class
            );

            log.debug("OnlyOffice响应: {}", objectMapper.writeValueAsString(response.getBody()));

            // 检查响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Integer error = (Integer) responseBody.get("error");

                if (error != null && error == 0) {
                    log.info("文档强制保存成功: fileId={}", fileId);
                    return true;
                }else if(error==4){
                    log.info("文档未修改所以此处仍然返回true: fileId={}", fileId);
                    return true;
                } else {
                    String errorMsg = getErrorMessage(error);
                    log.error("文档强制保存失败: fileId={}, error={}, 错误说明: {}", fileId, error, errorMsg);
                    return false;
                }
            } else {
                log.error("OnlyOffice命令服务响应异常: status={}", response.getStatusCode());
                return false;
            }

        } catch (Exception e) {
            log.error("强制保存文档失败: fileId={}", fileId, e);
            return false;
        }
    }

    /**
     * 获取OnlyOffice错误代码的说明
     *
     * @param errorCode 错误代码
     * @return 错误说明
     */
    private String getErrorMessage(Integer errorCode) {
        if (errorCode == null) {
            return "未知错误";
        }

        switch (errorCode) {
            case 0:
                return "成功";
            case 1:
                return "文档Key未找到或没有活动的编辑会话";
            case 2:
                return "回调URL不正确";
            case 3:
                return "内部服务器错误";
            case 4:
                return "文档没有更改或没有活动的编辑会话";
            case 5:
                return "命令不正确";
            case 6:
                return "无效的Token";
            case -1:
                return "未知错误";
            default:
                return "错误代码: " + errorCode;
        }
    }



    /**
     * 获取文档信息
     *
     * @param fileId 文件ID
     * @return 文档信息
     */
    public Map<String, Object> getDocumentInfo(String fileId) {
        try {
            String documentKey = documentService.getDocumentKey(fileId);

            Map<String, Object> command = new HashMap<>();
            command.put("c", "info");
            command.put("key", documentKey);

            String commandUrl = onlyOfficeConfig.getDocumentServerCommandUrl();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (onlyOfficeConfig.getJwtEnabled()) {
                String token = jwtTokenUtil.generateToken(command);
                headers.set(onlyOfficeConfig.getJwtHeader(), "Bearer " + token);
            }

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(command, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                commandUrl,
                HttpMethod.POST,
                request,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            return null;
        } catch (Exception e) {
            log.error("获取文档信息失败: fileId={}", fileId, e);
            return null;
        }
    }

    /**
     * 删除文档编辑会话
     *
     * @param fileId 文件ID
     * @return 是否成功
     */
    public boolean dropDocument(String fileId) {
        try {
            String documentKey = documentService.getDocumentKey(fileId);

            Map<String, Object> command = new HashMap<>();
            command.put("c", "drop");
            command.put("key", documentKey);

            String commandUrl = onlyOfficeConfig.getDocumentServerCommandUrl();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (onlyOfficeConfig.getJwtEnabled()) {
                String token = jwtTokenUtil.generateToken(command);
                headers.set(onlyOfficeConfig.getJwtHeader(), "Bearer " + token);
            }

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(command, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                commandUrl,
                HttpMethod.POST,
                request,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Integer error = (Integer) responseBody.get("error");
                return error != null && error == 0;
            }

            return false;
        } catch (Exception e) {
            log.error("删除文档编辑会话失败: fileId={}", fileId, e);
            return false;
        }
    }
}

