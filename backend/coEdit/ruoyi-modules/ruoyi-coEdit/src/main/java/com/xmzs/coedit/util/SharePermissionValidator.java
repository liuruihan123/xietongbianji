package com.xmzs.coedit.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 共享权限验证器
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
public class SharePermissionValidator {

    /**
     * 访问操作类型
     */
    public static final String ACTION_VIEW = "VIEW";
    public static final String ACTION_EDIT = "EDIT";
    public static final String ACTION_DOWNLOAD = "DOWNLOAD";

    /**
     * 权限类型
     */
    public static final int PERMISSION_VIEW_ONLY = 0;      // 只读
    public static final int PERMISSION_EDITABLE = 1;       // 可编辑
    public static final int PERMISSION_DOWNLOADABLE = 2;   // 可下载
    public static final int PERMISSION_FULL_ACCESS = 3;    // 完全权限

    /**
     * 检查操作权限
     *
     * @param permissionType 权限类型
     * @param action 操作类型
     * @return 是否有权限
     */
    public static boolean checkPermission(Integer permissionType, String action) {
        if (permissionType == null || action == null) {
            return false;
        }

        switch (permissionType) {
            case PERMISSION_VIEW_ONLY:
                // 只读权限：只能查看
                return ACTION_VIEW.equals(action);

            case PERMISSION_EDITABLE:
                // 可编辑权限：可以查看和编辑
                return ACTION_VIEW.equals(action) || ACTION_EDIT.equals(action);

            case PERMISSION_DOWNLOADABLE:
                // 可下载权限：可以查看和下载
                return ACTION_VIEW.equals(action) || ACTION_DOWNLOAD.equals(action);

            case PERMISSION_FULL_ACCESS:
                // 完全权限：可以执行所有操作
                return true;

            default:
                log.warn("未知的权限类型: {}", permissionType);
                return false;
        }
    }

    /**
     * 获取权限描述
     *
     * @param permissionType 权限类型
     * @return 权限描述
     */
    public static String getPermissionDescription(Integer permissionType) {
        if (permissionType == null) {
            return "未知";
        }

        switch (permissionType) {
            case PERMISSION_VIEW_ONLY:
                return "只读";
            case PERMISSION_EDITABLE:
                return "可编辑";
            case PERMISSION_DOWNLOADABLE:
                return "可下载";
            case PERMISSION_FULL_ACCESS:
                return "完全权限";
            default:
                return "未知";
        }
    }

    /**
     * 获取操作描述
     *
     * @param action 操作类型
     * @return 操作描述
     */
    public static String getActionDescription(String action) {
        if (action == null) {
            return "未知";
        }

        switch (action) {
            case ACTION_VIEW:
                return "查看";
            case ACTION_EDIT:
                return "编辑";
            case ACTION_DOWNLOAD:
                return "下载";
            default:
                return "未知";
        }
    }

    /**
     * 将操作字符串转换为操作代码
     *
     * @param action 操作字符串
     * @return 操作代码
     */
    public static Integer getActionCode(String action) {
        if (action == null) {
            return null;
        }

        switch (action) {
            case ACTION_VIEW:
                return 0;
            case ACTION_EDIT:
                return 1;
            case ACTION_DOWNLOAD:
                return 2;
            default:
                return null;
        }
    }

    /**
     * 将操作代码转换为操作字符串
     *
     * @param actionCode 操作代码
     * @return 操作字符串
     */
    public static String getActionString(Integer actionCode) {
        if (actionCode == null) {
            return null;
        }

        switch (actionCode) {
            case 0:
                return ACTION_VIEW;
            case 1:
                return ACTION_EDIT;
            case 2:
                return ACTION_DOWNLOAD;
            default:
                return null;
        }
    }

}

