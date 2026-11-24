package com.xmzs.coedit.constant;

/**
 * 工作空间常量
 *
 * @author zuleed
 * @date 2025-11-04
 */
public class WorkspaceConstants {

    /**
     * 工作空间类型
     */
    public static class WorkspaceType {
        /** 个人空间 */
        public static final int PERSONAL = 1;
        /** 部门空间 */
        public static final int DEPARTMENT = 2;
        /** 团队空间 */
        public static final int TEAM = 3;
    }

    /**
     * 工作空间状态
     */
    public static class WorkspaceStatus {
        /** 禁用 */
        public static final int DISABLED = 0;
        /** 正常 */
        public static final int NORMAL = 1;
        /** 归档 */
        public static final int ARCHIVED = 2;
    }

    /**
     * 成员角色
     */
    public static class MemberRole {
        /** 所有者 */
        public static final String OWNER = "owner";
        /** 管理员 */
        public static final String ADMIN = "admin";
        /** 编辑者 */
        public static final String EDITOR = "editor";
        /** 查看者 */
        public static final String VIEWER = "viewer";
        /** 普通成员 */
        public static final String MEMBER = "member";

        /**
         * 校验角色是否有效
         */
        public static boolean isValid(String role) {
            return OWNER.equals(role) || ADMIN.equals(role) || EDITOR.equals(role)
                || VIEWER.equals(role) || MEMBER.equals(role);
        }
    }

    /**
     * 成员状态
     */
    public static class MemberStatus {
        /** 已移除 */
        public static final int REMOVED = 0;
        /** 正常 */
        public static final int NORMAL = 1;
        /** 已禁用 */
        public static final int DISABLED = 2;
    }

    /**
     * 加入方式
     */
    public static class JoinType {
        /** 邀请 */
        public static final int INVITE = 1;
        /** 申请 */
        public static final int APPLY = 2;
        /** 系统添加 */
        public static final int SYSTEM = 3;
    }

    /**
     * 文件夹类型
     */
    public static class FolderType {
        /** 普通文件夹 */
        public static final int NORMAL = 1;
        /** 私密文件夹 */
        public static final int PRIVATE = 2;
        /** 共享文件夹 */
        public static final int SHARED = 3;
        /** 系统文件夹 */
        public static final int SYSTEM = 4;
    }

    /**
     * 文件夹状态
     */
    public static class FolderStatus {
        /** 已删除 */
        public static final int DELETED = 0;
        /** 正常 */
        public static final int NORMAL = 1;
        /** 已锁定 */
        public static final int LOCKED = 2;
    }

    /**
     * 权限类型
     */
    public static class PermissionType {
        /** 用户 */
        public static final int USER = 1;
        /** 部门 */
        public static final int DEPARTMENT = 2;
        /** 角色 */
        public static final int ROLE = 3;
        /** 团队 */
        public static final int TEAM = 4;
    }

    /**
     * 权限级别
     */
    public static class PermissionLevel {
        /** 查看 */
        public static final String VIEW = "view";
        /** 编辑 */
        public static final String EDIT = "edit";
        /** 管理 */
        public static final String ADMIN = "admin";
        /** 所有者 */
        public static final String OWNER = "owner";
    }

    /**
     * 权限状态
     */
    public static class PermissionStatus {
        /** 已撤销 */
        public static final int REVOKED = 0;
        /** 正常 */
        public static final int NORMAL = 1;
        /** 已过期 */
        public static final int EXPIRED = 2;
    }

    /**
     * 文件状态
     */
    public static class FileStatus {
        /** 已删除 */
        public static final int DELETED = 0;
        /** 正常 */
        public static final int NORMAL = 1;
        /** 归档 */
        public static final int ARCHIVED = 2;
    }

    /**
     * 文件编辑状态
     */
    public static class EditStatus {
        /** 未编辑 */
        public static final int NOT_EDITED = 0;
        /** 编辑中 */
        public static final int EDITING = 1;
        /** 保存中 */
        public static final int SAVING = 2;
        /** 已保存 */
        public static final int SAVED = 3;
    }

    /**
     * 文件变更类型
     */
    public static class ChangeType {
        /** 创建 */
        public static final int CREATE = 1;
        /** 编辑 */
        public static final int EDIT = 2;
        /** 上传 */
        public static final int UPLOAD = 3;
        /** 替换 */
        public static final int REPLACE = 4;
    }

    /**
     * 回收站状态
     */
    public static class RecycleBinStatus {
        /** 已清理 */
        public static final int CLEANED = 0;
        /** 待清理 */
        public static final int PENDING = 1;
        /** 已恢复 */
        public static final int RESTORED = 2;
    }

    /**
     * 文件类型
     */
    public static class FileType {
        /** Word文档 */
        public static final String WORD = "docx";
        /** Excel表格 */
        public static final String EXCEL = "xlsx";
        /** PowerPoint演示文稿 */
        public static final String POWERPOINT = "pptx";
        /** PDF文档 */
        public static final String PDF = "pdf";
        /** 文本文件 */
        public static final String TEXT = "txt";

        /**
         * 判断是否为Office文档
         */
        public static boolean isOfficeDocument(String fileType) {
            return WORD.equalsIgnoreCase(fileType)
                || EXCEL.equalsIgnoreCase(fileType)
                || POWERPOINT.equalsIgnoreCase(fileType);
        }

        /**
         * 判断是否为可编辑文档
         */
        public static boolean isEditable(String fileType) {
            return isOfficeDocument(fileType) || TEXT.equalsIgnoreCase(fileType);
        }
    }

    /**
     * 默认值
     */
    public static class Default {
        /** 默认最大存储空间（10GB） */
        public static final long MAX_STORAGE = 10737418240L;
        /** 默认最大成员数量 */
        public static final int MAX_MEMBERS = 100;
        /** 根文件夹父ID */
        public static final long ROOT_PARENT_ID = 0L;
        /** 回收站保留天数 */
        public static final int RECYCLE_BIN_DAYS = 30;
        /** 默认文件版本号 */
        public static final int DEFAULT_VERSION = 1;
    }
}

