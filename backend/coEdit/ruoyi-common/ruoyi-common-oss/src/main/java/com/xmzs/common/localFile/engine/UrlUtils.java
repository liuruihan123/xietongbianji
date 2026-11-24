package com.xmzs.common.localFile.engine;


import com.xmzs.common.core.utils.RequestUtils;

/**
 * 文件路径处理工具
 */
public class UrlUtils {

    /**
     * 访问前缀
     */
    private static final String uploadPrefix = "/api/uploads/";
    /**
     * 转绝对路径
     * 转前: uploads/11.png
     * 转后: https://127.0.0.1/uploads/11.png
     *
     * @author fzr
     * @param url 相对路径
     * @return String
     */
    public static String toAbsoluteUrl(String url) {
        if (url == null || url.equals("")) {
            return "";
        }

        if(url.indexOf("/") != 0) {
            url = "/" + url;
        }

        if (url.startsWith("/api/static/")) {
            return RequestUtils.uri() + url;
        }

        return RequestUtils.uri() + "/" + uploadPrefix + url;
    }

    /**
     * 转相对路径
     * 转前: https://127.0.0.1/uploads/11.png
     * 转后: uploads/11.png
     *
     * @author fzr
     * @param url 绝对路径
     * @return String
     */
    public static String toRelativeUrl(String url) {
        if (url == null || url.equals("")) {
            return "";
        }
            return url.replace(RequestUtils.uri(), "")
                      .replace("/" + uploadPrefix + "/", "");

    }

    /**
     * 获取存储域名
     * 示例: https://127.0.0.1/
     *
     * @author fzr
     * @return String
     */
    public static String domain() {
            return RequestUtils.uri() + "/";
    }

}
