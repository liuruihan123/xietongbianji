package com.xmzs.knowledge.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 知识库文件对象 knowledge_attach
 *
 * @author zhuzi
 * @date 2024-06-27
 */
@Data
//@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_attach")
public class KnowledgeAttach implements Serializable {
//public class KnowledgeAttach extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 知识库ID
     */
    private String kid;

    /**
     * 文档ID
     */
    private String docId;

    /**
     * 文档名称
     */
    private String docName;

    /**
     * 文档地址
     */
    private String docUrl;

    /**
     * 文档类型
     */
    private String docType;

    /**
     * 文档内容
     */
    private String content;


//    @TableField(value = "create_time")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;


    private String createBy;


}
