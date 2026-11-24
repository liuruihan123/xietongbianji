package com.xmzs.coedit.service;

import cn.dev33.satoken.stp.StpUtil;
import com.xmzs.coedit.config.OnlyOfficeConfig;
import com.xmzs.coedit.constant.OnlyOfficeConstants;
import com.xmzs.coedit.domain.dto.DocumentPermissionConfig;
import com.xmzs.coedit.domain.dto.EditorConfig;
import com.xmzs.coedit.utils.FileUtil;
import com.xmzs.coedit.utils.JwtTokenUtil;
import com.xmzs.common.satoken.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 编辑器配置服务
 * 用于生成OnlyOffice编辑器配置
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EditorConfigService {

    private final OnlyOfficeConfig onlyOfficeConfig;
    private final JwtTokenUtil jwtTokenUtil;
    private final DocumentService documentService;
    /**
     * 创建编辑器配置
     *
     * @param fileId 文件ID (String类型,支持Long ID和UUID)
     * @param fileName 文件名
     * @param fileUrl 文件URL
     * @param documentKey 文档Key
     * @param mode 编辑模式
     * @param permissionConfig 权限配置（可选，为null时使用默认配置）
     * @return 编辑器配置
     */
    public EditorConfig createEditorConfig(Long fileId, String fileName, String fileUrl,
                                          String documentKey, String mode,
                                          DocumentPermissionConfig permissionConfig) {
        // 获取文件扩展名和文档类型
        String extension = FileUtil.getFileExtension(fileName);
        String documentType = FileUtil.getDocumentType(extension);

        // 获取当前用户信息并生成authToken
        Long userId = LoginHelper.getUserId();
        String username = LoginHelper.getUserName();

        // 【用户显示名称配置】
        // 获取用户的昵称，用于在OnlyOffice编辑器中显示
        // 当前配置：优先使用昵称(nickName)，如果昵称为空则使用用户名(userName)
        // 可选配置：
        //   1. 只使用昵称：String userDisplayName = LoginHelper.getLoginUser().getNickName();
        //   2. 只使用用户名：String userDisplayName = username;
        //   3. 自定义格式：String userDisplayName = nickName + "(" + username + ")";
        String nickName = LoginHelper.getLoginUser().getNickName();
        String userDisplayName = (nickName != null && !nickName.isEmpty()) ? nickName : username;

        // 修复: 使用当前用户的Token而非创建新会话, 避免logout导致Token失效
        // 旧代码: String authToken = StpUtil.createLoginSession("sys_user:"+userId);
        // 新方案: 直接使用当前用户的Token, 确保OnlyOffice下载文档时Token有效
        String authToken = StpUtil.getTokenValue();

        // 构建文档信息，针对TXT/CSV文件添加编码配置
        EditorConfig.Info.InfoBuilder infoBuilder = EditorConfig.Info.builder()
            .owner(userDisplayName)  // 使用用户显示名称（昵称或用户名）
            .uploaded(String.valueOf(System.currentTimeMillis()));

        // 如果是TXT或CSV文件，添加编码信息以避免弹出编码选择对话框
        if ("txt".equalsIgnoreCase(extension) || "csv".equalsIgnoreCase(extension)) {
            infoBuilder.encoding("utf-8")
                      .codePage(65001);  // 65001 = UTF-8
            log.debug("为{}文件添加UTF-8编码配置", extension);
        }

        // 构建文档配置
        EditorConfig.Document document = EditorConfig.Document.builder()
            .fileType(extension)
            .key(documentKey)
            .title(fileName)
            .url(documentService.getDocumentUrl(String.valueOf(fileId), onlyOfficeConfig.getServerUrl(), authToken))
            .permissions(createPermissions(mode, extension, permissionConfig))
            .info(infoBuilder.build())
            .build();

        // 构建编辑器配置
        EditorConfig.Editor editor = EditorConfig.Editor.builder()
            .callbackUrl(onlyOfficeConfig.getCallbackUrl() + "/" + fileId)
            .lang("zh-CN")
            .mode(mode)
            .user(EditorConfig.User.builder()
                .id(String.valueOf(userId))
                .name(userDisplayName)  // 使用用户显示名称（昵称或用户名）
                .build())
            .customization(createCustomization())
            .build();

        // 构建完整配置
        EditorConfig config = EditorConfig.builder()
            .document(document)
            .documentType(documentType)
            .editorConfig(editor)
            .build();

        // 如果启用JWT，生成token
        if (onlyOfficeConfig.getJwtEnabled()) {
            String token = jwtTokenUtil.generateDocumentToken(config);
            config.setToken(token);
        }

        return config;
    }

    /**
     * 创建权限配置
     *
     * @param mode 编辑模式
     * @param extension 文件扩展名
     * @param config 权限配置（可选）
     * @return 权限配置
     */
    private EditorConfig.Permissions createPermissions(String mode, String extension,
                                                       DocumentPermissionConfig config) {
        // 如果没有传入配置，使用默认配置
        if (config == null) {
            config = DocumentPermissionConfig.defaultConfig();
            log.debug("使用默认权限配置");
        } else {
            log.debug("使用自定义权限配置: {}", config.getPermissionLevel());
        }

        // 判断是否可以编辑（需要同时满足：模式为编辑、文件格式支持编辑、权限允许编辑）
        boolean canEdit = OnlyOfficeConstants.Mode.EDIT.equals(mode)
                         && FileUtil.isEditableFormat(extension)
                         && Boolean.TRUE.equals(config.getAllowEdit());

        return EditorConfig.Permissions.builder()
            .edit(canEdit)
            .download(config.getAllowDownload())
            .print(config.getAllowPrint())
            .copy(config.getAllowCopy())
            .comment(canEdit && Boolean.TRUE.equals(config.getAllowComment()))
            .fillForms(canEdit)
            .review(canEdit)
            .modifyFilter(canEdit)
            .modifyContentControl(canEdit)
            .build();
    }

    /**
     * 创建自定义配置
     *
     * @return 自定义配置
     */
    private EditorConfig.Customization createCustomization() {
        return EditorConfig.Customization.builder()
            .autosave(true)
            .forcesave(true)
            .plugins(true)
            .comments(true)
            .chat(true)
            .compactToolbar(false)
            .help(false)  // 设置为false以避免加载不存在的帮助文件（Contents.json 404错误）
            .suggestFeature(false)  // 隐藏"提出功能建议"按钮 (id="fm-btn-suggest")
            .feedback(EditorConfig.Feedback.builder()
                .visible(false)  // 隐藏"反馈与支持"按钮
                .build())
            .build();
    }

    /**
     * 创建编辑器配置（向后兼容，使用默认权限）
     *
     * @param fileId 文件ID
     * @param fileName 文件名
     * @param fileUrl 文件URL
     * @param documentKey 文档Key
     * @param mode 编辑模式
     * @return 编辑器配置
     */
    public EditorConfig createEditorConfig(Long fileId, String fileName, String fileUrl,
                                          String documentKey, String mode) {
        return createEditorConfig(fileId, fileName, fileUrl, documentKey, mode, null);
    }

    /**
     * 创建查看模式配置
     *
     * @param fileId 文件ID
     * @param fileName 文件名
     * @param fileUrl 文件URL
     * @param documentKey 文档Key
     * @return 编辑器配置
     */
    public EditorConfig createViewConfig(Long fileId, String fileName, String fileUrl, String documentKey) {
        return createEditorConfig(fileId, fileName, fileUrl, documentKey, OnlyOfficeConstants.Mode.VIEW, null);
    }

    /**
     * 创建编辑模式配置
     *
     * @param fileId 文件ID
     * @param fileName 文件名
     * @param fileUrl 文件URL
     * @param documentKey 文档Key
     * @return 编辑器配置
     */
    public EditorConfig createEditConfig(Long fileId, String fileName, String fileUrl, String documentKey) {
        return createEditorConfig(fileId, fileName, fileUrl, documentKey, OnlyOfficeConstants.Mode.EDIT, null);
    }

    /**
     * 创建评论模式配置
     *
     * @param fileId 文件ID
     * @param fileName 文件名
     * @param fileUrl 文件URL
     * @param documentKey 文档Key
     * @return 编辑器配置
     */
    public EditorConfig createCommentConfig(Long fileId, String fileName, String fileUrl, String documentKey) {
        return createEditorConfig(fileId, fileName, fileUrl, documentKey, OnlyOfficeConstants.Mode.COMMENT, null);
    }

    /**
     * 创建审阅模式配置
     *
     * @param fileId 文件ID
     * @param fileName 文件名
     * @param fileUrl 文件URL
     * @param documentKey 文档Key
     * @return 编辑器配置
     */
    public EditorConfig createReviewConfig(Long fileId, String fileName, String fileUrl, String documentKey) {
        return createEditorConfig(fileId, fileName, fileUrl, documentKey, OnlyOfficeConstants.Mode.REVIEW, null);
    }

    /**
     * 根据分享权限创建编辑器配置（使用细粒度权限配置）
     *
     * @param fileId 文件ID
     * @param fileName 文件名
     * @param fileUrl 文件URL
     * @param documentKey 文档Key
     * @param permissionConfig 权限配置对象
     * @param shareCode 分享码（用于回调URL）
     * @param userId 用户ID
     * @param userName 用户名称
     * @return 编辑器配置
     */
    public EditorConfig createShareLinkConfig(Long fileId, String fileName, String fileUrl,
                                             String documentKey, DocumentPermissionConfig permissionConfig,
                                             String shareCode, Long userId, String userName) {

        // 注意: fileUrl参数已经包含了正确的authToken，由Controller层生成
        // 不需要在这里重新生成authToken

        // 获取文件扩展名和文档类型
        String extension = FileUtil.getFileExtension(fileName);
        String documentType = FileUtil.getDocumentType(extension);

        // 如果没有传入权限配置，使用默认配置
        if (permissionConfig == null) {
            permissionConfig = DocumentPermissionConfig.defaultConfig();
            log.warn("共享链接未指定权限配置，使用默认配置");
        }

        // 根据权限配置确定编辑模式
        String mode = Boolean.TRUE.equals(permissionConfig.getAllowEdit())
            ? OnlyOfficeConstants.Mode.EDIT
            : OnlyOfficeConstants.Mode.VIEW;

        // 确定最终的用户ID和名称
        String finalUserId;
        String finalUserName;

        if (userId != null) {
            // 已认证用户
            finalUserId = String.valueOf(userId);
            finalUserName = userName;
        } else {
            // 访客用户，生成一个临时的唯一ID以支持协作
            finalUserId = "visitor_" + java.util.UUID.randomUUID().toString();
            finalUserName = (userName != null && !userName.isEmpty()) ? userName : "访客";
        }

        // 使用权限配置创建权限对象
        boolean canEdit = Boolean.TRUE.equals(permissionConfig.getAllowEdit())
                         && FileUtil.isEditableFormat(extension);

        EditorConfig.Permissions permissions = EditorConfig.Permissions.builder()
            .edit(canEdit)
            .download(permissionConfig.getAllowDownload())
            .print(permissionConfig.getAllowPrint())
            .copy(permissionConfig.getAllowCopy())
            .comment(canEdit && Boolean.TRUE.equals(permissionConfig.getAllowComment()))
            .fillForms(canEdit)
            .review(canEdit)
            .modifyFilter(canEdit)
            .modifyContentControl(canEdit)
            .build();

        log.info("共享链接权限配置: shareCode={}, 权限级别={}, download={}, print={}, copy={}, edit={}, comment={}",
            shareCode, permissionConfig.getPermissionLevel(),
            permissionConfig.getAllowDownload(), permissionConfig.getAllowPrint(),
            permissionConfig.getAllowCopy(), permissionConfig.getAllowEdit(),
            permissionConfig.getAllowComment());

        // 构建文档信息
        EditorConfig.Info.InfoBuilder infoBuilder = EditorConfig.Info.builder()
            .owner("分享文档")
            .uploaded(String.valueOf(System.currentTimeMillis()));

        // 如果是TXT或CSV文件，添加编码信息
        if ("txt".equalsIgnoreCase(extension) || "csv".equalsIgnoreCase(extension)) {
            infoBuilder.encoding("utf-8")
                      .codePage(65001);
            log.debug("为{}文件添加UTF-8编码配置", extension);
        }

        // 构建文档配置
        // 修复: 直接使用Controller传入的fileUrl，该URL已包含正确的authToken
        EditorConfig.Document document = EditorConfig.Document.builder()
            .fileType(extension)
            .key(documentKey)
            .title(fileName)
            .url(fileUrl)  // 使用传入的fileUrl，而不是重新生成
            .permissions(permissions)
            .info(infoBuilder.build())
            .build();

        // 构建编辑器配置 - 使用分享码作为回调URL的一部分
        EditorConfig.Editor editor = EditorConfig.Editor.builder()
            .callbackUrl(onlyOfficeConfig.getCallbackUrl() + "/share/" + shareCode)
            .lang("zh-CN")
            .mode(mode)
            .user(EditorConfig.User.builder()
                .id(finalUserId)
                .name(finalUserName)
                .build())
            .customization(createCustomization())
            .build();

        // 构建完整配置
        EditorConfig config = EditorConfig.builder()
            .document(document)
            .documentType(documentType)
            .editorConfig(editor)
            .build();

        // 如果启用JWT，生成token
        if (onlyOfficeConfig.getJwtEnabled()) {
            String token = jwtTokenUtil.generateDocumentToken(config);
            config.setToken(token);
        }

        return config;
    }

    /**
     * 根据分享权限创建编辑器配置（向后兼容，使用旧的 permissionType 参数）
     *
     * @param fileId 文件ID
     * @param fileName 文件名
     * @param fileUrl 文件URL
     * @param documentKey 文档Key
     * @param permissionType 权限类型：0-只读 1-可编辑 2-可下载 3-完全权限
     * @param shareCode 分享码
     * @param userId 用户ID
     * @param userName 用户名称
     * @return 编辑器配置
     */
    public EditorConfig createShareLinkConfig(Long fileId, String fileName, String fileUrl,
                                             String documentKey, Integer permissionType,
                                             String shareCode, Long userId, String userName) {
        // 将旧的 permissionType 转换为新的 DocumentPermissionConfig
        DocumentPermissionConfig permissionConfig;

        switch (permissionType) {
            case 1: // 可编辑
                permissionConfig = DocumentPermissionConfig.builder()
                    .allowDownload(false)
                    .allowPrint(true)
                    .allowCopy(true)
                    .allowEdit(true)
                    .allowComment(true)
                    .build();
                break;
            case 2: // 可下载
                permissionConfig = DocumentPermissionConfig.previewConfig();
                break;
            case 3: // 完全权限
                permissionConfig = DocumentPermissionConfig.defaultConfig();
                break;
            default: // 0-只读
                permissionConfig = DocumentPermissionConfig.readOnlyConfig();
                break;
        }

        log.debug("向后兼容: 将 permissionType={} 转换为权限配置: {}", permissionType, permissionConfig.getPermissionLevel());

        // 调用新的方法
        return createShareLinkConfig(fileId, fileName, fileUrl, documentKey,
                                    permissionConfig, shareCode, userId, userName);
    }
}

