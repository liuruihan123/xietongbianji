package com.xmzs.coedit.constant;

/**
 * OnlyOffice常量定义
 *
 * @author zuleed
 * @date 2025-11-04
 */
public class OnlyOfficeConstants {

    /**
     * 文档类型
     */
    public static class DocumentType {
        public static final String WORD = "word";
        public static final String CELL = "cell";
        public static final String SLIDE = "slide";
    }

    /**
     * 文档模式
     */
    public static class Mode {
        public static final String VIEW = "view";
        public static final String EDIT = "edit";
        public static final String REVIEW = "review";
        public static final String COMMENT = "comment";
        public static final String FILLFORMS = "fillForms";
        public static final String EMBEDDED = "embedded";
    }

    /**
     * 文档权限
     */
    public static class Permission {
        public static final String EDIT = "edit";
        public static final String REVIEW = "review";
        public static final String COMMENT = "comment";
        public static final String FILLFORMS = "fillForms";
        public static final String READ = "read";
    }

    /**
     * 回调状态
     */
    public static class CallbackStatus {
        public static final int EDITING = 1;
        public static final int READY_FOR_SAVING = 2;
        public static final int SAVING_ERROR = 3;
        public static final int CLOSED_WITHOUT_CHANGES = 4;
        public static final int FORCE_SAVING = 6;
        public static final int CORRUPTED_OR_ERROR = 7;
    }

    /**
     * 文档格式映射
     */
    public static class FileExtension {
        // Word文档（包含WPS文字格式）
        public static final String[] WORD_FORMATS = {
            "doc", "docx", "docm", "dot", "dotx", "dotm", "odt", "fodt", "ott", "rtf", "txt",
            "wps"  // WPS文字格式
        };

        // Excel表格（包含WPS表格格式）
        public static final String[] CELL_FORMATS = {
            "xls", "xlsx", "xlsm", "xlt", "xltx", "xltm", "ods", "fods", "ots", "csv",
            "et", "ett"  // WPS表格格式
        };

        // PowerPoint演示（包含WPS演示格式）
        public static final String[] SLIDE_FORMATS = {
            "pps", "ppsx", "ppsm", "ppt", "pptx", "pptm", "pot", "potx", "potm", "odp", "fodp", "otp",
            "dps", "dpt"  // WPS演示格式
        };
    }

    /**
     * 错误码
     */
    public static class ErrorCode {
        public static final int SUCCESS = 0;
        public static final int DOCUMENT_KEY_ERROR = -1;
        public static final int CALLBACK_URL_ERROR = -2;
        public static final int INTERNAL_SERVER_ERROR = -3;
        public static final int DOWNLOAD_ERROR = -4;
        public static final int CONVERSION_TIMEOUT = -5;
        public static final int CONVERSION_ERROR = -6;
        public static final int UNEXPECTED_GUID = -7;
        public static final int DATABASE_ERROR = -8;
    }

    /**
     * 文件操作类型
     */
    public static class FileAction {
        public static final String CREATE = "create";
        public static final String UPLOAD = "upload";
        public static final String DOWNLOAD = "download";
        public static final String DELETE = "delete";
        public static final String RENAME = "rename";
        public static final String MOVE = "move";
        public static final String COPY = "copy";
        public static final String SHARE = "share";
    }

    /**
     * 共享权限类型
     */
    public static class SharePermission {
        public static final String VIEW = "view";
        public static final String EDIT = "edit";
        public static final String DOWNLOAD = "download";
        public static final String PRINT = "print";
        public static final String COMMENT = "comment";
    }

    /**
     * 工作空间类型
     */
    public static class WorkspaceType {
        public static final String PERSONAL = "personal";
        public static final String DEPARTMENT = "department";
        public static final String TEAM = "team";
    }

    /**
     * 文件夹类型
     */
    public static class FolderType {
        public static final String NORMAL = "normal";
        public static final String PRIVATE = "private";
        public static final String SHARED = "shared";
    }

    /**
     * 回收站状态
     */
    public static class RecycleStatus {
        public static final String DELETED = "deleted";
        public static final String RESTORED = "restored";
        public static final String PERMANENTLY_DELETED = "permanently_deleted";
    }
}

