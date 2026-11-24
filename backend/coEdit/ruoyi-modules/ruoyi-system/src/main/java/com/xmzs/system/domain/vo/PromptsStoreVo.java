package com.xmzs.system.domain.vo;

import com.xmzs.system.domain.PromptsStore;
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
 * 提示词商店视图对象 prompts_store
 *
 * @author zuleed
 * @date 2024-10-15
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = PromptsStore.class)
public class PromptsStoreVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ExcelProperty(value = "id")
    private Integer id;

    /**
     * 提示词名称
     */
    @ExcelProperty(value = "提示词名称")
    private String name;

    /**
     * 模型id
     */
    @ExcelProperty(value = "模型id", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "base_model_list")
    private String modelId;

    /**
     * 角色设定
     */
    @ExcelProperty(value = "角色设定")
    private String systemRole;

    /**
     * 用户要求
     */
    @ExcelProperty(value = "用户要求")
    private String userPrompts;

    /**
     * 上下文数量
     */
    @ExcelProperty(value = "上下文数量")
    private Integer talkCount;

    /**
     * 随机性
     */
    @ExcelProperty(value = "随机性")
    private Double temperature;

    /**
     * 核采样
     */
    @ExcelProperty(value = "核采样")
    private Double topP;

    /**
     * 单次回复限制
     */
    @ExcelProperty(value = "单次回复限制")
    private Integer maxTokens;

    /**
     * 话题新鲜度
     */
    @ExcelProperty(value = "话题新鲜度")
    private Double presencePenalty;

    /**
     * 频率惩罚度
     */
    @ExcelProperty(value = "频率惩罚度")
    private Double frequencyPenalty;

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_common_status")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
