package com.xmzs.system.domain.vo;

import com.xmzs.system.domain.ModelSetting;
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
 * 功能参数视图对象 model_setting
 *
 * @author zhuzi
 * @date 2024-08-27
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ModelSetting.class)
public class ModelSettingVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ExcelProperty(value = "id")
    private Integer id;

    /**
     * 知识库ID
     */
    @ExcelProperty(value = "知识库ID")
    private String kid;

    /**
     * 启用知识库（0启动知识库 1不启用知识库）
     */
    @ExcelProperty(value = "启用知识库", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "use_knowledge")
    private String useKnowledge;

    /**
     * 多少字符以上开启知识库，0为不限制
     */
    @ExcelProperty(value = "字符开启字符", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "short_no_open")
    private Integer shortNoOpen;

    /**
     * 用户问题二次包装模版
     */
    @ExcelProperty(value = "用户问题二次包装模版", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "standard_template_id")
    private Long standardTemplateId;

    /**
     * 上下文数量结束后的操作
     */
    @ExcelProperty(value = "上下文数量结束后的操作", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "after_talk_count")
    private String afterTalkCount;

    /**
     * 播放速度
     */
    @ExcelProperty(value = "播放速度", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "tts_speed")
    private Double ttsSpeed;

    /**
     * 业务类型（0AI面试 1面试例子 2公司背景 3面试要点）
     */
    @ExcelProperty(value = "业务类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "bus_type")
    private Integer busType;

    /**
     * 模型id
     */
    @ExcelProperty(value = "模型id")
    private Long modelId;

    /**
     * 名称
     */
    @ExcelProperty(value = "名称")
    private String name;

    /**
     * 角色设定
     */
    @ExcelProperty(value = "角色设定")
    private String systemRole;

    /**
     * 上下文数量
     */
    @ExcelProperty(value = "上下文数量")
    private Integer talkCount;

    /**
     * 语音人物
     */
    @ExcelProperty(value = "语音人物", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "tts_voice")
    private String ttsVoice;

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
    @ExcelDictFormat(readConverterExp = "0=正常,1=停用")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


    /**
     * 系统模型
     */
    private SysModelVo sysModel;


}
