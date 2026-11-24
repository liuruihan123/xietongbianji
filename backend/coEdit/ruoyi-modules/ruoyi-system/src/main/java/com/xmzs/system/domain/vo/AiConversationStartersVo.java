package com.xmzs.system.domain.vo;

import com.xmzs.system.domain.AiConversationStarters;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.common.excel.annotation.ExcelDictFormat;
import com.xmzs.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 对话开场视图对象 ai_conversation_starters
 *
 * @author zuleed
 * @date 2024-11-26
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiConversationStarters.class)
public class AiConversationStartersVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ExcelProperty(value = "id")
    private Long id;

    /**
     * 中文标题
     */
    @ExcelProperty(value = "中文标题")
    private String titleCn;

    /**
     * 英文标题
     */
    @ExcelProperty(value = "英文标题")
    private String titleEn;


    /**
     * 启动案例库（0启动知识库 1不启用知识库
     */
    private String useKnowledgeAttach;


    /**
     * 案例库Id(多选)
     */
    private String knowledgeAttachIds;


    /**
     * 是否固定在开头(0不固定 1固定)
     */
    private String templateFixed;


    /**
     * 模版id
     */
    private String templateId;

    /**
     * 启用知识库（0启动知识库 1不启用知识库）
     */
    @ExcelProperty(value = "启用知识库", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_yes_no")
    private String useKnowledge;

    /**
     * 知识库ID(多选)
     */
    @ExcelProperty(value = "知识库ID(多选)")
    private String kidIds;

    /**
     * 业务类型（0AI面试 1面试例子 2公司背景 3面试要点）
     */
    @ExcelProperty(value = "业务类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "bus_type")
    private Integer busType;

    /**
     * 关联标签
     */
    @ExcelProperty(value = "关联标签")
    private String tags;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_common_status")
    private String status;

    /**
     * 创建者
     */
    @ExcelProperty(value = "创建者")
    private String createdBy;

    /**
     * 缺省字段1
     */
    @ExcelProperty(value = "缺省字段1")
    private String val1;

    /**
     * 缺省字段1
     */
    @ExcelProperty(value = "缺省字段1")
    private String val2;


}
