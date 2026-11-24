package com.xmzs.coedit.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * OnlyOffice回调请求DTO
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallbackRequest {

    /**
     * 文档标识符
     */
    private String key;

    /**
     * 文档状态
     * 1: 文档正在编辑
     * 2: 文档已准备好保存
     * 3: 文档保存错误
     * 4: 文档已关闭且无更改
     * 6: 文档正在编辑，但当前文档状态已保存
     * 7: 强制保存文档时发生错误
     */
    private Integer status;

    /**
     * 已编辑文档的链接
     * 状态为2、3、6、7时存在
     */
    private String url;

    /**
     * 文档更改历史的链接
     * 状态为2、3、6、7时存在
     */
    @JsonProperty("changesurl")
    private String changesUrl;

    /**
     * 文档更改历史（已废弃，使用history代替）
     */
    @JsonProperty("changeshistory")
    private String changesHistory;

    /**
     * 文档更改历史对象
     */
    private History history;

    /**
     * 文件类型
     */
    @JsonProperty("filetype")
    private String fileType;

    /**
     * 强制保存类型
     * 0: 命令服务
     * 1: 保存按钮点击
     * 2: 定时器
     * 3: 表单提交
     */
    @JsonProperty("forcesavetype")
    private Integer forceSaveType;

    /**
     * 表单数据URL
     */
    @JsonProperty("formsdataurl")
    private String formsDataUrl;

    /**
     * 用户操作数组
     */
    private List<Action> actions;

    /**
     * 当前编辑用户列表
     */
    private List<String> users;

    /**
     * 用户数据
     */
    @JsonProperty("userdata")
    private String userData;

    /**
     * JWT Token
     */
    private String token;

    /**
     * 用户操作对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Action {
        /**
         * 操作类型
         * 0: 用户断开连接
         * 1: 新用户连接
         * 2: 用户点击强制保存按钮
         */
        private Integer type;

        /**
         * 用户标识符
         */
        @JsonProperty("userid")
        private String userId;
    }

    /**
     * 文档历史对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class History {
        /**
         * 更改内容
         */
        private Object changes;

        /**
         * 服务器版本
         */
        @JsonProperty("serverVersion")
        private String serverVersion;
    }

    /**
     * 判断是否需要保存文档
     *
     * @return 是否需要保存
     */
    public boolean needSave() {
        return status != null && (status == 2 || status == 6);
    }

    /**
     * 判断是否为错误状态
     *
     * @return 是否为错误
     */
    public boolean isError() {
        return status != null && (status == 3 || status == 7);
    }

    /**
     * 判断是否为编辑状态
     *
     * @return 是否正在编辑
     */
    public boolean isEditing() {
        return status != null && status == 1;
    }

    /**
     * 判断是否为关闭状态（无更改）
     *
     * @return 是否已关闭
     */
    public boolean isClosed() {
        return status != null && status == 4;
    }
}

