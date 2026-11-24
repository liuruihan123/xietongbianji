package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文件历史视图对象
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@Schema(description = "文件历史视图对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoFileHistoryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 历史ID
     */
    @Schema(description = "历史ID")
    @ExcelProperty(value = "历史ID")
    private Long id;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    @ExcelProperty(value = "文件ID")
    private Long fileId;

    /**
     * 版本号
     */
    @Schema(description = "版本号")
    @ExcelProperty(value = "版本号")
    private Integer version;

    /**
     * 文档Key
     */
    @Schema(description = "文档Key")
    @ExcelProperty(value = "文档Key")
    private String documentKey;

    /**
     * 文件URL
     */
    @Schema(description = "文件URL")
    private String fileUrl;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    @ExcelProperty(value = "文件大小")
    private Long fileSize;

    /**
     * 变更数据URL
     */
    @Schema(description = "变更数据URL")
    private String changesUrl;

    /**
     * 服务器版本号
     */
    @Schema(description = "服务器版本号")
    private Integer serverVersion;

    /**
     * 变更描述
     */
    @Schema(description = "变更描述")
    @ExcelProperty(value = "变更描述")
    private String changeDescription;

    /**
     * 变更类型
     */
    @Schema(description = "变更类型")
    @ExcelProperty(value = "变更类型")
    private Integer changeType;

    /**
     * 变更类型描述
     */
    @Schema(description = "变更类型描述")
    private String changeTypeDesc;

    /**
     * 是否当前版本
     */
    @Schema(description = "是否当前版本")
    @ExcelProperty(value = "是否当前版本")
    private Boolean isCurrent;

    /**
     * 创建者ID
     */
    @Schema(description = "创建者ID")
    private Long creatorId;

    /**
     * 创建者名称
     */
    @Schema(description = "创建者名称")
    @ExcelProperty(value = "创建者名称")
    private String creatorName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 变更列表
     */
    @Schema(description = "变更列表")
    private List<CoFileChangeVo> changes;

    /**
     * OnlyOffice历史用户对象
     */
    @Schema(description = "OnlyOffice历史用户对象")
    private HistoryUser user;

    /**
     * OnlyOffice历史用户内部类
     */
    @Data
    @Schema(description = "OnlyOffice历史用户对象")
    public static class HistoryUser implements Serializable {
        
        private static final long serialVersionUID = 1L;

        @Schema(description = "用户ID")
        private String id;

        @Schema(description = "用户名称")
        private String name;
    }
}

