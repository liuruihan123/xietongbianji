package com.xmzs.knowledge.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.knowledge.domain.KnowledgeAttach;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 知识库文件视图对象 knowledge_attach
 *
 * @author zhuzi
 * @date 2024-06-27
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = KnowledgeAttach.class)
public class KnowledgeAttachVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @ExcelProperty(value = "编号")
    private Integer id;

    /**
     * 知识库ID
     */
    @ExcelProperty(value = "知识库ID")
    private String kid;

    /**
     * 文档ID
     */
    @ExcelProperty(value = "文档ID")
    private String docId;

    /**
     * 文档名称
     */
    @ExcelProperty(value = "文档名称")
    private String docName;

    /**
     * 文档地址
     */
    private String docUrl;

    /**
     * 文档类型
     */
    @ExcelProperty(value = "文档类型")
    private String docType;

    /**
     * 文档内容
     */
    @ExcelProperty(value = "文档内容")
    private String content;


}
