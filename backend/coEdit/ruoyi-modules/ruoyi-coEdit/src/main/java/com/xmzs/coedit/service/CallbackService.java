package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.CoShareLink;
import com.xmzs.coedit.domain.dto.CallbackRequest;
import com.xmzs.coedit.domain.dto.CallbackResponse;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.mapper.CoShareLinkMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * OnlyOffice回调处理服务
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackService {

    private final DocumentService documentService;
    private final CoShareLinkMapper shareLinkMapper;
    private final ICoFileService coFileService;

    /**
     * 处理回调请求
     *
     * @param fileId 文件ID
     * @param request 回调请求
     * @return 回调响应
     */
    public CallbackResponse handleCallback(String fileId, CallbackRequest request) {
        try {
            log.info("收到OnlyOffice回调: fileId={}, status={}, key={}",
                fileId, request.getStatus(), request.getKey());

            // 验证文档是否存在
            if (!documentService.exists(fileId)) {
                log.error("文档不存在: fileId={}", fileId);
                return CallbackResponse.error("文档不存在");
            }

            // 根据状态处理
            switch (request.getStatus()) {
                case 1:
                    // 文档正在编辑
                    return handleEditing(fileId, request);
                case 2:
                    // 文档准备保存
                    return handleSave(fileId, request);
                case 3:
                    // 文档保存错误
                    return handleSaveError(fileId, request);
                case 4:
                    // 文档关闭无更改
                    return handleClose(fileId, request);
                case 6:
                    // 强制保存
                    return handleForceSave(fileId, request);
                case 7:
                    // 强制保存错误
                    return handleForceSaveError(fileId, request);
                default:
                    log.warn("未知的回调状态: status={}", request.getStatus());
                    return CallbackResponse.success();
            }
        } catch (Exception e) {
            log.error("处理回调失败: fileId={}", fileId, e);
            return CallbackResponse.error("处理回调失败: " + e.getMessage());
        }
    }

    /**
     * 处理编辑状态
     */
    private CallbackResponse handleEditing(String fileId, CallbackRequest request) {
        log.info("文档正在编辑: fileId={}, users={}", fileId, request.getUsers());

        // 记录用户操作
        if (request.getActions() != null) {
            for (CallbackRequest.Action action : request.getActions()) {
                String actionType = action.getType() == 0 ? "断开连接" :
                                   action.getType() == 1 ? "连接" : "强制保存";
                log.info("用户操作: userId={}, action={}", action.getUserId(), actionType);
            }
        }

        return CallbackResponse.success();
    }

    /**
     * 处理保存状态
     */
    private CallbackResponse handleSave(String fileId, CallbackRequest request) {
        log.info("[回调保存] 文档准备保存: fileId={}, key={}, url={}", fileId, request.getKey(), request.getUrl());

        // 【关键修复】验证回调的 documentKey 是否与数据库中的一致
        // 防止版本回滚后，旧编辑会话的保存回调覆盖回滚的文件
        if (!isDocumentKeyValid(fileId, request.getKey())) {
            log.warn("[回调保存] ⚠️ 拒绝保存：回调的 documentKey 与数据库不匹配，可能是过时的编辑会话");
            log.warn("[回调保存]    回调 key={}", request.getKey());
            // 返回成功，让 OnlyOffice 认为保存完成，但实际上我们忽略了这次保存
            return CallbackResponse.success();
        }

        try {
            // 下载文档
            File downloadedFile = downloadDocument(request.getUrl());

            // 保存文档
            documentService.saveDocument(fileId, downloadedFile);

            // 更新文档Key
            documentService.updateDocumentKey(fileId);

            // 删除临时文件
            downloadedFile.delete();

            log.info("[回调保存] 文档保存成功: fileId={}", fileId);
            return CallbackResponse.success();
        } catch (Exception e) {
            log.error("[回调保存] 保存文档失败: fileId={}", fileId, e);
            return CallbackResponse.error("保存文档失败: " + e.getMessage());
        }
    }

    /**
     * 处理保存错误
     */
    private CallbackResponse handleSaveError(String fileId, CallbackRequest request) {
        log.error("文档保存错误: fileId={}, key={}", fileId, request.getKey());
        return CallbackResponse.success();
    }

    /**
     * 处理关闭状态
     */
    private CallbackResponse handleClose(String fileId, CallbackRequest request) {
        log.info("文档已关闭（无更改）: fileId={}", fileId);
        return CallbackResponse.success();
    }

    /**
     * 处理强制保存
     */
    private CallbackResponse handleForceSave(String fileId, CallbackRequest request) {
        log.info("[回调强制保存] 强制保存文档: fileId={}, key={}, forceSaveType={}, url={}",
            fileId, request.getKey(), request.getForceSaveType(), request.getUrl());

        // 【关键修复】验证回调的 documentKey 是否与数据库中的一致
        // 防止版本回滚后，旧编辑会话的保存回调覆盖回滚的文件
        if (!isDocumentKeyValid(fileId, request.getKey())) {
            log.warn("[回调强制保存] ⚠️ 拒绝强制保存：回调的 documentKey 与数据库不匹配，可能是过时的编辑会话");
            log.warn("[回调强制保存]    回调 key={}", request.getKey());
            // 返回成功，让 OnlyOffice 认为保存完成，但实际上我们忽略了这次保存
            return CallbackResponse.success();
        }

        try {
            // 下载文档
            File downloadedFile = downloadDocument(request.getUrl());

            // 保存文档
            documentService.saveDocument(fileId, downloadedFile);

            // 删除临时文件
            downloadedFile.delete();

            String saveType = getForceSaveTypeName(request.getForceSaveType());
            log.info("[回调强制保存] 强制保存成功: fileId={}, type={}", fileId, saveType);
            return CallbackResponse.success();
        } catch (Exception e) {
            log.error("[回调强制保存] 强制保存失败: fileId={}", fileId, e);
            return CallbackResponse.error("强制保存失败: " + e.getMessage());
        }
    }

    /**
     * 处理强制保存错误
     */
    private CallbackResponse handleForceSaveError(String fileId, CallbackRequest request) {
        log.error("强制保存错误: fileId={}, key={}", fileId, request.getKey());
        return CallbackResponse.success();
    }

    /**
     * 从URL下载文档
     *
     * @param downloadUrl 下载URL
     * @return 下载的文件
     */
    private File downloadDocument(String downloadUrl) throws Exception {
        log.info("开始下载文档: url={}", downloadUrl);

        URL url = new URL(downloadUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(30000);

        try (InputStream inputStream = connection.getInputStream()) {
            // 创建临时文件
            Path tempFile = Files.createTempFile("onlyoffice_", ".tmp");
            File file = tempFile.toFile();

            // 写入文件
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytes = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }

                log.info("文档下载完成: size={} bytes", totalBytes);
            }

            return file;
        } finally {
            connection.disconnect();
        }
    }

    /**
     * 处理分享链接的回调请求
     *
     * @param shareCode 分享码
     * @param request 回调请求
     * @return 回调响应
     */
    public CallbackResponse handleShareCallback(String shareCode, CallbackRequest request) {
        try {
            log.info("收到分享链接OnlyOffice回调: shareCode={}, status={}, key={}",
                shareCode, request.getStatus(), request.getKey());

            // 获取分享链接信息
            CoShareLink shareLink = shareLinkMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CoShareLink>()
                    .eq(CoShareLink::getShareCode, shareCode)
            );

            if (shareLink == null) {
                log.error("分享链接不存在2: shareCode={}", shareCode);
                return CallbackResponse.error("分享链接不存在");
            }

            // 获取文件ID
            String fileId = String.valueOf(shareLink.getFileId());

            // 验证文档是否存在
            if (!documentService.exists(fileId)) {
                log.error("文档不存在: fileId={}", fileId);
                return CallbackResponse.error("文档不存在");
            }

            // 根据状态处理
            switch (request.getStatus()) {
                case 1:
                    // 文档正在编辑
                    return handleEditing(fileId, request);
                case 2:
                    // 文档准备保存
                    return handleSave(fileId, request);
                case 3:
                    // 文档保存错误
                    return handleSaveError(fileId, request);
                case 4:
                    // 文档关闭无更改
                    return handleClose(fileId, request);
                case 6:
                    // 强制保存
                    return handleForceSave(fileId, request);
                case 7:
                    // 强制保存错误
                    return handleForceSaveError(fileId, request);
                default:
                    log.warn("未知的回调状态: status={}", request.getStatus());
                    return CallbackResponse.success();
            }
        } catch (Exception e) {
            log.error("处理分享链接回调失败: shareCode={}", shareCode, e);
            return CallbackResponse.error("处理回调失败: " + e.getMessage());
        }
    }

    /**
     * 获取强制保存类型名称
     */
    private String getForceSaveTypeName(Integer type) {
        if (type == null) {
            return "未知";
        }
        switch (type) {
            case 0:
                return "命令服务";
            case 1:
                return "保存按钮";
            case 2:
                return "定时器";
            case 3:
                return "表单提交";
            default:
                return "未知(" + type + ")";
        }
    }

    /**
     * 验证回调的 documentKey 是否与数据库中的一致
     *
     * 【重要】此方法用于防止版本回滚后，旧编辑会话的保存回调覆盖回滚的文件
     *
     * 场景说明：
     * 1. 用户在编辑器中编辑文档（编辑器持有 documentKey A）
     * 2. 用户执行版本回滚操作
     * 3. 后端成功复制版本文件，并生成新的 documentKey B
     * 4. 此时编辑器仍持有旧的 documentKey A
     * 5. 编辑器触发保存回调（使用 documentKey A）
     * 6. 如果不验证，编辑器内存中的内容会覆盖刚回滚的文件
     *
     * 解决方案：
     * - 对比回调中的 key 与数据库中的 documentKey
     * - 如果不匹配，说明是过时的编辑会话，拒绝保存
     *
     * @param fileId 文件ID
     * @param callbackKey 回调请求中的 key
     * @return true: key 匹配可以保存; false: key 不匹配应拒绝保存
     */
    private boolean isDocumentKeyValid(String fileId, String callbackKey) {
        if (callbackKey == null || callbackKey.isEmpty()) {
            log.warn("[DocumentKey验证] 回调 key 为空，允许保存");
            return true;
        }

        try {
            Long id = Long.parseLong(fileId);
            CoFileVo fileVo = coFileService.queryById(id);

            if (fileVo == null) {
                log.warn("[DocumentKey验证] 文件不存在，允许保存: fileId={}", fileId);
                return true;
            }

            String dbDocumentKey = fileVo.getDocumentKey();
            log.info("[DocumentKey验证] 数据库 documentKey={}, 回调 key={}", dbDocumentKey, callbackKey);

            if (dbDocumentKey == null || dbDocumentKey.isEmpty()) {
                log.warn("[DocumentKey验证] 数据库中 documentKey 为空，允许保存: fileId={}", fileId);
                return true;
            }

            // 首先检查是否完全匹配
            if (callbackKey.equals(dbDocumentKey)) {
                log.debug("[DocumentKey验证] documentKey 完全匹配: key={}", callbackKey);
                return true;
            }

            // 判断 key 的格式类型
            // 旧格式: {fileId}_{version}_{timestamp}，例如 1996470165604286464_1_1764830555883
            // 新格式: UUID-{timestamp}，例如 f66dcdde-0df9-4f24-8431-601c1d665e20-1764830604240
            boolean dbKeyIsUUID = isUUIDFormat(dbDocumentKey);
            boolean callbackKeyIsOldFormat = callbackKey.startsWith(fileId + "_");

            log.debug("[DocumentKey验证] 格式判断: dbKeyIsUUID={}, callbackKeyIsOldFormat={}",
                dbKeyIsUUID, callbackKeyIsOldFormat);

            // 【关键场景】如果数据库中的 key 是 UUID 格式（回滚后生成的新 key）
            // 而回调 key 是旧格式（回滚前的编辑会话）
            // 则拒绝保存 - 这是版本回滚后旧编辑器的保存请求
            if (dbKeyIsUUID && callbackKeyIsOldFormat) {
                log.warn("[DocumentKey验证] ❌ 检测到过时的编辑会话!");
                log.warn("[DocumentKey验证]    数据库 documentKey（新格式/UUID）={}", dbDocumentKey);
                log.warn("[DocumentKey验证]    回调 key（旧格式）={}", callbackKey);
                log.warn("[DocumentKey验证]    这是版本回滚后旧编辑器的保存请求，已拒绝！");
                return false;
            }

            // 【修复】如果两个 key 都是旧格式，只比较 fileId 和 version 部分
            // 因为时间戳可能因为并发或网络延迟而略有不同
            if (!dbKeyIsUUID && callbackKeyIsOldFormat) {
                String dbPrefix = extractFileIdAndVersion(dbDocumentKey);
                String callbackPrefix = extractFileIdAndVersion(callbackKey);

                if (dbPrefix != null && dbPrefix.equals(callbackPrefix)) {
                    log.info("[DocumentKey验证] ✓ 旧格式 key 前缀匹配（忽略时间戳差异）: prefix={}", dbPrefix);
                    return true;
                }
            }

            // 其他情况，如果 key 不匹配，记录警告但允许保存
            // 避免因为验证逻辑问题阻塞正常的保存操作
            log.warn("[DocumentKey验证] ⚠️ documentKey 不完全匹配，但允许保存: 数据库={}, 回调={}",
                dbDocumentKey, callbackKey);
            return true;

        } catch (NumberFormatException e) {
            log.debug("[DocumentKey验证] 文档ID不是Long类型，跳过验证: fileId={}", fileId);
            return true;
        } catch (Exception e) {
            log.error("[DocumentKey验证] 验证时发生错误: fileId={}", fileId, e);
            // 发生错误时允许保存，避免阻塞正常的保存操作
            return true;
        }
    }

    /**
     * 判断 documentKey 是否为 UUID 格式
     * UUID 格式示例: f66dcdde-0df9-4f24-8431-601c1d665e20-1764830604240
     */
    private boolean isUUIDFormat(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        // UUID 格式包含多个连字符，且第一部分是 8 个字符
        // 旧格式是 {fileId}_{version}_{timestamp}，使用下划线分隔
        // 简单判断：如果包含连字符且不以数字开头（fileId 是数字），则认为是 UUID 格式
        return key.contains("-") && !Character.isDigit(key.charAt(0));
    }

    /**
     * 从旧格式 key 中提取 fileId 和 version 部分
     * 旧格式: {fileId}_{version}_{timestamp}
     * 返回: {fileId}_{version}
     */
    private String extractFileIdAndVersion(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        // 找到最后一个下划线的位置（时间戳之前）
        int lastUnderscoreIndex = key.lastIndexOf('_');
        if (lastUnderscoreIndex > 0) {
            return key.substring(0, lastUnderscoreIndex);
        }
        return null;
    }
}

